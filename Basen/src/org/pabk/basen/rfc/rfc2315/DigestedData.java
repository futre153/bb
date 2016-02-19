package org.pabk.basen.rfc.rfc2315;

import org.pabk.basen.asn1.ASN1Impl;
import org.pabk.basen.asn1.BERInteger;
import org.pabk.basen.asn1.OctetString;
import org.pabk.basen.asn1.Sequence;
import org.pabk.basen.ber.BERImpl;
import org.pabk.basen.rfc.AlgorithmIdentifier;

public class DigestedData extends Sequence {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String VER_NAME = "version";
	private static final boolean VER_OPT = false;
	private static final String DIG_ALG_NAME = "digestAlgorith";
	private static final boolean DIG_ALG_OPT = false;
	private static final String CONTENT_IOINFO_NAME = "contentInfo";
	private static final boolean CONTENT_INFO_OPT = false;
	private static final String DIG_NAME = "digest";
	private static final int DIG_PC = BERImpl.PRIMITIVE_ENCODING;
	private static final boolean DIG_OPT = false;
	
	private final ASN1Impl[] DIGEST_DATA_SEQ = {
		new BERInteger (VER_NAME, VER_OPT),
		new AlgorithmIdentifier(DIG_ALG_NAME, DIG_ALG_OPT),
		new ContentInfo(CONTENT_IOINFO_NAME, CONTENT_INFO_OPT),
		new OctetString(DIG_NAME, DIG_PC, DIG_OPT)
	};
	
	public DigestedData(String name, boolean optional) {
		super(name, optional);
		this.setSequences(DIGEST_DATA_SEQ);
	}
	private DigestedData() {}
	@Override
	public DigestedData clone() {
		return (DigestedData) ASN1Impl.clone(new DigestedData(), this);
	}
}
