package org.acepricot.finance.web.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;

public class HTTPClient {
	
	private static final String DEFAULT_PROTOCOL = "HTTP";
	private static final int DEFAULT_PORT = 80;
	private static final String DEFAULT_FILE = "/";
	public static final String PROXY_HOST = "proxy.pabk.sk";
	public static final int PROXY_PORT = 3128;
	private static final String CONTENT_TYPE = "Content-Type";
	private static final String CONTENT_LENGTH = "Content-Length";
	private static final String ACTION = "SOAPAction";
	public static final int HTTP_OK = 200;
	
	private static String USER_NAME="brandys";
	private static String USER_PASSWORD="Nikoleta-15";
	private static Authenticator HTTP_AUTHENTICATOR=new BBAuthenticator(USER_NAME, USER_PASSWORD);
	
	private static Proxy PROXY = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(PROXY_HOST,PROXY_PORT));
	
	private URL url;
	private Content content;
	private HttpURLConnection conn;
	public HTTPClient() {
		setContent(new DefaultContent());
	}
	public void setURL(String url) throws MalformedURLException{setURL(new URL(url));}
	public void setURL(URL url) {this.url = url;}
	public void setURL(String protocol, String host, int port, String file) throws MalformedURLException {
		if(protocol==null) {protocol=HTTPClient.DEFAULT_PROTOCOL;}
		if(host==null) {throw new NullPointerException("Hostname cannot be null");}
		if(port<0&&port>65535){port=DEFAULT_PORT;}
		if(file==null) {file=DEFAULT_FILE;}
		if(file.length()==0) {file=DEFAULT_FILE;}
		setURL(new URL(protocol,host,port,file));
	}
	public URL getURL() {return url;}
	public void setContent(Content content) {this.content = content;}
	public Content getContent() {return content;}
	
	public final int execute(boolean proxy) throws IOException {
		System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
		java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
		Authenticator.setDefault(HTTP_AUTHENTICATOR);
		if(proxy) {conn=(HttpURLConnection) url.openConnection(PROXY);}
		else {conn=(HttpURLConnection) url.openConnection();}
		int len=-1;
		if(content!=null) {len=content.getLength();}
		if(len>=0) {
			if(len>0) {
				conn.setRequestProperty(CONTENT_LENGTH, Integer.toString(len));
				conn.setFixedLengthStreamingMode(len);
			}
			else {
				throw new IOException("Chunking is not supported");
			}
			conn.setRequestProperty(CONTENT_TYPE, content.getType()+"; "+content.getEncoding());
			if(content instanceof WSContent) {
				conn.setRequestProperty(ACTION,((WSContent) content).getAction());
			}
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
		}
		else {
			conn.setRequestMethod("GET");
		}
		if(len>0) {
			OutputStream out=conn.getOutputStream();
			out.write(content.getContent());
			out.flush();
		}
		return conn.getResponseCode();
	}

	public final void close() {conn.disconnect();}
	public final InputStream getErrorStream() throws IOException {return conn.getErrorStream();}
	public final InputStream getInputStream() throws IOException {return conn.getInputStream();}
	public final int getContentLength() {return conn.getContentLength();}
	public final String getContentEndoding() {return conn.getContentEncoding();}
	public final HttpURLConnection getConnection(){return conn;}
}
