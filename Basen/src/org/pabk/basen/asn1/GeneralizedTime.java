package org.pabk.basen.asn1;

import org.pabk.basen.ber.BERImpl;

public class GeneralizedTime extends ASN1Impl {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public GeneralizedTime (String name, boolean optional) {
		this.setName(name);
		this.set_class(BERImpl.UNIVERSAL_CLASS);
		this.setConstructed(BERImpl.PRIMITIVE_ENCODING);
		this.setTag(BERImpl.GENERALIZED_TIME_TAG);
		this.setImplicit(false);
		this.setOptional(optional);
	}
	private GeneralizedTime() {}
	@Override
	public GeneralizedTime clone() {
		return (GeneralizedTime) ASN1Impl.clone(new GeneralizedTime(), this);
	}
}
