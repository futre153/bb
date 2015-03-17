package org.pabk.http.tserver.context;

import java.io.IOException;
import java.io.OutputStream;

public interface Page {
	int getLength();
	void writeBody(OutputStream out, Object body) throws IOException;
	void setPage(Object ...objs);
}
