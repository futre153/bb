package org.acepricot.asn1.rfc2986;

import org.acepricot.asn1.ASN1NodeImpl;
import org.acepricot.asn1.ContextSpecific;
import org.acepricot.asn1.Sequence;
import org.acepricot.ber.BERConst;

class CertificateRequestInfo extends Sequence {
	
	private Version version = new Version("version");
	private Name subject = new Name("subject");
	private SubjectPublicKeyInfo subjectPKInfo = new SubjectPublicKeyInfo("subjectPKInfo");
	private ContextSpecific attributes = new ContextSpecific(0x00, BERConst.TRUE, BERConst.FALSE, "attributes", new Attributes("attributes"));
	
	private ASN1NodeImpl[] ertificateRequestInfoSeq = {
			version,
			subject,
			subjectPKInfo,
			attributes
	};
	
	CertificateRequestInfo(String name) {
		super(BERConst.TRUE,BERConst.FALSE,name);
		this.setSeq(ertificateRequestInfoSeq);
	}
	
	
	
}
