package org.pabk.basen.rfc.rfc2986;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;

import org.pabk.basen.asn1.ASN1Impl;
import org.pabk.basen.asn1.BitString;
import org.pabk.basen.asn1.Sequence;
import org.pabk.basen.ber.BERImpl;
import org.pabk.basen.rfc.AlgorithmIdentifier;
import org.pabk.basen.rfc.rfc5280.TBSCertificate;
import org.pabk.util.Base64Coder;

public class Request extends Sequence {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String TBS_REQ_NAME = "tbsRequest";
	private static final boolean TBS_REQ_OPT = false;
	private static final String SIG_ALG_NAME = "signatureAlgorithm";
	private static final boolean SIG_ALG_OPT = false;
	private static final String SIG_NAME = "signature";
	private static final int SIG_PC = BERImpl.PRIMITIVE_ENCODING;
	private static final boolean SIG_OPT = false;
	public static final String PEM_FORMAT = "PEM";
	private static final byte[] BEGIN_REQUEST = "-----BEGIN CERTIFICATE REQUEST-----".getBytes();
	private static final byte[] END_REQUEST = "-----END CERTIFICATE REQUEST-----".getBytes();
	private static final String UNKNOWN_REQ_FILE_FORMAT = "Unknown request file format %s";

	private static final String DEFAULT_SIGNATURE = "SHA256withRSA";
	
	private final ASN1Impl[] REQ_SEQ = {
		new TBSRequest(TBS_REQ_NAME, TBS_REQ_OPT),
		new AlgorithmIdentifier(SIG_ALG_NAME, SIG_ALG_OPT),
		new BitString(SIG_NAME, SIG_PC, SIG_OPT)
	};
	
	public Request (String name, boolean optional) {
		super(name, optional);
		this.setSequences(REQ_SEQ);
	}
	public Request (String alias, InputStream in) throws IOException, CertificateException {
		this(alias, true);
		BERImpl ber = new BERImpl();
		ber.decode(in);
		if(!this.setBERObject(ber)) {
			throw new IOException("Failed to decode request from Input Stream " + in);
		}
	}
	
	public static BERImpl createRequest(KeyPair pair, String[] issuer, Object[] extensions) throws IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		BERImpl req = new BERImpl (BERImpl.SEQUENCE_TAG, BERImpl.CONSTRUCTED_ENCODING);
		BERImpl tbs = TBSRequest.createTBSRequest(pair, issuer, extensions);
		BERImpl sig = AlgorithmIdentifier.createAlgorithmId(DEFAULT_SIGNATURE);
		BERImpl siv = new BERImpl (BERImpl.BITSTRING_TAG, BERImpl.PRIMITIVE_ENCODING);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		tbs.encode(out);
		Signature sign = Signature.getInstance(DEFAULT_SIGNATURE);
		sign.initSign(pair.getPrivate());
		sign.update(out.toByteArray());
		siv.getEncoder().setValue(siv, TBSCertificate.joinByteArrays(new byte[]{0},sign.sign()));
		sign = null;
		req.getEncoder().setValue(req, new BERImpl[] {tbs, sig, siv});
		return req;
	}
	
	public static void exportToStream(BERImpl ber, OutputStream out, String format) throws IOException {
		try {
			out.write(BEGIN_REQUEST);
			out.write(System.getProperty("line.separator").getBytes());
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			ber.encode(bout);
			out.write(Base64Coder.encodeLines(bout.toByteArray()).getBytes());
			out.write(END_REQUEST);
		}
		catch (Exception e) {
			throw new IOException (e);
		}
		finally {
			out.close();
		}
	}
	
	public static void exportToFile(BERImpl ber, String fName, String format) throws IOException {
		if(format.equalsIgnoreCase(PEM_FORMAT)) {
			Request.exportToStream(ber, new FileOutputStream(fName), format);
		}
		else {
			throw new IOException(String.format(UNKNOWN_REQ_FILE_FORMAT, format));
		}
	}
}
