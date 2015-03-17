package org.pabk.http.tserver.context;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import javax.xml.bind.DatatypeConverter;

import org.pabk.http.tserver.TConst;
import org.pabk.http.tserver.TServer;

import com.sun.net.httpserver.HttpExchange;

public class TinyHttpSession {
	
	private static final  List<TinyHttpSession> sessions = new ArrayList<TinyHttpSession>();
		
	final static int DEFAULT_AUTH	= TServer.getAuthType();
	
	private static final long INACTIVITY_TIMEOUT = 3 * 60 * 1000;
	
	private final String TSID = getSessionID();
	private int authState	= TConst.NO_ACTION;
	private int authType	= TConst.NO_ACTION; 
	private TinyPrincipal principal;
	private final Hashtable<String, Object> attributes = new Hashtable<String, Object>();

	private long timeout;  
	private static SessionListener sessionListener;
	
	private static class SessionListener extends Thread {
		private static final long POOL_INTERVAL = 10 * 1000;
		private boolean shutdown;
		
		private class Sleeper {
			private synchronized void sleep(long l) throws InterruptedException {
				wait(l);
			}
		}
		
		public void run() {
			Sleeper s = new Sleeper();
			this.shutdown = false;
			boolean wait = true;
			System.out.println("Session listener START at " + new Date());
			while(!shutdown) {
				if(wait) {
					try {
						s.sleep(POOL_INTERVAL);
					} catch (InterruptedException e) {}
				}
				wait = true;
				long time = new Date().getTime();
				for(int i = 0; i < TinyHttpSession.sessions.size(); i ++) {
					if(TinyHttpSession.sessions.get(i).getTimeout() < time) {
						TinyHttpSession session = TinyHttpSession.sessions.remove(i);
						System.out.println("Session " + session.getID() + " cancelled due inactivity time-out. Principal = " + session.getPrincipal());
						i --;
					}
				}
				if(TinyHttpSession.sessions.size() == 0) {
					wait = false;
					try {
						s.sleep(POOL_INTERVAL);
					} catch (InterruptedException e) {}
					shutdown = TinyHttpSession.sessions.size() == 0;
				}
				System.out.println("Session listener end turn.");
			}
			System.out.println("Session listener STOP at " + new Date());
		}
	}
	
	TinyHttpSession () {
		if(sessionListener == null || (!sessionListener.isAlive())) {
			sessionListener = null;
			sessionListener = new SessionListener();
			sessionListener.setDaemon(true);
			sessionListener.start();
		}
		this.setTimeout(new Date().getTime() + INACTIVITY_TIMEOUT);
	}
	
	private synchronized void setTimeout(long inactivityTimeout) {
		timeout = 	inactivityTimeout;
	}
	
	private long getTimeout() {
		return this.timeout;
	}
	
	public boolean isAuthorised(HttpExchange ex) {
		return principal != null ? principal.equals(ex) : false;
	}

	private static String getSessionID() {
		while(true) {
			String id = DatatypeConverter.printHexBinary(BigInteger.probablePrime(16 * 8, new Random()).toByteArray()).substring(2);
			for(int i = 0; i < sessions.size(); i ++) {
				if(id.equals(sessions.get(i).getID())) {
					continue;
				}
			}
			return id;
		}
	}

	public static List<TinyHttpSession> getSessions() {
		return sessions;
	}

	public String getID() {
		return TSID;
	}

	static TinyHttpSession findSession(TinyCookie tsid) {
		for(int i = 0; i < sessions.size(); i ++) {
			if(sessions.get(i).getID().equals(tsid.getValue())) {
				return sessions.get(i);
			}
		}
		return null;
	}

	public int getAuthState() {
		return this.authState ;		
	}

	public int getAuthType() {
		return this.authType ;
	}

	public void resetStates(HttpExchange ex) {
		authState = TConst.NO_ACTION;
		authType = TConst.NO_ACTION;
		long timeout = new Date().getTime() + INACTIVITY_TIMEOUT;
		this.setTimeout(timeout);
		Date expires = new Date(timeout);
		ex.getResponseHeaders().add("Set-Cookie", new TinyCookie(TinyCookie.USERNAME, principal.getUsername(), expires).toString());
		ex.getResponseHeaders().add("Set-Cookie", new TinyCookie(TinyCookie.DOMAIN, principal.getDomain(), expires).toString());
	}

	public void clearState() {
		authState = TConst.NO_ACTION;
		authType = TConst.NO_ACTION;
		principal  = null;
		this.attributes.clear();
	}

	public void setAuthState(int state) {
		this.authState = state;
	}

	public void setAuthType(int type) {
		this.authType = type;
	}

	void setAttribute(String key, Object obj) {
		this.attributes.put(key, obj);	
	}
	
	Object getAttribute(String key) {
		return this.attributes.get(key);
	}

	public Object removeAttribute(String key) {
		return this.attributes.remove(key);
	}

	final void setPrincipal(TinyPrincipal principal) {
		this.principal = principal;
	}

	public TinyPrincipal getPrincipal() {
		return principal;
	}
}
