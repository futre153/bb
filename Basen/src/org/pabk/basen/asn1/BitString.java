package org.pabk.basen.asn1;

import java.io.IOException;

import org.pabk.basen.ber.BERImpl;

public class BitString extends ASN1Impl {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BitString(String name, int pc, boolean optional) {
		this.setName(name);
		this.set_class(BERImpl.UNIVERSAL_CLASS);
		this.setConstructed(pc);
		this.setTag(BERImpl.BITSTRING_TAG);
		this.setImplicit(false);
		this.setOptional(optional);
	}

	public boolean setBERObject(boolean implicit, BERImpl ber) throws IOException {
		if(super.setBERObject(ber)) {
			setEqualSequence (this, ber);
		}
		return false;	
	}
	static void setEqualSequence (ASN1Impl asn, BERImpl ber) throws IOException {
		try {
			for(int i = 0; i < ber.size(); i ++) {
				ASN1Impl child = asn.clone();
				child.setBERObject(ber);
			}
		}
		catch (Exception e) {
			throw new IOException(e);
		}
	}
	private BitString() {}
	@Override
	public BitString clone() {
		return (BitString) ASN1Impl.clone(new BitString(), this);
	}
	@Override
	public void setValue(Object ... objs) throws IOException {
		if(objs != null && objs.length == 2 && objs[0] != null && objs[1] != null && (objs[0] instanceof Integer) && (objs[1] instanceof byte[])) {
			byte[] bs = (byte[]) objs[1];
			byte[] b = new byte[bs.length + 1];
			System.arraycopy(bs, 0, b, 1, bs.length);
			b[0] = (byte) objs[0];
			super.setValue(bs);
		}
	}
}
