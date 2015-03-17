package org.pabk.web;

import org.pabk.html.Doctype;
import org.pabk.html.Html;

public class RefreshHtmlObject extends BaseHtmlObject {
	private static final String REFRESH_CONTENT = "0; url=home";

	RefreshHtmlObject() {
		Html h = Html.getInstance(APP_TITLE);
		h.setDoctype(Doctype.HTML_5);
		h.addMetadata(null, null, null, null, APP_CHARSET);
		h.addMetadata("refresh", null, null, REFRESH_CONTENT, null);
		html = h;
	}
}
