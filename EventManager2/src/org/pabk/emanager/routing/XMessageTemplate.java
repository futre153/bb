package org.pabk.emanager.routing;

public class XMessageTemplate implements IMessageTemplate {

	private String encoding;
	private XMsgRecipients msgRecipients;
	private XSubject subject;
	private XBody body;
	private XAttachments attachments;
	private String _class;
	private String condition;
	
	protected XMessageTemplate(String encoding, String className, String condition, XMsgRecipients msgRecipients, XSubject subject, XBody body, XAttachments attachments) { 
		this.setEncoding(encoding);
		this.set_Class(className);
		this.setCondition(condition);
		this.setMsgRecipients(msgRecipients);
		this.setSubject(subject);
		this.setBody(body);
		this.setAttachments(attachments);
	}
	
	@Override
	public String getEncoding() {
		return this.encoding;
	}

	@Override
	public XMsgRecipients getMsgRecipients() {
		return this.msgRecipients;
	}

	protected final void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	protected final void setMsgRecipients(XMsgRecipients msgRecipients) {
		this.msgRecipients = msgRecipients;
	}

	protected final void setSubject(XSubject subject) {
		this.subject = subject;
	}

	protected final void setBody(XBody body) {
		this.body = body;
	}

	protected final void setAttachments(XAttachments attachments) {
		this.attachments = attachments;
	}

	protected final void set_Class(String _class) {
		this._class = _class;
	}

	@Override
	public XSubject getSubject() {
		return this.subject;
	}

	@Override
	public XBody getBody() {
		return this.body;
	}

	@Override
	public XAttachments getAttachments() {
		return this.attachments;
	}

	@Override
	public String get_Class() {
		return this._class;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}
	
	public boolean equal (XMessageTemplate msg) {
		return msg == null ? false : this.get_Class().equals(msg.get_Class());
	}
}
