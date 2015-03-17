package org.pabk.http.tserver;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManagerFactory;
import javax.security.auth.callback.CallbackHandler;

import org.pabk.util.Huffman;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;

public class TServer extends Thread {
	
	private static final String TSERVER_PROPS_RESOURCE = "/org/pabk/http/tserver/tserver.xml";
	private static final String HTTP_PORT = "httpPort";
	private static final String DEFAULT_HTTP_PORT = "8081";
	private static final int MAX_STOP_DELAY = 1;
	private static final String SERVER_CONTEXT = "context";
	private static final String DEFAULT_SERVER_CONTEXT = "/org/pabk/http/tserver/context.xml";
	private static final String AUTH_CONTEXT = "authContext";
	private static final String RESOURCE_PREFIX = "classpath:";
	private static final String HTTPS_PORT = "httpsPort";
	private static final String DEFAULT_HTTPS_PORT = "8444";
	private static Properties tProps = null;
	private static HttpServer svr;
	private static HttpsServer svrs;
	private static String realm;
	private static int authType = TConst.NO_ACTION;
	private static Properties secretStorage;
	private static CallbackHandler callbackHandler;
	
	private static Properties pro;
	private static boolean shutdown = false;
	private static String shutdownPassword;
	private static ExecutorService httpThreadPools;
	private static ExecutorService httpsThreadPools;
	private static ServerSocket ss;
			
	protected TServer(Properties tProps) {
		if(tProps == null) { 
			System.err.println("Tiny server failed to start.");
		}
		else {
			pro = tProps;
		}
	}
	
