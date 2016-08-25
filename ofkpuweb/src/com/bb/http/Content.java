package com.bb.http;

import java.io.IOException;
import java.io.InputStream;

interface Content {
	void setInputStream (InputStream in);
	void setContentType (String type) throws IOException;
	void setContentDisposition (String disp) throws IOException;
	void setContentLength (long length);
}
