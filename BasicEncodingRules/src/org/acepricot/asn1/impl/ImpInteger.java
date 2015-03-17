package org.acepricot.asn1.impl;

import org.acepricot.asn1.ASN1NodeImpl;
import org.acepricot.asn1.Integer;

public class ImpInteger extends Integer {

	public ImpInteger(String name) {
		super(1, 0, name);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected ASN1NodeImpl clone() {
		// TODO Auto-generated method stub
		return new ImpInteger(this.getNme());
	}

}
