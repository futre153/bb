package org.pabk.rfc.rfc2315;

import org.pabk.ber.BERChoice;
import org.pabk.ber.BERInteger;
import org.pabk.ber.BEROctetString;
import org.pabk.ber.BEROid;
import org.pabk.ber.BERSequence;
import org.pabk.ber.BERSetOf;
import org.pabk.ber.Optional;
import org.pabk.ber.Tagged;
import org.pabk.rfc.rfc5280.RFC5280;

public final class RFC2315 {
	
	private static final BEROid DATA_CONTENT_TYPE = new BEROid("dataContentType",new int[]{1,2,840,113549,1,7,1});
	private static final BEROid SIGD_CONTENT_TYPE = new BEROid("signedDataContentType",new int[]{1,2,840,113549,1,7,2});
	private static final BEROid ENVD_CONTENT_TYPE = new BEROid("envelopedDataContentType",new int[]{1,2,840,113549,1,7,3});
	private static final BEROid SIED_CONTENT_TYPE = new BEROid("signedAndEnvelopedDataContentType",new int[]{1,2,840,113549,1,7,4});
	private static final BEROid DIGD_CONTENT_TYPE = new BEROid("DigestedDataContentType",new int[]{1,2,840,113549,1,7,5});
	private static final BEROid ENCD_CONTENT_TYPE = new BEROid("EncryptedDataContentType",new int[]{1,2,840,113549,1,7,6});
	
	private static final BERInteger VERSION = new BERInteger("version");
	
	private static final Object ALGORITHM = new BEROid("algorithm");
	
	private static final Optional PARAMETERS = new Optional("parameters", new BEROctetString("parameters"));
		
	public static final Object[] ALGORITHM_IDENTIFIER_SEQUENCE = {
		ALGORITHM,
		PARAMETERS
	};
	
	private static final BERSequence ALGORITHM_IDENTIFIER = new BERSequence("algorithmIdentifier", ALGORITHM_IDENTIFIER_SEQUENCE);
	
	
	private static final Object[] DIGEST_ALGORITHMS_SET = {
		ALGORITHM_IDENTIFIER
	};
	
	private static final BERSetOf DIGEST_ALGORITHMS = new BERSetOf("digestAlgorithms", DIGEST_ALGORITHMS_SET);
	
	private static final Optional CONTENT = new Optional("content", new BEROctetString("content"));
	private static final BEROid CONTENT_TYPE = new BEROid("contentType");
		
	private static final Object[] CONTENT_INFO_SEQUENCE = {
		CONTENT_TYPE,
		CONTENT
	};
			
	private static final BERSequence CONTENT_INFO = new BERSequence("contentInfo",CONTENT_INFO_SEQUENCE);
	
	//not finished yet
	private static final BEROctetString CERTIFICATE = new BEROctetString("certificate");
	private static final BEROctetString EXTENDED_CERTIFICATE = new BEROctetString("certificate");
	
	private static final Object[] CERTIFICATE_CHOICE = {
		CERTIFICATE,
		EXTENDED_CERTIFICATE
	};
	
	private static final BERChoice CERTIFICATE_OR_EXTENDED_CERTIFICATE = new BERChoice("certificate", CERTIFICATE_CHOICE, -1);
	
	private static final Object[] CERTIFICATE_SET = {
		CERTIFICATE_OR_EXTENDED_CERTIFICATE
	};
	
	private static final Optional CERTIFICATES = new Optional("certificates", new Tagged("certificates", new BERSetOf(null, CERTIFICATE_SET), 0, true, false));
	
	//not finished yet
	private static final BEROctetString CERTIFICATE_REVOCATION_LIST = new BEROctetString("certificateRevocationList");
	
	private static final Object[] CRLS_SET = {
		CERTIFICATE_REVOCATION_LIST
	};
	
	private static final Optional CRLS = new Optional("crls",new Tagged("crls", new BERSetOf(null, CRLS_SET),1,true,false));
	
