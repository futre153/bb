package org.pabk.web;

import org.pabk.html.Doctype;
import org.pabk.html.Html;


class MainHtmlObject extends BaseHtmlObject {

	private static final Object JQUERY_LINK = "https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js";
	private static final Object APP_MAIN_SCRIPT = "scripts/login.js";

	MainHtmlObject() {
		Html h = Html.getInstance(APP_TITLE);
		h.setDoctype(Doctype.HTML_5);
		h.addMetadata(null, null, null, null, APP_CHARSET);
		h.addScript(null, JQUERY_LINK);
		h.addScript(null, APP_MAIN_SCRIPT);
		html = h;
	}
	
}
