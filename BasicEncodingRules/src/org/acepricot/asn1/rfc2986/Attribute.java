package org.acepricot.asn1.rfc2986;

import org.acepricot.asn1.ASN1NodeImpl;
import org.acepricot.asn1.Sequence;
import org.acepricot.ber.BERConst;

class Attribute extends Sequence {
	
	protected AttributeType attributeType = new AttributeType("type");
	protected ASN1NodeImpl attributeValue = new AttributeValue(BERConst.TRUE, "value");
	
	ASN1NodeImpl[] attributeTypeAndValueSeq = {
			attributeType,
			attributeValue
	};
	
	protected Attribute(String name, ASN1NodeImpl builtIn) {
		super (BERConst.TRUE, BERConst.FALSE, name);
		if(builtIn != null) {
			attributeValue = builtIn;
		}
		this.setSeq(attributeTypeAndValueSeq);
	}
}
