package org.pabk.http.tserver.context;

import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;

public class TinyPrincipal implements Principal {
	
	private static final String AMPERSAND = "@";
	private static final String NO_MESSAGE = "No message";
	private static final String URI_NOT_AUTHORISED = "URI %s is not authorised";
	private static final String AUTHORISED = "Successfully authorised";
	private static final String USER_NOT_AUTHORISED = "User %s is not authorised";
	private static final String USER_IS_NULL = "User is not defined";
	private static final String CLASS_NOT_SUPPORTED = "Class %s is not supported";
	private String username;
	private String domain;
	private final List<URI> URIES = new ArrayList<URI>();
	private String message = NO_MESSAGE;
	
	TinyPrincipal(String name, String domain) throws IOException {
		setUsername(name);
		setDomain(domain);
	}
	
	@Override
	public String getName() {
		return username + AMPERSAND + domain;
	}

	final String getUsername() {
		return username;
	}

	private final void setUsername(String username) throws IOException {
		if(username == null) {
			throw new IOException("Username of principal cannot be null");
		}
		this.username = username;
	}

	final String getDomain() {
		return domain == null ? "" : domain;
	}

	private final void setDomain(String domain) {
		this.domain = username == null ? null : (domain == null ? "" : domain);
	}
	
	final void addURL(URI uri) {
		if(!this.URIES.contains(uri)) {
			this.URIES.add(uri);
		}
	}
	
	public final boolean equals(Object obj) {
		if(obj != null && obj instanceof HttpExchange) {
			HttpExchange ex = (HttpExchange) obj;
			Hashtable<String, TinyCookie> cookies = TinyCookie.getCookies(ex.getRequestHeaders().get("Cookie"));
			TinyCookie cookie = cookies.get(TinyCookie.USERNAME);
			if(cookie != null) {
				String name = cookie.getValue().toString() + AMPERSAND;
				cookie = cookies.get(TinyCookie.DOMAIN);
				name += (cookie == null ? "" : cookie.getValue());
				if(name.equals(getName())) {
					if(URIES.contains(ex.getRequestURI())) {
						setMessage(AUTHORISED);
						return true;
					}
					else {
						setMessage(String.format(URI_NOT_AUTHORISED, ex.getRequestURI().toString()));
					}	
				}
				else {
					setMessage(String.format(USER_NOT_AUTHORISED, this.getUsername()));
				}
			}
			else {
				setMessage(USER_IS_NULL);
			}
		}
		if(obj instanceof TinyPrincipal) {
			TinyPrincipal p = (TinyPrincipal) obj;
			return p.getName().equals(this.getName());
		}
		else {
			setMessage(String.format(CLASS_NOT_SUPPORTED, obj.getClass().getName()));
		}
		return false;
	}

	private void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
	
	public String toString() {
		return getName() + " " + URIES;
	}
}
