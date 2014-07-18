package org.pabk.ber;

import java.io.IOException;

public class BooleanContent extends ContentOctets {
	private static final long serialVersionUID = 1L;
	private Byte value=null;
	
	public BooleanContent(boolean b) {value=(byte) (b?-1:0);}
	public BooleanContent() {value=null;}

	@Override
	public Object getValue() throws UnsupportedOperationException {
		if(value==null)throw new UnsupportedOperationException("Value not set");
		return value!=0;
	}

	@Override
	public void decode(Encoder en, long l) throws IOException {
		byte b=(byte) en.read();
		if(value==null)value=b;
		if((b!=0 && value==0) || (b==0 && value!=0))throw new IOException("Boolean value is not equal with predefined value");
	}

	@Override
	public void encode(Encoder en) throws IOException {
		if(value==null)throw new UnsupportedOperationException("Value not set");
		en.read();
	}

	@Override
	public long length() throws UnsupportedOperationException {return getBytes().length;}

	@Override
	public byte[] getBytes() throws UnsupportedOperationException {
		if(value==null)throw new UnsupportedOperationException("Value not set");
		return new byte[]{value.byteValue()};
	}
	
	public String toString() {return getValue().toString();}
	public String toString(int i) {return " value="+toString();}
	@Override
	public void clearContent() {this.value=null;}
	
}
