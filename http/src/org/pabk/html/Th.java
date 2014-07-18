package org.pabk.html;

public class Th extends HtmlTag implements TableCell {
	private Th(String text) {
		TextTag tt=TextTag.NBSP;
		if(text!=null)tt=TextTag.getInstance(text);
		this.appendChild(tt);
	}
	protected static Th getInstance(String text){return new Th(text);}
}
