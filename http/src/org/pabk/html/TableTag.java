package org.pabk.html;

interface TableTag {
	void setCaption(String caption);
	void setColgroup(String ...classes);
	Caption removeCaption();
	Caption getCaption();
	void setHeader(String[] header);
	Thead removeHeader();
	Thead getHeader();
	void setFooter(String[] footerValue);
	Tfoot removeFooter();
	Tfoot getFooter();
	void setEmptyTableBody(int width,int height);
	void clearTableBody();
	void setTableBody(Tbody tbody);
	Tbody getTableBody();
	TableRow[] getRows();
	boolean setContent(Tag tag, int x, int y);
}
