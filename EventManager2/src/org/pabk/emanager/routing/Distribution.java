package org.pabk.emanager.routing;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.pabk.emanager.parser.EMessageParser;
import org.pabk.emanager.parser.TextParser;
import org.pabk.emanager.util.Base64Coder;
import org.pabk.emanager.util.Sys;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class Distribution {
	
	private static final String DISTRIBUTION_LIST_TAG_NAME 		= "dls:distribution-list";
	private static final String RECIPIENTS_TAG_NAME 			= "dls:address-list";
	private static final String GROUPS_TAG_NAME 				= "dls:address-group-list";
	private static final String ROUTING_TAG_NAME 				= "dls:condition-list";
	private static final String MESSAGES_TAG_NAME 				= "dls:msg-list";
	
	private static final String RECIPIENT_TAG_NAME 				= "rec:recipient";
	private static final String RECIPIENT_ID_TAG_NAME 			= "rec:id";
	private static final String RECIPIENT_NAME_TAG_NAME 		= "rec:name";
	private static final String RECIPIENT_EMAIL_TAG_NAME 		= "rec:e-mail";
	private static final String RECIPIENT_ENABLED_TAG_NAME 		= "rec:enabled";
	private static final String MAIN_ID_ATTR 					= "rec:main";

	private static final String GROUP_TAG_NAME 					= "rgr:address-group";
	private static final String ID_TAG_NAME 					= "rgr:id";
	private static final String GROUP_NAME_ATTR 				= "rgr:name";
	
	private static final String CONDITION_ITEM_TAG_NAME 		= "cnl:condition-item";
	private static final String CONDITION_GROUP_TAG_NAME 		= "cnl:group";
	private static final String CONDITION_ITEM_REGEXP_ATTR 		= "cnl:regExp";
	private static final String CONDITION_TAG_NAME				= "cnl:condition";
	private static final String CONDITION_NAME_ATTR 			= "rgr:name";
	private static final String CONDITION_PRIORITY_ATTR 		= "cnl:priority";
	
	private static final String LINE_TAG_NAME 					= "msg:line";
	private static final String LINE_CONDITION_ATTR 			= "msg:condition";
	private static final String BODY_TAG_NAME 					= "msg:body";
	private static final String ATTACHMENT_TAG_NAME 			= "msg:attachment";
	private static final String ATTACHMENTS_TAG_NAME 			= "msg:attachments";
	private static final String SUBJECT_TAG_NAME 				= "msg:subject";
	private static final String MSG_ROUTING_TAG_NAME 			= "msg:group";
	private static final String MSG_ROUTING_CONDITION_ATTR 		= "msg:condition";
	private static final String MSG_RECIPIENTS_TAG_NAME 		= "msg:recipients";
	private static final String MSG_RECIPIENTS_TYPE_ATTR 		= "msg:type";
	private static final String MESSAGE_TEMPLATE_TAG_NAME 		= "msg:message-template";
	private static final String MESSAGE_TEMPLATE_ENCODING_ATTR 	= "msg:encoding";
	private static final String MESSAGE_TEMPLATE_CLASS_ATTR 	= "msg:class";
	private static final String MESSAGE_TEMPLATE_CONDITION_ATTR = "msg:condition";

	static final Object MSG_RECIPIENTS_TYPE_FIXED 		= "fixed";
	static final Object MSG_RECIPIENTS_TYPE_AND 		= "and";
	
	private static final String ELEMENT_IS_NULL  			= "DOM Element cannot be null (%s is expected)";
	//private static final String ELEMENT_IS_NULL_ATT  		= "DOM Element cannot be null (failed to find %s attribute)";
	private static final String MISHMATCH_TAG_NAME 			= "Mishmatch DOM Element name %s (%s is expected)";
	private static final String NODELIST_IS_NULL 			= "NodeList cannot be null";
	private static final String FAILED_FIND_NODE 			= "Failed to find node with name %s and type %d";
	private static final String FAILED_TO_LAOD_MSG_ROUTING 	= "Failed to load Message Routing";
	private static final String UNKNOWN_MSG_RECIPIENTS_TYPE = "Unknown Message Recipients type (%s)";
	private static final String CANNOT_INST 				= "Cannot create instance of %s";
	
	private static final String MESSAGE_ID_KEY = "DistributionMessageId";
	
	private static XRecipients recipients;
	private static Groups groups;
	private static XRouting routing;
	private static XMessages messages;
		
	private Distribution() throws IOException {
		throw new IOException(String.format(CANNOT_INST, Distribution.class.getSimpleName()));
	}
	
	public static void init (File dl, File crl) throws Exception {
		FileInputStream in = new FileInputStream(dl);
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = db.parse(in);
		in.close();
		
		Distribution.checkTagName(doc.getDocumentElement(), DISTRIBUTION_LIST_TAG_NAME);
		NodeList nodes = doc.getDocumentElement().getChildNodes();
		setRecipients(Distribution.loadRecipientsFromXML((Element) Distribution.findNode(nodes, RECIPIENTS_TAG_NAME, Node.ELEMENT_NODE)));
		setGroups(Distribution.loadGroupsFromXML((Element) Distribution.findNode(nodes, GROUPS_TAG_NAME, Node.ELEMENT_NODE), Distribution.getRecipients()));
		setRouting(Distribution.loadRoutingFromXML((Element) Distribution.findNode(nodes, ROUTING_TAG_NAME, Node.ELEMENT_NODE), Distribution.getGroups()));
		setMessages(Distribution.loadMessagesFromXML((Element) Distribution.findNode(nodes, MESSAGES_TAG_NAME, Node.ELEMENT_NODE), Distribution.getRouting(), Distribution.getGroups()));
		in=new FileInputStream(crl);
		doc=db.parse(in);
		Codes.init(doc.getDocumentElement());
		in.close();
	}
	
	public static String getCode(String code){return Codes.getCodeDescription(code);}
	
	public static void createMessage (Hashtable<String, Object> join) {
		try {
			XMessageTemplate template = Distribution.getMessages().getMessageTemplate((String) join.get(Distribution.MESSAGE_ID_KEY));
			String[] comp = Distribution.route (template, join);
			EMessageParser.addMessage(comp);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static XRecipient getMainRecipient() {
		return Distribution.getRecipients().getMainRecipient();
	}
	
	public static XRecipients getMainRecipientAsArray() {
		XRecipients main = new XRecipients();
		main.add(getMainRecipient());
		return main;
	}
	
	
	static String[] route(XMessageTemplate template, Hashtable<String, Object> join) throws IOException {

		XRecipients recipients = template.getMsgRecipients().getRecipients(join);
		String subject = TextParser.parse(template.getSubject().getText(), join, template.getEncoding());
		String body = Distribution.parseBody(template, join, recipients.size() < 1 && recipients.contains(Distribution.getMainRecipient()));
		String att1 = null, att2 = null;
		if(template.getAttachments().size() > 0) {
			att1 = Distribution.parseAttachment(template.getAttachments().get(0).getText(), join);
		}
		if(template.getAttachments().size() == 2) {
			att2 = Distribution.parseAttachment(template.getAttachments().get(1).getText(), join);
		}
		return new String[] {Base64Coder.encodeString(Distribution.getEmailAddresses(recipients)), subject, body, att1, att2, template.getEncoding()};
	}
	
	private static String getEmailAddresses(XRecipients recipients) {
		StringBuffer sb = new StringBuffer(255);
		for(int i = 0; i < recipients.size(); i ++) {
			if(i > 0) {
				sb.append(',');
			}
			sb.append(recipients.get(i).getEmailAddress());
		}
		return sb.toString();
	}

	private static String parseAttachment(String s, Hashtable<String, Object> join) {
		String[] a = s.split(new String(new char[]{TextParser.EXPRESSION_MARKER}));
		if(a.length == 2) {
			String attName = new String(TextParser.parseExpression(a[0], join));
			String filename = null;
			try {
				File f=new File("tmp\\"+new String(TextParser.parseExpression(a[1],join)));
				//System.out.println("FILE="+f.getName());
				//System.out.println("FILE EXISTS="+f.exists());
				if(f.exists()) {
					filename = f.getName();
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			if(filename == null) {
				try {
					filename = Sys.createTmpFile(TextParser.parseExpression(a[1], join));
				}
				catch (IOException e) {
					e.printStackTrace();
					filename="error";
				}
			}
			return attName + "," + filename;
		}
		return null;
	}

	
	private static String parseBody(XMessageTemplate template, Hashtable<String, Object> join, boolean mainOnly) {
		ArrayList<String> bd = new ArrayList<String>();
		for(int i = 0; i < template.getBody().size(); i ++) {
			String condition = template.getBody().get(i).getCondition();
			boolean b = true;
			if(condition != null && condition.length() != 0) {
				b = check (join, condition);
			}
			if(b) {
				bd.add(TextParser.parse(template.getBody().get(i).getText(), join, template.getEncoding()));
			}
		}
		if(mainOnly) {
			bd.add(TextParser.parse(XLine.EMPTY_LINE.getText(), join, template.getEncoding()));
			bd.add(TextParser.parse(XLine.NO_RECIPIENT_WARNING.getText(), join, template.getEncoding()));
		}
		String tmp;
		try {
			tmp = new String(new byte[]{}, template.getEncoding());
		}
		catch (UnsupportedEncodingException e) {
			tmp="";
		}
		String ls = System.getProperty("line.separator");
		for(int i = 0; i < bd.size(); i ++) {
			if(i > 0) {
				try {
					tmp = tmp.concat(new String(ls.getBytes(), template.getEncoding()));
				}
				catch (UnsupportedEncodingException e) {
					tmp=tmp.concat(ls);
				}	
			}
			tmp=tmp.concat(bd.get(i));
		}
		return tmp;
	}
	
	private static boolean check(Hashtable<String, Object> tab, String s) {
		if(s != null) {
			String[] a = s.split("[=<>]", 2);
			try {
				switch(s.charAt(a[0].length())) {
					case 0:
						return tab.get(a[0]).equals(a[1]);
					case 1:
						return Integer.parseInt(tab.get(a[0]).toString())>Integer.parseInt(a[1]);
					case 2:
						return Integer.parseInt(tab.get(a[0]).toString())<Integer.parseInt(a[1]);
					default:
				}
			}
			catch(Exception e) {}
		}
		return false;
	}
	
	private static XMessages loadMessagesFromXML (Element domElement, XRouting routing, Groups groups) throws IOException {
		try {
			Distribution.checkTagName(domElement, MESSAGES_TAG_NAME);
			String encoding = domElement.getAttribute(MESSAGE_TEMPLATE_ENCODING_ATTR);
			XMessages messages = new XMessages(encoding);
			NodeList nl = domElement.getChildNodes();
			for(int i = 0; i < nl.getLength(); i ++) {
				Node n = nl.item(i);
				if(n.getNodeType() == Node.ELEMENT_NODE) {
					try {
						messages.add(Distribution.loadMessageTemplateFromXML((Element) n, routing, groups, encoding));
					}
					catch (IOException e) {}
				}
			}
			return messages;
		}
		catch (Exception e) {
			throw new IOException (e);
		}
	}
	
	private static XMessageTemplate loadMessageTemplateFromXML (Element domElement, XRouting routing, Groups groups, String defaultEncoding) throws IOException {
		try {
			Distribution.checkTagName(domElement, MESSAGE_TEMPLATE_TAG_NAME);
			String encoding = domElement.getAttribute(MESSAGE_TEMPLATE_ENCODING_ATTR);
			encoding = encoding.length() > 0 ? encoding : defaultEncoding;
			String className = domElement.getAttribute(MESSAGE_TEMPLATE_CLASS_ATTR);
			String condition = domElement.getAttribute(MESSAGE_TEMPLATE_CONDITION_ATTR);
			NodeList nl = domElement.getChildNodes();
			XMsgRecipients msgRecipients = Distribution.loadMsgRecipientsFromXML((Element) Distribution.findNode(nl, MSG_RECIPIENTS_TAG_NAME, Node.ELEMENT_NODE), routing, groups);
			XSubject subject = Distribution.loadSubjectFromXML((Element) Distribution.findNode(nl, SUBJECT_TAG_NAME, Node.ELEMENT_NODE));
			XBody body = Distribution.loadBodyFromXML((Element) Distribution.findNode(nl, BODY_TAG_NAME, Node.ELEMENT_NODE));
			XAttachments attachments = Distribution.loadAttachmentsFromXML((Element) Distribution.findNode(nl, ATTACHMENTS_TAG_NAME, Node.ELEMENT_NODE));
			return new XMessageTemplate (encoding, className, condition, msgRecipients, subject, body, attachments);
		}
		catch(Exception e) {
			throw new IOException(e);
		}
	}
	
	private static XMsgRecipients loadMsgRecipientsFromXML (Element domElement, XRouting routing, Groups groups) throws IOException {
		try {
			Distribution.checkTagName(domElement, MSG_RECIPIENTS_TAG_NAME);
			String type = domElement.getAttribute(MSG_RECIPIENTS_TYPE_ATTR);
			Object x;
			if(type.equals(MSG_RECIPIENTS_TYPE_FIXED)) {
				x = groups;
			}
			else if (type.equals(MSG_RECIPIENTS_TYPE_AND)) {
				x = routing;
			}
			else {
				throw new IOException (String.format(UNKNOWN_MSG_RECIPIENTS_TYPE, type));
			}
			XMsgRecipients msgRecipients = new XMsgRecipients(type);
			NodeList nl = domElement.getChildNodes();
			for(int i = 0; i < nl.getLength(); i ++) {
				Node n = nl.item(i);
				if(n.getNodeType() == Node.ELEMENT_NODE) {
					try {
						msgRecipients.add(Distribution.loadMsgRoutingFromXML((Element) n, x));
					}
					catch(IOException e) {}
				}
			}
			return msgRecipients;
		}
		catch (Exception e) {
			throw new IOException (e);
		}
	}
	
	private static XMsgRouting loadMsgRoutingFromXML (Element domElement, Object x) throws IOException {
		try {
			Distribution.checkTagName(domElement, MSG_ROUTING_TAG_NAME);
			String condition = domElement.getAttribute(MSG_ROUTING_CONDITION_ATTR);
			String text = domElement.getTextContent().trim();
			if((x instanceof XRouting) && condition.length() > 0) {
				return new XMsgRouting(text, ((XRouting) x).getRountingCondition(condition), null);
			}
			else if ((x instanceof Groups) && condition.length() == 0) {
				return new XMsgRouting(null, null, (Group) ((Groups) x).getGroup(text));
			}
			else {
				throw new IOException(FAILED_TO_LAOD_MSG_ROUTING);
			}
		}
		catch (Exception e) {
			throw new IOException(e);
		}
	}
	
	private static XSubject loadSubjectFromXML (Element domElement) throws IOException {
		try {
			Distribution.checkTagName(domElement, SUBJECT_TAG_NAME);
			return new XSubject (domElement.getTextContent().trim());
		}
		catch (Exception e) {
			throw new IOException (e);
		}
	}
	
	private static XAttachments loadAttachmentsFromXML (Element domElement) throws IOException {
		try {
			XAttachments attachments = new XAttachments();
			Distribution.checkTagName(domElement, ATTACHMENTS_TAG_NAME);
			NodeList nl = domElement.getChildNodes();
			for(int i = 0; i < nl.getLength(); i ++) {
				Node n = nl.item(i);
				if(n.getNodeType() == Node.ELEMENT_NODE) {
					try {
						attachments.add(Distribution.loadAttachmentFromXML((Element) n));
					}
					catch(IOException e) {}
				}
			}
			return attachments;
		}
		catch (Exception e) {
			throw new IOException (e);
		}
	}
	
	private static XAttachment loadAttachmentFromXML (Element domElement) throws IOException {
		try {
			Distribution.checkTagName(domElement, ATTACHMENT_TAG_NAME);
			return new XAttachment (domElement.getTextContent().trim());
		}
		catch (Exception e) {
			throw new IOException (e);
		}
	}
	
	private static XBody loadBodyFromXML (Element domElement) throws IOException {
		try {
			XBody body = new XBody();
			Distribution.checkTagName(domElement, BODY_TAG_NAME);
			NodeList nl = domElement.getChildNodes();
			for(int i = 0; i < nl.getLength(); i ++) {
				Node n = nl.item(i);
				if(n.getNodeType() == Node.ELEMENT_NODE) {
					try {
						body.add(Distribution.loadLineFromXML((Element) n));
					}
					catch(IOException e) {}
				}
			}
			return body;
		}
		catch (Exception e) {
			throw new IOException (e);
		}
	}
	
	private static XLine loadLineFromXML (Element domElement) throws IOException {
		try {
			Distribution.checkTagName(domElement, LINE_TAG_NAME);
			return new XLine (domElement.getAttribute(LINE_CONDITION_ATTR), domElement.getTextContent().trim());
		}
		catch (Exception e) {
			throw new IOException (e);
		}
	}
	
	private static void checkTagName(Element domElement, String fullName) throws IOException {
		if(domElement == null) {
			throw new IOException (String.format(ELEMENT_IS_NULL, fullName));
		}
		String name = domElement.getTagName();
		if(!(fullName != null && name.equalsIgnoreCase(fullName))) {
			throw new IOException(String.format(MISHMATCH_TAG_NAME, name, fullName));
		}
	}
	/*
	private static String getAttribute(Element domElement, String attName) throws IOException {
		try {
			if(domElement == null) {
				throw new IOException (String.format(ELEMENT_IS_NULL_ATT, attName));
			}
			if(attName != null) {
				Node n = domElement.getAttributeNode(attName);
				if(n != null) {
					return n.getNodeValue();
				}
			}
			return null;
		}
		catch (Exception e) {
			throw new IOException(e);
		}
	}
	*/
	private static XRecipients loadRecipientsFromXML (Element domElement) throws IOException {
		XRecipients recs = new XRecipients();
		try {
			checkTagName(domElement, RECIPIENTS_TAG_NAME);
			NodeList nl = domElement.getChildNodes();
			for(int i = 0; i < nl.getLength(); i ++) {
				Node n = nl.item(i);
				if(n.getNodeType() == Node.ELEMENT_NODE) {
					try {
						recs.add(Distribution.loadRecipientFromXML((Element) n));
					}
					catch(IOException e) {}
				}
			}
			try {
				recs.setMainRecipient(recs.getRecipient(Integer.parseInt(domElement.getAttribute(MAIN_ID_ATTR))));
			}
			catch (IndexOutOfBoundsException e) {
				throw new IOException(e);
			}
			return recs;
		}
		catch (Exception e) {
			throw new IOException(e);
		}
	}
	
	private static XRecipient loadRecipientFromXML (Element domElement) throws IOException {
		try {
			checkTagName(domElement, RECIPIENT_TAG_NAME);
			NodeList nl = domElement.getChildNodes();
			return new XRecipient(
					Integer.parseInt(findNode(nl, RECIPIENT_ID_TAG_NAME, Node.ELEMENT_NODE).getTextContent().trim()),
					findNode(nl, RECIPIENT_NAME_TAG_NAME, Node.ELEMENT_NODE).getTextContent().trim(),
					findNode(nl, RECIPIENT_EMAIL_TAG_NAME, Node.ELEMENT_NODE).getTextContent().trim(),
					Boolean.parseBoolean(findNode(nl, RECIPIENT_ENABLED_TAG_NAME, Node.ELEMENT_NODE).getTextContent().trim()));
		}
		catch (Exception e) {
			throw new IOException(e);
		}
	}

	private static Node findNode(NodeList nl, String nodeName, short nodeType) throws IOException {
		if(nl != null) {
			for(int i = 0; i < nl.getLength(); i ++) {
				Node n = nl.item(i);
				if(nodeName !=null && n.getNodeType() == nodeType && n.getNodeName().equalsIgnoreCase(nodeName)) {
					return n;
				}
			}
			throw new IOException(String.format(FAILED_FIND_NODE, nodeName, nodeType));
		}
		throw new IOException (NODELIST_IS_NULL);
	}
	
	private static Groups loadGroupsFromXML (Element domElement, XRecipients recs) throws IOException {
		try {
			Groups groups = new Groups();
			checkTagName(domElement, GROUPS_TAG_NAME);
			NodeList nl = domElement.getChildNodes();
			for(int i = 0; i < nl.getLength(); i ++) {
				Node n = nl.item(i);
				if(n.getNodeType() == Node.ELEMENT_NODE) {
					try {
						checkTagName((Element) n, GROUP_TAG_NAME);
						groups.add(loadGroupFromXML((Element) n, recs));
					}
					catch(IOException e) {}
				}
			}
			return groups;
		}
		catch (Exception e) {
			throw new IOException (e);
		}
	}
	
	private static Group loadGroupFromXML (Element domElement, XRecipients recs) throws IOException {
		try {
			Group group = new Group();
			checkTagName(domElement, GROUP_TAG_NAME);
			NodeList nl = domElement.getChildNodes();
			for(int i = 0; i < nl.getLength(); i ++) {
				Node n = nl.item(i);
				if(n.getNodeType() == Node.ELEMENT_NODE) {
					try {
						checkTagName((Element) n, ID_TAG_NAME);
						group.add(recs.getRecipient(Integer.parseInt(n.getTextContent().trim())));
					}
					catch(IOException e) {}
				}
			}
			group.setMainRecipient(recs.getMainRecipient());
			group.setName(domElement.getAttribute(GROUP_NAME_ATTR));
			return group;
		}
		catch (Exception e) {
			throw new IOException (e);
		}
	}
	
	private static XRouting loadRoutingFromXML (Element domElement, Groups groups) throws IOException {
		try {
			XRouting routing = new XRouting();
			Distribution.checkTagName(domElement, ROUTING_TAG_NAME);
			NodeList nl = domElement.getChildNodes();
			for(int i = 0; i < nl.getLength(); i ++) {
				Node n = nl.item(i);
				if(n.getNodeType() == Node.ELEMENT_NODE) {
					try {
						checkTagName((Element) n, CONDITION_TAG_NAME);
						routing.add(Distribution.loadRoutingConditionFromXML((Element) n, groups));
					}
					catch(IOException e) {}
				}
			}
			return routing;			
		}
		catch (Exception e) {
			throw new IOException(e);
		}
	}
	
	private static XRoutingCondition loadRoutingConditionFromXML (Element domElement, Groups groups) throws IOException {
		try {
			XRoutingCondition rc = new XRoutingCondition();
			Distribution.checkTagName(domElement, CONDITION_TAG_NAME);
			NodeList nl = domElement.getChildNodes();
			for(int i = 0; i < nl.getLength(); i ++) {
				Node n = nl.item(i);
				if(n.getNodeType() == Node.ELEMENT_NODE) {
					try {
						checkTagName((Element) n, CONDITION_ITEM_TAG_NAME);
						rc.add(Distribution.loadRoutingPointFromXML((Element) n, groups));
					}
					catch(IOException e) {}
				}
			}
			rc.setName(domElement.getAttribute(CONDITION_NAME_ATTR));
			rc.setPriority(Integer.parseInt(domElement.getAttribute(CONDITION_PRIORITY_ATTR)));
			return rc;
		}
		catch (Exception e) {
			throw new IOException (e);
		}
	}
	
	private static XRoutingPoint loadRoutingPointFromXML (Element domElement, Groups groups) throws IOException {
		try {
			XRoutingPoint rp = new XRoutingPoint();
			Distribution.checkTagName(domElement, CONDITION_ITEM_TAG_NAME);
			NodeList nl = domElement.getChildNodes();
			for(int i = 0; i < nl.getLength(); i ++) {
				Node n = nl.item(i);
				if(n.getNodeType() == Node.ELEMENT_NODE) {
					try {
						checkTagName((Element) n, CONDITION_GROUP_TAG_NAME);
						rp.add((Group) groups.getGroup(n.getTextContent().trim()));
					}
					catch(IOException e) {}
				}
			}
			rp.setRegExp(domElement.getAttribute(CONDITION_ITEM_REGEXP_ATTR));
			return rp;
		}
		catch (Exception e) {
			throw new IOException (e);
		}
	}
	
	public static XRecipients getRecipients() {
		return recipients;
	}

	public static void setRecipients(XRecipients recipients) {
		Distribution.recipients = recipients;
	}

	public static Groups getGroups() {
		return groups;
	}

	public static void setGroups(Groups groups) {
		Distribution.groups = groups;
	}

	public static XRouting getRouting() {
		return routing;
	}

	public static void setRouting(XRouting routing) {
		Distribution.routing = routing;
	}

	public static XMessages getMessages() {
		return messages;
	}

	public static void setMessages(XMessages messages) {
		Distribution.messages = messages;
	}
}
