package org.pabk.basen.asn1;

import org.pabk.basen.ber.BERImpl;

public class Any extends ASN1Impl {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Any(String name, boolean optional) {
		this.setName(name);
		this.setImplicit(false);
		this.setOptional(optional);
	}
	
	private Any() {}
	@Override
	public Any clone() {
		return (Any) ASN1Impl.clone(new Any(), this);
	}
	public boolean checkId (BERImpl ber) {
		this.set_class(ber.get_class());
		this.setConstructed(ber.getConstructed());
		this.setTag(ber.getTag());
		return true;
	}
}
