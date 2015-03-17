package org.pabk.web;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.realm.RealmBase;

public class PrepAppRealm extends RealmBase {

	@Override
	protected String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	protected String getPassword(String username) {
		return "test123";
	}

	@Override
	protected Principal getPrincipal(String username) {
		final List<String> roles = new ArrayList<String>();
	    roles.add("tomcat");
	    return new GenericPrincipal(username, "test123", roles);
	}
	
	public Principal authenticate(String username, String credentials) {
		return getPrincipal(username);
	}
	
}
