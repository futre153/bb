package org.pabk.http.tserver.context;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Hashtable;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.pabk.http.tserver.TConst;
import org.pabk.util.Base64Coder;

import com.sun.net.httpserver.HttpExchange;

public class NTLMMessage extends AuthMessageImpl {
	private static final String PROTOCOL = "NTLMSSP\0";
	static final int NEGOTIATE_MESSAGE 		= 0x01;
	static final int CHALLENGE_MESSAGE 		= 0x02;
	static final int AUTHENTICATE_MESSAGE 	= 0x03;
	public static final int NTLMSSP_NEGOTIATE_56						= 0x1F;	//W
	public static final int NTLMSSP_NEGOTIATE_KEY_EXCH					= 0x1E;	//V
	public static final int NTLMSSP_NEGOTIATE_128						= 0x1D;	//U
	@SuppressWarnings("unused")
	private static final int r1											= 0x1C;
	@SuppressWarnings("unused")
	private static final int r2											= 0x1B;
	@SuppressWarnings("unused")
	private static final int r3											= 0x1A;
	public static final int NTLMSSP_NEGOTIATE_VERSION					= 0x19;	//T
	@SuppressWarnings("unused")
	private static final int r4											= 0x18;
	public static final int NTLMSSP_NEGOTIATE_TARGET_INFO				= 0x17;	//S
	public static final int NTLMSSP_REQUEST_NON_NT_SESSION_KEY			= 0x16;	//R
	@SuppressWarnings("unused")
	private static final int r5											= 0x15;
	public static final int  NTLMSSP_NEGOTIATE_IDENTIFY					= 0x14;	//Q
	public static final int NTLMSSP_NEGOTIATE_EXTENDED_SESSIONSECURITY	= 0x13;	//P
	@SuppressWarnings("unused")
	private static final int r6											= 0x12;
	public static final int NTLMSSP_TARGET_TYPE_SERVER					= 0x11;	//O
	public static final int NTLMSSP_TARGET_TYPE_DOMAIN					= 0x10;	//N
	public static final int NTLMSSP_NEGOTIATE_ALWAYS_SIGN				= 0x0F;	//M
	@SuppressWarnings("unused")
	private static final int r7											= 0x0E;
	public static final int NTLMSSP_NEGOTIATE_OEM_WORKSTATION_SUPPLIED	= 0x0D;	//L
	public static final int NTLMSSP_NEGOTIATE_OEM_DOMAIN_SUPPLIED		= 0x0C;	//K
	public static final int ANONYMOUS_CONNECTION						= 0x0B;	//J
	@SuppressWarnings("unused")
	private static final int r8											= 0x0A;
	public static final int NTLMSSP_NEGOTIATE_NTLM						= 0x09;	//H
	@SuppressWarnings("unused")
	private static final int r9											= 0x08;
	public static final int NTLMSSP_NEGOTIATE_LM_KEY					= 0x07;	//G
	public static final int NTLMSSP_NEGOTIATE_DATAGRAM					= 0x06;	//F
	public static final int NTLMSSP_NEGOTIATE_SEAL						= 0x05;	//E
	public static final int NTLMSSP_NEGOTIATE_SIGN						= 0x04;
	@SuppressWarnings("unused")
	private static final int r10										= 0x03;
	public static final int NTLMSSP_REQUEST_TARGET						= 0x02;	//C
	public static final int NTLM_NEGOTIATE_OEM							= 0x01;	//B
	public static final int NTLMSSP_NEGOTIATE_UNICODE					= 0x00;	//A
	 
	 
	private static final String NTLM_HEADER_ERROR	= "Failed to parse NTLM header";
	private static final String NTLM_TYPE_ERROR		= "Failed to read NTLM message type %d,  1, 3 is expected";
	private static final String NTLM_2_ERROR		= "NTLM message type 2 is not expected";
	private static final String NTLM_FLAGS_ERROR = "Failed to read NTLM flags";
	private static final String NTLM_FLAGS = "NegotiateFlags";
	private static final String NTLM_FLAG_ERROR = "Could not retrieve NTLM flag under index %d";
	private static final String NTLM_WRK_LEN = "WorkstationLen";
	private static final String NTLM_WRK_MAX_LEN = "WorkstationMaxLen";
	private static final String NTLM_WRK_OFF = "WorkstationBufferOffset";
	private static final String NTLM_DOM_LEN = "DomainNameLen";
	private static final String NTLM_DOM_MAX_LEN = "DomainNameMaxLen";
	private static final String NTLM_DOM_OFF = "DomainNameBufferOffset";
	private static final String UNEXPECTED_EOF = "Unexpected end of stream reached while reading NTLM string";
	private static final String NTLM_PROD_MAJOR_VERSION = "ProductMajorVersion";
	private static final String NTLM_PROD_MINOR_VERSION = "ProductMinorVersion";
	private static final String NTLM_PROD_BUILD = "ProductBuild";
	private static final String NTLM_RESERVED_VS = "ReservedVs";
	private static final String NTLM_REV_CURR = "NTLMRevisionCurrent";
	
