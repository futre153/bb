package org.pabk.html;

import java.io.IOException;
import java.io.PrintWriter;


public class Html extends HtmlTag {
		
	private Doctype doctype;
	private Head head;
	private Body body;
	
	private Html() {}
	
	public static Html getInstance(String title) {
		Html tag=new Html();
		tag.setDoctype();
		tag.setHead();
		tag.head.appendChild(Title.getInstance(title));
		tag.appendChild(tag.head);
		tag.setBody();
		tag.appendChild(tag.body);
		return tag;
	}

	public Doctype getDoctype() {return doctype;}

	private void setDoctype() {doctype=Doctype.getInstance();}
	
	public void setDoctype(int i) {doctype.setType(i);}

	public Head getHead() {return head;}

	private void setHead() {this.head = Head.getInstance();}

	public Body getBody() {return body;}

	private void setBody() {this.body = Body.getInstance();}
	
	public void doFinal(PrintWriter out, int c) throws IOException {
		doctype.doFinal(out, c);
		super.doFinal(out, c);
		out.flush();
	}
	
	public void addMetadata(String httpEquiv, String name, String scheme, String content, String charset) {
		this.getHead().addMetadata(httpEquiv, name, scheme, content, charset);
	}

	public void doFinal(PrintWriter out) throws IOException {
		doFinal(out,0);
	}
	
	public void addScript(String type, Object obj) {this.getHead().addScript(type, obj);}
	
	public void addScript(String type, Object obj, String charset) {this.getHead().addScript(type, obj, charset);}
	
	public void addLink(int type, String[] obj) {this.getHead().addLink(type, obj);}
	
}
