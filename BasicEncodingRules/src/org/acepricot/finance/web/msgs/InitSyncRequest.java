package org.acepricot.finance.web.msgs;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class InitSyncRequest extends RegistrationRequest {

	public InitSyncRequest(String eMail, String algorithm, byte[] pwd)	throws IOException, NoSuchAlgorithmException {
		super(eMail, algorithm, pwd);
	}

}
