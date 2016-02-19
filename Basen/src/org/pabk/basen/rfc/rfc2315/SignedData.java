package org.pabk.basen.rfc.rfc2315;

import org.pabk.basen.asn1.ASN1Impl;
import org.pabk.basen.asn1.BERInteger;
import org.pabk.basen.asn1.Sequence;
import org.pabk.basen.asn1.Set;
import org.pabk.basen.asn1.Tag;
import org.pabk.basen.ber.BERImpl;
import org.pabk.basen.rfc.AlgorithmIdentifier;
import org.pabk.basen.rfc.rfc5280.CRLs;
import org.pabk.basen.rfc.rfc5280.Certificate;

public class SignedData extends Sequence {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String VER_NAME = "version";
	private static final boolean VER_OPT = false;
	private static final String DIG_INFO_NAME = "digestAlgorithm";
	private static final boolean DIG_INFO_OPT = false;
	private static final String DIG_ALGS_NAME = "digestAlgorithms";
	private static final boolean DIG_ALGS_OPT = false;
	private static final String CERTS_NAME = "certificates";
	private static final int CERTS_CLASS = BERImpl.CONTEXT_SPECIFIC_CLASS;
	private static final int CERTS_PC = BERImpl.CONSTRUCTED_ENCODING;
	private static final int CERTS_TAG = 0x00;
	private static final boolean CERTS_OPT = true;
	private static final boolean CERTS_IMPL = false;
	private static final String CERT_NAME = "certificate";
	private static final boolean CERT_OPT = false;
	private static final boolean CERTS_ASN_IMP = true;
	private static final String FULL_CERT_NAME = "fullCert";
	private static final boolean FULL_CERT_OPT = true;
	private static final String CRLS_NAME = "crls";
	private static final int CRLS_CLASS = BERImpl.CONTEXT_SPECIFIC_CLASS;
	private static final int CRLS_PC = BERImpl.CONSTRUCTED_ENCODING;
	private static final int CRLS_TAG = 0x01;
	private static final boolean CRLS_OPT = true;
	private static final boolean CRLS_IMPL = false;
	private static final boolean CRLS_ASN_IMP = true;
	private static final String CRL_NAME = "crl";
	private static final boolean CRL_OPT = false;
	private static final String SIMPLE_CRL_NAME = "simpleCrl";
	private static final boolean SIMPLE_CRL_OPT = true;
	private static final String CONTENT_INFO_NAME = "contentInfo";
	private static final boolean CONTENT_INFO_OPT = false;
	private static final String SIGNER_INFOS_NAME = "signerInfos";
	private static final boolean SIGNER_INFOS_OPT = false;
	private static final boolean SIGNER_INFO_OBJECT_OPT = true;
	
	private final ASN1Impl[] SIGNER_INFOS_SEQ = {
		new SignerInfo (SIGNER_INFOS_NAME, SIGNER_INFO_OBJECT_OPT)
	};
	
	private final ASN1Impl[] CRL_SEQ = {
		new CRLs (SIMPLE_CRL_NAME, SIMPLE_CRL_OPT)
	};
	
	private final Set CRLS_ASN = new Set(CRL_NAME, CRL_OPT, CRL_SEQ);
		
	private final ASN1Impl[] CERT_SEQ = {
		new Certificate (FULL_CERT_NAME, FULL_CERT_OPT)
	};
	
	private final Set CERTS_ASN = new Set (CERT_NAME, CERT_OPT, CERT_SEQ);
		
	private final ASN1Impl[] DIG_ALGS_SEQ = {
		new AlgorithmIdentifier(DIG_INFO_NAME, DIG_INFO_OPT)
	};
	
	private final ASN1Impl[] SIGNED_DATA_SEQ = {
		new BERInteger (VER_NAME, VER_OPT),
		new Set(DIG_ALGS_NAME, DIG_ALGS_OPT, DIG_ALGS_SEQ),
		new ContentInfo(CONTENT_INFO_NAME, CONTENT_INFO_OPT),
		new Tag(CERTS_NAME, CERTS_CLASS, CERTS_PC, CERTS_TAG, CERTS_OPT, CERTS_IMPL, CERTS_ASN),
		new Tag(CRLS_NAME, CRLS_CLASS, CRLS_PC, CRLS_TAG, CRLS_OPT, CRLS_IMPL, CRLS_ASN),
		new Set(SIGNER_INFOS_NAME, SIGNER_INFOS_OPT, SIGNER_INFOS_SEQ)
	};

	public SignedData(String name, boolean optional) {
		super(name, optional);
		this.setSequences(SIGNED_DATA_SEQ);
		this.CERTS_ASN.setImplicit(CERTS_ASN_IMP);
		this.CRLS_ASN.setImplicit(CRLS_ASN_IMP);
	}
	private SignedData() {}
	@Override
	public SignedData clone() {
		return (SignedData) ASN1Impl.clone(new SignedData(), this);
	}
}
