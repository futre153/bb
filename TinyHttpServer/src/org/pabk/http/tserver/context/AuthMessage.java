package org.pabk.http.tserver.context;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;

import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;

public interface AuthMessage {
	void parse(InputStream in) throws IOException;
	Principal authorize(CallbackHandler cb, Principal p, Object ... msgs) throws IOException, LoginException;
}
