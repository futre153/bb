package org.pabk.basen.rfc.rfc5280;

import org.pabk.basen.asn1.ASN1Impl;
import org.pabk.basen.asn1.BERInteger;
import org.pabk.basen.asn1.Sequence;
import org.pabk.basen.asn1.Tag;
import org.pabk.basen.ber.BERImpl;
import org.pabk.basen.rfc.AlgorithmIdentifier;
import org.pabk.basen.rfc.Name;
import org.pabk.basen.rfc.Time;

public class TBSCRLs extends Sequence {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String VERSION_NAME = "version";
	private static final boolean VERSION_OPT = true;
	private static final String SIG_NAME = "signature";
	private static final boolean SIG_OPT = false;
	private static final String ISSUER_NAME = "issuer";
	private static final boolean ISSUER_OPT = false;
	private static final String THIS_UPDATE_NAME = "thisUpdate";
	private static final boolean THIS_UPDATE_OPT = false;
	private static final String NEXT_UPDATE_NAME = "nextUpdate";
	private static final boolean NEXT_UPDATE_OPT = true;
	private static final String REV_CERTS_NAME = "revokedCertificates";
	private static final boolean REV_CERTS_OPT = true;
	private static final String REV_CERT_NAME = "revokedCertificate";
	private static final boolean REV_CERT_OPT = true;
	private static final String SERIAL_NR_NAME = "userCertificate";
	private static final boolean SERIAL_NR_OPT = false;
	private static final String REV_DATE_NAME = "revocationDate";
	private static final boolean REV_DATE_OPT = false;
	private static final String CRL_ENTRY_EXTS_NAME = "crlEntryExtensions";
	private static final boolean CRL_ENTRY_EXTS_OPT = true;
	private static final String CRL_EXTS_NAME = "crlExtensions";
	private static final int CRL_EXTS_CLASS = BERImpl.CONTEXT_SPECIFIC_CLASS;
	private static final int CRL_EXTS_PC = BERImpl.CONSTRUCTED_ENCODING;
	private static final int CRL_EXTS_TAG = 0x00;
	private static final boolean CRL_EXTS_OPT = true;
	private static final boolean CRL_EXTS_IMP = false;
	private static final String EXTS_NAME = CRL_EXTS_NAME;
	private static final boolean EXTS_OPT = false;
		
	private final ASN1Impl[] REV_CERT_SEQ = {
		new BERInteger(SERIAL_NR_NAME, SERIAL_NR_OPT),
		new Time(REV_DATE_NAME, REV_DATE_OPT),
		new Extensions(CRL_ENTRY_EXTS_NAME, CRL_ENTRY_EXTS_OPT)
	};
	
	private final ASN1Impl[] REV_CERTS_SEQ = {
		new Sequence(REV_CERT_NAME, REV_CERT_OPT, REV_CERT_SEQ)
	};
	
	private final Extensions CRL_EXTS_OBJECT = new Extensions(EXTS_NAME, EXTS_OPT);
		
	private final ASN1Impl[] TBS_CERT_SEQ = {
		new BERInteger(VERSION_NAME, VERSION_OPT),
		new AlgorithmIdentifier(SIG_NAME, SIG_OPT),
		new Name(ISSUER_NAME, ISSUER_OPT),
		new Time(THIS_UPDATE_NAME, THIS_UPDATE_OPT),
		new Time(NEXT_UPDATE_NAME, NEXT_UPDATE_OPT),
		new Sequence(REV_CERTS_NAME, REV_CERTS_OPT, REV_CERTS_SEQ),
		new Tag(CRL_EXTS_NAME, CRL_EXTS_CLASS, CRL_EXTS_PC, CRL_EXTS_TAG, CRL_EXTS_IMP, CRL_EXTS_OPT, CRL_EXTS_OBJECT)
	};

	public TBSCRLs(String name, boolean optional) {
		super(name, optional);
		this.setSequences(TBS_CERT_SEQ);
	}

}
