package org.pabk.basen.asn1;

import java.io.IOException;

import org.pabk.basen.ber.BERImpl;

public class IA5String extends ASN1Impl {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IA5String(String name, int pc, boolean optional) {
		this.setName(name);
		this.set_class(BERImpl.UNIVERSAL_CLASS);
		this.setConstructed(pc);
		this.setTag(BERImpl.IA5_STRING_TAG);
		this.setImplicit(false);
		this.setOptional(optional);
	}

	public boolean setBERObject(boolean implicit, BERImpl ber) throws IOException {
		if(super.setBERObject(ber)) {
			BitString.setEqualSequence (this, ber);
		}
		return false;	
	}
	private IA5String() {}
	@Override
	public IA5String clone() {
		return (IA5String) ASN1Impl.clone(new IA5String(), this);
	}
}
