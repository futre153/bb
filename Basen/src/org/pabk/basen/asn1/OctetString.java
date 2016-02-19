package org.pabk.basen.asn1;

import java.io.IOException;

import org.pabk.basen.ber.BERImpl;

public class OctetString extends ASN1Impl {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OctetString(String name, int pc, boolean optional) {
		this.setName(name);
		this.set_class(BERImpl.UNIVERSAL_CLASS);
		this.setConstructed(pc);
		this.setTag(BERImpl.OCTETSTRING_TAG);
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
	private OctetString() {}
	@Override
	public OctetString clone() {
		return (OctetString) ASN1Impl.clone(new OctetString(), this);
	}
}
