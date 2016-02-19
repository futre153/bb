package org.pabk.basen.rfc.rfc5280;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Iterator;

import org.pabk.basen.asn1.Any;
import org.pabk.basen.ber.BERImpl;

public class SimpleCert extends Any {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private X509Certificate certificate;
	
	public SimpleCert(String alias, InputStream in) throws IOException, CertificateException {
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
	
	public SimpleCert(String name, boolean optional) {
		super(name, optional);
	}
	
	public X509Certificate getCertificate() {
		return certificate;
	}
	
}
