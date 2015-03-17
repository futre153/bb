package org.pabk.web;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public class StreamedHtmlObject extends HtmlObjectImpl {

	//private StreamedContent content;

	public StreamedHtmlObject(String contentType, InputStream in) throws UnsupportedOperationException, IOException {
		//content = new StreamedContent(contentType);
		//content.setContent(in);
	}

	
	/*public StreamedContent getObject() {
		return this.content;
	}*/

	@Override
	public void doFinal(Closeable out) throws IOException {
		//getObject().doFinal((OutputStream) out);
	}


	@Override
	public Object getObject() {
		// TODO Auto-generated method stub
		return null;
	}
}
