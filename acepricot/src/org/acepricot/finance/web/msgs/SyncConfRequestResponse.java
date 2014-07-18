package org.acepricot.finance.web.msgs;

import java.io.IOException;

public class SyncConfRequestResponse extends AceResponse {

	

	public SyncConfRequestResponse(String status, String filename, byte[] response, String message) throws IOException {
		super(status);
		AceDataItemImpl item2 = new AceDataItemImpl(FILENAME_HEADER, "", filename.getBytes());
		AceDataItemImpl item3 = new AceDataItemImpl(CONFIGURATION_RESPONSE_HEAD, "", response);
		AceDataItemImpl item1 = new AceDataItemImpl(REQUEST_RESPONSE_TEXT, "", message.getBytes());
		this.addConstructedContent(item1);
		this.addConstructedContent(item2);
		this.addConstructedContent(item3);
		this.setMessageType(SYNCHRONIZATION_CONF_RESPONSE);
	}

}
