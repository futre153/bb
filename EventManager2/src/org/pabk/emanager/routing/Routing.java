package org.pabk.emanager.routing;

import java.util.ArrayList;
import java.util.Hashtable;

import org.pabk.emanager.parser.TextParser;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class Routing {
	
	private static final ArrayList<RoutingCondition> routing=new ArrayList<RoutingCondition>();
	private static final String CONDITION_NODE_NAME = "condition";
		
	private Routing(){}

	static void init(Element con) {
		NodeList nodes=con.getChildNodes();
		for(int i=0;i<nodes.getLength();i++) {
			Node node=nodes.item(i);
			if(node.getNodeType()==Node.ELEMENT_NODE) {
				if(((Element)node).getTagName().equals(Routing.CONDITION_NODE_NAME)) {
					routing.add(RoutingCondition.getCondition((Element) node));
				}
			}
		}
		
	}

	static Recipients apply(Hashtable<String, Object> join, MsgRecipient rec) {
		RoutingCondition rc=Routing.getCondition(rec.getCondition());
		Recipients recs=Recipients.getInstance();
		for(int i=0;i<rc.size();i++) {
			if((new String(TextParser.parseExpression(rec.getExpression(),join))).matches(rc.get(i).getRegExp())) {
				String[] grps=rc.get(i).getGroups().split(",");
				for(i=0;i<grps.length;i++) {
					recs=Recipients.join(recs, Recipients.getGroup(grps[i]));
				}
				break;
			}
		}
		return recs;
		
	}
	
	static RoutingCondition getCondition(String condition) {
		for(int i=0; i<routing.size();i++) {
			if(routing.get(i).getName().equals(condition))return routing.get(i);
		}
		return null;
	}
	
}
