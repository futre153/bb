package org.pabk.web;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;

import org.pabk.html.Tag;

public abstract class BaseHtmlObject extends HtmlObjectImpl {
	
	protected static final String APP_TITLE = "PREPAID Card SMS Notification viewer";
	protected static final String APP_CHARSET = "UTF-8";
	protected Tag html;
	
	@Override
	public void doFinal(Closeable out) throws IOException {
		getObject().doFinal((PrintWriter) out, 0);
	}
	
	@Override
	public Tag getObject() {
		return html;
	}
}
