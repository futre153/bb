package org.acepricot.finance.web.msgs;

import java.io.IOException;

public class ErrorResponse extends AceResponse {

	public ErrorResponse(String errorMessage, String status) throws IOException {
		super(status);
		AceDataItemImpl item1 = new AceDataItemImpl(ERROR_MESSAGE_HEADER, "", errorMessage.getBytes());
		this.addConstructedContent(item1);
		this.setMessageType(ERROR_MESSAGE);
	}

}
