package org.pabk.html;

public class Button extends HtmlTag {
	
	private Button (String value) {
		this.appendChild(TextTag.getInstance(value));
	}
	
	public static Button getInstance(String value) {return new Button(value);}
	
}