	private static final String NTLM_WORKSTATION = "WorkstationName";
	private static final String NTLM_PAYLOAD_ERROR = "Failed to load %s from NTLM payload";
	private static final String NTLM_LMRES_LEN = "LmChallengeResponseLen";
	private static final String NTLM_LMRES_MAX_LEN = "LmChallengeResponseMaxLen";
	private static final String NTLM_LMRES_OFF = "LmChallengeResponseBufferOffset";
	private static final String NTLM_LMRES = "LmChallengeResponse";
	private static final String NTLM_NTRES_LEN = "NtChallengeResponseLen";
	private static final String NTLM_NTRES_MAX_LEN = "NtChallengeResponseMaxLen";
	private static final String NTLM_NTRES_OFF = "NtChallengeResponseBufferOffset";
	private static final String NTLM_NTRES = "NtChallengeResponse";
	private static final String NTLM_USER_LEN = "UserNameLen";
	private static final String NTLM_USER_MAX_LEN = "UserNameMaxLen";
	private static final String NTLM_USER_OFF = "UserNameBufferOffset";
	
	private static final String NTLM_SKEY_LEN = "EncryptedRandomSessionKeyLen";
	private static final String NTLM_SKEY_MAX_LEN = "EncryptedRandomSessionKeyMaxLen";
	private static final String NTLM_SKEY_OFF = "EncryptedRandomSessionKeyBufferOffset";
	private static final String NTLM_SKEY = "EncryptedRandomSessionKey";
	private static final String NTLM_MIC_ERROR = "Failed to read NTLM MIC";
	private static final String NTLM_MIC = "MIC";
	private static final String BYTES = "BYTES";
	private static final String NTLM_TNAME = "TargetName";
	private static final String NTLM_SERVER_NONCE = "ServerChallenge";
	private static final String NTML_RESERVED = "Reserved";
	private static final String NTLM_TINFO = "TargetInfo";
	private static final String DEFAULT_NEGOTIATE_ENCODING = "UTF-16LE";
	private static final String NULL_ERROR = "Authentication failed for null username, workstation";
	
	private final Hashtable<String, Object> component = new Hashtable<String, Object>();
	private int type = -1;
	
	NTLMMessage(byte[] b) throws IOException {
		parse(new ByteArrayInputStream(b));
	}

	private NTLMMessage() {
		this.type = 2;
	}

	@Override
	public void parse(InputStream in) throws IOException {
		byte[] b = new byte[8];
		int i = in.read(b);
		if(i < b.length || ! Arrays.equals(PROTOCOL.getBytes(), b)) {
			throw new IOException(NTLM_HEADER_ERROR);
		}
		DataInputStream din = new DataInputStream(in);
		type = (int) readInt(din);
		switch(type) {
		case NEGOTIATE_MESSAGE:
			parseNTLM1(din);
			break;
		case CHALLENGE_MESSAGE:
			throw new IOException(NTLM_2_ERROR);
		case AUTHENTICATE_MESSAGE:
			parseNTLM3(din);
			break;
			default:
				throw new IOException(String.format(NTLM_TYPE_ERROR, type));
		}
	}
	
