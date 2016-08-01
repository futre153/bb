package org.pabk.html;

public class Tbody extends TableContentImpl {
	static Tbody getInstance(Object... bodyValue) {
		Tbody tbody = new Tbody();
		tbody.addRow(bodyValue);
		return tbody;
	}
	
	static Tbody getInstance(String... bodyValue) {
		Tbody tbody = new Tbody();
		tbody.addRow(bodyValue);
		return tbody;
	}

}
