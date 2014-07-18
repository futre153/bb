package org.pabk.html;

import java.io.IOException;
import java.io.PrintWriter;

public class Input extends HtmlTag {
	private static final String NAME = "name";
	private static final String TYPE = "type";
	private static final String VALUE = "value";
	private Input(String name, String type, String value) {
		if(name!=null){this.setAttribute(NAME, name);}
		if(type!=null){this.setAttribute(TYPE, type);}
		if(value!=null){this.setAttribute(VALUE, value);}
		hasEndTag=false;
		isShort=true;
	}
	
	public static Input getInstance(String name, String type, String value) {return new Input(name,type,value);}
	
	public void doFinal (PrintWriter out, int c) throws IOException  {
		boolean tmp=hasEndTag;
		hasEndTag=hasEndTag|isXHtml();
		super.doFinal(out, c);
		hasEndTag=tmp;
	}
}
