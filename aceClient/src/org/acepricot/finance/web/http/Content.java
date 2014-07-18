package org.acepricot.finance.web.http;

import java.io.IOException;

public interface Content {
	int getLength();
	byte[] getContent();
	void setContent(Object obj) throws UnsupportedOperationException, IOException;
	String getEncoding();
	String getType();
}
