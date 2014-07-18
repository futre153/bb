package org.pabk.net.http;

public class DefaultContent extends ContentImpl {

	public DefaultContent(Object content, int type) {
		super(content, type, HttpClientConst.UTF8_ENCODING, HttpClientConst.NULL);
	}
	
	public DefaultContent(int type) {
		super(null, type, HttpClientConst.UTF8_ENCODING, HttpClientConst.NULL);
	}

	public DefaultContent() {
		super();
	}

	public DefaultContent(String content, String type) {
		
	}

}
