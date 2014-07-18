package org.pabk.net.http.auth;

import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.security.Principal;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextOutputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;

import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;
import org.pabk.net.http.HttpClientConst;
import org.pabk.util.Base64Coder;

import sun.net.www.protocol.http.HttpCallerInfo;
import sun.security.jgss.GSSManagerImpl;
import sun.security.jgss.HttpCaller;

public class BBAuthenticator extends Authenticator {

	private static String username;
	private static String password;
	
	private static Principal principal;// = ps.iterator().next();
	
	public BBAuthenticator(String userName2, String userPassword) {
		username = userName2; password = userPassword;
	}

	public PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(username, password.toCharArray());
	}
	
	public PassiveCallbackHandler getCallbackHandler() {
		return new PassiveCallbackHandler();
	}
	
	private class PassiveCallbackHandler implements CallbackHandler {	
		@Override
		public void handle(Callback[] cb) throws IOException, UnsupportedCallbackException {
			for(int i = 0; i < cb.length; i++) {
				if(cb[i] instanceof TextOutputCallback) {
					System.out.println(((TextOutputCallback)cb[i]).getMessage());
				}
				else if(cb[i] instanceof NameCallback) {
					//System.out.println("Set default username for NameCallback");
					((NameCallback)cb[i]).setName(username);
				}
				else if(cb[i] instanceof PasswordCallback) {
					//System.out.println("Set default password for PasswordCallback");
					((PasswordCallback)cb[i]).setPassword(password.toCharArray());
				}
				else {
					throw new UnsupportedCallbackException(cb[i], "Callback not recognized");
				}
			}
		}
	}
	
	public final static String getSpnego(URL url, String service) throws Exception {
		final GSSManager man=new GSSManagerImpl(new HttpCaller(new HttpCallerInfo(url)));
		final GSSName name=man.createName(principal.getName(), new Oid(HttpClientConst.KRB5_PRINCIPAL_NAME_OID));
		final GSSName server=man.createName(service, new Oid(HttpClientConst.KRB5_PRINCIPAL_NAME_OID));
		final GSSCredential client=man.createCredential(name, 8*3600, new Oid(HttpClientConst.KERB_V5_OID), GSSCredential.INITIATE_ONLY);
		final GSSContext con =man.createContext(server,new Oid(HttpClientConst.SPNEGO),client,GSSContext.DEFAULT_LIFETIME);
		final byte[] b=con.initSecContext(new byte[0], 0, 0);
		return new String(Base64Coder.encode(b));
		
	}

	public final static void setSpnego(CallbackHandler cb) throws Exception {
		if(principal == null) {
			final Subject sub=new Subject();
			LoginContext lc = new LoginContext("com.sun.security.jgss.krb5.login", sub, cb);
			lc.login();
			principal = sub.getPrincipals().iterator().next();
			lc.logout();
		}
	}
}