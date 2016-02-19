package org.pabk.web;

import java.io.InputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.catalina.Container;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.realm.RealmBase;
import org.pabk.util.Huffman;
import org.w3c.dom.NodeList;

public class PrepAppRealm extends RealmBase {
	
	private static final String USER_DB_PATH = "data/userdb.xml";
	private static final String USER_ENTRY_NAME = "userdb:entry";
	private static final String SEPARATOR = ",";
	private Hashtable<String, List<String>> users;

	@Override
	protected String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	protected String getPassword(String username) {
		return null;
	}

	@Override
	protected Principal getPrincipal(String username) {
		users = users == null ? loadUserDB() : users;
		System.out.println(username);
	    return new GenericPrincipal(username, getPassword(username), users != null ? users.get(username) : null);
	}
	
	private Hashtable<String, List<String>> loadUserDB() {
		Hashtable<String, List<String>> table = new Hashtable<String, List<String>>();
		Container cont = this.getContainer();
		ServletContext se = null;
		while(cont != null) {
			if(cont instanceof StandardContext) {
				se = ((StandardContext) cont).getServletContext();
				break;
			}
			cont = cont.getParent();
		}
		if(se != null) {
			InputStream in = se.getResourceAsStream(USER_DB_PATH);
			try {
				DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				NodeList nl = db.parse(in).getDocumentElement().getElementsByTagName(USER_ENTRY_NAME);
				for(int i = 0; i < nl.getLength(); i ++) {
					String entry[] = Huffman.decode(nl.item(i).getTextContent().trim(), null).split(SEPARATOR, 2);
					String username = Huffman.decode(entry[0], null);
					String[] roles = Huffman.decode(entry[1], null).split(SEPARATOR);
					ArrayList<String> list = new ArrayList<String>();
					for(int j = 0; j < roles.length; j ++) {
						list.add(Huffman.decode(roles[j], null));
					}
					table.put(username, list);
				}
				users = table;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return table;
	}

	public Principal authenticate(String username, String credentials) {
		System.out.println("auth " + username);
		System.out.println("auth " + credentials);
		return getPrincipal(username);
	}
	
}
