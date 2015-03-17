package org.fds.prepaid.notification;

import org.apache.axis2.context.MessageContext;
import org.so.sms.notification.client._Stub;

public class SMSNotification {
	public void notify(MessageContext msg)  throws Exception {new _Stub().requestNotification(msg);}
}