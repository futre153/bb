package org.pabk.html;

public class Th extends TableCell {
	
	private Th () {}
	
	protected static Th getInstance(Object cell) {
		Th th = new Th();
		th.setContent(cell);
		return th;
	}
}
