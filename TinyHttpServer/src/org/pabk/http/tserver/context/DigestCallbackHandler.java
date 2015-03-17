package org.pabk.http.tserver.context;

import javax.security.auth.callback.CallbackHandler;

abstract class DigestCallbackHandler implements CallbackHandler {
	
	private DigestMessage message;
	private char[] secret;
		
	public char[] getSecret() {
		return secret;
	}
	
	protected void setSecret(char[] secret) {
		this.secret = secret;
	}
	
	protected void setMessage(DigestMessage msg) {
		this.message = msg;
	}
	
	public DigestMessage getMessage() {
		return this.message;
	}
}
