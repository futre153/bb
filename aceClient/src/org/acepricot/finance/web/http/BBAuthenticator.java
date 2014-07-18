package org.acepricot.finance.web.http;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class BBAuthenticator extends Authenticator {
	private static String username;
	private static String password;
	public BBAuthenticator(String userName2, String userPassword) {
		username=userName2;password=userPassword;
	}
	public PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(username, password.toCharArray());
	}
}
