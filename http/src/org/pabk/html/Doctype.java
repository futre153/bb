package org.pabk.html;

import java.io.PrintWriter;

public class Doctype extends HtmlTag {
	private static final CharSequence HTML5=
			"<!DOCTYPE HTML>";
	private static final CharSequence STRICT=
			"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">";
	private static final CharSequence TRANSITIONAL=
			"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">";
	private static final CharSequence FRAMESET=
			"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Frameset//EN\" \"http://www.w3.org/TR/html4/frameset.dtd\">";
	private static final CharSequence XHTML_STRICT=
			"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">";
	private static final CharSequence XHTML_TRANSITIONAL=
			"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">";
	private static final CharSequence XHTML_FRAMESET=
			"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Frameset//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd\">";
	private static final CharSequence HTML3=
			"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">";
	private static final CharSequence HTML2=
			"<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML//EN\">";
	
	public static final int HTML_4_01_STRICT=0;
	public static final int HTML_4_01_TRANSITIONAL=1;
	public static final int HTML_4_01_FRAMESET=2;
	public static final int XHTML_1_0_STRICT=3;
	public static final int XHTML_1_0_TRANSITIONAL=4;
	public static final int XHTML_1_0_FRAMESET=5;
	public static final int HTML_3_2=6;
	public static final int HTML_2_0=7;
	public static final int HTML_5=8;
	
	private static final int DEFAULT_DOCTYPE = HTML_4_01_TRANSITIONAL;
	private static final CharSequence EMPTY = "";
	
	private int currentDoctype;
	
	private Doctype(){}
	
	protected void setType(int i) {this.currentDoctype=i;}
	
	protected static Doctype getInstance() {
		return getInstance(Doctype.DEFAULT_DOCTYPE);
	}
	protected static Doctype getInstance(int i) {
		Doctype tag=new Doctype();
		tag.setType(i);
		return tag;
	}
	
	public void doFinal(PrintWriter out, int c) {
		out.append(System.getProperty("line.separator"));
		HtmlTag.setLeftTabs(out, c);
		out.append(getType());
	}
	private CharSequence getType() {
		switch(currentDoctype) {
		case HTML_4_01_STRICT:return STRICT;
		case HTML_4_01_TRANSITIONAL:return TRANSITIONAL;
		case HTML_4_01_FRAMESET:return FRAMESET;
		case XHTML_1_0_STRICT:return XHTML_STRICT;
		case XHTML_1_0_TRANSITIONAL:return XHTML_TRANSITIONAL;
		case XHTML_1_0_FRAMESET:return XHTML_FRAMESET;
		case HTML_3_2:return HTML3;
		case HTML_2_0:return HTML2;
		case HTML_5:return HTML5;
		default:return EMPTY;
		}
	}
	
	public boolean isXHtml() {
		return currentDoctype==XHTML_1_0_STRICT||currentDoctype==XHTML_1_0_TRANSITIONAL||currentDoctype==XHTML_1_0_FRAMESET;
	}
	
	public boolean isHtml5() {
		return currentDoctype==HTML_5;
	}
}