	private static final BERChoice ISSUER = new BERChoice("issuer", RFC5280.NAME_CHOICE, -1);
	private static final BERInteger SERIAL_NUMBER = new BERInteger("serialNumber");
	
	private static final Object[] ISSUER_AND_SERIAL_NUMBER_SEQUENCE = {
		ISSUER,
		SERIAL_NUMBER
	};
	
	private static final BERSequence ISSUER_AND_SERIAL_NUMBER = new BERSequence("issuerAndSerialNumber", ISSUER_AND_SERIAL_NUMBER_SEQUENCE);
	
	private static final BERSequence DIGEST_ALGORITHM = new BERSequence("digestAlgorithm", ALGORITHM_IDENTIFIER_SEQUENCE);
	
	private static final BERSequence ATTRIBUTE = new BERSequence("attribute", RFC5280.ATTRIBUTE_TYPE_AND_VALUE_SEQUENCE);
	
	public static final Object[] ATTRIBUTES_SET = {
		ATTRIBUTE
	};
	
	private static final Optional AUTHENTICATED_ATTRIBUTES = new Optional("attributes", new Tagged("attributes", new BERSetOf("attributes", ATTRIBUTES_SET), 0, true, false));
	
	private static final BERSequence DIGEST_ENCRYPTION_ALGORITHM = new BERSequence("digestEncryptionAlgorithm", ALGORITHM_IDENTIFIER_SEQUENCE);
	
	private static final BEROctetString ENCRYPTED_DIGEST = new BEROctetString("encryptedDigest");
	
	private static final Optional UNAUTHENTICATED_ATTRIBUTES = new Optional("attributes", new Tagged("attributes", new BERSetOf("attributes", ATTRIBUTES_SET), 1, true, false));
	
	private static final Object[] SIGNER_INFO_SEQUENCE = {
		VERSION,
		ISSUER_AND_SERIAL_NUMBER,
		DIGEST_ALGORITHM,
		AUTHENTICATED_ATTRIBUTES,
		DIGEST_ENCRYPTION_ALGORITHM,
		ENCRYPTED_DIGEST,
		UNAUTHENTICATED_ATTRIBUTES
	};
	
	private static final BERSequence SIGNER_INFO = new BERSequence("signerInfo", SIGNER_INFO_SEQUENCE);
	
	private static final Object[] SIGNER_INFOS_SET = {
		SIGNER_INFO
	};
	
	private static final BERSetOf SIGNER_INFOS = new BERSetOf("signerInfos", SIGNER_INFOS_SET);
	
	private static final BERSequence KEY_ENCRYPTION_ALGORITHM = new BERSequence("keyEncryptionAlgorithm", ALGORITHM_IDENTIFIER_SEQUENCE);

	private static final BEROctetString ENCRYPTED_KEY = new BEROctetString("encryptedKey");
	
	private static final Object[] SECIPIENT_INFO_SEQUENCE = {
		VERSION,
		ISSUER_AND_SERIAL_NUMBER,
		KEY_ENCRYPTION_ALGORITHM,
		ENCRYPTED_KEY
	};
	
	private static final BERSequence RECIPIENT_INFO = new BERSequence("recipientInfo", SECIPIENT_INFO_SEQUENCE);
	
	private static final Object[] RECIPIENT_INFOS_SET = {
		RECIPIENT_INFO
	};
	
	private static final BERSetOf RECIPIENT_INFOS = new BERSetOf("recipientsInfo", RECIPIENT_INFOS_SET);
	
	private static final BERSequence CONTENT_ENCRYPTION_ALGORITHM = new BERSequence("contentEncryptionAlgorithm", ALGORITHM_IDENTIFIER_SEQUENCE);
	
	private static final Optional ENCRYPTED_CONTENT = new Optional("encryptedContent", new Tagged("encryptedContent", new BEROctetString("encryptedContent"), 0, true, false));
	
	private static final Object[] ENCRYPTED_CONTENT_INFO_SEQUENCE = {
		CONTENT_TYPE,
		CONTENT_ENCRYPTION_ALGORITHM,
		ENCRYPTED_CONTENT
	};
	