	private void parseNTLM3(DataInputStream din) throws IOException {
		ArrayList<Object> payload = new ArrayList<Object>();
		readLenAndOffset(din, NTLM_LMRES_LEN, NTLM_LMRES_MAX_LEN, NTLM_LMRES_OFF);
		readLenAndOffset(din, NTLM_NTRES_LEN, NTLM_NTRES_MAX_LEN, NTLM_NTRES_OFF);
		readLenAndOffset(din, NTLM_DOM_LEN, NTLM_DOM_MAX_LEN, NTLM_DOM_OFF);
		readLenAndOffset(din, NTLM_USER_LEN, NTLM_USER_MAX_LEN, NTLM_USER_OFF);
		readLenAndOffset(din, NTLM_WRK_LEN, NTLM_WRK_MAX_LEN, NTLM_WRK_OFF);
		readLenAndOffset(din, NTLM_SKEY_LEN, NTLM_SKEY_MAX_LEN, NTLM_SKEY_OFF);
		readFlags(din);
		readVersion(din);
		readMIC(din);
		int offset = getFlag(NTLMMessage.NTLMSSP_NEGOTIATE_VERSION) ? 88 : 80;
		if((int) component.get(NTLM_LMRES_LEN) != 0) {
			NTLMMessage.addLoad(payload, new Object[]{component.get(NTLM_LMRES_OFF), component.get(NTLM_LMRES_LEN), NTLM_LMRES, BYTES});
		}
		if((int) component.get(NTLM_NTRES_LEN) != 0) {
			NTLMMessage.addLoad(payload, new Object[]{component.get(NTLM_NTRES_OFF), component.get(NTLM_NTRES_LEN), NTLM_NTRES, BYTES});
		}
		if((int) component.get(NTLM_DOM_LEN) != 0) {
			NTLMMessage.addLoad(payload, new Object[]{component.get(NTLM_DOM_OFF), component.get(NTLM_DOM_LEN), TConst.AUTH_DOMAIN, DEFAULT_NEGOTIATE_ENCODING});
		}
		if((int) component.get(NTLM_USER_LEN) != 0) {
			NTLMMessage.addLoad(payload, new Object[]{component.get(NTLM_USER_OFF), component.get(NTLM_USER_LEN), TConst.AUTH_USERNAME, DEFAULT_NEGOTIATE_ENCODING});
		}
		if((int) component.get(NTLM_WRK_LEN) != 0) {
			NTLMMessage.addLoad(payload, new Object[]{component.get(NTLM_WRK_OFF), component.get(NTLM_WRK_LEN), NTLM_WORKSTATION, DEFAULT_NEGOTIATE_ENCODING});
		}
		if(getFlag(NTLMMessage.NTLMSSP_NEGOTIATE_KEY_EXCH)) {
			NTLMMessage.addLoad(payload, new Object[]{component.get(NTLM_SKEY_OFF), component.get(NTLM_SKEY_LEN), NTLM_SKEY, BYTES});
		}
		NTLMMessage.loadPayload(din, component, payload, offset);
	}
	
	private void readMIC(DataInputStream din) throws IOException {
		byte[] b = new byte[16];
		int i = din.read(b);
		if(i < b.length) {
			throw new IOException(NTLM_MIC_ERROR);
		}
		component.put(NTLM_MIC, b);
	}
	
	private void readFlags(DataInputStream din) throws IOException {
		byte[] b = new byte[4];
		int i = din.read(b);
		if(i < b.length) {
			throw new IOException(NTLM_FLAGS_ERROR); 
		}
		component.put(NTLM_FLAGS, BitSet.valueOf(b));
	}
	
	private void readLenAndOffset (DataInputStream din, String len, String maxLen, String offset) throws IOException {
		component.put(len, readShort(din));
		component.put(maxLen, readShort(din));
		component.put(offset, readInt(din));
	}
	
	private static void loadVersion (NTLMMessage neg, NTLMMessage chal) throws IOException {
		if(neg.getFlag(NTLMMessage.NTLMSSP_NEGOTIATE_VERSION)) {
			chal.component.put(NTLM_PROD_MAJOR_VERSION, neg.component.get(NTLM_PROD_MAJOR_VERSION));
			chal.component.put(NTLM_PROD_MINOR_VERSION, neg.component.get(NTLM_PROD_MINOR_VERSION));
			chal.component.put(NTLM_PROD_BUILD, neg.component.get(NTLM_PROD_BUILD));
			chal.component.put(NTLM_RESERVED_VS, neg.component.get(NTLM_RESERVED_VS));
			chal.component.put(NTLM_REV_CURR, neg.component.get(NTLM_REV_CURR));
		}
	}
	
	private void readVersion(DataInputStream din) throws IOException {
		if(getFlag(NTLMMessage.NTLMSSP_NEGOTIATE_VERSION)) {
			component.put(NTLM_PROD_MAJOR_VERSION, readByte(din));
			component.put(NTLM_PROD_MINOR_VERSION, readByte(din));
			component.put(NTLM_PROD_BUILD, readShort(din));
			component.put(NTLM_RESERVED_VS, readBytes(3, din));
			component.put(NTLM_REV_CURR, readByte(din));
		}
	}
	
