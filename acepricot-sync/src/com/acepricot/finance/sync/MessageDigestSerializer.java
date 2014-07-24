package com.acepricot.finance.sync;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

class MessageDigestSerializer {
	
	private static final ArrayList<MessageDigestSerializer> mdsList = new ArrayList<MessageDigestSerializer>();
	
	private MessageDigest md;
	private boolean reseted = true;
	
	public static synchronized int getInstance(String digestAlgorithm) throws NoSuchAlgorithmException {
		MessageDigestSerializer mds; 
		for(int i = 0; i < mdsList.size(); i ++) {
			mds = mdsList.get(i);  
			if(mds.getMd().getAlgorithm().equalsIgnoreCase(digestAlgorithm) && mds.isReseted()) {
				mds.setReseted(false);
				return i;
			}
		}
		mds = new MessageDigestSerializer();
		mds.setMd(MessageDigest.getInstance(digestAlgorithm));
		mds.setReseted(false);
		mdsList.add(mds);
		return mdsList.size() - 1;
	}
	
	final static MessageDigestSerializer getMDS(int index) {
		return mdsList.get(index);
	}
	
	private final MessageDigest getMd() {
		return md;
	}

	private final void setMd(MessageDigest md) {
		this.md = md;
	}

	private final boolean isReseted() {
		return reseted;
	}

	private final void setReseted(boolean reseted) {
		this.reseted = reseted;
	}

	void update(byte[] b, int offset, int len) {
		this.getMd().update(b, offset, len);
	}

	byte[] digest() {
		return this.getMd().digest();
	}

	public void reset() {
		this.getMd().reset();
		this.setReseted(true);
	}

	public static void reset(int index) {
		mdsList.get(index).reset();		
	}
}
