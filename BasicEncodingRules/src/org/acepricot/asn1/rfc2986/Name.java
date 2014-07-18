package org.acepricot.asn1.rfc2986;

import org.acepricot.asn1.ASN1NodeImpl;
import org.acepricot.asn1.SequenceOf;
import org.acepricot.ber.BERConst;

class Name extends SequenceOf {
	
	private RelativeDistinguishedName relativeDistinguishedName = new RelativeDistinguishedName("rdnSequence");
	ASN1NodeImpl[] rdnSequence = {
			relativeDistinguishedName
	};

	Name(String name) {
		super(BERConst.TRUE, BERConst.FALSE, name);
		this.setSeq(rdnSequence);
	}

}
