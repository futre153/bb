package org.pabk.net.http;

import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import org.pabk.util.Huffman;

public class HttpClientConst {
	public static final int NO_AUTHENTICATION = 0;
	public static final int NEGOTIATE_AUTHENTICATION = 1;
	public static final String NEGOTIATE_STRING = "Negotiate";
	public static final String KRB5_PRINCIPAL_NAME_OID = "1.2.840.113554.1.2.2.1";
	public final static String KERB_V5_OID = "1.2.840.113554.1.2.2";
	public static final String SPNEGO = "1.3.6.1.5.5.2";
	public static final String TRUST_STORE_KEY = "javax.net.ssl.trustStore";
	public static final String TRUST_STORE_PASSWORD_KEY = "javax.net.ssl.trustStorePassword";
	public final static String PROXY_HOST_KEY = "http.proxyHost";
	public static final String PROXY_PORT_KEY = "http.proxyPort";
	
	
	
	private static final String PRO_PATH = "/org/pabk/net/http/http-client.properties";
	private static Properties user;
	
	public static final String KRB5_CONF_KEY = "java.security.krb5.conf";
	public static final String LOGIN_CONF_KEY = "java.security.auth.login.config";
	public static final String USE_SUBJ_CRED_ONLY_KEY = "javax.security.auth.useSubjectCredsOnly";
	//***
	//public static final String DEFAULT_PROTOCOL_KEY = "org.pabk.net.http.defaultProtocol";
	//public static final String DEFAULT_PORT_KEY = "org.pabk.net.http.defaultPort";
	//public static final String DEFAULT_FILE_KEY = "org.pabk.net.http.defaulFile";
	//***
	public static final String USER_NAME_KEY = "org.pabk.net.http.auth.user";
	public static final String USER_PASSWORD_KEY = "org.pabk.net.http.auth.password";
	//public static final String HA_TABLE_KEY = "org.pabk.util.haTable";
	public static final String CONTENT_LENGTH = "Content-Length";
	public static final String CONTENT_TYPE = "Content-Type";
	public static final String ACTION = "SOAPAction";

	public static final String POST_METHOD = "POST";
	public static final String PUT_METHOD = "PUT";
	public static final String HEAD_METHOD = "HEAD";
	
