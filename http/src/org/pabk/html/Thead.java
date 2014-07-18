package org.pabk.html;

public class Thead extends HtmlTag {
	private Thead(String[] header) {
		Tr row=Tr.getInstance();
		for(int i=0;i<header.length;i++) {row.appendChild(Th.getInstance(header[i]));}
		this.appendChild(row);
	}
	
	protected static Thead getInstance(String[] header) {return new Thead(header);}
}
