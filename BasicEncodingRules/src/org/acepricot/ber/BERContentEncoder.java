package org.acepricot.ber;

import java.io.IOException;

public interface BERContentEncoder {

	long encode(BEROutputStream out, BERImpl ber) throws IOException;
	void setDefinite(boolean b);
	void setPrimitive(boolean b);
	void encodeValue(Object value, BERImpl ber, boolean definite) throws IOException;
}
