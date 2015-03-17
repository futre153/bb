package org.pabk.http.tserver;


public class TConst {
	
	public static final String CHECK_BROWSER_COMPATIBILITY = "checkBrowserCompatibility";
	public static final String SECRET_STORAGE = "secretStorage";
	
	public static final String BASIC_AUTHENTICATION 	= "Basic";
	public static final String NTLM_AUTHENTICATION 		= "NTLM";
	public static final String DIGEST_AUTHENTICATION 	= "Digest";
	public static final String NEGOTIATE_AUTHENTICATION = "Negotiate";
	
	//private static final String AUTH_STATE_NOT_DEF = "Authentication state %d is not allowed";
	public static final String NOT_SUPPORTED = "Operation not Supported";
	public static final String CHAR_NOT_FOUND = "Character %s not found";
	public static final String QSTRING_ERROR = "Failed to read quoted string";
	public static final String PARAMETERS_ERROR = "Input parameters are null or have wrong type";
	public static final String CHECK_DIGEST_ERROR = "Error while checking of digest response";
	public static final String USER_NOT_FOUND = "User %s not found";
	
	public static final String SETTER_PREFIX = "set";
	public static final String MD5 = "MD5";
	
	public static final String NTLM_NEGOTIATE_MESSAGE = "NTLMNegotiateMsg";
	public static final String NTLM_CHALLENGE_MESSAGE = "NTLMChallengeMsg";
	@SuppressWarnings("unused")
	private static final String DIGEST_NEGOTIATE_MESSAGE = "DigestNegotiateMsg";
	public static final String DIGEST_CHALLENGE_MESSAGE = "DigestChallengeMsg";
	
	public static final int NO_ACTION	 	= 0x00;
	public static final int IN_PROGRESS 	= 0x01;
	final static int BASIC 			= 0x10;
	public final static int NTLM 			= 0x20;
	public final static int DIGEST			= 0x30;
	static final int NEGOTIATE		= 0x50;
	
	public static final String REALM = "realm";
	public static final String NONCE = "nonce";
	public static final String OPAQUE = "opaque";
	
	public static final char EQUAL = '=';
	public static final char COMMA = ',';
	public static final char QUOTE = '"';
	public static final char COLON = ':';
	
	public static final String AUTH_USERNAME = "UserName";
	public static final String AUTH_DOMAIN = "DomainName";

	public static final String MSIE11_AND_ABOVE_USER_AGENT = "Mozilla/5\\.0 \\(Windows NT \\d+\\.\\d+; Trident/\\d+\\.\\d+; rv:\\d+\\.\\d+\\) like Gecko";
	public static final String MSIE_BROWSER_ID = "MSIE";
	public static final String MSIE11_BROWSER_VS_MASK = "rv:\\d+\\.\\d+";
	public static final String MSIE11_BROWSER_VS_DELIMITER = ":";
	public static final String MSIE10_BROWSER_VS_DELIMITER = " ";
	public static final String MSIE10_BROWSER_VS_MASK = MSIE_BROWSER_ID + MSIE10_BROWSER_VS_DELIMITER + "\\d+\\.\\d+";
	
	public static final String FIREFOX_BROWSER_ID = "Firefox";
	public static final String FIREFOX_BROWSER_VS_DELIMITER = "/";
	public static final String FIREFOX_BROWSER_VS_MASK = FIREFOX_BROWSER_ID + FIREFOX_BROWSER_VS_DELIMITER + "\\d+\\.\\d+";
	
	public static final String CHROME_BROWSER_ID = "Chrome";
	public static final String CHROME_BROWSER_VS_DELIMITER = "/";
	public static final String CHROME_BROWSER_VS_MASK = CHROME_BROWSER_ID + CHROME_BROWSER_VS_DELIMITER + "\\d+\\.\\d+";

	public static final String DIGEST_LOGIN_INDEX = "org.pabk.http.tserver.digestLogin";
	public static final String NTLM_LOGIN_INDEX = "org.pabk.http.tserver.ntlmLogin";
	public static final String SHUTDOWN_PORT = "shutdownPort";
	public static final String DEFAULT_SHUTDOWN_PORT = "8082";
	public static final String DEFAULT_SHUTDOWN_PASSWORD = "g9pICxJt9AA=";
	public static final String CREDENTIALS_EXTENSION = ".credentials";
	public static final String DEFAULT_CREDENTIALS = "user";
	

		
	
	private TConst(){}
}
