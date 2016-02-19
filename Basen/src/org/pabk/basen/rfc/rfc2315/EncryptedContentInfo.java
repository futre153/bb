package org.pabk.basen.rfc.rfc2315;

import org.pabk.basen.asn1.ASN1Impl;
import org.pabk.basen.asn1.ObjectIdentifier;
import org.pabk.basen.asn1.OctetString;
import org.pabk.basen.asn1.Sequence;
import org.pabk.basen.asn1.Tag;
import org.pabk.basen.ber.BERImpl;
import org.pabk.basen.rfc.AlgorithmIdentifier;

public class EncryptedContentInfo extends Sequence {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String CONTENT_TYPE_NAME = "contentType";
	private static final boolean CONTENT_TYPE_OPT = false;
	private static final String CONTENT_ENC_ALG_NAME = "contentEncryptionAlgorithm";
	private static final boolean CONTENT_ENC_ALG_OPT = false;
	private static final String ENC_CONTENT_NAME = "encryptedContent";
	private static final int ENC_CONTENT_CLASS = BERImpl.CONTEXT_SPECIFIC_CLASS;
	private static final int ENC_CONTENT_PC = BERImpl.PRIMITIVE_ENCODING;
	private static final int ENC_CONTENT_TAG = 0x00;
	private static final boolean ENC_CONTENT_OPT = true;
	private static final boolean ENC_CONTENT_IMPL = false;
	private static final boolean ENC_CONTENT_OBJECT_IMPL = true;
	
	private final OctetString ENC_CONTENT_OBJECT = new OctetString(ENC_CONTENT_NAME, ENC_CONTENT_PC, ENC_CONTENT_OPT);
	
	private final ASN1Impl[] ENCRYPTED_CONTENT_SEQ = {
		new ObjectIdentifier (CONTENT_TYPE_NAME, CONTENT_TYPE_OPT),
		new AlgorithmIdentifier (CONTENT_ENC_ALG_NAME, CONTENT_ENC_ALG_OPT),
		new Tag(ENC_CONTENT_NAME, ENC_CONTENT_CLASS, ENC_CONTENT_PC, ENC_CONTENT_TAG, ENC_CONTENT_OPT, ENC_CONTENT_IMPL, ENC_CONTENT_OBJECT)
	};

	public EncryptedContentInfo(String name, boolean optional) {
		super(name, optional);
		this.setSequences(ENCRYPTED_CONTENT_SEQ);
		this.ENC_CONTENT_OBJECT.setImplicit(ENC_CONTENT_OBJECT_IMPL);
	}
	private EncryptedContentInfo() {}
	@Override
	public EncryptedContentInfo clone() {
		return (EncryptedContentInfo) ASN1Impl.clone(new EncryptedContentInfo(), this);
	}
}
