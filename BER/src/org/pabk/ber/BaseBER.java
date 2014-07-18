package org.pabk.ber;

import java.io.IOException;

interface BaseBER {
	Object getValue() throws UnsupportedOperationException;
	void decode(Encoder en, long l) throws IOException;
	void encode(Encoder en) throws IOException;
	long length() throws UnsupportedOperationException;
	byte[] getBytes() throws UnsupportedOperationException;
	void clearContent();
}
