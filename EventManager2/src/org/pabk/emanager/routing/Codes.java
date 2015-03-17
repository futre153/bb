package org.pabk.emanager.routing;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class Codes extends ArrayList<CodeItem>{
	private static final long serialVersionUID = 1L;
	private static final String CODE_NODE_NAME = "code";
	private static final String CODE_NUMBER_NODE_NAME = "code-number";
	private static final String CODE_DESCRIPTION_NODE_NAME = "code-description";
	private static final String UNKNOWN_CODE_DESCRIPTION = "bez popisu";
	private static Codes codes;
	
	public static void init(Element elem) {
		NodeList nodes=elem.getChildNodes();
		Codes cods=getInstance();
		for(int i=0;i<nodes.getLength();i++) {
			Node node=nodes.item(i);
			if(node.getNodeType()==Node.ELEMENT_NODE) {
				if(((Element)node).getTagName().equals(Codes.CODE_NODE_NAME)) {
					cods.add(Codes.getCodeItem((Element) node));
				}
			}
		}
		
	}

	private static CodeItem getCodeItem(Element elem) {
		NodeList nodes=elem.getChildNodes();
		CodeItem c=new CodeItem();
		for(int i=0;i<nodes.getLength();i++) {
			Node node=nodes.item(i);
			if(node.getNodeType()==Node.ELEMENT_NODE) {
				if(((Element)node).getTagName().equals(Codes.CODE_NUMBER_NODE_NAME)) {
					c.setCodeNumber(Recipients.getNoteTextValue(node));
				}
				else if(((Element)node).getTagName().equals(Codes.CODE_DESCRIPTION_NODE_NAME)) {
					c.setDescription(Recipients.getNoteTextValue(node));
				}
			}
		}
		return c;
	}

	private static Codes getInstance() {
		codes=new Codes();
		return codes;
	}
	
	static String getCodeDescription(String code) {
		for(int i=0;i<codes.size();i++) {
			//System.out.println(codes.get(i).getCodeNumber()+"="+code);
			if(codes.get(i).getCodeNumber().equals(code))return codes.get(i).getDescription();
		}
		return UNKNOWN_CODE_DESCRIPTION;
	}
	
}
