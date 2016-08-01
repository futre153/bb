package org.pabk.html;

public class Td extends TableCell {
	
	private Td () {}
	
	protected static Td getInstance(Object cell) {
		Td td = new Td();
		td.setContent(cell);
		return td;
	}
}
