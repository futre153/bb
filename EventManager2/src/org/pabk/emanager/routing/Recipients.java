package org.pabk.emanager.routing;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class Recipients extends ArrayList<Recipient> {
	
	

	private static final long serialVersionUID = 1L;
	private static final String RECIPIENT_NODE_NAME = "name";
	private static final String RECIPIENT_ENABLED_ATT_NAME = "enabled";
	private static final ArrayList<Recipients> grpList = new ArrayList<Recipients>();
	private static final String DEFUALT_GROUP_NAME = "main";
	private static final String GROUP_NODE_NAME = "address-group";
	private static final String GROUP_NAME_ATT_NAME = "groupName";
	private static final String SEPARATOR_ATT_NAME = "separator";
	private static final String MAIN_ATT_NAME = DEFUALT_GROUP_NAME;
	private static final String DOMAIN_ATT_NAME = "domain";
	private static String separator=null;
	private static String domain=null;
	private static Recipient main=null;
	
	private String name=null;
	private Recipients() {			
	}
	
	public static void init(Element rec, Element grp) throws NullPointerException {
		grpList.clear();
		Recipients mainGroup=new Recipients();
		mainGroup.setName(DEFUALT_GROUP_NAME);
		grpList.add(mainGroup);
		NamedNodeMap atts= rec.getAttributes();
		String name=atts.getNamedItem(MAIN_ATT_NAME).getNodeValue();
		separator=atts.getNamedItem(Recipients.SEPARATOR_ATT_NAME).getNodeValue();
		domain=atts.getNamedItem(DOMAIN_ATT_NAME).getNodeValue();
		Recipients.setMain(new Recipient(name,separator,domain,false));
		NodeList nodes=rec.getChildNodes();
		for(int i=0;i<nodes.getLength();i++) {
			Node node=nodes.item(i);
			if(node.getNodeType()==Node.ELEMENT_NODE) {
				if(((Element)node).getTagName().equals(Recipients.RECIPIENT_NODE_NAME)) {
					name=Recipients.getNoteTextValue(node);
					String status=node.getAttributes().getNamedItem(Recipients.RECIPIENT_ENABLED_ATT_NAME).getNodeValue();
					mainGroup.add(new Recipient(name,separator,domain,Boolean.parseBoolean(status)));
				}
			}
		}
		nodes=grp.getChildNodes();
		for(int i=0;i<nodes.getLength();i++) {
			Node node=nodes.item(i);
			if(node.getNodeType()==Node.ELEMENT_NODE) {
				if(((Element)node).getTagName().equals(Recipients.GROUP_NODE_NAME)) {
					name=node.getAttributes().getNamedItem(Recipients.GROUP_NAME_ATT_NAME).getNodeValue();
					Recipients group=new Recipients();
					group.setName(name);
					grpList.add(group);
					NodeList nodes2=node.getChildNodes();
					for(int j=0;j<nodes2.getLength();j++) {
						Node node2=nodes2.item(j);
						if(node2.getNodeType()==Node.ELEMENT_NODE) {
							Recipient r=Recipients.findRecipient(Recipients.getNoteTextValue(node2));
							if(r!=null) group.add(r);
						}
					}
				}
			}
		}
		return;
	}

	private static Recipient findRecipient(String name) {
		Recipients list=Recipients.getGroup(Recipients.DEFUALT_GROUP_NAME);
		if(list!=null) {
			for(int i=0;i<list.size();i++) {
				if(list.get(i).equals(name))return list.get(i);
			}
		}
		return null;
	}

	static Recipients getGroup(String grpName) {
		for(int i=0;i<grpList.size();i++) {
			if(grpList.get(i).getName().equals(grpName))return grpList.get(i);
		}
		return getInstance();
	}

	static String getNoteTextValue(Node node) {
		StringBuffer buf=new StringBuffer();
		NodeList nodes=node.getChildNodes();
		for(int i=0;i<nodes.getLength();i++) {
			node=nodes.item(i);
			if(node.getNodeType()==Node.TEXT_NODE) {
				buf.append(node.getNodeValue());
			}
		}
		return buf.toString().trim();
	}

	public static Recipient getMain() {
		return main;
	}

	public static void setMain(Recipient main) {
		Recipients.main = main;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
		
	static Recipients getInstance() {return new Recipients();}

	static Recipients join(Recipients a, Recipients b) {
		Recipients c=(Recipients) a.clone();
		for(int i=0;i<b.size();i++) {
			if(!c.contains(b.get(i)))c.add(b.get(i));
		}
		return c;
	}

	static Recipients and(Recipients a, Recipients b) {
		Recipients c=Recipients.getInstance();
		for(int i=0;i<b.size();i++) {
			if(a.contains(b.get(i)))c.add(b.get(i));
		}
		return c;
	}

	static String[] toArray (Recipients recs) {
		String[] tmp=new String[recs.size()];
		for(int i=0;i<recs.size();i++) {
			tmp[i]=recs.get(i).getEMailAddress();
		}
		return tmp;
	}
}
