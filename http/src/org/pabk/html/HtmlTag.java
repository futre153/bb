package org.pabk.html;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import org.w3c.dom.DOMException;

public class HtmlTag implements Tag {
	private static final CharSequence LEFT_UNIT_TAB = "  ";
	private static final String CLASS_NAME = "class";
	private static final String IDENTIFICATION = "id";
	public static final String EMPTY_ATT_VALUE = "___EMPTY";
	private static final String EMPTY = "";
	protected boolean isShort=false;
	protected boolean hasEndTag=true;
	protected final Hashtable<String, String> atts=new Hashtable<String, String>();
	protected final ArrayList<Tag> children=new ArrayList<Tag>();
	protected Tag parent;
	protected boolean inline = false;
	
	public boolean isInline() {return inline;}
	public void setInline(boolean inline) {
		this.inline = inline;
	}
	
	@Override
	public void doFinal(PrintWriter out, int c) throws IOException {
		boolean is=isShort&(children.size()==0);
		if(! isInline()) {
			out.append(System.getProperty("line.separator"));
			HtmlTag.setLeftTabs(out, c);
		}
		out.append('<');
		out.append(this.getTagName());
		Iterator<String> keys=atts.keySet().iterator();
		while(keys.hasNext()) {
			String key=keys.next();
			out.append(' ');
			out.append(key);
			String value=atts.get(key);
			if(value.length()>0) {
				out.append('=');
				out.append('\'');
				String val = atts.get(key);
				val = val.equals(EMPTY_ATT_VALUE) ? EMPTY : val; 
				out.append(val);
				out.append('\'');
			}
		}
		if(is) {
			if(hasEndTag)out.append('/');
			out.append('>');
			return;
		}
		out.append('>');
		c++;
		for(int i=0;i<children.size();i++) {
			children.get(i).doFinal(out, c);
		}
		c--;
		if(children.size()!=0) {
			if(!lastChild().isTextTag()) {
				if(! isInline()) {
					out.append(System.getProperty("line.separator"));
					HtmlTag.setLeftTabs(out, c);
				}
			}
		}
		out.append('<');
		out.append('/');
		out.append(this.getTagName());
		out.append('>');
	}
	
	public static void setLeftTabs(PrintWriter out, int c) {for(int i=0;i<c;i++) {out.append(LEFT_UNIT_TAB);}}
	
	public boolean isTextTag() {return getClass().getSimpleName().toLowerCase().contains("text");}
	
	public Tag lastChild() {
		if(children.size()==0) return null;
		return children.get(children.size()-1);
	}
	
	public String getTagName() {return this.getClass().getSimpleName().toLowerCase();}
	
	public String getAttribute(String name) {return atts.get(name);}
	
	public void setAttribute(String name, String value) {atts.put(name, value);}
	
	public void removeAttribute(String name) throws DOMException {atts.remove(name);}
	
	public ArrayList<Tag> getElementsByTagName(String name) {
		ArrayList<Tag> tmp=new ArrayList<Tag>();
		for(int i=0;i<children.size();i++) {
			if(children.get(i).getTagName().equals(name.toLowerCase())) {
				tmp.add(children.get(i));
			}
		}
		return tmp;
	}
	
	public ArrayList<Tag> getElementsByTagName2(String name) {
		//ArrayList<Tag> tmp=new ArrayList<Tag>();
		return getElementsByTagName(name, new ArrayList<Tag>());
	}
	
	
	private ArrayList<Tag> getElementsByTagName(String name, ArrayList<Tag> a) {
		for(int i=0;i<this.getChildren().size();i++) {
			if(this.getTagName().equals(name.toLowerCase())) a.add(this);
			((HtmlTag)this.getChildren().get(i)).getElementsByTagName(name, a);
		}
		return a;
	}

	@Override
	public void appendChild(Tag tag) {
		children.add(tag);
		setParent(tag);
	}

	private final void setParent(Tag tag) {((HtmlTag)tag).parent=this;}

	@Override
	public boolean isXHtml() {
		Tag tag=this;
		while(tag.getParent()!=null) {
			tag=tag.getParent();
		}
		if(tag instanceof Html) return ((Html) tag).getDoctype().isXHtml();
		// Default is HTML
		return false;
	}

	@Override
	public void appendChildBefore(Tag tag, String classname) {
		int i=0;
		for(;i<children.size();i++) {if(children.get(i).getClass().getSimpleName().equalsIgnoreCase(classname)) {i++;break;}}
		i--;
		appendChild(i, tag);
		setParent(tag);
	}

	@Override
	public Tag removeChild(Tag tag) throws IndexOutOfBoundsException {return children.remove(children.indexOf(tag));}

	@Override
	public void appendChild(int index, Tag tag) {
		children.add(index, tag);
		setParent(tag);
	}

	@Override
	public void appendChildAtStart(Tag tag) {
		children.add(0, tag);
		setParent(tag);
	}

	@Override
	public void appendChildAfter(Tag tag, String classname) {
		int i=children.size()-1;
		for(;i>=0;i--) {if(children.get(i).getClass().getSimpleName().equalsIgnoreCase(classname)) {i++;break;}}
		if(i<0) {appendChild(tag);}
		else {appendChild(i,tag);}
		setParent(tag);
	}

	@Override
	public void appendText(String text) {
		Tag tag=TextTag.getInstance(text);
		appendChild(tag);
		TextTag.getInstance(text);
	}

	@Override
	public ArrayList<Tag> getChildren() {return children;}
	
	public String toString() {
		ByteArrayOutputStream out=new ByteArrayOutputStream();
		PrintWriter pw=new PrintWriter(out);
		String tmp="";//super.toString();
		try {
			this.doFinal(pw, 0);
			pw.flush();
			pw.close();
			tmp+=out.toString();
			out.close();
		} catch (IOException e) {}
		return tmp+System.getProperty("line.separator");
	}
	
	public void setClassName(String name) {this.setAttribute(CLASS_NAME, name);}
	public void setId(String id) {this.setAttribute(IDENTIFICATION, id);}

	@Override
	public Tag getParent() {return parent;}
		
}
