package org.pabk.basen.asn1;

import java.io.IOException;

import org.pabk.basen.ber.BERImpl;

public class BmpString extends ASN1Impl {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BmpString(String name, int pc, boolean optional) {
		this.setName(name);
		this.set_class(BERImpl.UNIVERSAL_CLASS);
		this.setConstructed(pc);
		this.setTag(BERImpl.BMP_STRING_TAG);
		this.setImplicit(false);
		this.setOptional(optional);
	}

	public boolean setBERObject(boolean implicit, BERImpl ber) throws IOException {
		if(super.setBERObject(ber)) {
			BitString.setEqualSequence (this, ber);
		}
		return false;	
	}
	private BmpString() {}
	@Override
	public BmpString clone() {
		return (BmpString) ASN1Impl.clone(new BmpString(), this);
	}
}
