package org.acepricot.asn1.impl;

import org.acepricot.asn1.ASN1NodeImpl;
import org.acepricot.asn1.Any;
import org.acepricot.ber.BERConst;

public class ImpAny extends Any {

	protected ImpAny(String name) {
		super(BERConst.TRUE, name);
	}

	@Override
	protected ASN1NodeImpl clone() {
		return new ImpAny(this.getNme());
	}

}
