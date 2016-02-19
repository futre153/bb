package org.pabk.html;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public interface Tag {
	void doFinal(PrintWriter out, int counter) throws IOException;
	String getTagName();
	String getAttribute(String name);
	void setAttribute(String key, String value);
	void removeAttribute(String name);
	ArrayList<Tag> getElementsByTagName(String name);
	Tag lastChild();
	boolean isTextTag();
	void appendChild(Tag tag);
	void appendChild(int index, Tag tag);
	void appendChildAtStart(Tag tag);
	void appendChildBefore(Tag tag, String classname);
	void appendChildAfter(Tag tag, String classname);
	boolean isXHtml();
	Tag removeChild(Tag tag);
	void appendText(String text);
	ArrayList<Tag> getChildren();
	Tag getParent();
	boolean isInline();
	void setInline(boolean inline);
}
