package org.fds.prepaid.notification;

import org.apache.axis2.context.MessageContext;
import org.so.sms.notification.client._Stub;

public class SMSNotification {
	public Object notify(MessageContext msg)  throws Exception {return new _Stub().requestNotification(msg);}
}