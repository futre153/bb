package org.acepricot.asn1.rfc2986;

import org.acepricot.asn1.ObjectIdentifier;
import org.acepricot.ber.BERConst;


class AttributeType extends ObjectIdentifier {

	AttributeType(String name) {
		super(BERConst.TRUE,BERConst.FALSE,name);
	}
	
}
