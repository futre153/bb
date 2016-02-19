package org.pabk.basen.rfc.rfc2986;

import org.pabk.basen.asn1.ASN1Impl;
import org.pabk.basen.asn1.BitString;
import org.pabk.basen.asn1.Sequence;
import org.pabk.basen.ber.BERImpl;
import org.pabk.basen.rfc.AlgorithmIdentifier;

public class SubjectPKInfo extends Sequence {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String ALG_NAME = "algorithm";
	private static final boolean ALG_OPT = false;
	private static final String SUB_KP_NAME = "subjectPublicKey";
	private static final int SUB_PK_PC = BERImpl.PRIMITIVE_ENCODING;
	private static final boolean SUB_PK_OPT = false;
	
	private final ASN1Impl[] SUB_PK_INFO_SEQ = {
		new AlgorithmIdentifier(ALG_NAME, ALG_OPT),
		new BitString(SUB_KP_NAME, SUB_PK_PC, SUB_PK_OPT)
	};
	
	public SubjectPKInfo(String name, boolean opt) {
		super(name, opt);
		this.setSequences(SUB_PK_INFO_SEQ);
	}
}
