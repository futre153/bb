package org.acepricot.asn1.rfc2986;

import org.acepricot.asn1.ASN1NodeImpl;
import org.acepricot.asn1.Sequence;
import org.acepricot.ber.BER;
import org.acepricot.ber.BERConst;

public class CertificateRequest extends Sequence {
	
	private CertificateRequestInfo certificateRequestInfo = new CertificateRequestInfo ("certificateRequestInfo");
	private AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier("algorithmIdentifier", null);
	private Signature signature = new Signature("signature");
	
	private ASN1NodeImpl[] certificateRequestSeq = {
			certificateRequestInfo,
			algorithmIdentifier,
			signature
	};
	
	public CertificateRequest(String name) {
		super(BERConst.TRUE,BERConst.FALSE,name);
		this.setSeq(certificateRequestSeq);
	}
	
	protected CertificateRequestInfo getCertificateRequestInfo() {
		return certificateRequestInfo;
	}

	protected void setCertificateRequestInfo(
		CertificateRequestInfo certificateRequestInfo) {
		this.certificateRequestInfo = certificateRequestInfo;
	}

	protected AlgorithmIdentifier getAlgorithmIdentifier() {
		return algorithmIdentifier;
	}

	protected void setAlgorithmIdentifier(AlgorithmIdentifier algorithmIdentifier) {
		this.algorithmIdentifier = algorithmIdentifier;
	}

	protected Signature getSignature() {
		return signature;
	}

	protected void setSignature(Signature signature) {
		this.signature = signature;
	}

	public BER getBer() {
		return ber;
	}

	public void setBer(BER ber) {
		this.ber = ber;
	}
	
}
