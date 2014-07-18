package org.acepricot.finance.client;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;

import org.acepricot.ber.BERIOStream;
import org.acepricot.finance.web.msgs.AceData;
import org.acepricot.finance.web.msgs.DeregistrationRequest;
import org.acepricot.finance.web.msgs.FileUploadRequest;
import org.acepricot.finance.web.msgs.FilepartUploadRequest;
import org.acepricot.finance.web.msgs.InitSyncRequest;
import org.acepricot.finance.web.msgs.RegistrationRequest;
import org.acepricot.finance.web.msgs.RemoveNode;
import org.acepricot.finance.web.msgs.SQLRequest;
import org.acepricot.finance.web.msgs.SyncConfRequest;
import org.acepricot.finance.web.util.OidRepository;
import org.acepricot.finance.web.ws.AceOperations;
import org.acepricot.finance.web.ws.Message;

public class MessageProcessor {
	
	private static final String CONNECTION_PROBLEM = "CONNECTION_PROBLEM";
	private static final String REQUEST_CREATION_FAILED = "Failed to create authorization request content data";
	private static final String FAILED_ENCODING_ACEDATA = "Failed to BER encode AceData";
	private static final String FAILED_TO_CREATE_MESSAGE = "Failed to create web service message";
	private static final String UNKNOWN_ERROR = "Unknown error found";
	private static final String FAILED_RESPONSE_MESSAGE = "Failed to encode response web service message";
	private static final String WORNG_RESPONSE_TYPE = "Wrong response type returned";
	private static final String RESPONSE_ERROR = "Error while processing response";
	private static final long FILEPART_LENGTH = 1024*16;
	private static final String DEFAULT_DIGEST_ALGORITHM = "SHA-256";
	//private static String status;
	//private static String message;
	//private static String congif;
	//private static String configFilename;
	
	private static final String[] retValueKeys = {
		AceData.RESPONSE_STATUS_HEADER,
		AceData.REQUEST_RESPONSE_TEXT,
		AceData.FILENAME_HEADER,
		AceData.CONFIGURATION_RESPONSE_HEAD
	};
	
	private static final Hashtable<String, String> retValues = new Hashtable<String, String>();
	
	private MessageProcessor(){}
	
	public static synchronized boolean requestSyncConfig(String clid) {
		SyncConfRequest req = null;
		try {
			req = new SyncConfRequest();
			return authorize(req, clid, AceData.SYNCHRONIZATION_CONF_RESPONSE);
		}
		catch(Exception e) {
			MessageProcessor.set(0, AceData.ERROR_RESPONSE);
			MessageProcessor.set(1, REQUEST_CREATION_FAILED);
		}
		return false;
	}
	
	public static synchronized boolean uploadFilepart(String clid, String filename, int filepartId, byte[] filepart) {
		FilepartUploadRequest req = null;
		try {
			req = new FilepartUploadRequest(filename, filepartId, filepart);
			return authorize (req, clid, AceData.REGISTRATION_RESPONSE);
		}
		catch(Exception e) {
			MessageProcessor.set(0, AceData.ERROR_RESPONSE);
			MessageProcessor.set(1, REQUEST_CREATION_FAILED);
		}
		return false;
	}
	
	public static synchronized boolean upload(String svid, File file) {
		long l = file.length();
		int count = (int) ((l - 1)/FILEPART_LENGTH + 1);
		FileUploadRequest req = null;
		try {
			String[] da = OidRepository.getPair(DEFAULT_DIGEST_ALGORITHM);
			req = new FileUploadRequest(svid, count, da[0], fileDigest(file, da[1]));
			return authorize (req, svid, AceData.REGISTRATION_RESPONSE);
		}
		catch(Exception e) {
			MessageProcessor.set(0, AceData.ERROR_RESPONSE);
			MessageProcessor.set(1, REQUEST_CREATION_FAILED);
		}
		return false;
	}
	
