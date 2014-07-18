package org.acepricot.asn1.rfc2986;

import org.acepricot.asn1.BitString;
import org.acepricot.ber.BERConst;


class Signature extends BitString {

	Signature(String name) {
		super(BERConst.TRUE, BERConst.FALSE,name);
	}
	
}
