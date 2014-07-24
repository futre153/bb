package org.pabk.net.http;

import java.io.InputStream;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.pabk.net.http.auth.BBAuthenticator;
import org.pabk.util.Huffman;

public class SimpleClient {
	
	private static Proxy proxy;
	private static Authenticator auth;
	private static boolean loadProps=false;
		
	private HttpURLConnection con;
	private int authType;
	private static boolean cookiesAllowed = false;
	private static CookieManager cMan;
	
	
	public SimpleClient(String url) throws Exception {
		this(url, false);
	}
	
	public SimpleClient(String url, boolean useProxy) throws Exception {
		setAuthenticator();
		setVerifier();
		if(useProxy) {
			con=(HttpURLConnection) new URL(url).openConnection(getProxy());
		}
		else {
			con=(HttpURLConnection) new URL(url).openConnection();
		}
		//System.out.println(con.getClass().getName());
		//System.out.println("HTTP="+(con instanceof HttpURLConnection));
		//System.out.println("HTTPS="+(con instanceof HttpsURLConnection));
	}
	
	private static void setVerifier() throws Exception {
		if(!loadProps) {
			HttpClientConst.setSysProperty(HttpClientConst.TRUST_STORE_KEY);
			HttpClientConst.setCryptedSysProperty(HttpClientConst.TRUST_STORE_PASSWORD_KEY);
			loadProps=true;
		}
		
	}
			
