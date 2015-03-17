package org.pabk.proxyforwarder;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.pabk.net.http.HttpClientConst;
import org.pabk.net.http.SimpleClient;
import org.pabk.net.http.auth.BBAuthenticator;

public class ProxyForwarder {


	private static final int PF_PORT = 8888;
	private static BBAuthenticator authenticator;
	static String logPath = null;
	static String proxy = null;
	static int port = 0;
	static boolean debug = false;
	
	public static void main(String[] args) {
		int pfPort = PF_PORT;
		if(args.length > 0) {
			try {
				pfPort = Integer.parseInt(args[0]);
			}
			catch (Exception e) {
			}
		}
		String logPath = null;
		if(args.length > 1) {
			File dir = new File(args[1]);
			if(dir.exists() && dir.isDirectory()) {
				logPath = args[1];
			}
			dir = null;
		}
		try {
			start(pfPort, logPath);
		}
		catch (IOException e) {
			System.out.println("Failed to startApp:\n" + e.getMessage() + "\n" + e.getLocalizedMessage());
			System.exit(1);
		}
	}
	
	public static void start(int pfPort, String logPath) throws IOException {
		try {
			proxy = HttpClientConst.get(HttpClientConst.PROXY_HOST_KEY);
			port = Integer.parseInt(HttpClientConst.get(HttpClientConst.PROXY_PORT_KEY));
			debug = Boolean.parseBoolean(HttpClientConst.get(HttpClientConst.DEBUG_KEY));
		}
		catch (Exception e) {
			throw new IOException("Proxy host or proxy port are not defined in properties file");
		}
		ProxyForwarder.logPath = logPath;
		debug = debug && logPath != null;
		ServerSocket ss = new ServerSocket(pfPort);
		Runtime.getRuntime().addShutdownHook(new StopListeners(ss));
		try {
			setAuthenticator(SimpleClient.setAuthenticator());
		} catch (Exception e) {
			e.printStackTrace();
		}
		while (true) {
			Socket s = null;
			try {
				s = ss.accept();
			}
			catch (IOException e) {
				break;
			}
			Listener l = new Listener(s);
			l.setDaemon(true);
			l.start();
		}

	}
	
	
	static BBAuthenticator getAuthenticator() {
		return authenticator;
	}

	static void setAuthenticator(BBAuthenticator authenticator) {
		ProxyForwarder.authenticator = authenticator;
	}

}
