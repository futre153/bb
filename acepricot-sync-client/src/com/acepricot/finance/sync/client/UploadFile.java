package com.acepricot.finance.sync.client;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpCookie;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.pabk.net.http.DefaultContent;
import org.pabk.net.http.HttpClientConst;
import org.pabk.net.http.SimpleClient;

import com.acepricot.finance.sync.share.JSONMessage;
import com.google.gson.Gson;

public class UploadFile extends Thread {
	
	private static final int MAX_BYTES = 0x100000;
	private static final int COMMON_NUMBER_OF_PARTS = 100;
	private File file;
	private JSONMessage message;
	private String url;
	private double progress;
	private boolean prepare;
	private String digestAlgorithm;
	private static final String DEFUALT_DIGEST_ALGORITHM = "SHA-256";
	private static final String PREPARATION = "Prepare to upload";
	private static final String UPLOAD = "Upload in progress";
	private String groupName;
	private byte[] secret;
	private MessageDigest messageDigest;
	
	public UploadFile(String groupName, CharSequence password, File f, String url, String mda, String passCharset) throws IOException, NoSuchAlgorithmException {
		this.setGroupName(groupName);
		if(mda == null) {
			mda = DEFUALT_DIGEST_ALGORITHM;
		}
		this.setDigestAlgorithm(mda);
		MessageDigest md;
		try {
			md = MessageDigest.getInstance(getDigestAlgorithm());
		} catch (NoSuchAlgorithmException e) {
			md = MessageDigest.getInstance(DEFUALT_DIGEST_ALGORITHM);
		}
		md.reset();
		this.setMessageDigest(md);
		if(passCharset == null) {
			this.setSecret(md.digest(password.toString().getBytes()));
		}
		else {
			this.setSecret(md.digest(password.toString().getBytes(passCharset)));
		}
		this.setFile(f);
		this.setUrl(url);
		this.setPrepare(true);
	}

	public void run() {
		try {
			setMessage(upload());
		} catch (IOException e) {
			setMessage(getMessage().sendAppError(e));
		}
	}
	
	JSONMessage upload() throws IOException {
		FileInputStream in = null;
		SimpleClient client = null;
		DefaultContent content = null;
		ByteArrayInputStream bin = null;
		boolean cookiesAllowed = SimpleClient.isCookiesAllowed();
		try {
				
			in = new FileInputStream(getFile());
			long l = this.getFile().length();
			long c = 0;
			setProgress(l == 0 ? 0 : ((double) c)/l);
			byte[] b = new byte[MAX_BYTES];
			int i;
			while((i = in.read(b)) >= 0) {
				this.getMessageDigest().update(b, 0, i);
				c += i;
				setProgress(l == 0 ? 0 : ((double) c)/l);
			}
			in.close();
			JSONMessage msg = new JSONMessage("initUpload", new Object[] {
				this.getGroupName(),
				this.getSecret(),
				this.getMessageDigest().digest(),
			});
		
			SimpleClient.setCookiesAllowed(true);
		
			client = new SimpleClient(getUrl());
			content = new DefaultContent(new Gson().toJson(msg), HttpClientConst.APP_JSON_CONTENT);
			setMessage(JSONMessageProcessorClient.responseProcessor(client.execute(content)));
			if(getMessage().isError()) {
				return getMessage();
			}
			
			setPrepare(false);
			
			int max = l > (COMMON_NUMBER_OF_PARTS * MAX_BYTES) ? (int) (l / MAX_BYTES) + 1 : COMMON_NUMBER_OF_PARTS;
			int size = (int) (l/max);
			int last_size = (int) (l - size * (max - 1));
			int bufferSize = size > last_size ? size : last_size;
			b = null;
			b = new byte[bufferSize];
		
		
			in = new FileInputStream(getFile());
			c = 0;
			setProgress(l == 0 ? 0 : ((double) c)/l);
			for(i = 0; i < max; i ++) {
				String id = Integer.toBinaryString(((Double) this.getMessage().getBody()[1]).intValue());
				String value = Integer.toBinaryString(((Double) this.getMessage().getBody()[2]).intValue());
				int x = -1;
				if((i + 1) == max) {
					x = in.read(b, 0, last_size);
					value = "LAST" + value;
				}
				else {
					x = in.read(b, 0, size);
				}
				if(x < 0) {
					throw new IOException("Unexpected end of file");
				}
				c += x;
				if(c > l) {
					throw new IOException("File reads past end");
				}
				HttpCookie cookie = new HttpCookie(id, value);
				SimpleClient.setCookie(getUrl(), cookie);
				client = new SimpleClient(getUrl());
				client.setMethod(HttpClientConst.PUT_METHOD);
				bin = new ByteArrayInputStream(b, 0, x);
				content = new DefaultContent(bin, HttpClientConst.APP_H2DB_CONTENT);
				setMessage(JSONMessageProcessorClient.responseProcessor(client.execute(content)));
				if(getMessage().isError()) {
					return getMessage();
				}
				setProgress(l == 0 ? 0 : ((double) c)/l);
				SimpleClient.removeCookie(getUrl(), cookie);
			}
			if(c != l) {
				throw new IOException("File length check failed");
			}
			setProgress(1);
		}
		catch(Exception e) {
			throw new IOException(e);
		}
		finally {
			in.close();
		}
		SimpleClient.setCookiesAllowed(cookiesAllowed);
		return getMessage().returnOK();
	}
	
	private final File getFile() {
		return file;
	}

	private final void setFile(File file) {
		this.file = file;
	}

	final JSONMessage getMessage() {
		return message;
	}

	private final void setMessage(JSONMessage message) {
		this.message = message;
	}

	private final String getUrl() {
		return url;
	}

	private final void setUrl(String url) {
		this.url = url;
	}

	double getProgress() {
		return progress * 100;
	}

	private void setProgress(double progress) {
		this.progress = progress;
	}

	private String getDigestAlgorithm() {
		return digestAlgorithm;
	}

	private void setDigestAlgorithm(String digestAlgorithm) {
		this.digestAlgorithm = digestAlgorithm;
	}

	private MessageDigest getMessageDigest() {
		return messageDigest;
	}

	private void setMessageDigest(MessageDigest messageDigest) {
		this.messageDigest = messageDigest;
	}

	private String getGroupName() {
		return groupName;
	}

	private void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	private byte[] getSecret() {
		return secret;
	}

	private void setSecret(byte[] secret) {
		this.secret = secret;
	}

	private boolean isPrepare() {
		return prepare;
	}

	private void setPrepare(boolean prepare) {
		this.prepare = prepare;
	}
	
	String getActionInProgress() {
		return (isPrepare() ? PREPARATION : UPLOAD) + String.format(" %.2f", getProgress()) + "%"; 
	}
}
