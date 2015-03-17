package org.pabk.rpc.datatypes;

import java.io.IOException;

public interface Encoder {
	byte[] encode(Object obj) throws IOException;
	void decode(Object obj, byte[] b) throws IOException;
}
