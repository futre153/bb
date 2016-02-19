package org.pabk.basen.rfc.rfc2315;

import org.pabk.basen.asn1.ASN1Impl;
import org.pabk.basen.asn1.BERInteger;
import org.pabk.basen.asn1.OctetString;
import org.pabk.basen.asn1.Sequence;
import org.pabk.basen.ber.BERImpl;
import org.pabk.basen.rfc.AlgorithmIdentifier;

public class RecipientInfo extends Sequence {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String VER_NAME = "version";
	private static final boolean VER_OPT = false;
	private static final String ISS_SN_NAME = "issuerAndSerialNumber";
	private static final boolean ISS_SN_OPT = false;
	private static final String KEY_ALG_NAME = "keyEncryptionAlgorithm";
	private static final boolean KEY_ALG_VALUE = false;
	private static final String ENC_KEY_NAME = "encryptedKey";
	private static final int ENC_KEY_PC = BERImpl.PRIMITIVE_ENCODING;
	private static final boolean ENC_KEY_OPT = false;
	
	private final ASN1Impl[] RECIPIENT_INFO_SEQ = {
		new BERInteger (VER_NAME, VER_OPT),
		new IssuerAndSerialNumber (ISS_SN_NAME, ISS_SN_OPT),
		new AlgorithmIdentifier (KEY_ALG_NAME, KEY_ALG_VALUE),
		new OctetString (ENC_KEY_NAME, ENC_KEY_PC, ENC_KEY_OPT)
	};

	public RecipientInfo(String name, boolean optional) {
		super(name, optional);
		this.setSequences(RECIPIENT_INFO_SEQ);
	}
	private RecipientInfo() {}
	@Override
	public RecipientInfo clone() {
		return (RecipientInfo) ASN1Impl.clone(new RecipientInfo(), this);
	}
}
