package org.pabk.http.tserver;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.security.auth.Subject;

public class Subjects extends HashMap <Object, Subject> {
	
	private static Subjects subjects = new Subjects();
	private static final long serialVersionUID = 1L;

	private Subjects(){}
	
	public static Subject getSubject(Subject subject) throws IOException {
		Object key = null;
		try {
			key = subject.getPublicCredentials().iterator().next();
		}
		catch (NoSuchElementException e) {
			throw new IOException (e);
		}
		Subject s = subjects.get(key);
		if(s == null) {
			subject.getPrincipals().removeAll(subject.getPrincipals());
			subjects.put(key, subject);
			return subject;
		}
		return s;
	}
	
	public static Subject getSubject(Principal p) throws IOException {
		Iterator<Object> keys = subjects.keySet().iterator();
		while (keys.hasNext()) {
			Object key = keys.next();
			Iterator<Principal> ps = subjects.get(key).getPrincipals().iterator();
			while(ps.hasNext()) {
				Principal px = ps.next();
				if(px.equals(p)) {
					return subjects.get(key);
				}
			}
		}
		throw new IOException("Subject not found");
	}

}
