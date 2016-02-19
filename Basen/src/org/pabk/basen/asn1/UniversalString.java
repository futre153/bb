package org.pabk.basen.asn1;

import java.io.IOException;

import org.pabk.basen.ber.BERImpl;

public class UniversalString extends ASN1Impl {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UniversalString(String name, int pc, boolean optional) {
		this.setName(name);
		this.set_class(BERImpl.UNIVERSAL_CLASS);
		this.setConstructed(pc);
		this.setTag(BERImpl.UNIVERSAL_STRING_TAG);
		this.setImplicit(false);
		this.setOptional(optional);
	}

	public boolean setBERObject(boolean implicit, BERImpl ber) throws IOException {
		if(super.setBERObject(ber)) {
			BitString.setEqualSequence (this, ber);
		}
		return false;	
	}
	private UniversalString() {}
	@Override
	public UniversalString clone() {
		return (UniversalString) ASN1Impl.clone(new UniversalString(), this);
	}
}
