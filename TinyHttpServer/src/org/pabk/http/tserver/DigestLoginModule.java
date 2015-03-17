package org.pabk.http.tserver;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.pabk.http.tserver.context.DigestMessage;
import org.pabk.http.tserver.context.TinyDigestCallbackHandler;

final public class DigestLoginModule implements LoginModule {
	
	private CallbackHandler callbackHandler;
	private Subject subject;
	
	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
		this.setCallbackHandler(callbackHandler);
		this.setSubject(subject);
	}

	@Override
	public boolean login() throws LoginException {
		try {
			this.getCallbackHandler().handle(new Callback[0]);
		} catch (IOException | UnsupportedCallbackException e) {
			throw new LoginException (e.getMessage());
		}
		if(this.getCallbackHandler() instanceof TinyDigestCallbackHandler) {
			TinyDigestCallbackHandler tdch = (TinyDigestCallbackHandler) this.getCallbackHandler();
			return DigestMessage.checkResponse(tdch.getMessage(), tdch.getSecret());
		}
		else {
			throw new LoginException("Callback handler " + this.getCallbackHandler().getClass().getSimpleName() + " is not supported");
		}
	}

	@Override
	public boolean commit() throws LoginException {
		try {
			Principal p = this.getSubject().getPrincipals().iterator().next();
			Subject subject = Subjects.getSubject(this.getSubject());
			subject.getPrincipals().add(p);
		}
		catch (Exception e) {
			return false;
		}
		return true;
	}

	@Override
	public boolean abort() throws LoginException {
		this.getSubject().getPrincipals().removeAll(this.getSubject().getPrincipals());
		return true;
	}

	@Override
	public boolean logout() throws LoginException {
		return true;
	}

	private final CallbackHandler getCallbackHandler() {
		return callbackHandler;
	}

	private final void setCallbackHandler(CallbackHandler callbackHandler) {
		this.callbackHandler = callbackHandler;
	}

	private final Subject getSubject() {
		return subject;
	}

	private final void setSubject(Subject subject) {
		this.subject = subject;
	}

}
