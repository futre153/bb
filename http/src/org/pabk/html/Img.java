package org.pabk.html;

public class Img extends HtmlTag {
	private Img(String url, String alt, int width, int height){
		this.setAttribute("src", url);
		this.setAttribute("alt", alt);
		if(height>=0)this.setAttribute("height", Integer.toString(height));
		if(width>=0)this.setAttribute("width", Integer.toString(width));
		isShort=true;
	}
	public static Img getInstance(String url){return getInstance(url,url,-1,-1);}
	public static Img getInstance(String url, int width, int height) {return getInstance(url,url,width,height);}
	public static Img getInstance(String url, String alt, int width, int height) {return new Img(url,alt,width,height);} 
}
