package org.pabk.html;

public class Iframe extends HtmlTag {
	private Iframe(String id, String url) {
		this.setAttribute("id", id);
		this.setAttribute("src", url);
	}
	public static Iframe getInstance(String id, String url) {return new Iframe(id, url);}
}
