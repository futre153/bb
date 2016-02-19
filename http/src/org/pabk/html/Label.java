package org.pabk.html;

public class Label extends HtmlTag {
	private static final String FOR = "for";
	private Label(String forTagId) {
		this.setAttribute(FOR, forTagId);
	}
	public static Label getInstance(String forTagId) {return new Label(forTagId);}
}
