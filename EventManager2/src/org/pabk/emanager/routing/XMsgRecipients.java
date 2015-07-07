package org.pabk.emanager.routing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import org.pabk.emanager.parser.TextParser;

public class XMsgRecipients extends ArrayList<XMsgRouting> implements IMsgRecipients {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String UNKNOWN_TYPE = "Type %s is unknown";
	private String type;
	protected XMsgRecipients(String type) {
		super();
		this.setType(type);
	}
	
	private static XRecipients appendRecipients(XRecipients dst, XRecipients src) {
		for(int i = 0; i < src.size(); i ++) {
			if(!dst.contains(src.get(i))) {
				dst.add(src.get(i));
			}
		}
		return dst;
	}
	
	private static XRecipients andRecipients(XRecipients dst, XRecipients src) {
		XRecipients and = new XRecipients();
		for (int i = 0 ; i < src.size(); i ++) {
			if(dst.contains(src.get(i))) {
				and.add(src.get(i));
			}
		}
		return (and.size() == 0) ? (dst.size() == 0 ? src : dst) : and;
	}
	
	@Override
	public XRecipients getRecipients(Hashtable<String, Object> join) throws IOException {
		XRecipients recipients = new XRecipients();
		if(this.getType().equals(Distribution.MSG_RECIPIENTS_TYPE_FIXED)) {
			for(int i = 0; i < this.size(); i ++) {
				recipients = appendRecipients(recipients, this.get(i).getGroup().getEnabledRecipients());
			}
		}
		else if (this.getType().equals(Distribution.MSG_RECIPIENTS_TYPE_AND)) {
			for(int i = 0; i <this.size(); i ++) {
				XRecipients and = new XRecipients();
				XMsgRouting rc = this.get(i);
				String x = new String(TextParser.parseExpression(rc.getCondition(),join));
				for(int j = 0; j < rc.size(); j ++) {
					if(x.matches(rc.get(j).getRegExp())) {
						for(int k = 0; k < rc.get(j).size(); k ++) {
							and = appendRecipients(and, rc.get(j).get(k).getEnabledRecipients());
						}
					}
				}
				recipients = andRecipients(recipients, and);
			}
		}
		else {
			throw new IOException (String.format(UNKNOWN_TYPE, this.getType()));
		}
		return appendRecipients(recipients, Distribution.getMainRecipientAsArray());
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
