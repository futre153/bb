package org.pabk.html;

public class Option extends HtmlTag{

	public static Option getInstance(String text, String value, boolean selected) {
		Option opt=new Option();
		text=text==null?text="null":text;
		value=value==null?text.toLowerCase():value;
		opt.setAttribute("value", value);
		if(selected) opt.setAttribute("selected", "");
		opt.appendChild(TextTag.getInstance(text));
		return opt;
	}

	
}
