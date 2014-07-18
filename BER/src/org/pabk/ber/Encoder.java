package org.pabk.ber;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Encoder {
	void setInputStream(InputStream in);
	void setOutputStream(OutputStream out);
	void write(int bt) throws IOException;
	void write(byte[] b) throws IOException;
	int read() throws IOException;
	int read(byte[] b) throws IOException;
	void close();
	void retBytes(byte[] b) throws IOException;
	void setLevel(int level);
	int getLevel();
}
