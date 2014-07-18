package org.pabk.html;

import java.io.PrintWriter;

public class TextTag extends HtmlTag {
	protected CharSequence value;
	protected static final TextTag CRLF = TextTag.getInstance(System.getProperty("line.separator"));
	private static final String NO_BREAK_SPACE = "&#160;";
	public static final TextTag NBSP = TextTag.getInstance(NO_BREAK_SPACE);
	private TextTag(String value) {this.value=value;}
	public void doFinal (PrintWriter out, int c) {out.append(value);}
	public static TextTag getInstance(String text) {return new TextTag(text);}
	public static TextTag getInstance(String msk, String rpl, String text) {return getInstance(text.replaceAll(msk , rpl));}
	public static TextTag getInstanceNBSP(String text) {return getInstance(" ",NO_BREAK_SPACE,text);}
}
