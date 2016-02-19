package org.pabk.basen.rfc.rfc2315;

import org.pabk.basen.asn1.ASN1Impl;
import org.pabk.basen.asn1.BERInteger;
import org.pabk.basen.asn1.Sequence;
import org.pabk.basen.asn1.Set;

public class EnvelopedData extends Sequence {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String VER_NAME = "version";
	private static final boolean VER_OPT = false;
	private static final String REC_INFOS_NAME = "recipientInfos";
	private static final boolean REC_INFOS_OPT = false;
	private static final String REC_INFO_NAME = "recipientInfo";
	private static final boolean REC_INFO_OPT = true;
	private static final String ENC_CONTENT_NAME = "encryptedContentInfo";
	private static final boolean ENC_CONTENT_OPT = false;
	
	private final ASN1Impl[] REC_INFOS_SEQ = {
		new RecipientInfo(REC_INFO_NAME, REC_INFO_OPT)
	};
	
	private final ASN1Impl[] ENVELOPED_DATA_SEQ = {
		new BERInteger (VER_NAME, VER_OPT),
		new Set(REC_INFOS_NAME, REC_INFOS_OPT, REC_INFOS_SEQ),
		new EncryptedContentInfo (ENC_CONTENT_NAME, ENC_CONTENT_OPT)
	};

	public EnvelopedData(String name, boolean optional) {
		super(name, optional);
		this.setSequences(ENVELOPED_DATA_SEQ);
	}
	
	private EnvelopedData() {}
	@Override
	public EnvelopedData clone() {
		return (EnvelopedData) ASN1Impl.clone(new EnvelopedData(), this);
	}
}
