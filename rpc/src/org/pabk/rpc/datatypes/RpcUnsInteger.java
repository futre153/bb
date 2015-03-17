package org.pabk.rpc.datatypes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

abstract class RpcUnsInteger implements RpcDatatype {
	
	private long value;
		
	protected RpcUnsInteger(Number nr) {
		setValue(nr);
	}
	
	@Override
	public Long getValue() {
		return this.value;
	}
	
	public void setValue(Object nr) {
		if(nr instanceof Number) {
			this.value = ((Number) nr).longValue() & getMaxValue();
		}
	}
	
	public void encode(OutputStream out) throws IOException {
		out.write(getEncoder().encode(this));
	}

	@Override
	public byte[] decode(InputStream in) throws IOException {
		byte[] b = new byte[Long.bitCount(getMaxValue()) / Byte.SIZE];
		int i = in.read(b);
		if(i > 0) {
			getEncoder().decode(this, b);
			return b;
		}
		throw new IOException("Unexcpected end of stream reached");
	}
	
	abstract long getMaxValue();
	
	abstract Encoder getEncoder();
	
	abstract void setEncoder(Encoder enc);
	
	public String toString() {
		return this.getValue().toString();
	}
}
