package org.acepricot.ber;

import java.io.IOException;

public interface BERContentDecoder {

	long decode(BERInputStream in, BERImpl ber) throws IOException;
	void setDefinite(boolean b);
	void setPrimitive(boolean b);
	Object decodeValue(BERImpl ber) throws IOException;
}
