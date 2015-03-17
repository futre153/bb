package org.pabk.emanager.routing;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class RoutingPoint {
	private static final String REGEX_ATT_NAME = "regExp";
	private static final String GROUP_NODE_NAME = "group";
	private String regExp=null;
	private String groups=null;
	
	RoutingPoint(String e,String g) {
		setRegExp(e);
		setGroups(g);
	}

	public String getRegExp() {
		return regExp;
	}

	public void setRegExp(String regExp) {
		this.regExp = regExp;
	}

	public String getGroups() {
		return groups;
	}

	public void setGroups(String groups) {
		this.groups = groups;
	}

	public static RoutingPoint getRoutingPoint(Element elem) {
		String regex=elem.getAttribute(REGEX_ATT_NAME);
		StringBuffer grps=new StringBuffer();
		NodeList nodes=elem.getChildNodes();
		for(int i=0;i<nodes.getLength();i++) {
			Node node=nodes.item(i);
			if(node.getNodeType()==Node.ELEMENT_NODE) {
				if(((Element)node).getTagName().equals(GROUP_NODE_NAME)) {
					if(grps.length()>0)grps.append(',');
					grps.append(Recipients.getNoteTextValue(node));
				}
			}
		}
		return new RoutingPoint(regex,grps.toString());
	}
}
