package org.pabk.basen.rfc.rfc5280;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;

import org.pabk.basen.asn1.ASN1Impl;
import org.pabk.basen.asn1.BERInteger;
import org.pabk.basen.asn1.BitString;
import org.pabk.basen.asn1.Sequence;
import org.pabk.basen.asn1.Tag;
import org.pabk.basen.ber.BERImpl;
import org.pabk.basen.rfc.AlgorithmIdentifier;
import org.pabk.basen.rfc.Name;
import org.pabk.basen.rfc.Time;

public class TBSCertificate extends Sequence {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String VERSION_NAME = "version";
	private static final int VERSION_CLASS = BERImpl.CONTEXT_SPECIFIC_CLASS;
	private static final int VERSION_PC = BERImpl.CONSTRUCTED_ENCODING;
	private static final int VERSION_TAG = 0x00;
	private static final boolean VERSION_OPT = true;
	private static final boolean VERSION_IMP = false;
	private static final String SERIAL_NR_NAME = "serialNumber";
	private static final boolean SERIAL_NR_OPT = false;
	private static final String SIG_NAME = "signature";
	private static final boolean SIG_OPT = false;
	private static final String ISSUER_NAME = "issuer";
	private static final boolean ISSUER_OPT = false;
	private static final String VALID_NAME = "validity";
	private static final boolean VALID_OPT = false;
	private static final String NOT_BEROFE_NAME = "notBefore";
	private static final boolean NOT_BEFORE_OPT = false;
	private static final String NOT_AFTER_NAME = "notAfter";
	private static final boolean NOT_AFTER_OPT = false;
	private final ASN1Impl[] VALID_SEQ = {
		new Time(NOT_BEROFE_NAME, NOT_BEFORE_OPT),
		new Time(NOT_AFTER_NAME, NOT_AFTER_OPT)
	};
	private static final String SUBJ_NAME = "subject";
	private static final boolean SUBJ_OPT = false;
	private static final String SUBJ_KEY_INFO_NAME = "subjectPublicKeyInfo";
	private static final boolean SUBJ_KEY_INFO_OPT = false;
	private static final String ALG_NAME = "algorithm";
	private static final boolean ALG_OPT = false;
	private static final String SUBJ_PUB_KEY_NAME = "subjectPublicKey";
	private static final int SUBJ_PUB_KEY_PC = BERImpl.PRIMITIVE_ENCODING;
	private static final boolean SUBJ_PUB_KEY_OPT = false;
	private final ASN1Impl[] SUBJ_KEY_INFO_SEQ = {
		new AlgorithmIdentifier(ALG_NAME, ALG_OPT),
		new BitString (SUBJ_PUB_KEY_NAME, SUBJ_PUB_KEY_PC, SUBJ_PUB_KEY_OPT)
	};
	private static final String ISSUER_UID_NAME = "issuerUniqueID";
	private static final int ISSUER_UID_CLASS = BERImpl.CONTEXT_SPECIFIC_CLASS;
	private static final int ISSUER_UID_PC = BERImpl.PRIMITIVE_ENCODING;
	private static final int ISSUER_UID_TAG = 0x01;
	private static final boolean ISSUER_UID_OPT = true;
	private static final boolean ISSUER_UID_IMP = false;
	private static final String UID_NAME = "uid";
	private static final int UID_PC = BERImpl.PRIMITIVE_ENCODING;
	private static final boolean UID_OPT = false;
	private final BitString ISSUER_UID_OBJECT = new BitString(UID_NAME, UID_PC, UID_OPT);
	private static final String SUBJ_UID_NAME = "subjectUniqueID";
	private static final int SUBJ_UID_CLASS = BERImpl.CONTEXT_SPECIFIC_CLASS;
	private static final int SUBJ_UID_PC = BERImpl.PRIMITIVE_ENCODING;
	private static final int SUBJ_UID_TAG = 0x02;
	private static final boolean SUBJ_UID_OPT = true;
	private static final boolean SUBJ_UID_IMP = false;
	private final BitString SUBJ_UID_OBJECT = new BitString(UID_NAME, UID_PC, UID_OPT);
	private static final String EXTS_NAME = "extensions";
	private static final int EXTS_CLASS = BERImpl.CONTEXT_SPECIFIC_CLASS;
	private static final int EXTS_PC = BERImpl.CONSTRUCTED_ENCODING;
	private static final int EXTS_TAG = 0x03;
	private static final boolean EXTS_OPT = true;
	private static final boolean EXTS_IMP = false;
	
	private final Extensions EXTS_OBJECT = new Extensions(EXTS_NAME, EXTS_OPT);
	
