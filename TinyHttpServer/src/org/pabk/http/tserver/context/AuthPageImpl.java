package org.pabk.http.tserver.context;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import com.sun.net.httpserver.HttpExchange;

public abstract class AuthPageImpl extends AuthContext implements Page {
	
	private int rs;
	private String body;
		
	final public void handle(HttpExchange ex) throws IOException {
		super.handle(ex);
		System.out.println(getResponseCode());
		int rs = getResponseCode();
		switch(rs) {
		case 403:
			setPage(AuthPageImpl.getFrobiddenPage());
			ex.getResponseHeaders().remove("Connection");
			ex.getResponseHeaders().add("Connection", "close");
			break;
			default:
				if(super.isBrowserCheck()) {
					setPage();
				}
				else {
					setPage(AuthPageImpl.getSupportBrowserPage());
				}
		}
		ex.sendResponseHeaders(getResponseCode(), getLength());
		writeBody(ex.getResponseBody(), body);
	}
	
	static String getFrobiddenPage() {
		StringBuffer sb = new StringBuffer();
		String nl = System.getProperty("line.separator");
		sb.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">" + nl +
				"<html>" + nl +
				"  <head>" + nl +
				"    <META HTTP-EQUIV = \"PRAGMA\" CONTENT = \"NO-CACHE\">" + nl +
				"    <meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\">" + nl +
				"    <title>Welcome page</title>" + nl +
				"  </head>" + nl +
				"  <body>" + nl +
				"   <p>Your are not authorized to access to this page.<br><br>" + nl +
				"   </p>" + nl +
				"  <p>" + nl +
				"    <img src=\"http://www.w3.org/Icons/valid-html401\" alt=\"Valid HTML 4.01 Strict\" height=\"31\" width=\"88\">" + nl +
				"  </p>" + nl +
				"  </body>" + nl +
				"</html>");
		return sb.toString();
	}

	static String getSupportBrowserPage() {
		ArrayList<Object[]> bs = BaseContext.getSupportedBrowsers();
		StringBuffer sb = new StringBuffer();
		String nl = System.getProperty("line.separator");
		sb.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">" + nl +
				"<html>" + nl +
				"  <head>" + nl +
				"    <meta HTTP-EQUIV = \"PRAGMA\" CONTENT = \"NO-CACHE\">" + nl +
				"    <meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\">" + nl +
				"    <title>Welcome page</title>" + nl +
				"  </head>" + nl +
				"  <body>" + nl +
				"    <p>Your browser is not currently supported.<br><br>" + nl +
				"		 Supported browser/s is/are<br><br>"  + nl);
		for(int i = 0; i < bs.size(); i ++) {
			sb.append("		 " + bs.get(i)[0] + " " + bs.get(i)[1] + " and above<br>" + nl);
		}
		sb.append("  </p>" + nl +
				"  <p>" + nl +
				"    <img src=\"http://www.w3.org/Icons/valid-html401\" alt=\"Valid HTML 4.01 Strict\" height=\"31\" width=\"88\">" + nl +
				"  </p>" + nl +
				"  </body>" + nl +
				"</html>");
		return sb.toString();
	}
	
	final int getResponseCode() {
		return rs;
	}

	@Override
	final void setResponseCode(int i) {
		this.rs = i;
	}


	@Override
	public int getLength() {
		return body.length();
	}

	@Override
	public void writeBody(OutputStream out, Object body) throws IOException {
		if(body instanceof String) {
			out.write(((String) body).getBytes());
			out.close();
		}
	}

	@Override
	public void setPage(Object ...objs) {
		if(objs.length > 0) {
			if(objs[0] instanceof String) {
				this.body = (String) objs[0];
			}
		}
	}
	
}
