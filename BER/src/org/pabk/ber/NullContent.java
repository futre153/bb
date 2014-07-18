package org.pabk.ber;

import java.io.IOException;

class NullContent extends ContentOctets {

	private static final long serialVersionUID = 1L;

	@Override
	public Object getValue() throws UnsupportedOperationException {return null;}

	@Override
	public void decode(Encoder en, long l) throws IOException {}

	@Override
	public void encode(Encoder en) throws IOException {}

	@Override
	public long length() throws UnsupportedOperationException {return 0;}

	@Override
	public byte[] getBytes() throws UnsupportedOperationException {return new byte[]{0,0};}
	
	public void clearContent() {}
}
