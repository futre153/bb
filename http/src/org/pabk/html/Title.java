package org.pabk.html;

public class Title extends HtmlTag {

	public static final String TAGNAME = "title";

	protected Title(String title) {appendChild(TextTag.getInstance(title));}

	protected static Tag getInstance(String title) {return new Title(title);}

}
