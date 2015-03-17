package org.pabk.emanager.routing;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;

import org.pabk.emanager.parser.TextParser;
import org.pabk.emanager.util.Base64Coder;
import org.pabk.emanager.util.Sys;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class Message {
	
	private static final String MSG_TEMPLATE_CLASS_ATT_NAME = "class";
	private static final String MSG_TEMPLATE_ENCODING_ATT_NAME = "encoding";
	private static final String RECIPIENTS_NODE_NAME = "recipients";
	private static final String SUBJECT_NODE_NAME = "subject";
	private static final String BODY_NODE_NAME = "body";
	private static final String ATTACHMENTS_NODE_NAME = "attachments";
	private static final String RECIPIENTS_TYPE_ATT_NAME = "type";
	private static final String RECIPIENTS_ITEM_NODE_NAME = "group";
	private static final String LINE_ITEM_NODE_NAME = "line";
	private static final String ATTACHMENT_ITEM_NODE_NAME = "attachment";
	private static final String MSG_TEMPLATE_CONDITION_ATT_NAME = "condition";
	private static final String FIXED_RECIPIENTS = "fixed";
	private static final String AND_RECIPIENTS = "and";
	private String encoding;
	private String className;
	private String condition;
	private String recType;
	private String subject;
	private final ArrayList<MsgRecipient> rec=new ArrayList<MsgRecipient>();
	private final ArrayList<Line> body=new ArrayList<Line>();
	private final ArrayList<String> atts=new ArrayList<String>();
		
	private Message(){}
	
	static Message getMsgTemplate(Element elem) {
		Message msg=new Message();
		msg.setClassName(elem.getAttribute(MSG_TEMPLATE_CLASS_ATT_NAME));
		String enc=elem.getAttribute(MSG_TEMPLATE_ENCODING_ATT_NAME);
		if(enc==null || enc.length()==0)enc=Messages.getDefaultEncoding();
		String cond=elem.getAttribute(MSG_TEMPLATE_CONDITION_ATT_NAME);
		msg.setEncoding(enc);
		if(cond!=null && enc.length()>0)msg.setCondition(cond);
		NodeList nodes=elem.getChildNodes();
		for(int i=0;i<nodes.getLength();i++) {
			Node node=nodes.item(i);
			if(node.getNodeType()==Node.ELEMENT_NODE) {
				elem=(Element)node;
				if(elem.getTagName().equals(Message.RECIPIENTS_NODE_NAME)) {setRecipients(msg,elem);}
				else if(elem.getTagName().equals(Message.SUBJECT_NODE_NAME)) {setSubject(msg,elem);}
				else if(elem.getTagName().equals(Message.BODY_NODE_NAME)) {setBody(msg,elem);}
				else if(elem.getTagName().equals(Message.ATTACHMENTS_NODE_NAME)) {setAttachments(msg,elem);}
			}
		}
		
		return msg;
	}

	private static void setAttachments(Message msg, Element elem) {
		//System.out.println(elem);
		NodeList nodes=elem.getChildNodes();
		for(int i=0;i<nodes.getLength();i++) {
			Node node=nodes.item(i);
			if(node.getNodeType()==Node.ELEMENT_NODE) {
				//System.out.println(node);
				if(((Element)node).getTagName().equals(ATTACHMENT_ITEM_NODE_NAME)) {
					msg.atts.add(Recipients.getNoteTextValue(node));
				}
			}
		}
		
	}

	private static void setBody(Message msg, Element elem) {
		NodeList nodes=elem.getChildNodes();
		for(int i=0;i<nodes.getLength();i++) {
			Node node=nodes.item(i);
			if(node.getNodeType()==Node.ELEMENT_NODE) {
				if(((Element)node).getTagName().equals(LINE_ITEM_NODE_NAME)) {
					msg.body.add(Line.getMsgLine((Element) node));
				}
			}
		}
		
	}

	private static void setSubject(Message msg, Element elem) {
		msg.subject=Recipients.getNoteTextValue(elem);
	}

	private static void setRecipients(Message msg, Element elem) {
		msg.setRecType((elem.getAttribute(RECIPIENTS_TYPE_ATT_NAME)));
		NodeList nodes=elem.getChildNodes();
		for(int i=0;i<nodes.getLength();i++) {
			Node node=nodes.item(i);
			if(node.getNodeType()==Node.ELEMENT_NODE) {
				if(((Element)node).getTagName().equals(RECIPIENTS_ITEM_NODE_NAME)) {
					msg.rec.add(MsgRecipient.getRecipientItem((Element) node));
				}
			}
		}
	}

	String getClassName() {return className;}
	void setClassName(String name) {this.className = name;}

	String getEncoding() {return encoding;}
	void setEncoding(String encoding) {this.encoding = encoding;}
	
	String getRecType() {return recType;}
	void setRecType(String recType) {this.recType = recType;}

	String getSubject() {return subject;}
	void setSubject(String subject) {this.subject = subject;}

	String getCondition() {return condition;}
	void setCondition(String condition) {this.condition = condition;}

	boolean checkCondition(Hashtable<String, Object> join) {
		return Message.check(join, this.getCondition());
	}
	
	private static boolean check(Hashtable<String, Object> tab, String s) {
		//System.out.println(s);
		if(s==null)return true;
		if(s.length()==0)return true;
		int type=0;
		int index=s.indexOf('=');
		if(index<0) {
			type++;
			index=s.indexOf('>');
			if(index<0) {
				type++;
				index=s.indexOf('<');
				if(index<0)return false;
			}
		}
		String[] a=s.split("[=<>]");
		//System.out.println(a[0]+","+a[1]);
		if(s.length()<2)return false;
		Object o=tab.get(a[0]);
		//System.out.println(a[0]+","+a[1]+","+o);
		if(o==null)return false;
		try {
			switch(type) {
			case 0:return o.equals(a[1]);
			case 1:return Integer.parseInt(o.toString())>Integer.parseInt(a[1]);
			case 2:return Integer.parseInt(o.toString())<Integer.parseInt(a[1]);
			default:return false;
			}
		}
		catch(Exception e) {
			return false;
		}
	}

	static String[] route(Message msg, Hashtable<String, Object> join) {
		//System.out.println(join);
		//System.out.println(msg);
		String[] recs=Message.routeRecs(msg,join);
		String subject=TextParser.parse(msg.getSubject(),join,msg.getEncoding());
		//System.out.println("Subject="+subject);
		String body=Message.parseBody(msg,join,recs.length);
		if(recs.length==0) {
			recs=new String[]{Recipients.getMain().getEMailAddress()};
		}
		String recipients=Base64Coder.encodeString(Sys.concatenate(recs,','));
		//System.out.println("Recipients="+recipients);
		//System.out.println("body=\r\n"+body);
		String att1=null,att2=null;
		if(msg.atts.size()>0)att1=Message.parseAttachment(msg.atts.get(0), join);
		//System.out.println("att1=\r\n"+att1);
		if(msg.atts.size()==2)att2=Message.parseAttachment(msg.atts.get(1), join);
		//System.out.println("att2=\r\n"+att2);
		
		//System.exit(1);
		return new String[]{recipients,subject,body,att1,att2,msg.getEncoding()};
	}
	
	
	
	private static String parseAttachment(String s, Hashtable<String, Object> join) {
		String[] a = s.split(new String(new char[]{TextParser.EXPRESSION_MARKER}));
		if(a.length==2) {
			String attName=new String(TextParser.parseExpression(a[0],join));
			String filename=null;
			try {
				File f=new File("tmp\\"+new String(TextParser.parseExpression(a[1],join)));
				//System.out.println("FILE="+f.getName());
				//System.out.println("FILE EXISTS="+f.exists());
				if(f.exists()) 
					filename=f.getName();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			if(filename==null) {
				try {filename=Sys.createTmpFile(TextParser.parseExpression(a[1],join));}
				catch (IOException e) {
					e.printStackTrace();
					filename="error";
				}
			}
			return attName+","+filename;
		}
		return null;
	}

	private static String parseBody(Message msg, Hashtable<String, Object> join, int len) {
		ArrayList<String> bd=new ArrayList<String>();
		for(int i=0;i<msg.body.size();i++) {
			String condition=msg.body.get(i).getCondition();
			boolean b=true;
			if(condition!=null && condition.length()!=0) {
				b=check(join,condition);
			}
			if(b) {
				bd.add(TextParser.parse(msg.body.get(i).getText(), join, msg.getEncoding()));
			}
		}
		if(len==0) {
			bd.add(TextParser.parse(Line.EMPTY_LINE.getText(), join, msg.getEncoding()));
			bd.add(TextParser.parse(Line.NO_RECIPIENT_WARNING.getText(), join, msg.getEncoding()));
		}
		String tmp;
		try {tmp=new String(new byte[]{},msg.getEncoding());}
		catch (UnsupportedEncodingException e) {tmp="";}
		String ls=System.getProperty("line.separator");
		for(int i=0;i<bd.size();i++) {
			if(i>0) {
				try {tmp=tmp.concat(new String(ls.getBytes(),msg.getEncoding()));}
				catch (UnsupportedEncodingException e) {tmp=tmp.concat(ls);}	
			}
			tmp=tmp.concat(bd.get(i));
		}
		return tmp;
	}

	private static String[] routeRecs(Message msg,Hashtable<String, Object> join) {
		Recipients recs=Recipients.getInstance();
		if(msg.getRecType().equals(FIXED_RECIPIENTS)) {
			//System.out.println("Recipients="+FIXED_RECIPIENTS);
			for(int i=0;i<msg.rec.size();i++) {
				recs=Recipients.join(recs,Recipients.getGroup(msg.rec.get(i).getExpression()));
			}
			//System.out.println("Recipients="+recs.size());
		}
		else if(msg.getRecType().equals(AND_RECIPIENTS)) {
			//System.out.println(join);
			for(int i=0;i<msg.rec.size();i++) {
				Recipients b=Routing.apply(join,msg.rec.get(i));
				//System.out.println("Recipients B="+b);
				Recipients c=Recipients.and(recs,b);
				//System.out.println("Recipients C="+c);
				recs=(c.size()==0?(recs.size()==0?b:recs):c);
				//System.out.println("Recipients AND="+recs);
			}
		}
		/*if(recs.size()==0) {
			recs.add(Recipients.getMain());
			msg.body.add(Line.EMPTY_LINE);
			msg.body.add(Line.NO_RECIPIENT_WARNING);
		}*/
		//System.out.println("Recipients ALL="+recs);
		return Recipients.toArray(recs);
	}
	
}