	private static final BERSequence ENCRYPTED_CONTENT_INFO = new BERSequence("encryptedContentInfo", ENCRYPTED_CONTENT_INFO_SEQUENCE);
	
	private static final BEROctetString DIGEST = new BEROctetString("digest");
	
	private static final Object[] SIGD_SEQUENCE = {
		VERSION,
		DIGEST_ALGORITHMS,
		CONTENT_INFO,
		CERTIFICATES,
		CRLS,
		SIGNER_INFOS
	};
		
	private static final Object[] ENVD_SEQUENCE = {
		VERSION,
		RECIPIENT_INFOS,
		ENCRYPTED_CONTENT_INFO
	};
	
	private static final Object[] SIED_SEQUENCE = {
		VERSION,
		RECIPIENT_INFOS,
		DIGEST_ALGORITHM,
		ENCRYPTED_CONTENT_INFO,
		CERTIFICATES,
		CRLS,
		SIGNER_INFOS
	};
		
	private static final Object[] DIGD_SEQUENCE = {
		VERSION,
		DIGEST_ALGORITHM,
		CONTENT_INFO,
		DIGEST
	};
	
	private static final Object[] ENCD_SEQUENCE = {
		VERSION,
		ENCRYPTED_CONTENT_INFO
	};
	
	private static final Tagged DATA_CONTENT = new Tagged("Content", new BEROctetString("data"), 0, false, false);
	private static final Tagged SIGD_CONTENT = new Tagged("Content", new BERSequence("signedData", SIGD_SEQUENCE), 0, false, false);
	private static final Tagged ENVD_CONTENT = new Tagged("Content", new BERSequence("envelopedData", ENVD_SEQUENCE), 0, false, false);
	private static final Tagged SIED_CONTENT = new Tagged("Content", new BERSequence("signedAndEnvelopedData", SIED_SEQUENCE), 0, false, false);
	private static final Tagged DIGD_CONTENT = new Tagged("Content", new BERSequence("digestedData", DIGD_SEQUENCE), 0, false, false);
	private static final Tagged ENCD_CONTENT = new Tagged("Content", new BERSequence("encryptedData", ENCD_SEQUENCE), 0, false, false);
	
	private static final Object[] DATA_CONTENT_INFO_SEQUENCE = {
		DATA_CONTENT_TYPE,
		DATA_CONTENT
	};
	
	private static final Object[] SIGD_CONTENT_INFO_SEQUENCE = {
		SIGD_CONTENT_TYPE,
		SIGD_CONTENT
	};
	
	private static final Object[] ENVD_CONTENT_INFO_SEQUENCE = {
		ENVD_CONTENT_TYPE,
		ENVD_CONTENT
	};
	
	private static final Object[] SIED_CONTENT_INFO_SEQUENCE = {
		SIED_CONTENT_TYPE,
		SIED_CONTENT
	};
	
	private static final Object[] DIGD_CONTENT_INFO_SEQUENCE = {
		DIGD_CONTENT_TYPE,
		DIGD_CONTENT
	};
	
	private static final Object[] ENCD_CONTENT_INFO_SEQUENCE = {
		ENCD_CONTENT_TYPE,
		ENCD_CONTENT
	};
	
	private static final BERSequence[] PKCS7 = {
		new BERSequence("DataContentInfo", DATA_CONTENT_INFO_SEQUENCE),
		new BERSequence("SignedDataContentInfo", SIGD_CONTENT_INFO_SEQUENCE),
		new BERSequence("EnvelopedDataContentInfo", ENVD_CONTENT_INFO_SEQUENCE),
		new BERSequence("SignedAndEnvelopedDataContentIngo", SIED_CONTENT_INFO_SEQUENCE),
		new BERSequence("DigestedDataContentInfo", DIGD_CONTENT_INFO_SEQUENCE),
		new BERSequence("EncryptedDataContentInfo", ENCD_CONTENT_INFO_SEQUENCE)
	};
	
	public static final BERSequence getMessage(String name, int i) {
		return (BERSequence) PKCS7[i].copy(name);
	}
}
