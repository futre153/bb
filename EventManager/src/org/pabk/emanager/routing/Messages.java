package org.pabk.emanager.routing;

import java.util.ArrayList;
import java.util.Hashtable;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class Messages {
	
	private static final ArrayList<Message> templates=new ArrayList<Message>();
	private static final String MSG_TEMPLATE_NODE_NAME = "message-template";
	private static final String ENCODING_ATT_NAME = "encoding";
	private static String defaultEncoding="utf-8";	
	
	private Messages(){}

	public static void init(Element con) {
		String enc=con.getAttribute(ENCODING_ATT_NAME);
		if(enc==null || enc.length()==0)setDefaultEncoding(enc);
		NodeList nodes=con.getChildNodes();
		for(int i=0;i<nodes.getLength();i++) {
			Node node=nodes.item(i);
			if(node.getNodeType()==Node.ELEMENT_NODE) {
				if(((Element)node).getTagName().equals(Messages.MSG_TEMPLATE_NODE_NAME)) {
					templates.add(Message.getMsgTemplate((Element) node));
				}
			}
		}
		
	}

	public static String getDefaultEncoding() {
		return defaultEncoding;
	}

	public static void setDefaultEncoding(String defaultEncoding) {
		Messages.defaultEncoding = defaultEncoding;
	}

	static Message findMessage(Hashtable<String, Object> join) {
		String id=(String) join.get(Distribution.MESSAGE_ID_KEY);
		//System.out.println(id);
		//System.out.println(templates.size());
		for(int i=0;i<templates.size();i++) {
			if(templates.get(i).getClassName().equals(id)){
				if(templates.get(i).checkCondition(join))return templates.get(i);
			}
		}
		return null;
	}
}
