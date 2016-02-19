package org.pabk.basen.rfc.rfc2315;

import org.pabk.basen.asn1.ASN1Impl;
import org.pabk.basen.asn1.BERInteger;
import org.pabk.basen.asn1.Sequence;

public class EncryptedData extends Sequence {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String VER_NAME = "version";
	private static final boolean VER_OPT = false;
	private static final String ENC_CONTENT_INFO_NAME = "encryptedContentInfo";
	private static final boolean ENC_CONTENT_INFO_OPT = false;
	
	private final ASN1Impl[] ENCRYPTED_DATA_SEQ = {
		new BERInteger (VER_NAME, VER_OPT),
		new EncryptedContentInfo(ENC_CONTENT_INFO_NAME, ENC_CONTENT_INFO_OPT)
	};

	public EncryptedData(String name, boolean optional) {
		super(name, optional);
		this.setSequences(ENCRYPTED_DATA_SEQ);
	}
	private EncryptedData() {}
	@Override
	public EncryptedData clone() {
		return (EncryptedData) ASN1Impl.clone(new EncryptedData(), this);
	}
}	
