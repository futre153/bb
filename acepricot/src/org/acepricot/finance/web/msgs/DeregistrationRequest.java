package org.acepricot.finance.web.msgs;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class DeregistrationRequest extends RegistrationRequest {

	public DeregistrationRequest(String eMail, String algorithm, byte[] pwd) throws IOException, NoSuchAlgorithmException {
		super(eMail, algorithm, pwd, null);
		this.setMessageType(DEREGISTRATION_REQUEST);
	}

}
