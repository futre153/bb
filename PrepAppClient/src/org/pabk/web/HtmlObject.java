package org.pabk.web;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;

interface HtmlObject {
	String HTML_OBJECT_EXTENSION = "HtmlObject";
	Object getObject();
	void doFinal(Closeable out) throws IOException;
	HtmlObject action(Map<String, String[]> map);
}
