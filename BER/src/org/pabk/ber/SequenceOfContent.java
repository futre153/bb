package org.pabk.ber;

import java.io.IOException;

public class SequenceOfContent extends ContentOctets {
	
	private static final long serialVersionUID = 1L;

	@Override
	public Object getValue() throws UnsupportedOperationException {
		BER[] ber=new BER[this.size()];
		return this.toArray(ber);
	}

	@Override
	public void decode(Encoder en, long l) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void encode(Encoder en) throws IOException {
		for(int i=0;i<this.size();i++) {this.get(i).encode(en);}
	}

	@Override
	public long length() throws UnsupportedOperationException {
		long l=0;
		for(int i=0;i<this.size();i++) {l+=this.get(i).length();}
		return l;
	}

	@Override
	public byte[] getBytes() throws UnsupportedOperationException {
		byte[] b=new byte[]{};
		for(int i=0;i<this.size();i++) {
			b=BER.join(b,this.get(i).getBytes());
		}
		return b;
	}
	public void clearContent() {this.clear();}
}
