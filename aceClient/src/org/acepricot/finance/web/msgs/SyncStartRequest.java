package org.acepricot.finance.web.msgs;

import java.io.IOException;

public class SyncStartRequest extends AceData {

	public SyncStartRequest() throws IOException {
		super();
		this.setMessageType(SYNCHRONIZATION_START_REQUEST);
	}

}