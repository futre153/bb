package org.pabk.http.tserver.context;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.xml.bind.DatatypeConverter;

import org.pabk.http.tserver.TConst;
import org.pabk.http.tserver.TServer;

import com.sun.net.httpserver.HttpExchange;


public class DigestMessage extends AuthMessageImpl {
	
	private MessageDigest md5;
	
	private String realm;
	private String nonce;
	private String opaque;
	private String username;
	private String response;
	private String uri;
	private String method;
	
	private DigestMessage() throws IOException {
		if(md5 == null) {
			try {
				md5 = MessageDigest.getInstance(TConst.MD5);
			} catch (NoSuchAlgorithmException e) {
				throw new IOException (e);
			}
		}
	}
	
	private DigestMessage(String realm) throws IOException {
		this();
		this.realm = realm;
		this.nonce = DatatypeConverter.printHexBinary(NTLMMessage.createNonce(17));
		this.opaque = DatatypeConverter.printHexBinary(NTLMMessage.createNonce(16));
	}
	
	DigestMessage(char[] chrs) throws IOException {
		this();
		int index = 0;
		String[] pair = new String[2];
		while(index < chrs.length) {
			try {
				index = getPair(pair, chrs, index);
				Field f = this.getClass().getDeclaredField(pair[0].toLowerCase());
				Method m = this.getClass().getDeclaredMethod(concatenate(TConst.SETTER_PREFIX, f.getName().substring(0, 1).toUpperCase(), f.getName().substring(1)), pair[1].getClass());
				m.invoke(this, pair[1]);
			}
			catch(IOException | NoSuchFieldException | SecurityException | NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
				throw new IOException (e);
			}
		}
	}
	
	private int getPair(String[] pair, char[] chrs, int index) throws IOException {
		pair[0] = ""; pair[1] = "";
		int i = DigestMessage.firstIndexOf(chrs, TConst.EQUAL, index);
		if(i < 0) {
			throw new IOException (String.format(TConst.CHAR_NOT_FOUND, new String(new char[]{TConst.EQUAL})));
		}
		pair[0] = new String(chrs, index, i - index).trim();
		index = i + 1;
		if(index < chrs.length) {
			if(chrs[index] == TConst.QUOTE) {
				StringBuffer sb = new StringBuffer();
				i = readQuotedString(sb, chrs, index);
				pair[1] = sb.toString();
				index = i;
			}
			else {
				i = firstIndexOf(chrs, TConst.COMMA, index);
				if(i < 0) {
					i = chrs.length;
				}
				pair[i] = new String(chrs, index, i - index);
				index = i;
			}
			i = firstIndexOf(chrs, TConst.COMMA, index);
			index = i < 0 ? chrs.length : i + 1;
		}
		if(pair[0].length() == 0) {
			throw new IOException (String.format(TConst.CHAR_NOT_FOUND, new String(new char[]{TConst.EQUAL})));
		}
		return index;
	}

	private static int readQuotedString(StringBuffer sb, char[] chrs, int index) {
		if(chrs[index] != TConst.QUOTE) {
			throw new UnsupportedOperationException (TConst.QSTRING_ERROR);
		}
		index ++;
		while (! (chrs[index] == TConst.QUOTE)) {
			switch(chrs[index]) {
			case '\\':
				index ++;
				default:
					sb.append(chrs[index]);
					index ++;
			}
			if(index == chrs.length) {
				throw new UnsupportedOperationException (TConst.QSTRING_ERROR);
			}
		}
		index ++;
		return index;
	}

	private static int firstIndexOf(char[] chrs, char c, int index) {
		int i = index;
		if(i >= 0) {
			for(; i < chrs.length; i ++) {
				if(chrs[i] == c) {
					break;
				}
			}
		}
		return (i >= chrs.length || i < 0) ? -1 : i;
	}

	
	
	@Override
	public void parse(InputStream in) throws IOException {
		throw new UnsupportedOperationException (TConst.NOT_SUPPORTED);
	}

