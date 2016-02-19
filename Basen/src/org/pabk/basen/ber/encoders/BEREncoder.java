package org.pabk.basen.ber.encoders;

import java.io.IOException;
import java.io.InputStream;

import org.pabk.basen.ber.BERImpl;

public interface BEREncoder {
	InputStream encode(BERImpl ber) throws IOException;
	long decode(BERImpl ber, InputStream in) throws IOException;
	long getLength(BERImpl ber) throws IOException;
	void setValue(BERImpl ber, Object obj) throws IOException;
	String[] toString(BERImpl ber);
}
