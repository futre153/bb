package org.pabk.util;

import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

public class Const {
	
	private static final String PRO_PATH = "/org/pabk/resources/properties.xml";
	private static Properties user;
	/*
	public final static String PROXY_HOST_KEY = "http.proxyHost";
	public static final String PROXY_PORT_KEY = "http.proxyPort";
	public static final String KRB5_CONF_KEY = "java.security.krb5.conf";
	public static final String LOGIN_CONF_KEY = "java.security.auth.login.config";
	public static final String USE_SUBJ_CRED_ONLY_KEY = "javax.security.auth.useSubjectCredsOnly";
	
	//***
	public static final String DEFAULT_PROTOCOL_KEY = "org.pabk.net.http.defaultProtocol";
	public static final String DEFAULT_PORT_KEY = "org.pabk.net.http.defaultPort";
	public static final String DEFAULT_FILE_KEY = "org.pabk.net.http.defaulFile";
	//***
	public static final String USER_NAME_KEY = "org.pabk.net.http.auth.user";
	public static final String USER_PASSWORD_KEY = "org.pabk.net.http.auth.password";
	public static final String HA_TABLE_KEY = "org.pabk.util.haTable";
	public static final String CONTENT_LENGTH = "Content-Length";
	public static final String CONTENT_TYPE = "Content-Type";
	public static final String ACTION = "SOAPAction";

	public static final String POST_METHOD = "POST";
	
	*/
	
	public static final String WSDL_PARSER_ERROR = "WSDL schema for OS service is not found!";
	public static final String XML_PARSER_ERROR = "XML parser failed to load!";
	public static final String SOAP_VERSION_URI = "http://schemas.xmlsoap.org/soap/envelope/";
	
	public static final String CHK1_ELEMENT = "Confirmation";
	public static final String CHK1_TEXT = "OK";
	public static final String CHECK_UNSUCCESSFUL_TEXT = "Response not found";
	public static final String FAULTSTRING_ELEMENT = "faultstring";
	public static final String UNKNOWN_ERROR_NOTATION = "Unknown Error";
	
	public static final String NOTS_WSDL_URI_KEY = "org.os.notification.wsdlURI";
	public static final String OS_ENDPOINT_KEY = "org.os.notification.endpoint";
	public static final String NOTIFICATION_ACTION_KEY = "org.os.notification.action";
	public static final String USE_PROXY_KEY = "org.pabk.net.http.useProxy";
	

	public static final String PROXY_SERVICE_KEY = "krb5.proxy.serviceName";
	public static final String PROXY_AUTHENTICATION = "Proxy-Authorization";
	public static final String AUTHENTICATION = "Authorization";
	public static final String GET_METHOD = "GET";
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
	
	public static final String DEBUG_KEY = "org.pabk.debug";
	
	/*
	public static final String URL="http://www.sme.sk";
	public static final String PROXY_HOST="proxy.pabk.sk";
	public static final String PROXY_PORT="3128";
	public final static String KRB5_CONF = "C:\\PrepApp\\conf\\krb5.conf";
    public final static String LOGIN_CONF = "C:\\PrepApp\\conf\\login.conf";
	*/
	
	private Const(){}
	
	public static String get(String key) throws Exception {
		if(user==null) {
			try {
				Const.loadProperties();
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
		pro.loadFromXML(Const.class.getResourceAsStream(PRO_PATH));
		user=pro;
	}

	private static Properties loadDefaultProperties() {
		Properties pro=new Properties();
		return pro;
	}
}