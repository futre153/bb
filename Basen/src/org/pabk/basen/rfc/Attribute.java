package org.pabk.basen.rfc;

import org.pabk.basen.asn1.ASN1Impl;
import org.pabk.basen.asn1.Sequence;
import org.pabk.basen.asn1.Any;
import org.pabk.basen.asn1.ObjectIdentifier;

public class Attribute extends Sequence {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String TYPE_NAME = "type";
	private static final boolean TYPE_OPT = false;
	private static final String VALUE_NAME = "value";
	private static final boolean VALUE_OPT = true;
	
	private final ASN1Impl[] ATT_SEQ = {
		new ObjectIdentifier(TYPE_NAME, TYPE_OPT),
		new Any(VALUE_NAME, VALUE_OPT)
	};
	
	
	public Attribute(String name, boolean optional) {
		super(name, optional);
		this.setSequences(ATT_SEQ);
	}
	private Attribute() {}
	@Override
	public Attribute clone() {
		return (Attribute) ASN1Impl.clone(new Attribute(), this);
	}

}
