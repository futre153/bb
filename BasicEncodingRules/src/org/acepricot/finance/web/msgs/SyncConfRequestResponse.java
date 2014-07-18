package org.acepricot.finance.web.msgs;

import java.io.IOException;

public class SyncConfRequestResponse extends AceResponse {

	public SyncConfRequestResponse(String status, String filename, byte[] response) throws IOException {
		super(status);
		AceDataItemImpl item1 = new AceDataItemImpl(FILENAME_HEADER, "", filename.getBytes());
		AceDataItemImpl item2 = new AceDataItemImpl(CONFIGURATION_RESPONSE_HEAD, "", response);
		this.addConstructedContent(item1);
		this.addConstructedContent(item2);
	}

}
