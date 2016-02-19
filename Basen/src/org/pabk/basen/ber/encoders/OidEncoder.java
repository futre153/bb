package org.pabk.basen.ber.encoders;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.ietf.jgss.Oid;
import org.pabk.basen.ber.BERImpl;

public class OidEncoder extends UniversalTagEncoder {
	private static final String CLASS_NOT_SUPPORTED = "Class %s is not supported to create OID value";

	public void setValue(BERImpl ber, Object obj) throws IOException {
		try {
			Oid oid = null;
			if(obj instanceof String) {
				oid = new Oid((String) obj);
			}
			else if (obj instanceof Oid) {
				oid = (Oid) obj;
			}
			else {
				throw new IOException(String.format(CLASS_NOT_SUPPORTED, obj.getClass().getName()));
			}
			ByteArrayInputStream in = new ByteArrayInputStream(oid.getDER());
			BERImpl tmp = new BERImpl();
			tmp.decode(in);
			ber.setLength(tmp.getLength());
			ber.setContent(tmp.getContent());
			tmp = null;
		}
		catch (Exception e) {
			throw new IOException(e);
		}
	}
}
