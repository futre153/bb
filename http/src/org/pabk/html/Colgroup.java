package org.pabk.html;

class Colgroup extends HtmlTag {
	private Colgroup (String ...classNames) {
		for(int i = 0; i < classNames.length; i ++) {
			this.appendChild(Col.getInstance(classNames[i]));
		}
	}
	protected static Colgroup getInstance(String ...classNames){return new Colgroup(classNames);}
}
