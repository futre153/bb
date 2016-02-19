package org.pabk.basen.asn1;

import java.io.IOException;

import org.pabk.basen.ber.BERImpl;


public interface ASN1Object {
	public void setName(String name);
	public String getName();
	public boolean setBERObject(BERImpl ber) throws IOException;
	public boolean checkId(BERImpl ber);
	public void setValue(Object ... objs) throws IOException;
}
