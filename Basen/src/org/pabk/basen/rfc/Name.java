package org.pabk.basen.rfc;

import java.io.IOException;
import java.util.Hashtable;

import org.pabk.basen.asn1.ASN1Impl;
import org.pabk.basen.asn1.BmpString;
import org.pabk.basen.asn1.Choice;
import org.pabk.basen.asn1.IA5String;
import org.pabk.basen.asn1.ObjectIdentifier;
import org.pabk.basen.asn1.PrintebleString;
import org.pabk.basen.asn1.Sequence;
import org.pabk.basen.asn1.Set;
import org.pabk.basen.asn1.TeletexString;
import org.pabk.basen.asn1.UTF8String;
import org.pabk.basen.asn1.UniversalString;
import org.pabk.basen.ber.BERImpl;

public class Name extends Choice {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String RDN_SEQ_NAME = "rdnSequence";
	private static final boolean RDN_SEQ_OPT = false;
	private static final String RDN_NAME = "rdn";
	private static final boolean RDN_OPT = false;
	private static final String RDN_SET_NAME = "rdnSet";
	private static final boolean RDN_SET_OPT = true;
	private static final String TYPE_NAME = "type";
	private static final boolean TYPE_OPT = false;
	private static final String VALUE_NAME = "value";
	private static final boolean VALUE_OPT = false;
	private static final String TELEX_NAME = "teletexString";
	private static final boolean TELEX_OPT = false;
	private static final String PRINT_NAME = "printableString";
	private static final boolean PRINT_OPT = false;
	private static final String UNI_NAME = "universalString";
	private static final boolean UNI_OPT = false;
	private static final String UTF_NAME = "utf8String";
	private static final boolean UTF_OPT = false;
	private static final String BMP_NAME = "bmpString";
	private static final boolean BMP_OPT = false;
	private static final String IA5_NAME = "ia5String";
	private static final boolean IA5_OPT = false;
	private static final int TELEX_PC = BERImpl.PRIMITIVE_ENCODING;
	private static final int PRINT_PC = BERImpl.PRIMITIVE_ENCODING;
	private static final int UNI_PC = BERImpl.PRIMITIVE_ENCODING;
	private static final int UTF_PC = BERImpl.PRIMITIVE_ENCODING;
	private static final int BMP_PC = BERImpl.PRIMITIVE_ENCODING;
	private static final int IA5_PC = BERImpl.PRIMITIVE_ENCODING;
	private static final String CN = "cn";
	private static final String DC = "dc";
	private static final String OU = "ou";
	private static final String O = "o";
	private static final String STREET = "street";
	private static final String L = "l";
	private static final String ST = "st";
	private static final String C = "c";
	private static final String UID = "uid";
	private static final String SERIALNUMBER = "serialnumber";
	private static final String SERIALNUMBER_OID = "2.5.4.5";
	private static final String CN_OID = "2.5.4.3";
	private static final String DC_OID = "0.9.2342.19200300.100.1.2.25";
	private static final String OU_OID = "2.5.4.11";
	private static final String O_OID = "2.5.4.10";
	private static final String STREET_OID = "2.5.4.9";
	private static final String L_OID = "2.5.4.7";
	private static final String ST_OID = "2.5.4.8";
	private static final String C_OID = "2.5.4.6";
	private static final String UID_OID = "0.9.2342.19200300.100.1.1";
	private static final int DEFAULT_STRIG_TAG = BERImpl.UTF8_STRING_TAG;
	private static final String DEFAULT_STRING_ENCODEING = "UTF-8";
	private static final String CREATE_NAME_FAILED = "Insuficient of source datas while creating Name";
	private static final String TITLE = "title";
	private static final String TITLE_OID = "2.5.4.12";
	private static final String MAIL = "mail";
	private static final String MAIL_OID = "0.9.2342.19200300.100.1.3";
	private static final String E = "E";
	private static final String E_OID = "1.2.840.113549.1.9.1";
	private static final String UNSTRUCTUREDNAME = "unstructuredname";
	private static final String UNSTRUCTUREDNAME_OID = "1.2.840.113549.1.9.2";
	private static final String UNSTRUCTUREDADDRESS = "unstructuredaddress";
	private static final String UNSTRUCTUREDADDRESS_OID = "1.2.840.113549.1.9.8";
	
	private static Hashtable<String, String> dn = null;  
	private static Hashtable<String, Integer> tg = null;
	
	private final ASN1Impl[] VALUE_SEQ = {
		new TeletexString(TELEX_NAME, TELEX_PC, TELEX_OPT),
		new PrintebleString(PRINT_NAME, PRINT_PC, PRINT_OPT),
		new UniversalString(UNI_NAME, UNI_PC, UNI_OPT),
		new UTF8String(UTF_NAME, UTF_PC, UTF_OPT),
		new BmpString(BMP_NAME, BMP_PC, BMP_OPT),
		new IA5String(IA5_NAME, IA5_PC, IA5_OPT)
	};
	private final ASN1Impl[] ATT_SEQ = {
		new ObjectIdentifier(TYPE_NAME, TYPE_OPT),
		new Choice(VALUE_NAME, VALUE_OPT, VALUE_SEQ)
	};
	
