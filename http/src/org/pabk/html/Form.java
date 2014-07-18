package org.pabk.html;

public class Form extends HtmlTag {
	private static final String ACTION = "action";
	private static final String METHOD = "method";

	private Form(String action, String method) {
		this.setAttribute(ACTION, action);
		if(method!=null)this.setAttribute(METHOD, method);
	}
	
	public static Form getInstance(String action, String method) {return new Form(action, method);}
}