	private void writeVersion(DataOutputStream dos) throws IOException {
		if(getFlag(NTLMSSP_NEGOTIATE_VERSION)) {
			dos.writeByte((int)component.get(NTLM_PROD_MAJOR_VERSION));
			dos.write((int)component.get(NTLM_PROD_MINOR_VERSION));
			dos.writeShort(Integer.reverseBytes((int) component.get(NTLM_PROD_BUILD)));
			dos.write((byte[])component.get(NTLM_RESERVED_VS));
			dos.write((int)component.get(NTLM_REV_CURR));
		}
	}
	
	
	private void parseNTLM1(DataInputStream din) throws IOException {
		ArrayList<Object> payload = new ArrayList<Object>();
		readFlags(din);
		readLenAndOffset(din, NTLM_DOM_LEN, NTLM_DOM_MAX_LEN, NTLM_DOM_OFF);
		readLenAndOffset(din, NTLM_WRK_LEN, NTLM_WRK_MAX_LEN, NTLM_WRK_OFF);
		readVersion(din);
		int offset = getFlag(NTLMMessage.NTLMSSP_NEGOTIATE_VERSION) ? 40 : 32;
		if(getFlag(NTLMMessage.NTLMSSP_NEGOTIATE_OEM_DOMAIN_SUPPLIED)) {
			NTLMMessage.addLoad(payload, new Object[]{component.get(NTLM_DOM_OFF), component.get(NTLM_DOM_LEN), TConst.AUTH_DOMAIN, null});
		}
		if(getFlag(NTLMMessage.NTLMSSP_NEGOTIATE_OEM_WORKSTATION_SUPPLIED)) {
			NTLMMessage.addLoad(payload, new Object[]{component.get(NTLM_WRK_OFF), component.get(NTLM_WRK_LEN), NTLM_WORKSTATION, null});
		}
		NTLMMessage.loadPayload(din, component, payload, offset);
	}
	
	private static void loadPayload(DataInputStream din, Hashtable<String, Object> component, ArrayList<Object> payload, int offset) throws IOException {
		for(int i = 0; i < payload.size(); i ++) {
			Object[] objs = (Object[]) payload.get(i);
			int off = (int) objs[0];
			int len = (int) objs[1];
			String name = (String) objs[2];
			String charset = (String) objs[3];
			if((off - offset) < 0) {
				throw new IOException(String.format(NTLM_PAYLOAD_ERROR, name));
			}
			if(off > offset) {
				offset += readBytes(off - offset, din).length;
			}
			if(charset != null) {
				if(charset.equals(BYTES)) {
					component.put(name, readBytes(len, din));
					//System.out.println(name + ": " + new String((byte[])component.get(name)));
				}
				else {
					component.put(name, new String(readBytes(len, din), charset));
					//System.out.println(name + ": " + component.get(name));
				}
			}
			else {
				component.put(name, new String(readBytes(len, din)));
				//System.out.println(name + ": " + component.get(name));
			}
			offset += len;
		}
	}

	private static void addLoad(ArrayList<Object> payload, Object[] objs) {
		int i = 0;
		for(; i < payload.size(); i ++) {
			if(((int) objs[0]) < ((int) (((Object[]) payload.get(i))[0]))) {
				break;
			}
		}
		payload.add(i, objs);
	}

	private static byte[] readBytes(int i, DataInputStream in) throws IOException {
		byte[] b = new byte[i];
		if(in.read(b) < b.length) {
			throw new IOException (UNEXPECTED_EOF);
		}
		return b;
	}

	private static int readByte(DataInputStream in) throws IOException {
		int i = in.read();
		if(i < 0) {
			throw new IOException (UNEXPECTED_EOF);
		}
		return i;
	}

	private static int readShort(DataInputStream in) throws IOException {
		try {
			return Short.reverseBytes(in.readShort()) & 0xFFFF;
		}
		catch(Exception e) {
			throw new IOException(e);
		}
	}

	private static int readInt(DataInputStream in) throws IOException {
		try {
			return Integer.reverseBytes(in.readInt());
		}
		catch(Exception e) {
			throw new IOException(e);
		}
	}
	
	private boolean getFlag(int flagIndex) throws IOException {
		int flag = NTLMMessage.getFlag(this, flagIndex);
		if(flag < 0) {
			throw new IOException(String.format(NTLM_FLAG_ERROR, flagIndex));
		}
		return flag == 1;
	}
	
	public static int getFlag(NTLMMessage ntlm, int flagIndex) {
		try {
			return ((BitSet) ntlm.component.get(NTLM_FLAGS)).get(flagIndex) ? 1 : 0;
		} catch (Exception e) {
			return -1;
		}
	}

	public static NTLMMessage createChallengeMsg(NTLMMessage neg) throws IOException {
		NTLMMessage chal = new NTLMMessage();
		//chal.component.put(NTLM_TNAME, "PABK.SK");
		chal.component.put(NTLM_SERVER_NONCE, NTLMMessage.createNonce(8));
		loadVersion(neg, chal);
		chal.component.put(NTLM_FLAGS, neg.component.get(NTLM_FLAGS));
		chal.component.put(NTML_RESERVED, new byte[8]);
		return chal;
	}

