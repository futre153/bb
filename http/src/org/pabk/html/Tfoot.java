package org.pabk.html;

public class Tfoot extends TableContentImpl {

	static Tfoot getInstance(Object... footerValue) {
		Tfoot tfoot = new Tfoot();
		tfoot.addRow(footerValue);
		return tfoot;
	}
	
	static Tfoot getInstance(String... footerValue) {
		Tfoot tfoot = new Tfoot();
		tfoot.addRow(footerValue);
		return tfoot;
	}
}