	public void run () {
		try {
			TServer.setAuthContext(pro.getProperty(AUTH_CONTEXT));
			int port = Integer.parseInt(pro.getProperty(HTTP_PORT, DEFAULT_HTTP_PORT));
			int httpsPort = Integer.parseInt(pro.getProperty(HTTPS_PORT, DEFAULT_HTTPS_PORT));
			final int sPort = Integer.parseInt(pro.getProperty(TConst.SHUTDOWN_PORT, TConst.DEFAULT_SHUTDOWN_PORT));
			if(shutdownPassword == null) {
				try {
					shutdownPassword = Huffman.decode(TConst.DEFAULT_SHUTDOWN_PASSWORD, null);
				} catch (Exception e) {
					throw new IOException (e);
				}
			}
			if(port == httpsPort && port > 0) {
				throw new IOException("Conflict on httpPort " + port);
			}
			else {
				if((port > 0 || httpsPort > 0) && (port == sPort || httpsPort == sPort)) {
					throw new IOException("Conflict on port " + sPort);
				}
			}
			if(sPort < 0) {
				throw new IOException("Port has a negative value");
			}
			if(port > 0) { 
				svr = HttpServer.create(new InetSocketAddress(port), 0);
				TServer.setContext(svr, pro.getProperty(SERVER_CONTEXT, DEFAULT_SERVER_CONTEXT));
				TServer.httpThreadPools = Executors.newFixedThreadPool(50);
				svr.setExecutor(TServer.httpThreadPools);
				svr.start();
				System.out.println("HTTP connector starts on port " + port);
			}
			if(httpsPort > 0) {
				svrs = HttpsServer.create(new InetSocketAddress(httpsPort), 0);
				
				TServer.setContext(svrs, pro.getProperty(SERVER_CONTEXT, DEFAULT_SERVER_CONTEXT));
				svrs.setExecutor(null);	
				SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
				SSLContext.setDefault(sslContext);
				KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
				InputStream in = getFileInputStream(System.getProperty("javax.net.ssl.keyStore"));
			    KeyStore ks = KeyStore.getInstance("JKS");
			    ks.load(in, System.getProperty("javax.net.ssl.keyStorePassword").toCharArray());
			    kmf.init(ks, System.getProperty("javax.net.ssl.keyStorePassword").toCharArray());
			    TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
				in = getFileInputStream(System.getProperty("javax.net.ssl.trustStore"));
			    KeyStore ts = KeyStore.getInstance("JKS");
			    ts.load(in, System.getProperty("javax.net.ssl.trustStorePassword").toCharArray());
			    tmf.init(ts);
			    sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());
				HttpsConfigurator httpsConf = new HttpsConfigurator(sslContext) {
					public void configure(HttpsParameters params) {
						SSLContext c = getSSLContext();
						SSLParameters sslParams = c.getDefaultSSLParameters();
						sslParams.setNeedClientAuth(true);
						sslParams.setNeedClientAuth(true);
						params.setSSLParameters(sslParams);
					}
				};
				svrs.setHttpsConfigurator(httpsConf);
				TServer.httpsThreadPools = Executors.newFixedThreadPool(50);
				svrs.setExecutor(TServer.httpsThreadPools);
				svrs.start();
				System.out.println("HTTPS connector starts on port " + httpsPort);
			}
			if(port > 0 || httpsPort > 0) {
				Thread t = new Thread() {
					public void run() {
						try {
							TServer.ss = new ServerSocket(sPort);
							while(!TServer.shutdown ) {
								Socket s = null;
								try {
									ss.setSoTimeout(1000);
									s = ss.accept();
								}
								catch(SocketTimeoutException e) {
									//System.out.println("shutwown listen");
									continue;
								}
								if(s != null) {
									try {
										s.setSoTimeout(10000);
										BufferedReader buf = new BufferedReader(new InputStreamReader(s.getInputStream()));
										String str = buf.readLine();
										System.out.println(str);
										if(str.equalsIgnoreCase(TServer.shutdownPassword)) {
											TServer.shutdown = true;
										}
										try {
											s.close();
										}
										catch  (Exception e){}
									}
									catch(SocketTimeoutException e) {
										//System.out.println("shutwown client");
										continue;
									}
								}
								//System.out.println("shutwown listen");
							}
							ss.close();
							TServer.stopServer();
						} catch (Exception e) {
							e.printStackTrace();
						}
						
					}
				};
				t.setDaemon(true);
				t.start();
				System.out.println("Shutdown is open on port " + sPort);
			}
				
		} catch (IOException | InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchAlgorithmException | KeyManagementException | KeyStoreException | UnrecoverableKeyException | CertificateException e) {
			e.printStackTrace();
		}
	}
	
	public static InputStream getFileInputStream(String property) throws IOException {
		if(property.startsWith(RESOURCE_PREFIX)) {
			return TServer.class.getResourceAsStream(property.replaceFirst(RESOURCE_PREFIX, ""));
		}
		else {
			return new FileInputStream(property);
		}
	}

	private static void setAuthContext(String authCtx) throws IOException {
		Properties auth = new Properties();
		try {
			auth.loadFromXML(new FileInputStream(authCtx));
		}
		catch (Exception e) {
			try {
				auth.loadFromXML(TServer.class.getResourceAsStream(authCtx));
			}
			catch(Exception e1) {
				throw new IOException (e1);
			}
		}
		Enumeration<?> authNames = auth.propertyNames();
		while(authNames.hasMoreElements()) {
			String authName = authNames.nextElement().toString();
			Object authValue = auth.get(authName);
			//System.out.println(authName.replaceAll("\\.", "/"));
			try {
				Field f = TServer.class.getDeclaredField(authName);
				Method m = TServer.class.getDeclaredMethod(TConst.SETTER_PREFIX + f.getName().substring(0, 1).toUpperCase() + f.getName().substring(1), new Class<?>[]{authValue.getClass()});
				m.invoke(null, new Object[]{authValue});
			}
			catch (Exception e) {
				System.setProperty(authName, authValue.toString());
			}
		}
	}

	private static void removeContext(HttpServer svr, String context) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		Properties ctx = new Properties();
		try {
			ctx.loadFromXML(new FileInputStream(context));
		} catch (Exception e) {
			ctx.loadFromXML(TServer.class.getResourceAsStream(context));
		}
		Enumeration<?> ctxNames = ctx.propertyNames();
		while(ctxNames.hasMoreElements()) {
			String ctxName = ctxNames.nextElement().toString();
			//System.out.println(ctxName.replaceAll("\\.", "/"));
			svr.removeContext(ctxName.replaceAll("\\.", "/"));
		}
	}
	
	private static void setContext(HttpServer svr, String context) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		Properties ctx = new Properties();
		try {
			ctx.loadFromXML(new FileInputStream(context));
		} catch (Exception e) {
			ctx.loadFromXML(TServer.class.getResourceAsStream(context));
		}
		Enumeration<?> ctxNames = ctx.propertyNames();
		while(ctxNames.hasMoreElements()) {
			String ctxName = ctxNames.nextElement().toString();
			//System.out.println(ctxName.replaceAll("\\.", "/"));
			svr.createContext(ctxName.replaceAll("\\.", "/"), (HttpHandler) Class.forName(ctx.getProperty(ctxName)).newInstance());
		}
	}

	public static void main(String[] args) {
		/*try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.reset();
			String a1 = "eric:testrealm:spyglass";
			String a2 = "GET:/simp/";
			a1 = DatatypeConverter.printHexBinary(md5.digest(a1.getBytes())).toLowerCase();
			md5.reset();
			a2 = DatatypeConverter.printHexBinary(md5.digest(a2.getBytes())).toLowerCase();
			md5.reset();
			String digest = DatatypeConverter.printHexBinary(md5.digest((a1 + ":72540723369:" + a2).getBytes())).toLowerCase();
			System.out.println(digest);
			System.exit(1);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		/*
		Enumeration<?> e = System.getProperties().propertyNames();
		while (e.hasMoreElements()) {
			Object name = e.nextElement();
			Object value = System.getProperty((String) name);
			System.out.println(name);
			System.out.println(value.getClass());
			System.out.println(value);
			System.out.println();
		}*/
		/*double d = 1.12;
		System.out.println(String.format("%s", Double.toString(d)));*/
		/*try {
			System.out.println(Huffman.encode("shutdown", null));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		//System.exit(1);
		
		startServer(args != null && args.length > 0 ? args[0] : null);
	}
	
	public static void stopServer() throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
		if(TServer.ss != null || (! TServer.ss.isClosed())) {
			try {
				TServer.ss.close();
			}
			catch (Exception e) {}
		}
		System.out.println("Shutdown port was closed");
		TServer.removeContext(svr, pro.getProperty(SERVER_CONTEXT, DEFAULT_SERVER_CONTEXT));
		System.out.println("Http connector stopping ... " + new Date());
		svr.stop(MAX_STOP_DELAY);
		TServer.httpThreadPools.shutdown();
		System.out.println("Http connector stopped "  + new Date());
		System.out.println("Https connector stopping ... "  + new Date());
		svrs.stop(MAX_STOP_DELAY);
		System.out.println("Https connector stopped "  + new Date());
		TServer.httpsThreadPools.shutdown();
		tProps = null;
	}
	
	public static void startServer(String proPath) {
		if(tProps != null) {
			System.err.println("Tiny server instance is allready started.");
		}
		tProps = new Properties();
		if(proPath != null && proPath.length() > 0) {
			try {
				tProps.loadFromXML(new FileInputStream(proPath));
			} catch (Exception e) {
				loadDefaultProperties(e);
			}
		}
		else {
			loadDefaultProperties(null);
		}
		new TServer(tProps).start();
	}
	
	private static void loadDefaultProperties(Exception e) {
		try {
			tProps.loadFromXML(TServer.class.getResourceAsStream(TSERVER_PROPS_RESOURCE));
		} catch (Exception e1) {
			if(e != null) {
				e.printStackTrace();
			}
			e1.printStackTrace();
			tProps = null;
		}
	}

	public static String getRealm() {
		return realm;
	}

	public static void setRealm(String realm) {
		TServer.realm = realm;
	}

	public static int getAuthType() {
		return authType;
	}

	public static void setAuthType(String authType) {
		if(authType.equalsIgnoreCase(TConst.BASIC_AUTHENTICATION)) {
			TServer.authType = TConst.BASIC;
		}
		else if(authType.equalsIgnoreCase(TConst.NTLM_AUTHENTICATION)) {
			TServer.authType = TConst.NTLM;
		}
		else if(authType.equalsIgnoreCase(TConst.DIGEST_AUTHENTICATION)) {
			TServer.authType = TConst.DIGEST;
		}
		else if(authType.equalsIgnoreCase(TConst.NEGOTIATE_AUTHENTICATION)) {
			TServer.authType = TConst.NEGOTIATE;
		}
	}
	
	public static String getProperty(String key) {
		return TServer.pro.getProperty(key);
	}

	public static Properties getSecretStorage() {
		return secretStorage;
	}

	public static void setSecretStorage(String secretStorage) {
		Properties prop = new Properties();
		try {
			prop.loadFromXML(TServer.getFileInputStream(secretStorage));
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			TServer.secretStorage = prop;
		}
	}

	public static CallbackHandler getCallbackHandler() {
		return callbackHandler;
	}

	public static void setCallbackHandler(String cb) {
		try {
			callbackHandler = (CallbackHandler) Class.forName(cb).newInstance();
		} catch (Exception e) {}
	}
}
