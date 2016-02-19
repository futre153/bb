package org.pabk.html;

class Col extends HtmlTag {
	protected Col(String className){
		this.setClassName(className);
		this.hasEndTag = false;
		this.isShort = true;
	}
	public static Col getInstance(String className){return new Col(className);}
}