	private final ASN1Impl VERSION_OBJECT = new BERInteger(VERSION_NAME, VERSION_OPT);
		
	private final ASN1Impl[] TBS_CERT_SEQ = {
		new Tag(VERSION_NAME, VERSION_CLASS, VERSION_PC, VERSION_TAG, VERSION_IMP, VERSION_OPT, VERSION_OBJECT),
		new BERInteger(SERIAL_NR_NAME, SERIAL_NR_OPT),
		new AlgorithmIdentifier(SIG_NAME, SIG_OPT),
		new Name(ISSUER_NAME, ISSUER_OPT),
		new Sequence(VALID_NAME, VALID_OPT, VALID_SEQ),
		new Name(SUBJ_NAME, SUBJ_OPT),
		new Sequence(SUBJ_KEY_INFO_NAME, SUBJ_KEY_INFO_OPT, SUBJ_KEY_INFO_SEQ),
		new Tag(ISSUER_UID_NAME, ISSUER_UID_CLASS, ISSUER_UID_PC, ISSUER_UID_TAG, ISSUER_UID_IMP, ISSUER_UID_OPT, ISSUER_UID_OBJECT),
		new Tag(SUBJ_UID_NAME, SUBJ_UID_CLASS, SUBJ_UID_PC, SUBJ_UID_TAG, SUBJ_UID_IMP, SUBJ_UID_OPT, SUBJ_UID_OBJECT),
		new Tag(EXTS_NAME, EXTS_CLASS, EXTS_PC, EXTS_TAG, EXTS_IMP, EXTS_OPT, EXTS_OBJECT)
	};
	
	public TBSCertificate(String name, boolean optional) {
		super(name, optional);
		this.setSequences(TBS_CERT_SEQ);
		this.ISSUER_UID_OBJECT.setImplicit(true);
		this.SUBJ_UID_OBJECT.setImplicit(true);
	}
	
	private static int DEFAULT_CERTIFICATE_VERSION = 0x02;
	
	public static byte[] joinByteArrays(byte[] a, byte[] b) {
		byte[] c = new byte[a.length + b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		a= null;
		b = null;
		return c;
	}
	
	public static BERImpl createTBSCertificate (KeyPair pair, int serialNo, String signature, String[] issuer, long notBefore, long notAfter, String[] subject) throws IOException {
		BERImpl tag = new BERImpl(0, BERImpl.CONSTRUCTED_ENCODING, BERImpl.CONTEXT_SPECIFIC_CLASS);
		BERImpl sno = new BERImpl(BERImpl.INTEGER_TAG, BERImpl.PRIMITIVE_ENCODING);
		BERImpl sig = AlgorithmIdentifier.createAlgorithmId(signature);
		BERImpl ina = Name.createName((Object) issuer);
		BERImpl val = new BERImpl (BERImpl.SEQUENCE_TAG, BERImpl.CONSTRUCTED_ENCODING);
		BERImpl sub = Name.createName((Object) subject);
		BERImpl spi = new BERImpl(BERImpl.SEQUENCE_TAG, BERImpl.CONSTRUCTED_ENCODING);
		BERImpl tbs = new BERImpl (BERImpl.SEQUENCE_TAG, BERImpl.CONSTRUCTED_ENCODING);
		BERImpl ver = new BERImpl(BERImpl.INTEGER_TAG, BERImpl.PRIMITIVE_ENCODING);
		ver.getEncoder().setValue(ver, new byte[]{(byte) DEFAULT_CERTIFICATE_VERSION});
		tag.getEncoder().setValue(tag, new BERImpl[]{ver});
		sno.getEncoder().setValue(sno, new BigInteger(Integer.toString(serialNo)).toByteArray());
		val.getEncoder().setValue(val, new BERImpl[]{Time.createUTCTime(notBefore), Time.createUTCTime(notAfter)});
		//BERImpl alg = AlgorithmIdentifier.createAlgorithmId(pair.getPublic().getAlgorithm());
		BERImpl spk = new BERImpl(BERImpl.BITSTRING_TAG, BERImpl.PRIMITIVE_ENCODING);
		System.out.println(new String(pair.getPublic().getEncoded()));
		spk.getEncoder().setValue(spk, joinByteArrays (new byte[]{0}, pair.getPublic().getEncoded()));
		spi.decode(new ByteArrayInputStream(pair.getPublic().getEncoded()));
		tbs.getEncoder().setValue(tbs, new BERImpl[]{tag, sno, sig, ina, val, sub, spi});
		return tbs;
	}
	
}
