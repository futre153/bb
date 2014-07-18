package org.acepricot.ber;

import java.io.IOException;
import java.io.InputStream;

interface BERInputStream {
	
	int read() throws IOException;
	int read(byte[] b) throws IOException;
	int read(byte[] b, int s, int l) throws IOException;
	void setInputStream(InputStream in);
	void close() throws IOException;
}
