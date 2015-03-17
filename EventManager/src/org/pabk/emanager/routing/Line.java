package org.pabk.emanager.routing;

import org.w3c.dom.Element;

class Line {
	
	private static final String CONDITION_ATT_NAME = "condition";
	static final Line EMPTY_LINE = new Line("");
	static final Line NO_RECIPIENT_WARNING = new Line("Táto správa bola odoslaná len Vám, prosím pošlite ju správnemu prijemcovi.");
	private String condition;
	private String text;
	
	private Line() {}
	private Line(String text){this.text=text;}
	
	public static Line getMsgLine(Element elem) {
		Line msg=new Line();
		msg.setCondition(elem.getAttribute(CONDITION_ATT_NAME));
		msg.setText(Recipients.getNoteTextValue(elem));
		return msg;
	}

	String getCondition() {return condition;}
	void setCondition(String condition) {this.condition = condition;}

	String getText() {return text;}
	void setText(String text) {this.text = text;}

}
