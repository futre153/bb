package org.pabk.basen.asn1;

import org.pabk.basen.ber.BERImpl;

public class UTCTime extends ASN1Impl {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public UTCTime (String name, boolean optional) {
		this.setName(name);
		this.set_class(BERImpl.UNIVERSAL_CLASS);
		this.setConstructed(BERImpl.PRIMITIVE_ENCODING);
		this.setTag(BERImpl.UTC_TIME_TAG);
		this.setImplicit(false);
		this.setOptional(optional);
	}
	private UTCTime() {}
	@Override
	public UTCTime clone() {
		return (UTCTime) ASN1Impl.clone(new UTCTime(), this);
	}
}
