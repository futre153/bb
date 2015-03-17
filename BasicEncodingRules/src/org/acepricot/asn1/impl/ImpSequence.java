package org.acepricot.asn1.impl;

import org.acepricot.asn1.ASN1NodeImpl;
import org.acepricot.asn1.Sequence;
import org.acepricot.ber.BERConst;

public class ImpSequence extends Sequence {

	protected ImpSequence(String name, ASN1NodeImpl ... sequences) {
		super(BERConst.TRUE, BERConst.FALSE, name, sequences);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected ASN1NodeImpl clone() {
		// TODO Auto-generated method stub
		return new ImpSequence(this.getNme(), this.getSeq());
	}

}
