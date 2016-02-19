package org.pabk.util.interpreter;

import java.io.InputStream;
import java.io.OutputStream;

public interface Interpreter {
	void interpret (InputStream in, OutputStream out);
	String getMessage();
	
}
