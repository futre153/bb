package org.pabk.emanager.routing;

interface IMessageTemplate {
	String getEncoding();
	XMsgRecipients getMsgRecipients();
	XSubject getSubject();
	XBody getBody();
	XAttachments getAttachments();
	String get_Class();
}
