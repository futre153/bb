package org.pabk.html;

public class Link extends HtmlTag {
	public static final int STYLEHEET = 0;
	private static final String RELATIONSHIP =			"rel";
	private static final String TYPE =					"type";
	private static final String HYPERTEXT_REFERENCE =	"href";

	private Link(int type, String[] att) {
		isShort=true;
		setAtributes(type, att);
	}

	private void setAtributes(int type, String[] att) {
		switch (type) {
		case STYLEHEET: setStylesheet(att);break;
		default: throw new UnsupportedOperationException("Link type "+type+" is not supported");
		}
	}

	private void setStylesheet(String[] att) {
		atts.put(RELATIONSHIP, att[0]);
		atts.put(TYPE, att[1]);
		atts.put(HYPERTEXT_REFERENCE, att[2]);
	}
	
	protected static Link getInstance(int type, String[] att) {return new Link(type,att);}
}
