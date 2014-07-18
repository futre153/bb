package org.acepricot.finance.web.http;

import java.io.IOException;

public class DefaultContent extends ContentImpl {

	@Override
	public int getLength() {
		return -1;
	}

	@Override
	public byte[] getContent() {
		return null;
	}

	@Override
	public void setContent(Object obj) throws UnsupportedOperationException,IOException {}

	@Override
	public String getEncoding() {
		return null;
	}

	@Override
	public String getType() {
		return null;
	}

}
