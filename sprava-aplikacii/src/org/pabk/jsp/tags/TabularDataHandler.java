package org.pabk.jsp.tags;

import java.util.Map;

interface TabularDataHandler {
	
	void setSource(Object src);
	String[] getHeader();
	Map<String, Object> getNext();
	boolean hasNext();
	void close();
}
