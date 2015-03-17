package org.acepricot.asn1.impl;

import org.acepricot.asn1.ASN1NodeImpl;
import org.acepricot.asn1.ObjectIdentifier;
import org.acepricot.ber.BERConst;

public class ImpOID extends ObjectIdentifier {

	public ImpOID(String name) {
		super(BERConst.TRUE, BERConst.FALSE, name);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected ASN1NodeImpl clone() {
		// TODO Auto-generated method stub
		return new ImpOID(this.getNme());
	}

}
