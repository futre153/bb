package org.pabk.http.tserver.context;

import java.io.IOException;
import java.util.List;

import org.pabk.http.tserver.Subjects;
import org.pabk.http.tserver.TConst;
import org.pabk.http.tserver.TServer;
import org.pabk.util.Base64Coder;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

abstract class AuthContext extends BaseContext {
	
	protected TinyPrincipal principal;
	
	public void handle(HttpExchange ex) throws IOException {
		super.handle(ex);
		ex.getResponseHeaders().add("Cache-Control", "no-cache");
		Headers head = ex.getRequestHeaders();
		List<String> auth = head.get("Authorization");
		TinyHttpSession session = super.getSession();
		//int authType = TinyHttpSession.DEFAULT_AUTH;
		if(auth != null) {
			try {
				String[] authString = auth.get(0).split(" ", 2); 
				if(authString[0].equals(TConst.NTLM_AUTHENTICATION)) {
			//		authType = TConst.NTLM;
					handleNTLM(ex, authString[1], session);
				}
				else if(authString[0].equals(TConst.DIGEST_AUTHENTICATION)) {
				//	authType = TConst.DIGEST;
					handleDigest(ex, authString[1], session);
				}
				principal = session.getPrincipal();
				return;
			}
			catch (Exception e) {	
				//e.printStackTrace();
				session.clearState();
				//try {
				//	this.setResponseCode(addAuthString(ex, session, authType));
				//} catch (Exception e1) {
					this.setResponseCode(403);
				//}
				return;
			}
		}
		else {
			if(session.isAuthorised(ex)) {
				session.resetStates(ex);
			}
			else {
				int as = session.getAuthState();
				switch(as) {
				case TConst.NO_ACTION:
					break;
				case TConst.IN_PROGRESS:
					default:
						session.clearState();
						this.setResponseCode(403);
						return;
				}
				this.setResponseCode(addAuthString(ex, session, TinyHttpSession.DEFAULT_AUTH));
			}
		}
	}
	
	private static int addAuthString(HttpExchange ex, TinyHttpSession session, int authType) throws IOException {
		session.setAuthState(TConst.IN_PROGRESS);
		int responseCode = 401;
		String authString = null;
		switch (authType) {
		case TConst.NTLM:
			authString = TConst.NTLM_AUTHENTICATION;
			break;
		case TConst.DIGEST:
			DigestMessage chl = DigestMessage.createChallenge();
			session.setAttribute(TConst.DIGEST_CHALLENGE_MESSAGE, chl);
			authString = TConst.DIGEST_AUTHENTICATION + " " + chl.getAuthString();
		}
		if(authString == null) {
			responseCode = 403;
			session.setAuthState(TConst.NO_ACTION);
		}
		else {
			ex.getResponseHeaders().add("WWW-Authenticate", authString);
		}
		return responseCode;
	}
	
	private final void handleDigest(HttpExchange ex, String auth, TinyHttpSession session) throws Exception {
		DigestMessage rsp = new DigestMessage(auth.toCharArray());
		TinyPrincipal principal = (TinyPrincipal) rsp.authorize(null, session.getPrincipal(), session.getAttribute(TConst.DIGEST_CHALLENGE_MESSAGE), ex);
		if(principal == null) {
			throw new IOException("Authorisation failed");
		}
		session.setPrincipal(principal);
		session.resetStates(ex);
		System.out.println("Session " + session.getID() + " was successfully authenticated with digest mechanism.  Principal = " + principal.toString() + " Subject = " + Subjects.getSubject(principal));
	}
	
	private final void handleNTLM(HttpExchange ex, String auth, TinyHttpSession session) throws Exception {
		//System.out.println(new String(Base64Coder.decode(auth)));
		NTLMMessage ntlm  = new NTLMMessage(Base64Coder.decode(auth));
		switch(ntlm.getType()) {
		case NTLMMessage.NEGOTIATE_MESSAGE:
			session.setAuthType(TConst.NTLM);
			session.setAttribute(TConst.NTLM_NEGOTIATE_MESSAGE, ntlm);
			NTLMMessage challenge = NTLMMessage.createChallengeMsg(ntlm);
			session.setAttribute(TConst.NTLM_CHALLENGE_MESSAGE, challenge);
			ex.getResponseHeaders().add("WWW-Authenticate", TConst.NTLM_AUTHENTICATION + " " + challenge.getAuthString());
			this.setResponseCode(401);
			break;
		case NTLMMessage.AUTHENTICATE_MESSAGE:
			TinyPrincipal principal = (TinyPrincipal) ntlm.authorize(TServer.getCallbackHandler(), session.getPrincipal(), session.getAttribute(TConst.NTLM_NEGOTIATE_MESSAGE), session.getAttribute(TConst.NTLM_CHALLENGE_MESSAGE), ex);
			if(principal == null) {
				throw new IOException("Authorisation failed");
			}
			session.setPrincipal(principal);
			session.resetStates(ex);
			System.out.println("Session " + session.getID() + " was successfully authenticated with NTLM mechanism. Principal = " + principal.toString() + " Subject = " + Subjects.getSubject(principal));
			break;
			default:
				throw new IOException ("Unexpected NTML message type " + ntlm.getType());
		}
		
	}

}
