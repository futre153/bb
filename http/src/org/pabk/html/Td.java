package org.pabk.html;

public class Td extends HtmlTag implements TableCell {
	
	private Td(String text) {
		TextTag tt=TextTag.NBSP;
		if(text!=null)tt=TextTag.getInstance(text);
		this.appendChild(tt);
	}
	protected static Td getInstance(String cell) {return new Td(cell);}
}
