package org.acepricot.asn1.rfc2986;

import org.acepricot.asn1.ASN1NodeImpl;
import org.acepricot.asn1.SetOf;
import org.acepricot.ber.BERConst;

class RelativeDistinguishedName extends SetOf {
	
	private Attribute attribute = new  Attribute("attributeTypeAndValue", new RDNAttributeValue("value"));
	
	private ASN1NodeImpl[] relativeDistinguishedNameSet = {
			attribute
	};
	
	RelativeDistinguishedName (String name) {
		super (BERConst.TRUE, BERConst.FALSE, name);
		this.setSeq(relativeDistinguishedNameSet);
	};
}
