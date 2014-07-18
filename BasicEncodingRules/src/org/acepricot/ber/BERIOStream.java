package org.acepricot.ber;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BERIOStream implements BERInputStream, BEROutputStream {
	
	private InputStream in;
	private OutputStream out;
	
	@Override
	public void write(int i) throws IOException {
		out.write(i);
	}

	@Override
	public void write(byte[] b) throws IOException {
		out.write(b);
	}

	@Override
	public int read() throws IOException {
		int b = in.read();
		if(b<0) throw new BERFormatException(String.format(BERFormatException.UNEXPECTED_END_OF_STREAM));
		return b;
	}

	@Override
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	@Override
	public int read(byte[] b, int s, int l) throws IOException {
		while(l > 0) {
			int i = in.read(b,s,l);
			l -= i;
			s += i;
			if(i<0) throw new BERFormatException(String.format(BERFormatException.UNEXPECTED_END_OF_STREAM));
		}
		return s;
	}

	@Override
	public void setOutputStream(OutputStream out) {
		this.out = out;
	}

	@Override
	public void setInputStream(InputStream in) {
		this.in = in;
		
	}

	@Override
	public void close () throws IOException {
		in.close();
		out.flush();
		out.close();
	}

}
