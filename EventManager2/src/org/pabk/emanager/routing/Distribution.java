package org.pabk.emanager.routing;

import java.io.File;
import java.io.FileInputStream;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.pabk.emanager.parser.EMessageParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class Distribution {
	
	private static final String RECIPIENTS_NODE_NAME = "address-list";
	private static final String GROUPS_NODE_NAME = "address-group-list";
	private static final String CONDITIONS_NODE_NAME = "condition-list";
	private static final String MESSAGES_NODE_NAME = "msg-list";
	public static final String MESSAGE_ID_KEY = "DistributionMessageId";
		
	private Distribution() {
	}
	
	public static void init(File dl, File crl) throws Exception {
		FileInputStream in=new FileInputStream(dl);
		DocumentBuilder db=DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc=db.parse(in);
		NodeList nodes=doc.getDocumentElement().getChildNodes();
		Element recList = null,grpList = null,conList=null,msgList=null;
		for(int i=0;i<nodes.getLength();i++) {
			Node node=nodes.item(i);
			if(node.getNodeType()==Node.ELEMENT_NODE) {
				Element elem=(Element)node;
				if(elem.getTagName().equals(Distribution.RECIPIENTS_NODE_NAME)) {recList=elem;}
				else if(elem.getTagName().equals(Distribution.GROUPS_NODE_NAME)) {grpList=elem;}
				else if(elem.getTagName().equals(Distribution.CONDITIONS_NODE_NAME)) {conList=elem;}
				else if(elem.getTagName().equals(Distribution.MESSAGES_NODE_NAME)) {msgList=elem;}
			}
		}
		Recipients.init(recList, grpList);
		Routing.init(conList);
		Messages.init(msgList);
		in.close();
		in=new FileInputStream(crl);
		doc=db.parse(in);
		/*nodes=doc.getDocumentElement().getChildNodes();
		Element codeList = null;
		for(int i=0;i<nodes.getLength();i++) {
			Node node=nodes.item(i);
			if(node.getNodeType()==Node.ELEMENT_NODE) {
				Element elem=(Element)node;
				if(elem.getTagName().equals(Distribution.CODE_REPOSITORY_NODE_NAME)) {codeList=elem;}
			}
		}*/
		Codes.init(doc.getDocumentElement());
		in.close();
	}
	
	public static String getCode(String code){return Codes.getCodeDescription(code);}
	
	public static void createMessage(Hashtable<String, Object> join) {
		Message msg=Messages.findMessage(join);
		if(msg!=null) {
			String[] comp=null;
			try {comp=Message.route(msg,join);}
			catch(Exception e) {
				e.printStackTrace();
			}
			if(comp!=null)EMessageParser.addMessage(comp);
			
			//System.out.println("Message="+msg.getClassName());
		}
		//System.out.println("Message="+msg);
	}

	public static String getMainRecipient() {
		return Recipients.getMain().getEMailAddress();
	}

}
