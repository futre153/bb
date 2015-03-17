package org.acepricot.asn1.impl;

import org.acepricot.asn1.ASN1NodeImpl;
import org.acepricot.asn1.OctetString;
import org.acepricot.ber.BERConst;

public class ImpOctetString extends OctetString {

	public ImpOctetString(String name) {
		super(BERConst.TRUE, BERConst.FALSE, name);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected ASN1NodeImpl clone() {
		return new ImpOctetString(this.getNme());
	}

}
