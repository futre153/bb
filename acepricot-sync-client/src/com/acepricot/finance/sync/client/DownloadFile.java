package com.acepricot.finance.sync.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;

import org.pabk.net.http.DefaultContent;
import org.pabk.net.http.HttpClientConst;
import org.pabk.net.http.SimpleClient;

import com.acepricot.finance.sync.share.AppConst;
import com.acepricot.finance.sync.share.JSONMessage;
import com.google.gson.Gson;

public class DownloadFile extends Thread {
	
	private static final String DEFAULT_DIGEST_ALGOSRITH = "SHA-256";
	private static final String DOWNLOAD = "Download in progress";
	private static final int MAX_READED_BYTES = 0x10000;
	private static Gson gson;
	private File file;
	private int uri, id;
	private String url, digestAlgorithm;
	private byte[] digest;
	private JSONMessage message;
	private double progress;
	private int approxParts;
	private int count;
			
	public DownloadFile(String dstPath, String url, int uri, int id, String digestAlgorithm, byte[] digest, int approxParts) throws IOException {
		if(gson == null) {
			 gson = new Gson();
		}
		File f = new File(dstPath);
		if(f.exists()) {
			if (!f.delete()) {
				throw new IOException("Failed to delete old file " + dstPath);
			}
		}
		if(!f.createNewFile()) {
			throw new IOException("Failed to create new file " + dstPath);
		}
		this.setFile(f);
		this.setUrl(url);
		this.setUri(uri);
		this.setFileId(id);
		this.setDigestAlgorithm(digestAlgorithm != null ? digestAlgorithm : DEFAULT_DIGEST_ALGOSRITH);
		this.setDigest(digest);
		this.setApproxParts(approxParts);
		this.setCount(0);
		this.setDaemon(true);
	}
	
	JSONMessage download() {
		JSONMessage msg;
		FileOutputStream out = null;
		SimpleClient client;
		boolean cookiesAllowed;
		MessageDigest md;
		DefaultContent content;
		StringBuffer sb;
		String encoding, contentType;
		boolean last;
		byte[] b;
		InputStream in;
		
		last = false;
		msg = new JSONMessage(JSONMessage.DOWNLOAD_HEADER);
		cookiesAllowed = SimpleClient.isCookiesAllowed();
		SimpleClient.setCookiesAllowed(true);
		sb = new StringBuffer();
		encoding = HttpClientConst.ENCODINGS[HttpClientConst.DEFAULT_ENCODING];
		this.setProgress(0x0L);
		
		try {
			md = MessageDigest.getInstance(this.getDigestAlgorithm());
			out = new FileOutputStream(this.getFile());
			do {
				msg.setBody(new Object[] {Integer.toBinaryString(this.getFileId()), Integer.toBinaryString(this.getUri())});
				sb.delete(0, sb.length());
				sb.append(this.getUrl());
				sb.append('?');
				sb.append(AppConst.JSON_HEADER_KEY);
				sb.append('=');
				sb.append(URLEncoder.encode(msg.getHeader(), encoding));
				sb.append('&');
				sb.append(AppConst.JSON_BODY_KEY);
				sb.append('=');
				sb.append(URLEncoder.encode(gson.toJson(msg.getBody()), encoding));
				client = new SimpleClient(sb.toString());
				content = new DefaultContent(null, HttpClientConst.APP_JSON_CONTENT);
				int rs = client.getResponseCode(content);
				switch(rs) {
				case HttpURLConnection.HTTP_OK:
					in = client.getInputStream();
					contentType = client.getContentType();
					if(contentType.contains(HttpClientConst.CONTENT_TYPES[HttpClientConst.APP_JSON_CONTENT])) {
						return JSONMessageProcessorClient.responseProcessor(in);
					}
					else if (contentType.contains(HttpClientConst.CONTENT_TYPES[HttpClientConst.APP_H2DB_CONTENT])) {
						int i;
						b = new byte[MAX_READED_BYTES];
						while((i = in.read(b)) >= 0) {
							out.write(b, 0, i);
							md.update (b, 0, i);
						}
						HttpCookie cookie = client.getCookie(this.getUrl(), Integer.toBinaryString(this.getFileId()));
						if(cookie == null) {
							throw new IOException("Failed to retrieve cookie");
						}
						else {
							String uri = cookie.getValue();
							last = uri.matches(AppConst.JSON_LAST_URI_MASK);
							if(last) {
								uri = uri.substring(AppConst.LAST_URI_SIGN.length());
								if(!MessageDigest.isEqual(getDigest(), md.digest())) {
									return msg.sendAppError("Message digest check of downloaded file fails");
								}
							}
							this.setUri(Integer.parseInt(uri, 2));
							break;
						}
					}
				default:
					in = client.getErrorStream();
					return JSONMessageProcessorClient.responseError(in, encoding);
				}
				this.setCount(this.getCount() + 1);
				setProgress(this.getApproxParts() == 0 ? 0 : ((double) this.getCount())/this.getApproxParts());
			}
			while(!last);
		}
		catch(Exception e) {
			return msg.sendAppError(e);
		}
		finally {
			try {
				out.close();
			} catch (IOException e) {}
			SimpleClient.setCookiesAllowed(cookiesAllowed);
			if(msg.isError()) {
				try {
					//this.getFile().delete();
				}
				catch (Exception e1) {}
				return msg;
			}
		}
		return msg.returnOK();
	}
		
		
		
		
		
		
	public void run() {
		setMessage(download());
	}
	
	private final File getFile() {
		return file;
	}

	private final void setFile(File file) {
		this.file = file;
	}

	private final int getUri() {
		return uri;
	}

	private final void setUri(int uri) {
		this.uri = uri;
	}

	private final int getFileId() {
		return id;
	}

	private final void setFileId(int id) {
		this.id = id;
	}

	private final String getDigestAlgorithm() {
		return digestAlgorithm;
	}

	private final void setDigestAlgorithm(String digestAlgorithm) {
		this.digestAlgorithm = digestAlgorithm;
	}

	private final byte[] getDigest() {
		return digest;
	}

	private final void setDigest(byte[] digest) {
		this.digest = digest;
	}

	final JSONMessage getMessage() {
		return message;
	}

	private final void setMessage(JSONMessage message) {
		this.message = message;
	}

	final double getProgress() {
		return progress * 100;
	}

	private final void setProgress(double progress) {
		this.progress = progress;
	}

	String getActionInProgress() {
		return DOWNLOAD + String.format(" %.2f", getProgress()) + "%"; 
	}

	private final String getUrl() {
		return url;
	}

	private final void setUrl(String url) {
		this.url = url;
	}

	private final int getApproxParts() {
		return approxParts;
	}

	private final void setApproxParts(int approxParts) {
		this.approxParts = approxParts;
	}

	private final int getCount() {
		return count;
	}

	private final void setCount(int count) {
		this.count = count;
	}
}
