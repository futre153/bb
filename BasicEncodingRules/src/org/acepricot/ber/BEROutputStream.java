package org.acepricot.ber;

import java.io.IOException;
import java.io.OutputStream;

interface BEROutputStream {

	void write(int i) throws IOException;
	void write(byte[] b) throws IOException;
	void setOutputStream(OutputStream in);
	void close() throws IOException;;
}
