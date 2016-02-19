package org.pabk.basen.rfc;

import java.io.IOException;
import java.util.Hashtable;

import org.pabk.basen.asn1.ASN1Impl;
import org.pabk.basen.asn1.Any;
import org.pabk.basen.asn1.ObjectIdentifier;
import org.pabk.basen.asn1.Sequence;
import org.pabk.basen.ber.BERImpl;

public class AlgorithmIdentifier extends Sequence {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String ALG_NAME = "algorithm";
	private static final String PARAM_NAME = "parameters";
	private static final boolean ALG_OPT = false;
	private static final boolean PARAM_OPT = true;
	private static final String SHA256WITHRSA = "SHA256withRSA";
	private static final String SHA256WITHRSA_OID = "1.2.840.113549.1.1.11";
	private static final String UNKNOWN_ALGORITHMS = "Unknown algorithm identifier - %s";
	private static final String PARAMETERS_NOT_SUPPORTED = "Parameters are not supported in algorithm identifier";
	private static final String CREATE_ALGID_FAILED = "Insuficient of source datas while creating Name";
	private static final String RSA = "RSA";
	private static final String RSA_OID = "1.2.840.113549.1.1.1";
	
	private static ASN1Impl[] ALG_ID_SEQ = new ASN1Impl[]{
		new ObjectIdentifier(ALG_NAME, ALG_OPT),
		new Any(PARAM_NAME, PARAM_OPT)
	};
	private static Hashtable<String, String> algorithms;
	
	public AlgorithmIdentifier(String name, boolean optional) {
		super(name, optional);
		this.setSequences(ALG_ID_SEQ);
	}
	private AlgorithmIdentifier() {}
	@Override
	public AlgorithmIdentifier clone() {
		return (AlgorithmIdentifier) ASN1Impl.clone(new AlgorithmIdentifier(), this);
	}
	
	public static BERImpl createAlgorithmId (Object ...objs) throws IOException {
		initAlgorithms();
		if(objs != null && objs.length > 0) {
			String algOid = algorithms.get(objs[0]);
			if(algOid == null) {
				throw new IOException (String.format(UNKNOWN_ALGORITHMS, objs[0]));
			}
			BERImpl seq = new BERImpl (BERImpl.SEQUENCE_TAG, BERImpl.CONSTRUCTED_ENCODING);
			BERImpl oid = new BERImpl (BERImpl.OBJECT_IDENTIFIER_TAG, BERImpl.PRIMITIVE_ENCODING);
			oid.getEncoder().setValue(oid, algOid);
			BERImpl par;
			if(objs.length > 1) {
				throw new IOException (String.format(PARAMETERS_NOT_SUPPORTED));
			}
			else {
				par = new BERImpl(BERImpl.NULL_TAG, BERImpl.PRIMITIVE_ENCODING);
				par.getEncoder().setValue(par, new byte[0]);
			}
			seq.getEncoder().setValue(seq, new BERImpl[] {oid, par});
			return seq;
		}
		throw new IOException (CREATE_ALGID_FAILED);
	}
	private static void initAlgorithms() {
		if(algorithms == null)	{
			algorithms = new Hashtable<String, String>();
			algorithms.put(SHA256WITHRSA, SHA256WITHRSA_OID);
			algorithms.put(RSA, RSA_OID);
		}
	}
	
}
