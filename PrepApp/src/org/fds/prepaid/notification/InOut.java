package org.fds.prepaid.notification;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.receivers.AbstractInOutMessageReceiver;
//import org.pabk.util.BusinessLogic;
import org.pabk.util.BusinessLogic;
import org.pabk.util.Const;
import org.xml.sax.SAXException;

public class InOut extends AbstractInOutMessageReceiver {
	
	//private static final String FDS_SCHEMA_LOCATION = "http://sprep01ba/ws/fds_conf/fds_soap.xsd";
	//private static final String FDS_SCHEMA_LOCATION_DOBI = "http://sprep01ba/ws/fds_conf/fds_soap_dobi.xsd";
	
	
	
	
	
	//private static boolean XMLCheckDisabled = Boolean.parseBoolean(XML_CHECK_DISABLED);

	private static Schema FDS_SCHEMA = null;
	private static Schema FDS_SCHEMA_DOBI = null;
		
	public void invokeBusinessLogic(MessageContext req, MessageContext res) throws AxisFault {
		Exception fault = null;
		Object result = null;
		try {
			int i;
			SOAPFactory fac = getSOAPFactory(req);
			SOAPEnvelope env = req.getEnvelope();
			SOAPBody body = env.getBody();
			String msgType = Const.AUTHORIZATION_NOTS;
			try {
				msgType = body.getFirstChildWithName(new QName(Const.MSG_TYPE)).getText();
			}
			catch (Exception localException1) { }
			i = 0;

			if (Boolean.parseBoolean(Const.get(Const.XML_CHECK_DISABLED_KEY))) {
				if (FDS_SCHEMA == null) {
					FDS_SCHEMA = loadSchema(req, Const.get(Const.FDS_SCHEMA_LOCATION_KEY));
				}
				if (FDS_SCHEMA_DOBI == null) {
					FDS_SCHEMA_DOBI = loadSchema(req, Const.get(Const.FDS_SCHEMA_LOCATION_DOBI_KEY));
				}
				if ((FDS_SCHEMA != null) || (FDS_SCHEMA_DOBI != null)) {
					i = 1;
				}
			}

			if (i != 0) {
				if (msgType.equals(Const.AUTHORIZATION_NOTS)) {
					try {
						FDS_SCHEMA.newValidator().validate(new StreamSource(transform(fac, body, Const.FDS_ROOT_ELEMENT, Const.FDS_NAMESPACE, Const.FDS_NAMESPACE_PREFIX, Const.get(Const.FDS_SCHEMA_LOCATION_KEY))));
					}
					catch (SAXException e) {
							throw new AxisFault(e.getMessage());
					}
					catch (IOException e) {
						throw new AxisFault(e.getMessage());
					}
				}
				else if (msgType.equals(Const.DOBI_NOTS)) try {
					FDS_SCHEMA_DOBI.newValidator().validate(new StreamSource(transform(fac, body, Const.FDS_ROOT_ELEMENT, Const.FDS_NAMESPACE, Const.FDS_NAMESPACE_PREFIX, Const.get(Const.FDS_SCHEMA_LOCATION_DOBI_KEY))));
				}
				catch (SAXException e) {
					throw new AxisFault(e.getMessage());
				}
				catch (IOException e) {
					throw new AxisFault(e.getMessage());
				}
				else {
					throw new AxisFault("MsgType is invalid");
				}
			}
			try {
				if (msgType.equals(Const.AUTHORIZATION_NOTS)) {
					result = new SMSNotification().notify(req);
				}
				else if (msgType.equals(Const.DOBI_NOTS)) {
					result = new SMSNotification().notify(req);
				}
				else {
					throw new AxisFault("MsgType is invalid");
				}
			}
			catch (AxisFault f) {
				f.printStackTrace();
				throw f;
			}
			env = fac.createSOAPEnvelope(env.getNamespace());
			body = fac.createSOAPBody(env);
			OMElement elem = fac.createOMElement(Const.CONFIRMATION, null);
			elem.setText(Const.CONFIRMATION_CONTENT);
			body.addChild(elem);
			res.setEnvelope(env);
		}
		catch (Exception e) {
			e.printStackTrace();
			fault = e;
		}

		BusinessLogic.process(req.getEnvelope(), fault, result);

		if (fault != null) {
			throw new AxisFault(fault.getMessage());
		}
	}
	
	private static Reader transform(SOAPFactory fac, SOAPBody body, String rootName, String namespace, String prefix, String schemaLocation) {
		Iterator<?> ite = body.getChildElements();
		OMNamespace ns1 = fac.createOMNamespace(namespace, prefix);
		OMNamespace ns = fac.createOMNamespace(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, Const.SCHEMA_INSTANCE_PREFIX);
		OMElement newBody = fac.createOMElement(rootName, ns1);
		newBody.declareNamespace(ns);
		newBody.addAttribute(Const.SCHEMA_LOCATION_ATTR_NAME, namespace + " " + schemaLocation, ns);
		while (ite.hasNext()) {
			OMElement node = (OMElement)ite.next();
			String name=node.getLocalName(); 
			String text=node.getText();	
			OMElement cpy=fac.createOMElement(name,ns1);
			cpy.setText(text);
			newBody.addChild(cpy);
		}
		return new InputStreamReader(new ByteArrayInputStream(newBody.toString().getBytes()));
	}

	private static final Schema loadSchema(MessageContext req, String schemaLocation) throws AxisFault {
		Schema sch = null;
		Object location = new File(schemaLocation);
		if (!(((File)location).exists())) {
			try {
				location = new URL(schemaLocation);
			}
			catch (MalformedURLException e) {
				throw new AxisFault(schemaLocation + " not found!");
			}
		}
		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try {
			if (location instanceof File) {
				sch = factory.newSchema((File)location);
			}
			else if (location instanceof URL) {
				sch = factory.newSchema((URL)location);
			}
		}
		catch (SAXException e) {
			e.printStackTrace();
			throw new AxisFault(e.getMessage());
		}
		return sch;
	}
}