package org.pabk.html;

interface TableTag {
	
	void setCaption(String caption);
	Caption getCaption();
	Caption removeCaption();
	
	void setColgroup(String ...classes);
	Colgroup getColgroup();
	Colgroup removeColgroup();
	
	void setHeader(String... headerValue);
	void setHeader(Object... headerValue);
	Thead getHeader();
	Thead removeHeader();
	
	void setBody(String... bodyValue);
	void setBody(Object... bodyValue);
	Tbody getBody();
	Tbody removeBody();
	
	void setFooter(String... footerValue);
	void setFooter(Object... footerValue);
	Tfoot getFooter();
	Tfoot removeFooter();
	
	void setEmptyTableBody(int width,int height);
	void clearTableBody();
	void setTableBody(Tbody tbody);
	Tbody getTableBody();
	TableRow[] getRows();
	boolean setContent(Tag tag, int x, int y);
}
