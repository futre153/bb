package org.pabk.basen.rfc.rfc5280;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Iterator;

import org.pabk.basen.asn1.ASN1Impl;
import org.pabk.basen.asn1.BitString;
import org.pabk.basen.asn1.Sequence;
import org.pabk.basen.ber.BERImpl;
import org.pabk.basen.rfc.AlgorithmIdentifier;
import org.pabk.util.Base64Coder;

public class Certificate extends Sequence {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String TBS_CERT_NAME = "tbsCertificate";
	private static final boolean TBS_CERT_OPT = false;
	private static final String ALG_ID_NAME = "signatureAlgorithm";
	private static final boolean ALG_ID_OPT = false;
	private static final String SIG_VALUE_NAME = "signatureValue";
	private static final int SIG_VALUE_PC = BERImpl.PRIMITIVE_ENCODING;
	private static final boolean SIG_VALUE_OPT = false;
	private static final String PEM_FORMAT = "PEM";
	private static final byte[] BEGIN_CERTIFICATE = "-----BEGIN CERTIFICATE-----".getBytes();
	private static final byte[] END_CERTIFICATE = "-----END CERTIFICATE-----".getBytes();
	private static final String UNKNOWN_CERT_FILE_FORMAT = "Unknown certificate file format %s";
	
	private X509Certificate certificate;
	
	private final ASN1Impl[] CERT_SEQ = {
		new TBSCertificate (TBS_CERT_NAME, TBS_CERT_OPT),
		new AlgorithmIdentifier(ALG_ID_NAME, ALG_ID_OPT),
		new BitString(SIG_VALUE_NAME, SIG_VALUE_PC, SIG_VALUE_OPT)
	};
	
	public Certificate(String alias, InputStream in) throws IOException, CertificateException {
		this(alias, true);
		BERImpl ber = new BERImpl();
		ber.decode(in);
		if(this.setBERObject(ber)) {
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ber.encode(out);
			ByteArrayInputStream bin = new ByteArrayInputStream(out.toByteArray());
			Collection<?> c = cf.generateCertificates(bin);
			Iterator<?> i = c.iterator();
			if (i.hasNext()) {
				Object obj = i.next();
				this.certificate = (X509Certificate) obj;
			}
		}
		else {
			throw new IOException("Failed to decode certificate from Input Stream " + in);
		}
	}
	
	public static BERImpl createCertificate(KeyPair pair, int serialNo, String signature, String[] issuer, long notBefore, long notAfter, String[] subject) throws IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		BERImpl cer = new BERImpl (BERImpl.SEQUENCE_TAG, BERImpl.CONSTRUCTED_ENCODING);
		BERImpl tbs = TBSCertificate.createTBSCertificate(pair, serialNo, signature, issuer, notBefore, notAfter, subject);
		BERImpl sig = AlgorithmIdentifier.createAlgorithmId(signature);
		BERImpl siv = new BERImpl (BERImpl.BITSTRING_TAG, BERImpl.PRIMITIVE_ENCODING);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		tbs.encode(out);
		Signature sign = Signature.getInstance(signature);
		sign.initSign(pair.getPrivate());
		sign.update(out.toByteArray());
		siv.getEncoder().setValue(siv, TBSCertificate.joinByteArrays(new byte[]{0},sign.sign()));
		sign = null;
		cer.getEncoder().setValue(cer, new BERImpl[] {tbs, sig, siv});
		return cer;
	}
	
	
	public Certificate() {
		this("Certificate", false);
	}
	
	public Certificate(String name, boolean optional) {
		super(name, optional);
		this.setSequences(CERT_SEQ);
	}

	public X509Certificate getCertificate() {
		return certificate;
	}

	public static void exportToFile(BERImpl ber, String fName, String format) throws IOException {
		FileOutputStream out = null;
		if(format.equalsIgnoreCase(PEM_FORMAT)) {
			try {
				out = new FileOutputStream(fName);
				out.write(BEGIN_CERTIFICATE);
				out.write(System.getProperty("line.separator").getBytes());
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				ber.encode(bout);
				out.write(Base64Coder.encodeLines(bout.toByteArray()).getBytes());
				out.write(END_CERTIFICATE);
			}
			catch (Exception e) {
				throw new IOException (e);
			}
			finally {
				out.close();
			}
		}
		else {
			throw new IOException(String.format(UNKNOWN_CERT_FILE_FORMAT, format));
		}
	}
	
}