	private static byte[] fileDigest(File file, String alg) throws IOException, NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance(alg);
		FileInputStream in = new FileInputStream(file);
		int i = 0;
		byte[] b = new byte[(int) FILEPART_LENGTH];
		while((i = in.read(b)) >= 0) {
			digest.update(b, 0, i);
		}
		in.close();
		return digest.digest();
	}

	public static synchronized boolean register(String regEmail, CharSequence password, String version) {
		try {
			return authorize (new RegistrationRequest(regEmail, null, password.toString().getBytes(), version), null, AceData.REGISTRATION_RESPONSE);
		}
		catch(Exception e) {
			MessageProcessor.set(0, AceData.ERROR_RESPONSE);
			MessageProcessor.set(1, REQUEST_CREATION_FAILED);
		}
		return false;
	}
	
	private static synchronized boolean authorize (AceData req, String id, String resType) {
		retValues.clear();
		AceData resp = null;
		try {
			resp = MessageProcessor.process(req, id);
			if(resp.getMessageType().equals(AceData.ERROR_MESSAGE)) {
				return processError(resp);
			}
			if(!resp.getMessageType().equals(resType)) {
				return processWrongType(resp);
			}
		}
		catch (Exception e) {
			MessageProcessor.set(0, AceData.ERROR_RESPONSE);
			return false;
		}
		try {
			for (int i = 0; i < retValueKeys.length; i++) {
				try {
					MessageProcessor.set(i, new String(resp.get(retValueKeys[i]).getItemValue()));
				}
				catch(IOException e) {}
			}
		}
		catch (Exception e) {
			return processResponseError();
		}
		return true;
	}
	
	
	public static synchronized boolean initSynchronization (String id, String regEmail, CharSequence password, String version) {
		try {
			return authorize (new InitSyncRequest(regEmail, null, password.toString().getBytes(), version), id, AceData.REGISTRATION_RESPONSE);
		}
		catch(Exception e) {
			MessageProcessor.set(0, AceData.ERROR_RESPONSE);
			MessageProcessor.set(1, REQUEST_CREATION_FAILED);
		}
		return false;
	}
	
	
	private static boolean processResponseError() {
		MessageProcessor.set(0, AceData.ERROR_RESPONSE);
		MessageProcessor.set(1, RESPONSE_ERROR);
		return false;
	}

	private static boolean processWrongType(AceData resp) {
		MessageProcessor.set(0, AceData.ERROR_RESPONSE);
		MessageProcessor.set(1, WORNG_RESPONSE_TYPE);
		return false;
	}

	private static boolean processError(AceData resp) {
		MessageProcessor.set(0, AceData.ERROR_RESPONSE);
		try {
			MessageProcessor.set(1, new String(resp.get(AceData.ERROR_MESSAGE_HEADER).getItemValue()));
		}
		catch (IOException e) {
			MessageProcessor.set(1, UNKNOWN_ERROR);
		}
		return false;
	}

	private static AceData process(AceData data, String id) throws Exception {
		try {
			Message msg = Message.getInstance();
			BERIOStream enc = new BERIOStream();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			enc.setOutputStream(out);
			try {
				data.encode(enc);
			}
			catch (IOException e) {
				set(1, FAILED_ENCODING_ACEDATA);
				throw e;
			}
			try {
				msg.setMessage(id, null, null, null, data.getMessageType() ,out.toByteArray());
			}
			catch (Exception e) {
				set(1, FAILED_TO_CREATE_MESSAGE);
				throw e;
			}
			try {
				msg = AceOperations.getMessage(msg);
			}
			catch (Exception e) {
				set(1, CONNECTION_PROBLEM);
				throw e;
			}
			try {
				return msg.getMessage();
			}
			catch (Exception e) {
				set(1, FAILED_RESPONSE_MESSAGE);
				throw e;
			}
			
		}
		catch (Exception e) {
			if(getMessage() == null) {
				set(1, UNKNOWN_ERROR);
			}
			throw e;
		}
	}
	
	private static void set(int i, String value) {
		retValues.put(retValueKeys[i], value);
	}
	
	private static String get(int i) {
		return retValues.get(retValueKeys[i]);
	}
	
	public static String getStatus() {
		return get(0);
	}

	public static String getMessage() {
		return get(1);
	}

	public static String getConfigFilename() {
		return get(2);
	}
	
	public static String getConfigFile() {
		try {
			return new String(Message.decodeText(get(3), Message.DEFAULT_TEXT_ENCODING));
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}

	public static boolean deregister(String regEmail, CharSequence password, String svid) {
		try {
			return authorize (new DeregistrationRequest(regEmail, null, password.toString().getBytes()), svid, AceData.REGISTRATION_RESPONSE);
		}
		catch(Exception e) {
			MessageProcessor.set(0, AceData.ERROR_RESPONSE);
			MessageProcessor.set(1, REQUEST_CREATION_FAILED);
		}
		return false;
		
	}
	
	public static boolean remoteInsert(Hashtable<String, Object> data, String clid) {
		return SQLOperation(AceData.SQL_OPERATION_INSERT, data, clid);
	}
	
	public static boolean remoteDelete(Hashtable<String, Object> data, String clid) {
		return SQLOperation(AceData.SQL_OPERATION_DELETE, data, clid);
	}
	
	public static boolean remoteUpdate(Hashtable<String, Object> data, String clid) {
		return SQLOperation(AceData.SQL_OPERATION_UPDATE, data, clid);
	}
	
	private static boolean SQLOperation(String operation, Hashtable<String, Object> data, String clid) {
		try {
			return authorize(new SQLRequest(operation, data), clid, AceData.REGISTRATION_RESPONSE);
		}
		catch(Exception e) {
			MessageProcessor.set(0, AceData.ERROR_RESPONSE);
			MessageProcessor.set(1, REQUEST_CREATION_FAILED);
		}
		return false;
	}
	
	public static boolean removeNode(String regEmail, String password, String svid, String clid) {
		try {
			return authorize (new RemoveNode(regEmail, null, password.toString().getBytes(), clid), svid, AceData.REGISTRATION_RESPONSE);
		}
		catch(Exception e) {
			MessageProcessor.set(0, AceData.ERROR_RESPONSE);
			MessageProcessor.set(1, REQUEST_CREATION_FAILED);
		}
		return false;
		
	}
}
