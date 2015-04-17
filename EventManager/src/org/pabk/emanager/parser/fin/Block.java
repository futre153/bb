package org.pabk.emanager.parser.fin;

import java.io.IOException;
import java.io.InputStreamReader;

interface Block {
	String getBlockIdentifier() throws IOException;
	void setBlockIdentifier(String bi) throws IOException;
	void parse(InputStreamReader in) throws IOException;
	Object getBlockContent();
}
