package org.pabk.html;

public class Meta extends HtmlTag {
	private static final String HTTP_EQUIV = "http-equiv";
	private static final String NAME = "name";
	private static final String CONTENT = "content";
	private static final String SCHEME = "scheme";
	private static final String CHARSET = "charset";
	private Meta(String httpEquiv, String name, String scheme, String content, String charset) {
		isShort=true;
		if(charset == null) {
			if(httpEquiv!=null)atts.put(HTTP_EQUIV, httpEquiv);
			if(name!=null)atts.put(NAME, name);
			if(scheme!=null)atts.put(SCHEME, scheme);
			if(content!=null)atts.put(CONTENT, content);
			else throw new UnsupportedOperationException ("Attribute content is required for tag meta");
		}
		else {
			atts.put(CHARSET, charset);
		}
	}
	protected static Meta getInstance (String h, String n, String s, String c, String charset){return new Meta(h,n,s,c,charset);}
}
