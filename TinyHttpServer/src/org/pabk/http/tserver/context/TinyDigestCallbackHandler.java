package org.pabk.http.tserver.context;

import java.io.IOException;

import javax.security.auth.callback.Callback;

import org.pabk.http.tserver.TConst;
import org.pabk.http.tserver.TServer;
import org.pabk.util.Huffman;

final public class TinyDigestCallbackHandler extends DigestCallbackHandler {
	
	private String[] privCred;
	
	@Override
	public void handle(Callback[] c) throws IOException {
		String secret = TServer.getSecretStorage().getProperty(this.getMessage().getUsername());
		String cred = TServer.getSecretStorage().getProperty(this.getMessage().getUsername() + TConst.CREDENTIALS_EXTENSION);
		if(secret != null) {
			try {
				secret = Huffman.decode(secret, null);
				if(cred == null) {
					cred = TConst.DEFAULT_CREDENTIALS;
				}
				setPrivCred(cred.split(","));
			}
			catch(Exception e) {
				throw new IOException (e);
			}
			super.setSecret(secret.toCharArray());
		}
		else {
			throw new IOException (String.format(TConst.USER_NOT_FOUND, this.getMessage().getUsername()));
		}
		
	}

	public final String[] getPrivCred() {
		return privCred;
	}

	private final void setPrivCred(String[] privCred) {
		this.privCred = privCred;
	}

}
