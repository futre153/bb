package org.so.sms.notification.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import javax.wsdl.Definition;
import javax.wsdl.factory.WSDLFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Stub;
import org.apache.axis2.context.MessageContext;
import org.pabk.net.http.DefaultContent;
import org.pabk.net.http.HttpClientConst;
import org.pabk.net.http.SimpleClient;
import org.pabk.net.http.WSContent;
import org.pabk.util.Const;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

	public class _Stub extends Stub {
		
		//private static final String WSDL_URI_DOBI = "http://hubert.os.sk/ib_sms_server.asmx?WSDL";
		//private static final String OS_ENDPOINT_DOBI = "https://hubert.os.sk/ib_sms_server.asmx";
		//private static final String NOTIFICATION_ACTION_DOBI = "https://hubert.os.sk/SendSMS";
		//private static final String CHK2_ELEMENT = "SendSMSResult";
		//private static final Object CHK2_TEXT = "OK";
		 
		private static Definition _def = loadWSDL(Const.get2(Const.NOTS_WSDL_URI_KEY), Boolean.parseBoolean(Const.get2(Const.USE_PROXY_KEY)));;
		//private static Definition _def_dobi;
		private static DocumentBuilder db = loadDocumentBuilder();
		private static OMElement docx = loadXMLFileOM("http://localhost:8080/PrepApp/ngw_conf/dobi-sms-text.xml", false);
		private boolean check = true;

		public _Stub() throws Exception {
			check=Boolean.parseBoolean(Const.get(Const.CHECK_RESPONSE_KEY));
			if (_def == null) throw new AxisFault(Const.WSDL_PARSER_ERROR);
			//if (_def_dobi == null) loadWSDLDobi();
			//if (_def_dobi == null) throw new AxisFault(WSDL_PARSER_ERROR);
			
			if (docx == null) throw new AxisFault(Const.XML_PARSER_ERROR);
		}

		private static DocumentBuilder loadDocumentBuilder() {
			try {
				return DocumentBuilderFactory.newInstance().newDocumentBuilder();
			}
			catch (ParserConfigurationException e) {
				e.printStackTrace();
				return null;
			}
		}

		public void requestNotification(MessageContext msg) throws Exception  {
			//SOAPEnvelope env = NotificationRequest.createEnvelope1(msg, Stub.getFactory(Const.SOAP_VERSION_URI), _def);
			SOAPEnvelope env = NotificationRequest.createEnvelope2(msg, Stub.getFactory(Const.SOAP_VERSION_URI), docx);
			System.out.println(env);
			/*SimpleClient sc=new SimpleClient(Const.get(Const.OS_ENDPOINT_KEY),Boolean.parseBoolean(Const.get(Const.USE_PROXY_KEY)));
			WSContent con = new WSContent(env.toString(), Const.get(Const.NOTIFICATION_ACTION_KEY));
			//WSContent con = new WSContent();
			//con.setContent(env.toString());
			//con.setAction(Const.get(Const.NOTIFICATION_ACTION_KEY));
			int rs = sc.getResponseCode(con);
			switch(rs) {
			case HttpURLConnection.HTTP_OK:
				Document doc = db.parse(sc.getInputStream());
				sc.close();
				check1(doc.getDocumentElement());
				if (this.check) throw new AxisFault(Const.CHECK_UNSUCCESSFUL_TEXT);
				break;
			default:throw new AxisFault(retreiveError(sc.getErrorStream())); 
			}*/
		}

		private String retreiveError(InputStream errorStream) throws AxisFault {
			Document doc = null;
			try {
				doc = db.parse(errorStream);
			}
			catch (SAXException e) {
				throw new AxisFault(e.getMessage());
			} catch (IOException e) {
				throw new AxisFault(e.getMessage());
			}
			NodeList nl = doc.getDocumentElement().getElementsByTagName(Const.FAULTSTRING_ELEMENT);
			for (int i = 0; i < nl.getLength(); ++i) {
				Node node = nl.item(i);
				if (node.getNodeType() == 1) {
					return node.getTextContent();
				}
			}
			return Const.UNKNOWN_ERROR_NOTATION; }

		private void check1(Element elem) {
			if (!(this.check)) return;
			NodeList nl = elem.getElementsByTagName(Const.CHK1_ELEMENT);
			for (int i = 0; i < nl.getLength(); ++i) {
				Node node = nl.item(i);
				if(node.getNodeType()==Node.ELEMENT_NODE) {
					if(node.getTextContent().equals(Const.CHK1_TEXT)) {check=false;return;}
				}
			}
		}
		
		private void check2(Element elem) {
			if (!(this.check)) return;
			NodeList nl = elem.getElementsByTagName(Const.CHK2_ELEMENT);
			for (int i = 0; i < nl.getLength(); ++i) {
				Node node = nl.item(i);
				if(node.getNodeType()==Node.ELEMENT_NODE) {
					if(node.getTextContent().matches(Const.CHK2_TEXT_MASK)) {check=false;return;}
				}
			}
		}
		
  		public static Definition loadWSDL(String uri, boolean proxy) {
  			try {
	  			SimpleClient sc=new SimpleClient(uri, proxy);
				DefaultContent con=new DefaultContent();
				sc.setAuthentication(HttpClientConst.NO_AUTHENTICATION);
				switch (sc.getResponseCode(con)) {
				case HttpURLConnection.HTTP_OK:
					InputStream in=sc.getInputStream();
					return WSDLFactory.newInstance().newWSDLReader().readWSDL(null, new InputSource(in));
				default:
					return null;
				}
  			}
  			catch(Exception e) {
  				e.printStackTrace();
  				return null;
  			}
		}
  		
  		public static OMElement loadXMLFileOM (String url, boolean proxy) {
  			try {
	  			SimpleClient sc=new SimpleClient(url, proxy);
		  		DefaultContent con=new DefaultContent();
		  		sc.setAuthentication(HttpClientConst.NO_AUTHENTICATION);
		  		int response = sc.getResponseCode(con);
		  		switch (response) {
		  		case HttpURLConnection.HTTP_OK:
		  			ByteArrayOutputStream out = new ByteArrayOutputStream();
		  			InputStream in=sc.getInputStream();
		  			byte[] b = new byte[4096];
		  			int i = -1;
		  			while((i = in.read(b)) >= 0) {
		  				out.write(b, 0, i);
		  			}
		  			return OMXMLBuilderFactory.createOMBuilder(new ByteArrayInputStream(out.toByteArray())).getDocumentElement();
				default: return null;
		  		}
  			}
		  	catch (Exception e) {
		  		e.printStackTrace();
		  		return null;
		  	}
  		}
}