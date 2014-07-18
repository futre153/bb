package org.pabk.html;

public class Caption extends HtmlTag {
	private Caption(String caption) {this.appendChild(TextTag.getInstance(caption));}
	protected static Caption getInstance(String caption){return new Caption(caption);}
}
