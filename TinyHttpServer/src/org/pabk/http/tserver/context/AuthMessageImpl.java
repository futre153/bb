package org.pabk.http.tserver.context;

import java.io.IOException;

import org.pabk.http.tserver.TConst;

abstract class AuthMessageImpl implements AuthMessage {
	
	abstract Object get(String key);
	
	protected TinyPrincipal createPrincipal(TinyPrincipal old) throws IOException {
		TinyPrincipal principal = new TinyPrincipal((String) get(TConst.AUTH_USERNAME), (String) get(TConst.AUTH_DOMAIN));
		if(old != null) {
			if(principal.equals(old)) {
				principal = null;
				principal = old;
			}
		}
		return principal;
	}
}
