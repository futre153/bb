package org.pabk.basen.rfc.rfc2986;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.util.ArrayList;

import org.pabk.basen.asn1.ASN1Impl;
import org.pabk.basen.asn1.BERInteger;
import org.pabk.basen.asn1.Sequence;
import org.pabk.basen.asn1.Tag;
import org.pabk.basen.ber.BERImpl;
import org.pabk.basen.rfc.Attributes;
import org.pabk.basen.rfc.Name;
import org.pabk.basen.rfc.rfc5280.Extensions;

public class TBSRequest extends Sequence {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String VER_NAME = "version";
	private static final boolean VER_OPT = false;
	private static final String SUB_NAME = "subject";
	private static final boolean SUB_OPT = false;
	private static final String SUB_PK_INFO_NAME = "subjectPKInfo";
	private static final boolean SUB_PK_INFO_OPT = false;
	private static final String ATTS_NAME = "Attributes";
	private static final int ATTS_CLASS = BERImpl.CONTEXT_SPECIFIC_CLASS;
	private static final int ATTS_PC = BERImpl.CONSTRUCTED_ENCODING;
	private static final int ATTS_TAG = 0;
	private static final boolean ATTS_IMP = true;
	private static final boolean ATTS_OPT = true;

	private static final int DEFAULT_CSR_VERSION = 0;
	private static final int EXTENSION_TAG = 0;
	private static final Object PKCS9_OID = "1.2.840.113549.1.9.14";
	
	public static final String KEY_USAGE_OID = "2.5.29.15";
	private static final boolean KEY_USAGE_CRIT = true;
	public static final String SAN_OID = "2.5.29.17";
	private static final boolean SAN_CRIT = false;
	public static final String EKU_OID = "2.5.29.37";
	private static final boolean EKU_CRIT = false;

	public static final String IP_SAN = "iPAddress";
	public static final String DNS_NAME_SAN = "dNSName";
	public static final String RFC882_NAME = "rfc822Name";

	private static final int RFC882_NAME_TAG = 1;
	private static final int DNS_NAME_SAN_TAG = 2;
	private static final int IP_SAN_TAG = 7;
	
	private static final String FAILED_CREATE_KEY_USAGE = "Failed to create key usage due to null parameters";
	private static final String FAILED_ALT_NAME_NULL = "Failed to create subject alternative name because type or value of name is null";
	private static final String UBSUPPORTED_SAN = "Unsupported subject alternative name - %s";
	private static final String EXTENSION_NOT_SUPPORTED = "Extension %s is not supported";
	private static final String EXT_KEY_USAGE_NULL = "Extended key usage parameters are null";


	

	private final ASN1Impl ATTS_OBJ = new Attributes(ATTS_NAME, ATTS_OPT);

	private final ASN1Impl[] TBS_REQ_SEQ = {
		new BERInteger(VER_NAME, VER_OPT),
		new Name (SUB_NAME, SUB_OPT),
		new SubjectPKInfo(SUB_PK_INFO_NAME, SUB_PK_INFO_OPT),
		new Tag(ATTS_NAME, ATTS_CLASS, ATTS_PC, ATTS_TAG, ATTS_IMP, ATTS_OPT, ATTS_OBJ)
	};

	public TBSRequest(String name, boolean opt) {
		super(name, opt);
		this.setSequences(TBS_REQ_SEQ);
		this.ATTS_OBJ.setImplicit(true);
	}
	
