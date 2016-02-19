package org.pabk.basen.ber.encoders;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import org.pabk.basen.ber.BERImpl;

public class OctetStringEncoder implements BEREncoder {

	private static final long MAX_BYTE_ARRAY_LENGTH = 1024 * 1024;
	private static final int MAX_READED_BYTES = 1024 * 16;
	private static final String PREFIX_OF_TMP_FILES = "BASEN_TMP_";
	private static final String END_OF_STREAM = "Unexpected end of stream reached";
	private static final String NULL_CONTENT = "Content cannot be null";

	@Override
	public InputStream encode(BERImpl ber) throws IOException {
		Object content = ber.getContent();
		if(content != null) {
			if(content instanceof File) {
				return new FileInputStream((File) content);
			}
			return new ByteArrayInputStream (((ByteArrayOutputStream) content).toByteArray());
		}
		throw new IOException (NULL_CONTENT);
	}

	@Override
	public long decode(BERImpl ber, InputStream in) throws IOException {
		long l = ber.getLength();
		OutputStream out = null;
		File f = null;
		try {
			if(l > MAX_BYTE_ARRAY_LENGTH) {
				f = File.createTempFile(PREFIX_OF_TMP_FILES + new Date().getTime(), null);
				f.deleteOnExit();
				out = new FileOutputStream(f);
			}
			else {
				out = new ByteArrayOutputStream();
			}
			byte[] b = new byte[MAX_READED_BYTES];
			while (l > 0) {
				int i = (int) (l < MAX_READED_BYTES ? l : MAX_READED_BYTES);
				i = in.read(b, 0, i);
				if(i < 0) {
					throw new IOException(END_OF_STREAM);
				}
				l -= i;
				out.write(b, 0, i);
			}
		}
		catch (Exception e) {
			throw new IOException (e);
		}
		finally {
			if(f != null) {
				out.close();
			}
		}
		if(f == null) {
			ber.setContent(out);
		}
		else {
			ber.setContent(f);
		}
		return ber.getLength();
	}

	@Override
	public long getLength(BERImpl ber) throws IOException {
		Object content = ber.getContent();
		if(content != null) {
			if(content instanceof File) {
				return ((File) content).length();
			}
			return ((ByteArrayOutputStream) content).size();
		}
		throw new IOException (NULL_CONTENT);
	}

	@Override
	public void setValue(BERImpl ber, Object obj) throws IOException {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			out.write((byte[]) obj);
			ber.setContent(out);
		}
		catch (Exception e) {
			throw new IOException (e);
		}
	}

	@Override
	public String[] toString(BERImpl ber) {
		return EncoderUtil.printHex(ber.getContentOctets());
	}

}
