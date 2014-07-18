package org.acepricot.ber;

import java.io.IOException;

interface BERBase {
	long decode(BERInputStream in) throws IOException;
	long encode(BEROutputStream out) throws IOException;
	void setValue(byte[] b) throws IOException;
	byte[] getValue() throws IOException;
}
