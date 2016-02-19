package org.pabk.basen.rfc.rfc5280;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;

import org.pabk.basen.asn1.ASN1Impl;
import org.pabk.basen.asn1.BitString;
import org.pabk.basen.asn1.Sequence;
import org.pabk.basen.ber.BERImpl;
import org.pabk.basen.rfc.AlgorithmIdentifier;

public class CRLs extends Sequence {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String CRL_NAME = "tbsCrlList";
	private static final boolean CRL_OPT = false;
	private static final String ALG_ID_NAME = "signatureAlgorithm";
	private static final boolean ALG_ID_OPT = false;
	private static final String SIG_VALUE_NAME = "signatureValue";
	private static final int SIG_VALUE_PC = BERImpl.PRIMITIVE_ENCODING;
	private static final boolean SIG_VALUE_OPT = false;
	
	private final ASN1Impl[] CRL_LIST_SEQ = {
		new TBSCRLs(CRL_NAME, CRL_OPT),
		new AlgorithmIdentifier(ALG_ID_NAME, ALG_ID_OPT),
		new BitString(SIG_VALUE_NAME, SIG_VALUE_PC, SIG_VALUE_OPT)
	};
	private X509CRL crl;

	public CRLs(String name, boolean optional) {
		super(name, optional);
		this.setSequences(CRL_LIST_SEQ);
	}
	
	public CRLs(String alias, InputStream in) throws IOException, CRLException, CertificateException {
		this(alias, true);
		BERImpl ber = new BERImpl();
		ber.decode(in);
		if(this.setBERObject(ber)) {
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ber.encode(out);
			ByteArrayInputStream bin = new ByteArrayInputStream(out.toByteArray());
			this.setCrl((X509CRL) cf.generateCRL(bin));
		}
		else {
			throw new IOException("Failed to decode certificate from Input Stream " + in);
		}
	}

	public X509CRL getCrl() {
		return crl;
	}

	public void setCrl(X509CRL crl) {
		this.crl = crl;
	}
	
}
