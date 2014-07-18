package org.acepricot.asn1.rfc2986;

import org.acepricot.asn1.Integer;
import org.acepricot.ber.BERConst;

class Version extends Integer {

	Version(String name) {
		super(BERConst.TRUE,BERConst.FALSE,name);
	}
}
