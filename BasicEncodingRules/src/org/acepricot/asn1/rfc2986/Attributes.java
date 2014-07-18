package org.acepricot.asn1.rfc2986;

import org.acepricot.asn1.ASN1NodeImpl;
import org.acepricot.asn1.SetOf;
import org.acepricot.ber.BERConst;

public class Attributes extends SetOf {
	
	private ASN1NodeImpl attirbutes = new Attribute("atribute", null);
	
	private ASN1NodeImpl[] attributesSeq = {
			 attirbutes
	};

	Attributes (String name) {
		super (BERConst.TRUE,BERConst.FALSE,name);
		this.setSeq(attributesSeq);
	};
}