	private final ASN1Impl[] RDN_SET_SEQ = {
		new Sequence(RDN_NAME, RDN_OPT, ATT_SEQ)
	};
	
	private final ASN1Impl[] RDN_SEQ = {
		new Set(RDN_SET_NAME, RDN_SET_OPT, RDN_SET_SEQ)
	};
	private final ASN1Impl[] NAME_SEQ = {
		new Sequence(RDN_SEQ_NAME, RDN_SEQ_OPT, RDN_SEQ)
	};
	public Name(String name, boolean optional) {
		super(name, optional);
		this.setSequences(NAME_SEQ);
	}
	
	public static BERImpl createName(Object dn) throws IOException {
		initDN();
		if(dn != null) {
			String[] objs = (String[]) dn;
			if(objs != null && (objs.length % 2 == 0)) {
				BERImpl name = new BERImpl(BERImpl.SEQUENCE_TAG, BERImpl.CONSTRUCTED_ENCODING);
				BERImpl[] sets = new BERImpl[objs.length / 2];
				for (int i = 0; i < objs.length; i += 2) {
					sets[i / 2] = new BERImpl(BERImpl.SET_TAG, BERImpl.CONSTRUCTED_ENCODING);
					BERImpl seq = new BERImpl(BERImpl.SEQUENCE_TAG, BERImpl.CONSTRUCTED_ENCODING);
					BERImpl oid = new BERImpl(BERImpl.OBJECT_IDENTIFIER_TAG, BERImpl.PRIMITIVE_ENCODING);
					oid.getEncoder().setValue(oid, Name.dn.get(objs[i].toString().toLowerCase()));
					BERImpl str = new BERImpl(tg.get(objs[i].toString().toLowerCase()), BERImpl.PRIMITIVE_ENCODING);
					str.getEncoder().setValue(str, objs[i + 1].toString().getBytes(DEFAULT_STRING_ENCODEING));
					seq.getEncoder().setValue(seq, new BERImpl[]{oid, str});
					sets[i / 2].getEncoder().setValue(sets[i / 2], new BERImpl[]{seq});
				}
				name.getEncoder().setValue(name, sets);
				return name;
			}
		}
		throw new IOException (CREATE_NAME_FAILED);
	}
	
	private static void initDN() {
		if(dn == null) {
			dn = new Hashtable<String, String>();
			dn.put(CN, CN_OID);
			dn.put(OU, OU_OID);
			dn.put(O, O_OID);
			dn.put(C, C_OID);
			dn.put(L, L_OID);
			dn.put(ST, ST_OID);
			dn.put(STREET, STREET_OID);
			dn.put(TITLE, TITLE_OID);
			dn.put(UID, UID_OID);
			dn.put(MAIL, MAIL_OID);
			dn.put(E, E_OID);
			dn.put(DC, DC_OID);
			dn.put(SERIALNUMBER, SERIALNUMBER_OID);
			dn.put(UNSTRUCTUREDNAME, UNSTRUCTUREDNAME_OID);
			dn.put(UNSTRUCTUREDADDRESS , UNSTRUCTUREDADDRESS_OID);
		}
		if(tg == null) {
			tg = new Hashtable<String, Integer>();
			tg.put(CN, DEFAULT_STRIG_TAG);
			tg.put(OU, DEFAULT_STRIG_TAG);
			tg.put(O, DEFAULT_STRIG_TAG);
			tg.put(C, BERImpl.PRINTABLE_STRING_TAG);
			tg.put(L, DEFAULT_STRIG_TAG);
			tg.put(ST, DEFAULT_STRIG_TAG);
			tg.put(STREET, DEFAULT_STRIG_TAG);
			tg.put(TITLE, DEFAULT_STRIG_TAG);
			tg.put(UID, DEFAULT_STRIG_TAG);
			tg.put(MAIL, BERImpl.IA5_STRING_TAG);
			tg.put(E, BERImpl.IA5_STRING_TAG);
			tg.put(DC, BERImpl.IA5_STRING_TAG);
			tg.put(SERIALNUMBER, BERImpl.PRINTABLE_STRING_TAG);
			tg.put(UNSTRUCTUREDNAME, BERImpl.IA5_STRING_TAG);
			tg.put(UNSTRUCTUREDADDRESS , BERImpl.PRINTABLE_STRING_TAG);
		}
	}

	private Name() {}
	@Override
	public Name clone() {
		return (Name) ASN1Impl.clone(new Name(), this);
	}

}
