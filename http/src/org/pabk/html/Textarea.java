package org.pabk.html;

public class Textarea extends HtmlTag {
	
	private static final String COLS_ATT_NAME = "cols";
	private static final String ROWS_ATT_NAME = "rows";
	private static final String MAXLENGTH_ATT_NAME = "maxlength";
	
	public static Textarea getInstance(int cols, int rows) {
		return getInstance(cols, rows, -1);
	}
	
	public static Textarea getInstance(int cols, int rows, int max) {
		Textarea textarea = new Textarea();
		if(cols > 0) {
			textarea.setAttribute(COLS_ATT_NAME, Integer.toString(cols));
		}
		if(rows > 0) {
			textarea.setAttribute(ROWS_ATT_NAME, Integer.toString(rows));
		}
		if(max > 0) {
			textarea.setAttribute(MAXLENGTH_ATT_NAME, Integer.toString(max));
		}
		return textarea;
	}
}