	private static final Proxy getProxy() throws Exception {
		if(proxy==null) {
			HttpClientConst.setSysProperty(HttpClientConst.PROXY_HOST_KEY);
			HttpClientConst.setSysProperty(HttpClientConst.PROXY_PORT_KEY);
			String proxyHost=System.getProperty(HttpClientConst.PROXY_HOST_KEY);
			String proxyPort=System.getProperty(HttpClientConst.PROXY_PORT_KEY);
			if(proxyHost!=null && proxyPort!=null) {
				try {
					proxy=new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, Integer.parseInt(proxyPort)));
				}
				catch(IllegalArgumentException e) {}
			}
		}
		if(proxy==null) throw new Exception("PROXY not found");
		return proxy;
	}
	
	public static final BBAuthenticator setAuthenticator() throws Exception {
		if(auth==null) {
			HttpClientConst.setSysProperty(HttpClientConst.KRB5_CONF_KEY);
			HttpClientConst.setSysProperty(HttpClientConst.LOGIN_CONF_KEY);
			HttpClientConst.setSysProperty(HttpClientConst.USE_SUBJ_CRED_ONLY_KEY);
			
			auth=new BBAuthenticator(
	  				Huffman.decode(HttpClientConst.get(HttpClientConst.USER_NAME_KEY), null),
	  				Huffman.decode(HttpClientConst.get(HttpClientConst.USER_PASSWORD_KEY), null));
			Authenticator.setDefault(auth);
		}
		BBAuthenticator.setSpnego(((BBAuthenticator)auth).getCallbackHandler());
		return (BBAuthenticator) auth;
	}
	
	public void setAuthentication(int i) {
		authType=i;
	}
	
	public int getAuthType() {
		return authType;
	}
	
	public void setMethod(String method) throws ProtocolException  {
		con.setRequestMethod(method);
	}
	
	public void setAuthentication(String s) {
		if(s.equalsIgnoreCase(HttpClientConst.NEGOTIATE_STRING)) {
			authType=HttpClientConst.NEGOTIATE_AUTHENTICATION;
		}
		else {
			authType=HttpClientConst.NO_AUTHENTICATION;
		}
	}
	
	String getAuthString(URL url, String service) throws Exception {
		switch(authType) {
		case HttpClientConst.NO_AUTHENTICATION:
			return null;
		case HttpClientConst.NEGOTIATE_AUTHENTICATION:
			return HttpClientConst.NEGOTIATE_STRING+" "+BBAuthenticator.getSpnego(url, service);
		default: throw new NullPointerException("Authentication type "+ authType+" is not supported");
		}
	}

	final public InputStream execute(ContentImpl c) throws Exception {
		int rs=getResponseCode(c);
		//Object obj = con.getContent();
		//System.out.println(obj);
		//System.out.println(obj.getClass());
		/*
		System.out.println("Response Code="+rs);
		System.out.println("Content length = " + con.getContentLength());
		System.out.println("Content type = " + con.getContentType());
		System.out.println("Content encoding = " + con.getContentEncoding());
		System.out.println("Transfer encoding = " + con.getHeaderField("Transfer-Encoding"));
		*/
		switch (rs) {
		case HttpURLConnection.HTTP_OK:
			return con.getInputStream();
		default:
			return con.getErrorStream();
		}
	}
	
	final public void close() {
		con.disconnect();
	}
	
	final public int getResponseCode(ContentImpl c) throws Exception {
		applyAuthentication (con);
		//con.addRequestProperty("Connection", "Keep-Alive");
		//System.out.println(System.getProperty("http.keepAlive"));
		SimpleClient.applyContent(con, c);
		int rs = con.getResponseCode();/*
		if(cMan != null) {
			CookieStore cs = cMan.getCookieStore();
			List<HttpCookie> cookies = cs.getCookies();
			for(HttpCookie cookie: cookies) {
				System.out.printf ("%s%n", cookie);
				System.out.printf ("%s%n", cookie.getComment());
				System.out.printf ("%s%n", cookie.getCommentURL());
				System.out.printf ("%s%n", cookie.getDiscard());
				System.out.printf ("%s%n", cookie.getDomain());
				System.out.printf ("%s%n", cookie.getMaxAge());
				System.out.printf ("%s%n", cookie.getName());
				System.out.printf ("%s%n", cookie.getPath());
				System.out.printf ("%s%n", cookie.getPortlist());
				System.out.printf ("%s%n", cookie.getSecure());
				System.out.printf ("%s%n", cookie.getValue());
				System.out.printf ("%s%n", cookie.getVersion());
				System.out.printf ("%s%n", cookie.hasExpired());
				System.out.printf ("%s%n", cookie.isHttpOnly());
			}
		}*/
		return rs;
	}
	
	final public InputStream getInputStream() throws Exception {
		return con.getInputStream();
	}
	
	final public InputStream getErrorStream() throws Exception {
		return ((HttpURLConnection)con).getErrorStream();
	}
	/*
	private final int getResponseCode() throws IOException {
		if(con instanceof HttpURLConnection) {
			return ((HttpURLConnection) con).getResponseCode();
		}
		else if(con instanceof HttpsURLConnection) {
			return ((HttpsURLConnection) con).getResponseCode();
		}
		throw new IOException ("Invalid URLConnection class");
	}
	*/
	private final void applyAuthentication(HttpURLConnection con) throws Exception {
		if(getAuthType()!=0) {
			String service = HttpClientConst.get(HttpClientConst.PROXY_SERVICE_KEY);
			if(con.usingProxy()) {
				con.setRequestProperty(HttpClientConst.PROXY_AUTHENTICATION, getAuthString(con.getURL(), service));
			}
			else {
				con.setRequestProperty(HttpClientConst.AUTHENTICATION, getAuthString(con.getURL(), service));
			}
		}
	}
	
	private static final void applyContent(HttpURLConnection con, Content content) throws Exception {
		
		if(content == null) {
			content = new DefaultContent(); 
		}
		
		String type = content.getContentType();
		String enc = content.getCharacterEncoding();
			
		if(type != null) {
			con.setRequestProperty(HttpClientConst.CONTENT_TYPE, type + ((enc != null)?(";charset=" + enc):("")));
		}
		
		long l = content.getLength();
		if(!content.isChunked() && l >=0) {
			con.addRequestProperty(HttpClientConst.CONTENT_LENGTH, Long.toString(l));
		}
		else {
			con.addRequestProperty(HttpClientConst.TRANSFER_ENCODING, HttpClientConst.CHUNKED);
		}
		
		enc = content.getContentEncoding();
		if(enc != null) {
			con.addRequestProperty(HttpClientConst.CONTENT_ENCODING, enc);
		}
		//System.out.println(con.getRequestMethod());
		content.applyAdditionalProperties(con);
		
		if(content.getLength() == 0) {
			if(con.getRequestMethod().equals(HttpClientConst.PUT_METHOD)) {
				
			}
			else {
				con.setRequestMethod(HttpClientConst.GET_METHOD);
			}
		}
		else {
			if(con.getRequestMethod().equals(HttpClientConst.PUT_METHOD)) {
				
			}
			else {
				con.setRequestMethod(HttpClientConst.POST_METHOD);
			}
			con.setDoOutput(true);
			content.doFinal(con.getOutputStream());
		}
	}

	public static boolean isCookiesAllowed() {
		return cookiesAllowed;
	}

	public static void setCookiesAllowed(boolean cookiesAllowed) {
		if(cookiesAllowed ^ SimpleClient.cookiesAllowed) {
			if(SimpleClient.cookiesAllowed = cookiesAllowed) {
				if(cMan == null) {
					cMan = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
					CookieHandler.setDefault(cMan);
				}
			}
			else {
				if(cMan != null) {
					cMan.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
				}
			}
		}
	}

	public static void setCookie(String url, HttpCookie cookie) {
		if(isCookiesAllowed()) {
			try {
				URI uri = new URI(url);
				cookie.setDomain(uri.getHost());
				cookie.setPath(uri.getPath());
				cookie.setVersion(0);
				cookie.setHttpOnly(true);
				cMan.getCookieStore().add(uri, cookie);
				
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
	}

	public static void removeCookie(String url, HttpCookie cookie) {
		if(isCookiesAllowed()) {
			try {
				cMan.getCookieStore().remove(new URI(url), cookie);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
	}
	
}