	public static BERImpl createTBSRequest (KeyPair pair, Object sna, Object extensions) throws IOException {
		BERImpl tbs = new BERImpl(BERImpl.SEQUENCE_TAG, BERImpl.CONSTRUCTED_ENCODING);
		BERImpl ver = new BERImpl(BERImpl.INTEGER_TAG, BERImpl.PRIMITIVE_ENCODING);
		BERImpl nme = Name.createName(sna);
		BERImpl spi = new BERImpl(BERImpl.SEQUENCE_TAG, BERImpl.CONSTRUCTED_ENCODING);
		BERImpl tag = new BERImpl(EXTENSION_TAG, BERImpl.CONSTRUCTED_ENCODING, BERImpl.CONTEXT_SPECIFIC_CLASS);
		BERImpl ext = createCertificateExtension (extensions);
		ver.getEncoder().setValue(ver, new byte[]{DEFAULT_CSR_VERSION});
		spi.decode(new ByteArrayInputStream(pair.getPublic().getEncoded()));
		tag.getEncoder().setValue(tag, ext == null ? new BERImpl[0] : new BERImpl[]{ext});
		tbs.getEncoder().setValue(tbs, new BERImpl[] {ver, nme, spi, tag});
		return tbs;
	}
	/*
	 * 	KeyUsage ::= BIT STRING {
			digitalSignature (0),
			contentCommitment (1),
			keyEncipherment (2),
			dataEncipherment (3),
			keyAgreement (4),
			keyCertSign (5),
			cRLSign (6),
			encipherOnly (7),
			decipherOnly (8) }
	 */
	
