package org.pabk.net.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

abstract interface Content {
	void setContent(Object paramObject);
	long getLength() throws IOException;
	void setContentType(int contentType);
	String getContentType();
	void setCharacterEncoding(int charEncoding);
	String getCharacterEncoding();
	void setContentEncoding(int contentEncoding);
	String getContentEncoding();
	void setChunked(boolean chunked);
	boolean isChunked();
	void setAdditionalProperty(String key, String value);
	void applyAdditionalProperties(HttpURLConnection con);
	void doFinal(OutputStream out) throws IOException;
}