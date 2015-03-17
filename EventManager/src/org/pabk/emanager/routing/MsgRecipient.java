package org.pabk.emanager.routing;

import org.w3c.dom.Element;

class MsgRecipient {
	
	private static final String CONDITION_ATT_NAME = "condition";
	
	private String condition;
	private String expression;
		
	private MsgRecipient(){}
	
	
	
	static MsgRecipient getRecipientItem(Element elem) {
		
		MsgRecipient msg=new MsgRecipient();
		msg.setCondition(elem.getAttribute(CONDITION_ATT_NAME));
		msg.setExpression(Recipients.getNoteTextValue(elem));
		return msg;
	}



	String getCondition() {return condition;}
	void setCondition(String condition) {this.condition = condition;}

	String getExpression() {return expression;}
	void setExpression(String expression) {this.expression = expression;}
}
