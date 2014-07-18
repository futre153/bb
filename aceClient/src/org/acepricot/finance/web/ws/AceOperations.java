package org.acepricot.finance.web.ws;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.acepricot.finance.web.http.HTTPClient;
import org.acepricot.finance.web.http.WSContent;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class AceOperations {
	
	private static final int MAX_CONNECTION_ATTEMPT = 0x0A;
	private static final boolean USE_PROXY = true;
	private static final String HTTP_CALL_ERROR = "HTTP call failed. Server returned %d";
	private static final String DEFAULT_PROTOCOL = "https";
	private static final String DEFAULT_HOST = "23.239.26.122";
	private static final int DEFAULT_PORT = 8443;
	private static final String DEFAULT_FILE = "/acepricot/services/AceOperations";
	private static final String DEFUALT_SOAP_ACTION = "getMessage";
	
	public static synchronized Message getMessage(Message message) throws ParserConfigurationException, TransformerException, SAXException, IOException {
		Document d = message.createSOAPEnvelope();
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(d);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		StreamResult result = new StreamResult(out);
		transformer.transform(source, result);
		byte[] b = out.toByteArray();
		//System.out.println(new String(b));
		InputStream in = null;
		Exception e = null;
		for(int i = 0; i < MAX_CONNECTION_ATTEMPT; i++) {
			try {
				HTTPClient client = new HTTPClient();
				in = callServer(client, b, DEFAULT_PROTOCOL, DEFAULT_HOST, DEFAULT_PORT, DEFAULT_FILE);
			}
			catch(Exception e1) {
				// place for logging connection error
				e = e1;
				in = null;
				continue;
			}
			break;
		}
		if(in == null) throw new IOException(e);
		return message.parseSOAP(in);
	}
	
	private static synchronized InputStream callServer(HTTPClient client, byte[] b, String protocol, String host, int port, String file) throws IOException {
		WSContent con = new WSContent();
		con.setContent(b);
		con.setAction(DEFUALT_SOAP_ACTION);
		client.setURL(protocol, host, port, file);
		client.setContent(con);
		int status = client.execute(USE_PROXY);
		switch(status) {
		case HTTPClient.HTTP_OK:
			return client.getInputStream();
		default:
			InputStream err = client.getErrorStream();
			if(err != null) {
				for(int i = err.read(); i >= 0; i = err.read()) {
					System.err.print((char)i);
				}
				System.err.println();
			}
		}
		throw new IOException(String.format(HTTP_CALL_ERROR, status));
	}
	
}
