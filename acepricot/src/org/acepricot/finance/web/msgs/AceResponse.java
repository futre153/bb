package org.acepricot.finance.web.msgs;

import java.io.IOException;

abstract class AceResponse extends AceData {

	AceResponse(String status) throws IOException {
		super();
		AceDataItemImpl item1 = new AceDataItemImpl(RESPONSE_STATUS_HEADER, "", status.getBytes());
		this.setConstructedContent(item1);
	}

}