	/*
	public static final String WSDL_PARSER_ERROR = "WSDL schema for OS service is not found!";
	public static final String XML_PARSER_ERROR = "XML parser failed to load!";
	public static final String SOAP_VERSION_URI = "http://schemas.xmlsoap.org/soap/envelope/";
	*/
	/*
	public static final String CHK1_ELEMENT = "Confirmation";
	public static final String CHK1_TEXT = "OK";
	public static final String CHECK_UNSUCCESSFUL_TEXT = "Response not found";
	public static final String FAULTSTRING_ELEMENT = "faultstring";
	public static final String UNKNOWN_ERROR_NOTATION = "Unknown Error";
	*/
	/*
	public static final String NOTS_WSDL_URI_KEY = "org.os.notification.wsdlURI";
	public static final String OS_ENDPOINT_KEY = "org.os.notification.endpoint";
	public static final String NOTIFICATION_ACTION_KEY = "org.os.notification.action";
	public static final String USE_PROXY_KEY = "org.pabk.net.http.useProxy";
	*/
	public static final String PROXY_SERVICE_KEY = "krb5.proxy.serviceName";
	public static final String PROXY_AUTHENTICATION = "Proxy-Authorization";
	public static final String AUTHENTICATION = "Authorization";
	public static final String GET_METHOD = "GET";
	/*
	public static final String MSG_TYPE = "MsgType";
	public static final String XML_CHECK_DISABLED_KEY = "org.fds.XMLCheck";
	public static final String AUTHORIZATION_NOTS = "0";
	public static final String DOBI_NOTS = "6";
	public static final String FDS_SCHEMA_LOCATION_KEY = "org.fds.AuthSchemaLocation";
	public static final String FDS_SCHEMA_LOCATION_DOBI_KEY = "org.fds.DobiSchemaLocation";
	public static final String FDS_ROOT_ELEMENT = "SOAPBody";
	public static final String FDS_NAMESPACE_PREFIX = "";
	public static final String FDS_NAMESPACE = "http://www.firstdata.sk/txn-notify/soap";
	public static final String SCHEMA_INSTANCE_PREFIX = "xsi";
	public static final String SCHEMA_LOCATION_ATTR_NAME = "schemaLocation";
	public static final String CONFIRMATION = "Confirmation";
	public static final String CONFIRMATION_CONTENT = "OK";
	public static final String CHECK_RESPONSE_KEY = "org.os.checkResponse";
	public static final String NOTIFICATION_REQUEST1_ROOT = "Notification";
	*/
	/*
	public static final String PS = System.getProperty("file.separator");
	public static final String LS = System.getProperty("line.separator");
	public static final String SCRAP_PATH_KEY = "org.pabk.util.msgStore";
	public static final String SCRAP_FILENAME = "scrap";
	public static final String SEPARATOR = "_";
	public static final String EXTENTION = ".txt";
	public static final String FAULT = "FAULT";
	public static final String NULL_FAULT_CODE = "OK";
	public static final String REQUEST = "REQUEST";
	public static final String EXECUTION_TIME = "EXECUTIONTIME";
	*/
	public static final String DEBUG_KEY = "org.pabk.debug";
	public static final String TEXT_JAVASCRIPT_CONTENT = "text/javascript";
	public static final String TEXT_HTML_CONTENT = "text/html";
	public static final String[] CONTENT_TYPES = {
		null,
		//text content types
		"text/html",
		"text/css",
		"text/javascript",
		"text/xml",
		//application content types
		"application/json",
		"application/x-h2-db"
	};
	public static final String[] ENCODINGS =  {
		null,
		"UTF-8",
		"US-ASCII",
		"ISO-8859-1",
		"UTF-16BE",
		"UTF-16LE",
		"UTF-16"
	};
	public static final String[] CONTENT_ENCODINGS = {
		null,
		"gzip"
	};
	
	public static final int NULL = 	0x00;
	public static final int ONE = 	0x01;
	public static final int FOUR = 	0x04;
	public static final int FIVE = 	0x05;
	public static final int SIX = 	0x06;
	
	public static final int PLAIN_HTTP_CONTENT = ONE;
	public static final int TEXT_XML_CONTENT = FOUR;
	
	public static final int APP_JSON_CONTENT = FIVE;
	public static final int APP_H2DB_CONTENT = SIX;
	
	
	public static final int UTF8_ENCODING = ONE;
	
	public static final int GZIP_INDEX = ONE;
	
	public static final String TRANSFER_ENCODING = "Transfer-Encoding";
	public static final String CHUNKED = "chunked";
	public static final String CONTENT_ENCODING = "Content-Encoding";
	public static final int DEFAULT_ENCODING = UTF8_ENCODING;
	
	
	/*
	public static final String URL="http://www.sme.sk";
	public static final String PROXY_HOST="proxy.pabk.sk";
	public static final String PROXY_PORT="3128";
	public final static String KRB5_CONF = "C:\\PrepApp\\conf\\krb5.conf";
    public final static String LOGIN_CONF = "C:\\PrepApp\\conf\\login.conf";
	*/
	
	private HttpClientConst(){}
	
	public static String get(String key) throws Exception {
		if(user==null) {
			try {
				HttpClientConst.loadProperties();
			}
			catch (Exception e) {
				throw e;
			}
		}
		return user.getProperty(key);
	}
	
	public static void setSysProperty(String key) throws Exception {
		System.setProperty(key,get(key));
	}
	

	public static void setCryptedSysProperty(String key) throws Exception {
		System.setProperty(key,Huffman.decode(get(key), null));
	}
	
	
	private static void loadProperties() throws InvalidPropertiesFormatException, IOException {
		Properties pro=new Properties(loadDefaultProperties());
		pro.loadFromXML(HttpClientConst.class.getResourceAsStream(PRO_PATH));
		user=pro;
	}

	private static Properties loadDefaultProperties() {
		Properties pro=new Properties();
		return pro;
	}

	
}
