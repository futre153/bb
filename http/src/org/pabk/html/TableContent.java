package org.pabk.html;

interface TableContent {
	Tr[] getRows();
	Tr[] removeAll();
	Tr removeRow(int index) throws IndexOutOfBoundsException;
	void addRow(Object ... objs);
	void insertRow(int index, Object ... objs) throws IndexOutOfBoundsException;
	void addRow(String ... objs);
	void insertRow(int index, String ... objs) throws IndexOutOfBoundsException;
}
