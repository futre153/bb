package org.pabk.html;


public class A extends HtmlTag {
	protected A(){}
	public A(String url) {
		this.setAttribute("href", url);
		this.appendChild(TextTag.getInstance(url));
	}
	public static A getInstance(String url){return new A(url);}
}
