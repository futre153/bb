package org.pabk.rpc.datatypes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface RpcDatatype {
	Object getValue();
	void setValue(Object nr);
	void encode(OutputStream out) throws IOException;
	byte[] decode(InputStream in) throws IOException;
}
