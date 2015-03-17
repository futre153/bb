package org.acepricot.asn1.impl;

import org.acepricot.asn1.ASN1NodeImpl;
import org.acepricot.asn1.Null;
import org.acepricot.ber.BERConst;

public class ImpNull extends Null {

	public ImpNull(String name) {
		super(BERConst.TRUE, BERConst.FALSE, name);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected ASN1NodeImpl clone() {
		// TODO Auto-generated method stub
		return new ImpNull(this.getNme());
	}

}
