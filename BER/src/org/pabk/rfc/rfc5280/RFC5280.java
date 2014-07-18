package org.pabk.rfc.rfc5280;

import org.pabk.ber.BERBitString;
import org.pabk.ber.BERChoice;
import org.pabk.ber.BERInteger;
import org.pabk.ber.BEROctetString;
import org.pabk.ber.BEROid;
import org.pabk.ber.BERPrintableString;
import org.pabk.ber.BERSequence;
import org.pabk.ber.BERSequenceOf;
import org.pabk.ber.BERSetOf;
import org.pabk.ber.BERUTFString;
import org.pabk.ber.Optional;
import org.pabk.ber.Tagged;

public final class RFC5280 {
	
	public static final BEROid	DN_C = new BEROid("countryName", new int[]{2,5,4,6});
	public static final BEROid	DN_O = new BEROid("organizationName", new int[]{2,5,4,10});
	public static final BEROid	DN_OU = new BEROid("organizationUnitName", new int[]{2,5,4,11});
	public static final BEROid	DN_DNQ = new BEROid("distinguishedNameQualifier", new int[]{2,5,4,46});
	public static final BEROid	DN_ST = new BEROid("stateOrProvinceName", new int[]{2,5,4,8});
	public static final BEROid	DN_S = new BEROid("stateOrProvinceName", new int[]{2,5,4,8});
	public static final BEROid	DN_SN = new BEROid("surName", new int[]{2,5,4,4});
	public static final BEROid	DN_CN = new BEROid("commonName", new int[]{2,5,4,3});
	public static final BEROid	DN_SERIALNUMBER = new BEROid("serialNumber", new int[]{2,5,4,5});
	public static final BEROid	DN_L = new BEROid("localityName", new int[]{2,5,4,7});
	public static final BEROid	DN_T = new BEROid("title", new int[]{2,5,4,12});
	public static final BEROid	DN_TITLE = new BEROid("title", new int[]{2,5,4,12});
	public static final BEROid	DN_GN = new BEROid("givenName", new int[]{2,5,4,42});
	public static final BEROid	DN_G = new BEROid("givenName", new int[]{2,5,4,42});
	public static final BEROid	DN_E = new BEROid("e-mail", new int[]{1,2,840,113549,1,9,1});
	public static final BEROid	DN_INITIALS = new BEROid("initials", new int[]{2,5,4,43});
	public static final BEROid	DN_PSEUDOMYM = new BEROid("pseudonym", new int[]{2,5,4,65});
	public static final BEROid	DN_GENERATIONQUALIFIER = new BEROid("generationQualifier", new int[]{2,5,4,44});
	public static final BEROid	DN_MAIL = new BEROid("e-mail", new int[]{1,2,840,113549,1,9,1});
	public static final BEROid	DN_STREET = new BEROid("streetAddress", new int[]{2,5,4,9});
	public static final BEROid	DN_UID = new BEROid("streetAddress", new int[]{0,9,2342,19200300,100,1,1});
	public static final BEROid	DN_DC = new BEROid("streetAddress", new int[]{0,9,2342,19200300,100,1,25});
	
	public static final Object[] TIME_CHOICE = new Object[] {
		
	};
	
	public static final Object[] VALIDITY_SEQUENCE = new Object[] {
		new BERChoice("notBefore",TIME_CHOICE,-1),
		new BERChoice("notAfter",TIME_CHOICE,-1)
	};
	private static final Object[] ATTR_VALUE_CHOICE = {
		new BERUTFString("attrValue"),
		new BERPrintableString("attrValue"),
		new BEROctetString("attrValue")
	};
	
	public static final Object[] ATTRIBUTE_TYPE_AND_VALUE_SEQUENCE = new Object[] {
		new BEROid("attrType"),
		//new BERSetOf("attrValues", new Object[]{new BERAny("attrValue")})
		//new BERAny("attrValue")
		new BERChoice("attrValue", ATTR_VALUE_CHOICE, -1)
	};
	
	public static final Object[] RELATIVE_DISTINGUISHED_NAME_SET = new Object[] {
		new BERSequence(null, ATTRIBUTE_TYPE_AND_VALUE_SEQUENCE)
	};
	
	public static final Object[] RND_SEQUENCE_OF = new Object[] {
		new BERSetOf(null,RELATIVE_DISTINGUISHED_NAME_SET)
	};
	
	public static final Object[] NAME_CHOICE = new Object[] {
		new BERSequenceOf("rndSequence", RND_SEQUENCE_OF)
	};
	
	public static final Object[] ALGORITHM_IDENTIFIER_SEQUENCE = new Object[] {
		new BEROid("algorithm"),
		new Optional("parameters",new BEROctetString(null))
	};
	
	public static final Object[] TSB_CERTIFICATE_SEQUENCE = new Object[] {
		new Tagged("version",new BERInteger(null),1,false,false),
		new BERInteger("serialNumber"),
		new BERSequence("signature", ALGORITHM_IDENTIFIER_SEQUENCE),
		new BERChoice("issuer", NAME_CHOICE,1),
		new BERSequence("validity", VALIDITY_SEQUENCE),
		new BERChoice("subject", NAME_CHOICE,1),
		//new BERSequence("subjectPublicKeyInfo", SUBJECT_PUBLIC_KEY_INFO_SEQUENCE),
		new Optional("issuerUniqueID",new Tagged(null,new BERBitString(null),2,true,false)),
		new Optional("subjectUniqueID",new Tagged(null,new BERBitString(null),3,true,false)),
		//new Optional("extensions", new Tagged(null,new BERSequenceOf(null,EXTENSION_SEQUENCE),4,false,false))
	};
	
	public static final Object[] CERTIFICATE_SEQUENCE = new Object[] {
		new BERSequence("tsbCertificate",TSB_CERTIFICATE_SEQUENCE),
		new BERSequence("algorithmIdentifier", ALGORITHM_IDENTIFIER_SEQUENCE),
		new BERBitString("signatureValue")
	};
	
	public static final BERSequence CERTIFICATE = new BERSequence("X509Certificate",CERTIFICATE_SEQUENCE);
}
