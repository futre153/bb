package org.pabk.basen.rfc.rfc2315;

import org.pabk.basen.asn1.ASN1Impl;
import org.pabk.basen.asn1.BERInteger;
import org.pabk.basen.asn1.OctetString;
import org.pabk.basen.asn1.Sequence;
import org.pabk.basen.asn1.Tag;
import org.pabk.basen.ber.BERImpl;
import org.pabk.basen.rfc.AlgorithmIdentifier;
import org.pabk.basen.rfc.Attributes;

public class SignerInfo extends Sequence {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String VERSION_NAME = "version";
	private static final boolean VERSION_OPT = false;
	private static final String ISS_SN_NAME = "issuerAndSerialNumber";
	private static final boolean ISS_SN_OPT = false;
	private static final String DIG_ALG_NAME = "digestAlgorithm";
	private static final boolean DIG_ALG_OPT = false;
	private static final String AUTH_ATTS_NAME = "authenticateAttributes";
	private static final int AUTH_ATTS_CLASS = BERImpl.CONTEXT_SPECIFIC_CLASS;
	private static final int AUTH_ATTS_PC = BERImpl.CONSTRUCTED_ENCODING;
	private static final int AUTH_ATTS_TAG = 0x00;
	private static final boolean AUTH_ATTS_OPT = true;
	private static final boolean AUTH_ATTS_IMP = false;
	private static final boolean AUTH_ATTS_OBJECT_OPT = false;
	private static final String DIG_ENC_ALG_NAME = "digestEncryptionAlgorithm";
	private static final boolean DIG_ENC_ALG_OPT = false;
	private static final String ENC_DIGEST_NAME = "encryptedDigest";
	private static final int ENC_DIGEST_PC = BERImpl.PRIMITIVE_ENCODING;
	private static final boolean ENC_DIGEST_OPT = false;
	private static final String UNAUTH_ATTS_NAME = "unauthenticateAttributes";
	private static final int UNAUTH_ATTS_CLASS = BERImpl.CONTEXT_SPECIFIC_CLASS;
	private static final int UNAUTH_ATTS_PC = BERImpl.CONSTRUCTED_ENCODING;
	private static final int UNAUTH_ATTS_TAG = 0x01;
	private static final boolean UNAUTH_ATTS_OPT = true;
	private static final boolean UNAUTH_ATTS_IMP = false;
	private static final boolean UNAUTH_ATTS_OBJECT_OPT = false;
	private static final boolean UNAUTH_ATTS_OBJECT_IMP = true;
	private static final boolean AUTH_ATTS_OBJECT_IMP = true;
	
	private final ASN1Impl UNAUTH_ATTS_OBJECT = new Attributes(UNAUTH_ATTS_NAME, UNAUTH_ATTS_OBJECT_OPT);;
	private final Attributes AUTH_ATTS_OBJECT = new Attributes(AUTH_ATTS_NAME, AUTH_ATTS_OBJECT_OPT);
	
	private final ASN1Impl[] SIGNER_INFO_SEQ = {
		new BERInteger(VERSION_NAME, VERSION_OPT),
		new IssuerAndSerialNumber(ISS_SN_NAME, ISS_SN_OPT),
		new AlgorithmIdentifier (DIG_ALG_NAME, DIG_ALG_OPT),
		new Tag(AUTH_ATTS_NAME, AUTH_ATTS_CLASS, AUTH_ATTS_PC, AUTH_ATTS_TAG, AUTH_ATTS_OPT, AUTH_ATTS_IMP, AUTH_ATTS_OBJECT),
		new AlgorithmIdentifier(DIG_ENC_ALG_NAME, DIG_ENC_ALG_OPT),
		new OctetString(ENC_DIGEST_NAME, ENC_DIGEST_PC, ENC_DIGEST_OPT),
		new Tag(UNAUTH_ATTS_NAME, UNAUTH_ATTS_CLASS, UNAUTH_ATTS_PC, UNAUTH_ATTS_TAG, UNAUTH_ATTS_OPT, UNAUTH_ATTS_IMP, UNAUTH_ATTS_OBJECT),
	};

	public SignerInfo(String name, boolean optional) {
		super(name, optional);
		this.setSequences(SIGNER_INFO_SEQ);
		this.AUTH_ATTS_OBJECT.setImplicit(AUTH_ATTS_OBJECT_IMP);
		this.UNAUTH_ATTS_OBJECT.setImplicit(UNAUTH_ATTS_OBJECT_IMP);
	}
	private SignerInfo() {}
	@Override
	public SignerInfo clone() {
		return (SignerInfo) ASN1Impl.clone(new SignerInfo(), this);
	}
}
