package org.pabk.emanager.routing;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class RoutingCondition extends ArrayList<RoutingPoint> {
	

	private static final long serialVersionUID = 1L;
	private static final String COND_NAME_ATT_NAME = "name";
	private static final String COND_PRIORITY_ATT_NAME = "priority";
	private static final String COND_ITEM_NODE_NAME = "condition-item";
	private String name=null;
	private int priority=-1;
	
	RoutingCondition (String n, int p) {
		super();
		setName(n);
		setPriority(p);
	}

	String getName() {return name;}
	void setName(String name) {this.name = name;}

	int getPriority() {return priority;}
	void setPriority(int priority) {this.priority = priority;}

	static RoutingCondition getCondition(Element elem) {
		String name=elem.getAttribute(COND_NAME_ATT_NAME);
		int priority=Integer.parseInt(elem.getAttribute(COND_PRIORITY_ATT_NAME));
		RoutingCondition rc=new RoutingCondition(name,priority);
		NodeList nodes=elem.getChildNodes();
		for(int i=0;i<nodes.getLength();i++) {
			Node node=nodes.item(i);
			if(node.getNodeType()==Node.ELEMENT_NODE) {
				if(((Element)node).getTagName().equals(COND_ITEM_NODE_NAME)) {
					rc.add(RoutingPoint.getRoutingPoint((Element) node));
				}
			}
		}
		return rc;
	}
	
}
