package org.pabk.rpc.datatypes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class RpcUnsignedChar implements RpcDatatype {
	
	private String charset;
	private char[] value;
	
	public RpcUnsignedChar() {
		setValue(new char[0]);
		this.setCharset(null);
	}
	
	@Override
	public Object getValue() {
		return new String(value);
	}

	@Override
	public void setValue(Object nr) {
		if(nr instanceof String) {
			value = ((String) nr).toCharArray();
		}
	}

	@Override
	public void encode(OutputStream out) throws IOException {
		String charset = this.getCharset();
		if(charset == null) {
			out.write(new String((char[])this.getValue()).getBytes());
		}
		else {
			out.write(new String((char[])this.getValue()).getBytes(charset));
		}
	}

	@Override
	public byte[] decode(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int i = -1;
		while((i = in.read()) > 0) {
			out.write(i);
		}
		String charset = this.getCharset();
		if(charset == null) {
			this.setValue(new String(out.toByteArray()).toCharArray());
		}
		else {
			this.setValue(new String(out.toByteArray(), charset).toCharArray());
		}
		return out.toByteArray();
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

}