	public static BERImpl createCertificateExtension (Object tokens) {
		
		if(tokens != null) {
			try {
				ArrayList<BERImpl> tmp = new ArrayList<BERImpl>();
				Object[] tok = (Object[]) tokens;
				for(int i = 0; i < (tok.length - 1); i += 2) {
					BERImpl ext = null;
					if(tok[i].equals(KEY_USAGE_OID)) {
						ext = createKeyUsageExt((String) tok[i + 1]);
					}
					else if (tok[i].equals(SAN_OID)) {
						ext = createSubjAltName((String[]) tok[i + 1]);
					}
					else if (tok[i].equals(EKU_OID)) {
						ext = createExtKeyUsage((String[]) tok[i + 1]);
					}
					else {
						throw new IOException (String.format(EXTENSION_NOT_SUPPORTED, tok[i]));
					}
					tmp.add(ext);
				}
				if(tmp.size() > 0) {
					BERImpl att = new BERImpl (BERImpl.SEQUENCE_TAG, BERImpl.CONSTRUCTED_ENCODING);
					BERImpl oid = new BERImpl (BERImpl.OBJECT_IDENTIFIER_TAG, BERImpl.PRIMITIVE_ENCODING);
					BERImpl val = new BERImpl (BERImpl.SET_TAG, BERImpl.CONSTRUCTED_ENCODING);
					BERImpl exs = new BERImpl (BERImpl.SEQUENCE_TAG, BERImpl.CONSTRUCTED_ENCODING);
					BERImpl[] ext = new BERImpl[tmp.size()];
					ext = tmp.toArray(ext);
					exs.getEncoder().setValue(exs, ext);
					val.getEncoder().setValue(val, new BERImpl[]{exs});
					oid.getEncoder().setValue(oid, PKCS9_OID);
					att.getEncoder().setValue(att, new BERImpl[]{oid, val});
					return att;
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static BERImpl createExtKeyUsage (String[] extKeyUsage) throws IOException {
		if(extKeyUsage != null) {
			ArrayList<BERImpl> tmp = new ArrayList<BERImpl>();
			for(int i = 0; i < extKeyUsage.length; i ++) {
				BERImpl oid = new BERImpl(BERImpl.OBJECT_IDENTIFIER_TAG, BERImpl.PRIMITIVE_ENCODING);
				oid.getEncoder().setValue(oid, extKeyUsage[i]);
				tmp.add(oid);
			}
			if(tmp.size() > 0) {
				BERImpl seq = new BERImpl(BERImpl.SEQUENCE_TAG, BERImpl.CONSTRUCTED_ENCODING);
				BERImpl[] eku = new BERImpl[tmp.size()];
				eku = tmp.toArray(eku);
				seq.getEncoder().setValue(seq, eku);
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				seq.encode(out);
				return Extensions.createExtension(EKU_OID, EKU_CRIT, out.toByteArray());
			}
		}
		throw new IOException (String.format(EXT_KEY_USAGE_NULL));
	}
	
	
	public static BERImpl createSubjAltName(String[] san) throws IOException {
		if(san != null) {
			try {
				ArrayList<BERImpl> genNames = new ArrayList<BERImpl> ();
				for(int i = 0; i < (san.length - 1); i += 2) {
					genNames.add(createGeneralName(san[i], san[i + 1]));
				}
				if(genNames.size() > 0) {
					BERImpl seq = new BERImpl(BERImpl.SEQUENCE_TAG, BERImpl.CONSTRUCTED_ENCODING);
					BERImpl[] tmp = new BERImpl[genNames.size()];
					tmp = genNames.toArray(tmp);
					seq.getEncoder().setValue(seq, tmp);
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					seq.encode(out);
					return Extensions.createExtension(SAN_OID, SAN_CRIT, out.toByteArray());
				}
				throw new IOException(String.format(FAILED_ALT_NAME_NULL));
			}
			catch (Exception e) {
				throw new IOException(e);
			}
		}
		throw new IOException (String.format(FAILED_ALT_NAME_NULL));
	}
	
	private static BERImpl createGeneralName(String type, String value) throws IOException {
		if(type != null && value != null) {
			try {
				BERImpl val = null;
				Object b = null;
				if(type.equals(IP_SAN)) {
					val = new BERImpl(BERImpl.OCTETSTRING_TAG, BERImpl.PRIMITIVE_ENCODING);
					val.setTag(IP_SAN_TAG);
					String[] ip = value.split("\\.");
					if(ip.length == 4 || ip.length == 6) {
						b = new byte[ip.length];
						for(int i = 0; i < ip.length; i ++) {
							((byte[])b)[i] = (byte)(Integer.parseInt(ip[i]));
						}
					}
				}
				else if (type.equals(DNS_NAME_SAN)) {
					val = new BERImpl(BERImpl.IA5_STRING_TAG, BERImpl.PRIMITIVE_ENCODING);
					val.setTag(DNS_NAME_SAN_TAG);
					b = value.getBytes();
				}
				else if (type.equals(RFC882_NAME)) {
					val = new BERImpl(BERImpl.IA5_STRING_TAG, BERImpl.PRIMITIVE_ENCODING);
					val.setTag(RFC882_NAME_TAG);
					b = value.getBytes();
				}
				else {
					val = null;
				}
				if(val != null) {
					val.set_class(BERImpl.CONTEXT_SPECIFIC_CLASS);
					val.getEncoder().setValue(val, b);
					return val;
				}
				throw new IOException (String.format(UBSUPPORTED_SAN, type));
			}
			catch(Exception e) {
				throw new IOException (e);
			}
		}
		throw new IOException (String.format(FAILED_ALT_NAME_NULL));
	}
	
	public static BERImpl createKeyUsageExt(String keyUsage) throws IOException {
		if(keyUsage != null && keyUsage.length() > 0) {
			BERImpl kus = new BERImpl(BERImpl.BITSTRING_TAG, BERImpl.PRIMITIVE_ENCODING);
			byte[] b = new byte[(keyUsage.length() - 1) / 8 + 2];
			b[0] = (byte) ((8 - (keyUsage.length() % 8)) & 0x07);
			for (int i = 1; i < b.length; i ++) {
				int v = 0;
				for(int j = 0; j < 8; j ++) {
					int index = (i - 1) * 8 + j;
					v <<= 0x01;
					v |=(index < keyUsage.length() ? (keyUsage.charAt(index) == '1' ? 1 : 0) : 0);
				}
				b[i] = (byte) v;
			}
			kus.getEncoder().setValue(kus, b);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			kus.encode(out);
			return Extensions.createExtension(KEY_USAGE_OID, KEY_USAGE_CRIT, out.toByteArray());
		}
		throw new IOException (String.format(FAILED_CREATE_KEY_USAGE));
	}
	
}
