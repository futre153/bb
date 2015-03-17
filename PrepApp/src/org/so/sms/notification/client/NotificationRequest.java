package org.so.sms.notification.client;

import java.util.Iterator;
import javax.wsdl.Definition;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.context.MessageContext;
import org.pabk.util.Const;

class NotificationRequest {
  	
	public static SOAPEnvelope createEnvelope1(MessageContext msg, SOAPFactory fac, Definition def) {
		SOAPEnvelope env = msg.getEnvelope();
		SOAPBody body = env.getBody();
		SOAPEnvelope req = fac.createSOAPEnvelope(env.getNamespace());
		SOAPBody reqBody = fac.createSOAPBody(req);
		OMNamespace ns = fac.createOMNamespace(def.getTargetNamespace(), "");
		req.declareNamespace(ns);
		OMElement root = fac.createOMElement(Const.NOTIFICATION_REQUEST1_ROOT, ns);
		Iterator<?> ite = body.getChildElements();
		while (ite.hasNext()) {
			OMElement elem = (OMElement)ite.next();
			String text = elem.getText();
			OMElement elem2 = fac.createOMElement(elem.getLocalName(), ns);
			elem2.setText(text);
			root.addChild(elem2);
		}
		reqBody.addChild(root);
		return req;
	}
}