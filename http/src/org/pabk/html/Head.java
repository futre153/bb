package org.pabk.html;

import java.util.ArrayList;

public class Head extends HtmlTag {
	
	private final ArrayList<Meta> metadata=new ArrayList<Meta>();
	private final ArrayList<Link> link=new ArrayList<Link>();
	private final ArrayList<Script> script=new ArrayList<Script>();
	
	private Head(){}
	
	protected static Head getInstance() {return new Head();}
	
	protected void addMetadata(String httpEquiv, String name, String scheme, String content, String charset) {
		Meta meta=Meta.getInstance(httpEquiv, name, scheme, content, charset);
		metadata.add(0, meta);
		appendChildAtStart(meta);
	}
	
	protected void addLink(int type, String[] att) {
		Link l=Link.getInstance(type, att);
		link.add(0,l);
		appendChildAfter(l,Title.TAGNAME);
	}
	
	protected void addScript(String type, Object src) {
		Script s=Script.getInstance(type, src);
		script.add(s);
		appendChild(s);
	}
	
	protected void addScript(String type, Object src, String charset) {
		Script s=Script.getInstance(type, src, charset);
		script.add(s);
		appendChild(s);
	}
	
}
