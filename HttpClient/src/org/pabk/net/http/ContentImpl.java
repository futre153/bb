package org.pabk.net.http;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Enumeration;
import java.util.Properties;
import java.util.zip.GZIPOutputStream;

abstract class ContentImpl implements Content {
	
	private static final int MAX_READED_BYTES = 128*256;
	//protected int authType=HttpClientConst.NO_AUTHENTICATION;
	//protected String type;
	
	private Object content;
	private String contentType;
	private String characterEncoding;
	private String contentEncoding;
	private boolean chunked;
	private Properties additionalProperties = new Properties();
	
	protected ContentImpl() {
		this(null, HttpClientConst.PLAIN_HTTP_CONTENT, HttpClientConst.NULL, HttpClientConst.NULL);
	}
	
	protected ContentImpl(int type) {
		this(null, type, HttpClientConst.NULL, HttpClientConst.NULL);
	}
	
	protected ContentImpl(Object content, int type, int charEncoding, int conEncoding) {
		this.setContent(content);
		this.setContentType(type);
		this.setCharacterEncoding(charEncoding);
		this.setContentEncoding(conEncoding);
	}
	/*
	
	*/
	
	@Override
	public void setContent(Object obj) {
		this.content = obj;
		setChunked(obj == null || (obj instanceof String || obj instanceof File));
	}

	@Override
	public long getLength() throws IOException {
		if(content != null) {
			if(this.contentEncoding == null) {
				if(this.content instanceof String) {
					return ((String) this.content).getBytes(getCharacterEncoding()).length;
				}
				if(this.content instanceof File) {
					return ((File) this.content).length();
				}
				if(this.content instanceof ByteArrayInputStream) {
					return ((ByteArrayInputStream) this.content).available();
				}
			}
			return -1;
		}
		return 0;
	}

	@Override
	public void setContentType(int contentType) {
		this.contentType = HttpClientConst.CONTENT_TYPES[contentType];
	}

	@Override
	public String getContentType() {
		return this.contentType;
	}

	@Override
	public void setCharacterEncoding(int encoding) {
		this.characterEncoding = HttpClientConst.ENCODINGS[encoding];
		
	}

	@Override
	public String getCharacterEncoding() {
		return this.characterEncoding;
	}

	@Override
	public void setContentEncoding(int encoding) {
		this.contentEncoding = HttpClientConst.CONTENT_ENCODINGS[encoding];
		setChunked(this.characterEncoding != null);
	}

	@Override
	public String getContentEncoding() {
		return this.contentEncoding;
	}
	
	@Override
	public void setChunked(boolean chunked) {
		this.chunked = chunked;
	}
	
	public boolean isChunked() {
		return chunked;
	}
	
	@Override
	public void doFinal(OutputStream out) throws IOException {
		if(this.content instanceof String) {
			ByteArrayInputStream in = new ByteArrayInputStream(((String) this.content).getBytes(this.characterEncoding));
			doFinalEncode(in, out, this.contentEncoding);
		}
		else if (this.content instanceof File) {
			FileInputStream in = new FileInputStream((File) this.content);
			doFinalEncode(in, out, this.contentEncoding);
			in.close();
		}
		else if (this.content instanceof InputStream){
			doFinalEncode((InputStream) this.content, out, this.contentEncoding);
		}
		else {
			throw new IOException("Object cannot be transfer with no chunked");
		}
		out.flush();
	}	
	
	
	private static void doFinalEncode(InputStream in, OutputStream out, String enc) throws IOException {
		if(enc != null) {
			if(enc.equals(HttpClientConst.CONTENT_ENCODINGS[HttpClientConst.GZIP_INDEX])) {
				GZIPOutputStream gzip = new GZIPOutputStream(out);
				doFinal(in, gzip);
				gzip.finish();
			}
			else {
				throw new IOException("Unknown content encoding");
			}
		}
		else {
			doFinal(in, out);
		}
	}
	
	private static void doFinal(InputStream in, OutputStream out) throws IOException {
		byte[] b = new byte[MAX_READED_BYTES];
		int i = -1;
		while((i = in.read(b)) >= 0) {
			out.write(b, 0, i);
		}
	}

	@Override
	public void setAdditionalProperty(String key, String value) {
		this.additionalProperties.put(key, value);		
	}

	@Override
	public void applyAdditionalProperties(HttpURLConnection con) {
		Enumeration<Object> keys = this.additionalProperties.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			con.addRequestProperty(key, this.additionalProperties.getProperty(key));
		}
	}
}