package org.acepricot.asn1.rfc2986;

import org.acepricot.asn1.ASN1NodeImpl;
import org.acepricot.asn1.Sequence;
import org.acepricot.ber.BERConst;

class SubjectPublicKeyInfo extends Sequence {
	private AlgorithmIdentifier algorithm = new AlgorithmIdentifier("algorithm", null);
	private SubjectPublicKey subjectPublicKey = new SubjectPublicKey("subjectPublicKey");
	
	private ASN1NodeImpl[] subjectPublicKeyInfoSeq = {
			algorithm,
			subjectPublicKey 
	};

	SubjectPublicKeyInfo (String name) {
		super(BERConst.TRUE,BERConst.FALSE,	name);
		this.setSeq(subjectPublicKeyInfoSeq );
	}
}
