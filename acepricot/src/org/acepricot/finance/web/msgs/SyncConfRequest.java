package org.acepricot.finance.web.msgs;

import java.io.IOException;

public class SyncConfRequest extends AceData {

	

	public SyncConfRequest() throws IOException {
		super();
		this.setMessageType(SYNCHRONIZATION_CONF_REQUEST);
	}

}