	@Override
	public Principal authorize(CallbackHandler cb, Principal p, Object... msgs)	throws IOException, LoginException {
		if(msgs != null && msgs.length == 2 && msgs[0] instanceof DigestMessage && msgs[1] instanceof HttpExchange) {
			DigestMessage chl = (DigestMessage) msgs[0];
			HttpExchange ex = (HttpExchange) msgs[1];
			this.method = ex.getRequestMethod();
			if(!(checkPair(this.getOpaque(), chl.getOpaque()) && checkPair(this.getNonce(), chl.getNonce()))) {
				throw new IOException(TConst.CHECK_DIGEST_ERROR);
			}
			TinyPrincipal principal = createPrincipal((TinyPrincipal) p);
			principal.addURL(ex.getRequestURI());
			if(cb == null) {
				cb = new TinyDigestCallbackHandler();
			}
			if(cb != null && cb instanceof DigestCallbackHandler) {
				Subject subject = new Subject();
				subject.getPrincipals().add(principal);
				((DigestCallbackHandler)cb).setMessage(this);
				LoginContext lc = new LoginContext(TConst.DIGEST_LOGIN_INDEX, subject, cb, Configuration.getConfiguration());
				lc.login();
				return subject.getPrincipals().iterator().next();
			}
			else {
				throw new LoginException("Login error");
			}
		}
		throw new IOException (TConst.PARAMETERS_ERROR);
	}
	
		
	public static boolean checkResponse(DigestMessage response, char[] password) {
		response.getMd5().reset();
		String a1 = DatatypeConverter.printHexBinary(response.getMd5().digest(join(TConst.COLON, response.getUsername(), response.getRealm(), new String(password)).getBytes())).toLowerCase();
		response.getMd5().reset();
		String a2 = DatatypeConverter.printHexBinary(response.getMd5().digest(join(TConst.COLON, response.getMethod(), response.getUri()).getBytes())).toLowerCase();
		response.getMd5().reset();
		String digest = DatatypeConverter.printHexBinary(response.getMd5().digest(join(TConst.COLON, a1, response.getNonce(), a2).getBytes())).toLowerCase();
		return checkPair(response.getResponse(), digest);
	}
	
	private static boolean checkPair(String pair1, String pair2) {
		return pair1 == null ? pair2 == null : (pair2 == null ? false : pair1.equals(pair2));
	}

	public static DigestMessage createChallenge() throws IOException {
		String realm = TServer.getRealm();
		if(realm == null) {
			return null;
		}
		else {
			return new DigestMessage(realm); 
		}
	}
	
	final String getRealm() {
		return realm;
	}

	final String getNonce() {
		return nonce;
	}

	final String getOpaque() {
		return opaque;
	}
	
	static String envelope (char env, String base) {
		StringBuffer sb = new StringBuffer(base.length() + 2);
		sb.append(env);
		sb.append(base);
		sb.append(env);
		return sb.toString();
	}
	
	static String concatenate(String ... strs) {
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < strs.length; i ++) {
			sb.append(strs[i]);
		}
		return sb.toString();
	}
	
	static String join (char c, String ... strs) {
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < strs.length; i ++ ) {
			if(i > 0) {
				sb.append(c);
			}
			sb.append(strs[i]);
		}
		return sb.toString();
	}
	
	public String getAuthString() {
		String str =  join(TConst.COMMA, join(TConst.EQUAL, TConst.REALM, envelope(TConst.QUOTE, this.getRealm())), join(TConst.EQUAL, TConst.NONCE, envelope(TConst.QUOTE, this.getNonce())), join(TConst.EQUAL, TConst.OPAQUE, envelope(TConst.QUOTE, this.getOpaque())));
		return str;
	}

	final String getUsername() {
		return username;
	}

	final void setUsername(String username) {
		this.username = username;
	}

	final void setRealm(String realm) {
		this.realm = realm;
	}
	final String getResponse() {
		return response;
	}

	final void setResponse(String response) {
		this.response = response;
	}

	final String getUri() {
		return uri;
	}

	final void setUri(String uri) {
		this.uri = uri;
	}

	final void setNonce(String nonce) {
		this.nonce = nonce;
	}

	final void setOpaque(String opaque) {
		this.opaque = opaque;
	}

	@Override
	Object get(String key) {
		if(key.equals(TConst.AUTH_USERNAME)) {
			return this.getUsername();
		}
		return null;
	}

	private final MessageDigest getMd5() {
		return md5;
	}

	private final String getMethod() {
		return method;
	}
}
