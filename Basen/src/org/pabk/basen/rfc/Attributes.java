package org.pabk.basen.rfc;

import org.pabk.basen.asn1.ASN1Impl;
import org.pabk.basen.asn1.Sequence;

public class Attributes extends Sequence {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String ATT_NAME = "attribute";
	private static final boolean ATT_OPT = true;

	private final ASN1Impl[] ATT_SEQ = {
		new Attribute(ATT_NAME, ATT_OPT)
	};
	
	private final ASN1Impl[] ATTS_SEQ = {
		new Sequence(ATT_NAME, ATT_OPT, ATT_SEQ)
	};

	public Attributes(String name, boolean optional) {
		super(name, optional);
		this.setSequences(ATTS_SEQ);
	}
	private Attributes() {}
	@Override
	public Attributes clone() {
		return (Attributes) ASN1Impl.clone(new Attributes(), this);
	}
}
