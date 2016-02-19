package org.pabk.basen.rfc.rfc2315;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.ietf.jgss.Oid;
import org.pabk.basen.asn1.ASN1Impl;
import org.pabk.basen.asn1.Any;
import org.pabk.basen.asn1.ObjectIdentifier;
import org.pabk.basen.asn1.OctetString;
import org.pabk.basen.asn1.Sequence;
import org.pabk.basen.asn1.Tag;
import org.pabk.basen.ber.BERImpl;

public class ContentInfo extends Sequence {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String PKCS7_OID = 			  "1.2.840.113549.1.7";
	private static final String DATA_OID = PKCS7_OID + 						".1";
	private static final String SIGNED_DATA_OID = PKCS7_OID + 				".2";
	private static final String ENVELOPED_DATA_OID = PKCS7_OID + 			".3";
	private static final String SIGNED_AND_ENVELOPED_DATA_OID = PKCS7_OID + ".4";
	private static final String DIGESTED_DATA_OID = PKCS7_OID + 			".5";
	private static final String ENCRYPTED_OID = PKCS7_OID + 				".6";
	
	private static final String CONTENT_TYPE_NAME = "contentType";
	private static final boolean CONTENT_TYPE_OPT = false;
	private static final String CONTENT_NAME = 		"content";
	private static final int CONTENT_CLASS = 		BERImpl.CONTEXT_SPECIFIC_CLASS;
	private static final int CONTENT_PC = 			BERImpl.CONSTRUCTED_ENCODING;
	private static final int CONTENT_TAG = 			0x00;
	private static final boolean CONTENT_OPT = 		true;
	private static final boolean CONTENT_IMP = 		false;

	private static final String DATA_CONTENT_NAME = "data";
	private static final int DATA_CONTENT_PC = BERImpl.PRIMITIVE_ENCODING;
	private static final boolean DATA_CONTENT_OPT = false;
	private static final String SIGNED_DATA_CONTENT_NAME = "signedData";
	private static final boolean SIGNED_DATA_CONTENT_OPT = false;
	private static final String ENVELOPED_DATA_CONTENT_NAME = "envelopedData";
	private static final boolean ENVELOPED_DATA_CONTENT_OPT = false;
	private static final String SIGNED_AND_ENVELOPED_DATA_CONTENT_NAME = "signedAndEnvelopedData";
	private static final boolean SIGNED_AND_ENVELOPED_DATA_CONTENT_OPT = false;
	private static final String DIGESTED_DATA_CONTENT_NAME = "digestedData";
	private static final boolean DIGESTED_DATA_CONTENT_OPT = false;
	private static final String ENCRYPTED_DATA_CONTENT_NAME = "encryptedData";
	private static final boolean ENCRYPTED_DATA_CONTENT_OPT = false;
	
	private static Oid dataOid;
	private static Oid signedDataOid;
	private static Oid envelopedDataOid;
	private static Oid signedAndEnvelopedDataOid;
	private static Oid digestedDataOid;
	private static Oid encryptedDataOid;
	
	private final Any CONTENT_OBJECT = new Any(CONTENT_NAME, CONTENT_OPT);
	
	//private Oid contentType;
	//private ASN1Impl content;
	//private final Tag TAGGED_CONTENT = new Tag(CONTENT_NAME, CONTENT_CLASS, CONTENT_PC, CONTENT_TAG, CONTENT_IMP, CONTENT_OPT, CONTENT_OBJECT);
	
	private final ASN1Impl[] CONTENT_INFO_SEQ = {
		new ObjectIdentifier (CONTENT_TYPE_NAME, CONTENT_TYPE_OPT),
		new Tag(CONTENT_NAME, CONTENT_CLASS, CONTENT_PC, CONTENT_TAG, CONTENT_IMP, CONTENT_OPT, CONTENT_OBJECT)
	};
	
	public ContentInfo(String name, boolean optional) {
		super(name, optional);
		if(dataOid == null) {
			try {
				dataOid = new Oid(DATA_OID);
				signedDataOid = new Oid(SIGNED_DATA_OID);
				envelopedDataOid = new Oid(ENVELOPED_DATA_OID);
				signedAndEnvelopedDataOid = new Oid(SIGNED_AND_ENVELOPED_DATA_OID);
				digestedDataOid = new Oid(DIGESTED_DATA_OID);
				encryptedDataOid = new Oid(ENCRYPTED_OID);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.setSequences(CONTENT_INFO_SEQ);
	}
	
	public boolean setBERObject(BERImpl ber) {
		try {
			super.setBERObject(ber);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			this.get(0)[0].getBERObject().encode(out);
			Oid oid = new Oid(out.toByteArray());
			//this.setContentType(oid);
			ASN1Impl content = this.get(0)[1];
			if (content != null) {
				/*ByteArrayOutputStream out = new ByteArrayOutputStream();
				content.getBERObject().encode(out);
				ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());*/
				ber = content.getBERObject();
				if (oid.equals(dataOid)) {
					content = new OctetString (DATA_CONTENT_NAME, DATA_CONTENT_PC, DATA_CONTENT_OPT);
					//((OctetString)content).setBERObject(ber);
				}
				else if (oid.equals(signedDataOid)) {
					content = new SignedData (SIGNED_DATA_CONTENT_NAME, SIGNED_DATA_CONTENT_OPT);
					//((SignedData)content).setBERObject(ber);
				}
				else if (oid.equals(envelopedDataOid)) {
					content = new EnvelopedData (ENVELOPED_DATA_CONTENT_NAME, ENVELOPED_DATA_CONTENT_OPT);
					//((EnvelopedData)content).setBERObject(ber);
				}
				else if (oid.equals(signedAndEnvelopedDataOid)) {
					content = new SignedAndEnvelopedData (SIGNED_AND_ENVELOPED_DATA_CONTENT_NAME, SIGNED_AND_ENVELOPED_DATA_CONTENT_OPT);
					//((SignedAndEnvelopedData)content).setBERObject(ber);
				}
				else if (oid.equals(digestedDataOid)) {
					content = new DigestedData (DIGESTED_DATA_CONTENT_NAME, DIGESTED_DATA_CONTENT_OPT);
					//((DigestedData)content).setBERObject(ber);
				}
				else if (oid.equals(encryptedDataOid)) {
					content = new EncryptedData (ENCRYPTED_DATA_CONTENT_NAME, ENCRYPTED_DATA_CONTENT_OPT);
					//((EncryptedData)content).setBERObject(ber);
				}
				else {
					throw new IOException("Unknown oid for PKCS7 " + oid);
				}
				this.getSequences()[1].getSequences()[0] = content;
				this.get(0)[1].clear();
				this.get(0)[1].getSequences()[0] = content;
				if (!this.get(0)[1].setBERObject(ber)) {
					throw new IOException("Failed content info");
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			this.clear();
			return false;
		}
		return true;
	}
	
	private ContentInfo() {}
	@Override
	public ContentInfo clone() {
		return (ContentInfo) ASN1Impl.clone(new ContentInfo(), this);
	}
	
	/*
	public void setContentType (Oid oid) {
		this.contentType = oid;
	}
	
	public Oid getContentType() {
		return this.contentType;
	}

	public ASN1Impl getContent() {
		return content;
	}

	public void setContent(ASN1Impl content) {
		this.content = content;
	}*/
}
