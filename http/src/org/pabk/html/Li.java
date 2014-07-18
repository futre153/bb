package org.pabk.html;

public class Li extends HtmlTag {
	private Li(String item) {this.appendChild(TextTag.getInstance(item));}
	public static Li getInstance(String item) {return new Li(item);}
}
