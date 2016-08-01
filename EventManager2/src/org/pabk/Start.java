package org.pabk;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.pabk.emanager.Loader;
import org.pabk.emanager.routing.Distribution;
import org.pabk.emanager.routing.Groups;
import org.pabk.emanager.routing.XRecipient;
import org.pabk.emanager.routing.XRecipients;
import org.pabk.emanager.sql.sap.CompPred;
import org.pabk.emanager.sql.sap.Identifier;
import org.pabk.emanager.sql.sap.WhereClause;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Start {

	public static void main(String[] args) throws Exception {
		
		CompPred c = new CompPred(new Object[]{new Identifier("brano")}, new Object[]{"brandys"}, CompPred.EQUAL);
		Object[] objs = {c, WhereClause.AND, WhereClause.LEFT_BRACKET, c, WhereClause.OR, c, WhereClause.OR, c, WhereClause.RIGHT_BRACKET, WhereClause.AND, WhereClause.LEFT_BRACKET, c, WhereClause.RIGHT_BRACKET};
		int[] x = WhereClause.findBrackets(0, objs);
		WhereClause where = new WhereClause(objs);
		System.exit(0);
		
		// TODO Auto-generated method stub
		/*
		String filename = "conf\\distribution_list.xml";
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(filename);
		NodeList nl = doc.getDocumentElement().getChildNodes();
		NodeList nl2 = nl;
		System.out.println(nl.getLength());
		Node n = null;
		for(int i = 0; i < nl.getLength(); i ++) {
			n = nl.item(i);
			System.out.println(n.getNodeName());
			if(n.getNodeType() == Node.ELEMENT_NODE && n.getNodeName() != null && n.getNodeName() == "address-list") {
				nl = n.getChildNodes();
				break;
			}
		}
		System.out.println(Distribution.getAttribute((Element) n, "rec:main"));
		System.out.println(nl.getLength());
		ArrayList<XRecipient> a = new ArrayList<XRecipient>();
		int id = 0;
		for(int i = 0; i < nl.getLength(); i ++) {
			n = nl.item(i);
			//System.out.println(n.getNodeName());
			if(n.getNodeType() == Node.ELEMENT_NODE && n.getNodeName() != null && n.getNodeName() == "name") {
				String name = n.getTextContent().trim();
				String[] fullName = name.split(" ", 2);
				XRecipient x = new XRecipient(
						id,
						fullName[0].substring(0, 1).toUpperCase() + fullName[0].substring(1) + " " + fullName[1].substring(0, 1).toUpperCase() + fullName[1].substring(1),
						name.replaceAll(" ", ".") + "@pabk.sk",
						Boolean.parseBoolean(Distribution.getAttribute((Element) n, "enabled")));
				System.out.println(x);
				a.add(x);
				id ++;
			}
		}
		nl = nl2;
		n = null;
		for(int i = 0; i < nl.getLength(); i ++) {
			n = nl.item(i);
			System.out.println(n.getNodeName());
			if(n.getNodeType() == Node.ELEMENT_NODE && n.getNodeName() != null && n.getNodeName() == "address-group-list") {
				nl = n.getChildNodes();
				break;
			}
		}
		ArrayList<Object> aa = new ArrayList<Object>();
		for(int i = 0; i < nl.getLength(); i ++) {
			n = nl.item(i);
			//System.out.println(n.getNodeName());
			if(n.getNodeType() == Node.ELEMENT_NODE && n.getNodeName() != null && n.getNodeName() == "address-group") {
				aa.add(Distribution.getAttribute((Element) n, "groupName"));
				NodeList nl1 = n.getChildNodes();
				for(int j = 0; j < nl1.getLength(); j ++) {
					n = nl1.item(j);
					if(n.getNodeType() == Node.ELEMENT_NODE && n.getNodeName() != null && n.getNodeName() == "name") {
						String name = n.getTextContent().trim();
						String[] fullName = name.split(" ", 2);
						name = fullName[0].substring(0, 1).toUpperCase() + fullName[0].substring(1) + " " + fullName[1].substring(0, 1).toUpperCase() + fullName[1].substring(1);
						id = 0;
						for(; id < a.size(); id ++) {
							XRecipient x = a.get(id);
							if(x.getName().equals(name)) {
								aa.add(x.getRecipientId());
								break;
							}
						}
					}
				}
			}
		}
		
		*/
		
		
		/*
		for(int i = 0; i < aa.size();) {
			Node grp = doc.createElementNS("http://www.example.org/RecipientsGroup", "rgr:address-group");
			String name = (String) aa.get(i);
			Attr gName = doc.createAttribute("name");
			gName.setNodeValue(name);
			((Element) grp).setAttributeNode(gName);
			i ++;
			Object idx = null;
			while((i < aa.size()) && ((idx = aa.get(i)) instanceof Integer)) {
				i ++;
				Node grpId = doc.createElementNS("http://www.example.org/RecipientsGroup", "rgr:id");
				grpId.appendChild(doc.createTextNode(Integer.toString((int) idx)));
				grp.appendChild(doc.createTextNode("\n\t\t"));
				grp.appendChild(grpId);
			}
			if(i == 0) {
				n.appendChild(doc.createTextNode("\n\t"));
			}
			else {
				n.appendChild(doc.createTextNode("\t"));
			}
			n.appendChild(grp);
			n.appendChild(doc.createTextNode("\n"));
		}
		
		
		nl = nl2;
		n = null;
		for(int i = 0; i < nl.getLength(); i ++) {
			n = nl.item(i);
			System.out.println(n.getNodeName());
			if(n.getNodeType() == Node.ELEMENT_NODE && n.getNodeName() != null && n.getNodeName() == "msg-list") {
				nl = n.getChildNodes();
				break;
			}
		}
		Node e = n;
		*/
		int i = 14;
		int m = 2;
		System.out.println(Integer.toBinaryString((i)));
		System.out.println(Integer.toBinaryString((m)));
		System.out.println(Integer.toBinaryString((m ^ -1)));
		System.out.println(Integer.toBinaryString(((m ^ -1) & i)));
		System.out.println(Integer.toBinaryString(((i | m) ^ m)));
		//Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(filename);
		//XRecipients x = Distribution.loadRecipientsFromXML((Element) Distribution.findNode(doc.getDocumentElement().getChildNodes(), "dls:address-list", Node.ELEMENT_NODE));
		//Groups g = Distribution.loadGroupsFromXML((Element) Distribution.findNode(doc.getDocumentElement().getChildNodes(), "dls:address-group-list", Node.ELEMENT_NODE), x);
		/*nl = doc.getDocumentElement().getChildNodes();
		System.out.println(nl.getLength());
		n = null;
		for(int i = 0; i < nl.getLength(); i ++) {
			n = nl.item(i);
			System.out.println(n.getNodeName());
			if(n.getNodeType() == Node.ELEMENT_NODE && n.getNodeName() != null && n.getNodeName() == "dls:msg-list") {
				nl = n.getChildNodes();
				break;
			}
		}
		
		
		NodeList nl3 = e.getChildNodes();
		for(int i = 0; i < nl3.getLength(); i ++) {
			Node dst = cloneNode(doc, "http://www.example.org/MessagesList", "msg", nl3.item(i));
			if(dst != null) {
				n.appendChild(dst);
			}
		}
		*/
		
		/*
		System.out.println(Distribution.getAttribute((Element) n, "rec:main"));
		System.out.println(nl.getLength());
		for(int i = 0; i < a.size(); i ++) {
			XRecipient x = a.get(i);
			Node rec = doc.createElementNS("http://www.example.org/Recipients", "rec:recipient");
			Node recId = doc.createElementNS("http://www.example.org/Recipients", "rec:id");
			Node recName = doc.createElementNS("http://www.example.org/Recipients", "rec:name");
			Node recEnabled = doc.createElementNS("http://www.example.org/Recipients", "rec:enabled");
			Node recEmail = doc.createElementNS("http://www.example.org/Recipients", "rec:e-mail");
			recId.setTextContent(Integer.toString(x.getRecipientId()));
			recName.setTextContent(x.getName());
			recEnabled.setTextContent(Boolean.toString(x.isEnabled()));
			recEmail.setTextContent(x.getEmailAddress());
			rec.appendChild(doc.createTextNode("\n\t\t"));
			rec.appendChild(recId);
			rec.appendChild(doc.createTextNode("\n\t\t"));
			rec.appendChild(recName);
			rec.appendChild(doc.createTextNode("\n\t\t"));
			rec.appendChild(recEnabled);
			rec.appendChild(doc.createTextNode("\n\t\t"));
			rec.appendChild(recEmail);
			rec.appendChild(doc.createTextNode("\n\t"));
			//System.out.println(rec);
			//System.out.println(id);
			//System.out.println(rec.getChildNodes().getLength());
			if(i == 0) {
				n.appendChild(doc.createTextNode("\n\t"));
			}
			else {
				n.appendChild(doc.createTextNode("\t"));
			}
			n.appendChild(rec);
			n.appendChild(doc.createTextNode("\n"));
		}
		*/
		/*
		Transformer t = TransformerFactory.newInstance().newTransformer();
		DOMSource s = new DOMSource(doc);
		FileOutputStream out = new FileOutputStream("conf\\distribution_list_out.xml");
		PrintStream ps = new PrintStream(out);
		StreamResult res = new StreamResult(ps);
		t.transform(s, res);
		out.close();
		*/
	}
	
	
	@SuppressWarnings("unused")
	private static Node cloneNode(Document doc, String ns, String pfx, Node src) throws IOException {
		switch(src.getNodeType()) {
		case Node.ATTRIBUTE_NODE:
			System.out.println(src.getNodeName());
			Attr attr = doc.createAttribute(pfx + ":"+ src.getNodeName());
			attr.setNodeValue(src.getNodeValue());
			return attr;
			
		case Node.ELEMENT_NODE:
			Element dst = doc.createElementNS(ns, pfx + ":"+ src.getNodeName());
			NamedNodeMap nnm = src.getAttributes();
			for(int i = 0; i < nnm.getLength(); i ++) {
				Node att = cloneNode(doc, ns, pfx, nnm.item(i));
				if(att != null) {
					dst.setAttributeNode((Attr) att);
				}
			}
			NodeList nl = src.getChildNodes();
			for(int i = 0; i < nl.getLength(); i ++) {
				Node nd = cloneNode(doc, ns, pfx, nl.item(i));
				if(nd != null) {
					dst.appendChild(nd);
				}
			}
			return dst;
		case Node.TEXT_NODE:
			return doc.createTextNode(src.getTextContent());
		default:
			return null;
		}
	}

}
