package org.pabk.http.tserver.context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.pabk.http.tserver.TConst;
import org.pabk.http.tserver.TServer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

abstract class BaseContext implements HttpHandler {
	
	private TinyHttpSession session;
	private String browserName;
	private double browserVersion = -1;
	private static ArrayList<Object[]> compBrowser;
	private boolean browserCheck;
	
	@Override
	public void handle(HttpExchange ex) throws IOException {
		ex.getResponseHeaders().add("Connection", "Keep-Alive");
		Hashtable<String, TinyCookie> cookies = TinyCookie.getCookies(ex.getRequestHeaders().get("Cookie"));
		TinyCookie tsid = cookies.get(TinyCookie.TSID);
		if(tsid != null) {
			session = TinyHttpSession.findSession(tsid);
			
		}
		if(session == null) {
			session = new TinyHttpSession();
			TinyHttpSession.getSessions().add(session);
		}
		ex.getResponseHeaders().add("Set-Cookie", new TinyCookie(TinyCookie.TSID.toString(), session.getID(), null).toString());
		setResponseCode(200);
		browserCheck = checkUserAgent(ex.getRequestHeaders().get("User-Agent"));
		//ex.setAttribute(TinyHttpSession.class.getSimpleName(), session);
	}

	private boolean checkUserAgent(List<String> list) {
		getBrowser(list);
		if(compBrowser == null) {
			loadCompatibleBrowser();
			return checkUserAgent(list);
		}
		else {
			if(list.size() == 1) {
				if(compBrowser.size() > 0) {
					return checkBrowser(browserName, browserVersion);
				}
				else {
					return true;
				}
			}
			else {
				return false;
			}
		}
	}
	
	private static boolean checkBrowser(String name, double version) {
		for(int i = 0; i < compBrowser.size(); i ++) {
			Object[] browser = compBrowser.get(i);
			//System.out.println(Arrays.toString(compBrowser.get(i)));
			if(browser[0].equals(name) && ((double) browser[1]) <= version) {
				return true;
			}
		}
		return false;
	}

	private void getBrowser(List<String> list) {
		if(list.size() > 0) {
			if(!getMSIEBrowser(list.get(0))) {
				if(!getFirefoxBrowser(list.get(0))) {
					if(!getChromeBrowser(list.get(0))) {
						this.browserName = "Unknown";
						this.browserVersion = -1;
					}
				}
			}
		}
		System.out.println(String.format("Request from %s browser version %s", this.browserName, Double.toString(this.browserVersion)));
	}

	private boolean getChromeBrowser(String uas) {
		if (uas.contains(TConst.CHROME_BROWSER_ID)) {
			browserName = TConst.CHROME_BROWSER_ID;
			browserVersion = getVersion(uas, TConst.CHROME_BROWSER_VS_MASK, TConst.CHROME_BROWSER_VS_DELIMITER);
			return true;
		}
		else {
			return false;
		}
	}

	private boolean getFirefoxBrowser(String uas) {
		if (uas.contains(TConst.FIREFOX_BROWSER_ID)) {
			browserName = TConst.FIREFOX_BROWSER_ID;
			browserVersion = getVersion(uas, TConst.FIREFOX_BROWSER_VS_MASK, TConst.FIREFOX_BROWSER_VS_DELIMITER);
			return true;
		}
		else {
			return false;
		}
	}

	private boolean getMSIEBrowser(String ua) {
		if (ua.contains(TConst.MSIE_BROWSER_ID)) {
			browserName = TConst.MSIE_BROWSER_ID;
			browserVersion = getVersion(ua, TConst.MSIE10_BROWSER_VS_MASK, TConst.MSIE10_BROWSER_VS_DELIMITER);
			return true;
		}
		else if(ua.matches(TConst.MSIE11_AND_ABOVE_USER_AGENT)) {
			browserName = TConst.MSIE_BROWSER_ID;
			browserVersion = getVersion(ua, TConst.MSIE11_BROWSER_VS_MASK, TConst.MSIE11_BROWSER_VS_DELIMITER);
			return true;
		}
		else {
			return false;
		}
	}

	private static double getVersion(String ua, String mask, String delimiter) {
		try {
			String[] s = ua.split(mask, 2);
			String v = ua.replace(s[0], "").replace(s[1], "");
			if(delimiter != null) {
				v = v.split(delimiter, 2)[1];
			}
			return Double.parseDouble(v);
		}
		catch (Exception e) {
			return -1;
		}
	}

	private static void loadCompatibleBrowser() {
		compBrowser = new ArrayList<Object[]>();
		String comp = TServer.getProperty(TConst.CHECK_BROWSER_COMPATIBILITY);
		if(comp != null) {
			String[] bs = comp.split(",");
			for(int i = 0; i < bs.length; i ++) {
				String[] b = bs[i].split(" ", 2);
				if(b[0].length() > 0) {
					double vs = -1;
					if(b[1].length() > 0) {
						try {	
							vs = Double.parseDouble(b[1]);
						}
						catch(Exception e) {}
					}
					compBrowser.add(new Object[]{b[0], vs});
				}
			}
		}
	}

	protected final TinyHttpSession getSession() {
		return session;
	}

	protected final void setSession(TinyHttpSession session) {
		this.session = session;
	}
	
	abstract int getResponseCode();
	abstract void setResponseCode(int i);

	protected final boolean isBrowserCheck() {
		return browserCheck;
	}
	
	static ArrayList<Object[]> getSupportedBrowsers() {
		return compBrowser;
	}
}
