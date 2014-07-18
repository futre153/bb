package org.acepricot.finance.web.msgs;

import java.io.IOException;

public class RegistrationRequestResponse extends AceResponse {
	
	public  RegistrationRequestResponse(String response, String status) throws IOException {
		super(status);
		AceDataItemImpl item1 = new AceDataItemImpl(REQUEST_RESPONSE_TEXT, "", response.getBytes());
		this.addConstructedContent(item1);
	}
}
