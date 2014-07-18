package org.acepricot.finance.web.msgs;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class RemoveNode extends RegistrationRequest {

	public RemoveNode(String eMail, String algorithm, byte[] pwd, String clid) throws IOException, NoSuchAlgorithmException {
		super(eMail, algorithm, pwd, null);
		AceDataItemImpl item1 = new AceDataItemImpl(CLIENT_ID_HEADER, "", clid.getBytes());
		this.addConstructedContent(item1);
		this.setMessageType(REMOVE_NODE);
	}
}
