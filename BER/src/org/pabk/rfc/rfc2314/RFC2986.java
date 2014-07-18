package org.pabk.rfc.rfc2314;

import org.pabk.ber.BERBitString;
import org.pabk.ber.BERChoice;
import org.pabk.ber.BERInteger;
import org.pabk.ber.BERSequence;
import org.pabk.ber.BERSetOf;
import org.pabk.ber.Tagged;
import org.pabk.rfc.rfc2315.RFC2315;
import org.pabk.rfc.rfc5280.RFC5280;

public class RFC2986 {
	
	private static final BERInteger VERSION = new BERInteger("version");

	private static final BERChoice SUBJECT = new BERChoice("subject", RFC5280.NAME_CHOICE, -1);

	private static final BERSequence ALGORITHM = new BERSequence("algorithm", RFC2315.ALGORITHM_IDENTIFIER_SEQUENCE);

	private static final BERBitString SUBJECT_PUBLIC_KEY = new BERBitString("subjectPublicKey");

	private static final Object[] SUBJECT_PUBLIC_KEY_INFO_SEQUENCE = {
		ALGORITHM,
		SUBJECT_PUBLIC_KEY
	};

	private static final BERSequence SUBJECT_PKI_INFO = new BERSequence("subjectPKIInfo", SUBJECT_PUBLIC_KEY_INFO_SEQUENCE);
	
	private static final Tagged ATTRIBUTES = new Tagged("attributes", new BERSetOf("attributes", RFC2315.ATTRIBUTES_SET), 0, true, false);
		
	private static final Object[] CERTIFICATION_REQUEST_INFO_SEQUENCE = {
		VERSION,
		SUBJECT,
		SUBJECT_PKI_INFO,
		ATTRIBUTES
	};

	private static final BERSequence CERTIFICATION_REQUEST_INFO = new BERSequence("certificationRequestInfo", CERTIFICATION_REQUEST_INFO_SEQUENCE);

	private static final BERSequence SIGNATURE_ALGORITHM = new BERSequence("signatureAlgorithm", RFC2315.ALGORITHM_IDENTIFIER_SEQUENCE);

	private static final BERBitString SIGNATURE = new BERBitString("signature");

	private static final Object[] CERTIFICATION_REQUEST_SEQUENCE = {
		CERTIFICATION_REQUEST_INFO,
		SIGNATURE_ALGORITHM,
		SIGNATURE
	};
	
	private static final BERSequence CERTIFICATION_REQUEST = new BERSequence("certificationRequest", CERTIFICATION_REQUEST_SEQUENCE);
	
	public static BERSequence getCertificateRequest(String name) {
		return (BERSequence) CERTIFICATION_REQUEST.copy(name);
	}
}
