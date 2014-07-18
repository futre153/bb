package org.pabk.net.http;

public class WSContent extends ContentImpl {
	
	public WSContent (Object content, String action) {
		super(content, HttpClientConst.TEXT_XML_CONTENT, HttpClientConst.UTF8_ENCODING, HttpClientConst.NULL);
		this.setAdditionalProperty(HttpClientConst.ACTION, action);
	}
}
