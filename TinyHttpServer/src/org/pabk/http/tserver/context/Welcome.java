package org.pabk.http.tserver.context;


public final class Welcome extends AuthPageImpl {
	
	private static String NL = System.getProperty("line.separator");
	private static String BODY = "" +
			"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">" + NL +
			"<html>" + NL +
			"  <head>" + NL +
			"    <META HTTP-EQUIV = \"PRAGMA\" CONTENT = \"NO-CACHE\">" + NL +
			"    <meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\">" + NL +
			"    <title>Welcome page</title>" + NL +
			"  </head>" + NL +
			"  <body>" + NL +
			"  	<p>Welcome to Tiny server. Server is ready to use. <br><br>Bye%s" + NL +		
			"  	</p>" + NL +
			"  <p>" + NL +
			"    <img src=\"http://www.w3.org/Icons/valid-html401\" alt=\"Valid HTML 4.01 Strict\" height=\"31\" width=\"88\">" + NL +
			"  </p>" + NL +
			"  </body>" + NL +
			"</html>";
	private static String LOGGED = "    <br><br>User %s is successfully logged in";
	
	public void setPage(Object ...objs) {
		if(objs.length == 0) {
			super.setPage(String.format(BODY, principal == null ? "" : String.format(LOGGED, principal.getUsername())));
		}
		else {
			super.setPage(objs);
		}
	}
}