	static byte[] createNonce(int i) {
		byte[] b = new byte[i];
		for(i = 0; i < b.length; i ++) {
			b[i] = (byte) Math.floor(Math.random() * Byte.MAX_VALUE);
		}
		return b;
	}

	public String getAuthString() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		out.write(PROTOCOL.getBytes());
		DataOutputStream dos = new DataOutputStream(out);
		dos.writeInt(Integer.reverseBytes(type));
		switch(type) {
		case NTLMMessage.CHALLENGE_MESSAGE:
			encodeMTLM2(dos);
			return new String(Base64Coder.encode(out.toByteArray()));
		default:
			throw new IOException ("Not supported");
		}
	}

	private void encodeMTLM2(DataOutputStream dos) throws IOException {
		ByteArrayOutputStream payload = new ByteArrayOutputStream();
		int offset = getFlag(NTLMMessage.NTLMSSP_NEGOTIATE_VERSION) ? 56 : 48;
		offset = encodePayload(dos, payload, NTLMMessage.NTLM_TNAME, DEFAULT_NEGOTIATE_ENCODING, offset);
		dos.write(((BitSet)component.get(NTLM_FLAGS)).toByteArray());
		dos.write((byte[])component.get(NTLM_SERVER_NONCE));
		dos.write((byte[])component.get(NTML_RESERVED));
		offset = encodePayload(dos, payload, NTLMMessage.NTLM_TINFO, DEFAULT_NEGOTIATE_ENCODING, offset);
		writeVersion(dos);
		dos.write(payload.toByteArray());
	}

	private int encodePayload(DataOutputStream dos, ByteArrayOutputStream payload, String key, String charset, int offset) throws IOException {
		Object obj = component.get(key);
		byte[] b = obj != null ? (charset == null ? ((String) obj).getBytes() : (charset.equals(BYTES) ? (byte[]) obj : ((String) obj).getBytes(charset))) : new byte[0];
		int l = b.length;
		dos.writeShort(Short.reverseBytes((short) l));
		dos.writeShort(Short.reverseBytes((short) l));
		dos.writeInt(Integer.reverseBytes(offset));
		payload.write(b);
		return offset + b.length;
	}
	
	private static Object get(String key, NTLMMessage msg1, NTLMMessage msg2) {
		return  msg1.get(key) == null ? msg2.get(key) : (msg2.get(key) == null ? msg1.get(key) : (msg2.get(key).equals(msg1.get(key)) ? msg2.get(key) : null));
	}
	
	@Override
	public Principal authorize(CallbackHandler cb, Principal p, Object... msgs) throws IOException, LoginException {
		if(msgs != null && msgs.length == 3 && msgs[0] instanceof NTLMMessage && msgs[1] instanceof NTLMMessage && msgs[2] instanceof HttpExchange) {
			NTLMMessage neg = (NTLMMessage) msgs[0];
			NTLMMessage chl = (NTLMMessage) msgs[1];
			HttpExchange ex = (HttpExchange) msgs[2];
			String wrk = (String) NTLMMessage.get(NTLM_WORKSTATION, neg, this);
			String usr = (String) NTLMMessage.get(TConst.AUTH_USERNAME, neg, this);
			if(wrk == null || usr == null) {
				throw new IOException(NULL_ERROR);
			}
			TinyPrincipal principal = createPrincipal((TinyPrincipal) p);
			principal.addURL(ex.getRequestURI());
			if(cb == null) {
				cb = new TinyNTLMCallbackHandler();
			}
			if(cb != null && cb instanceof NTLMCallbackHandler) {
				Subject subject = new Subject();
				subject.getPrincipals().add(principal);
				((NTLMCallbackHandler)cb).setNegotiateMessage(neg);
				((NTLMCallbackHandler)cb).setChallengeMessage(chl);
				((NTLMCallbackHandler)cb).setResponseMessage(this);
				LoginContext lc = new LoginContext(TConst.NTLM_LOGIN_INDEX, subject, cb, Configuration.getConfiguration());
				lc.login();
				return subject.getPrincipals().iterator().next();
			}
			else {
				throw new LoginException("Login error");
			}
			return principal;
		}
		throw new IOException (TConst.PARAMETERS_ERROR);
	}

	final Object get(String key) {
		return this.component.get(key);
	}

	final int getType() {
		return type;
	}
	
}
