package org.acepricot.finance.web.msgs;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.acepricot.finance.web.util.OidRepository;

public class RegistrationRequest extends AceData {

	public RegistrationRequest(String eMail, String algorithm, byte[] pwd, String version) throws IOException, NoSuchAlgorithmException {
		super();
		AceDataItemImpl item1 = new AceDataItemImpl(EMAIL_HEADER, "", eMail.getBytes());
		String[] oid = null;
		if(algorithm != null) {
			oid = OidRepository.getPair(algorithm); 
		}
		else {
			oid = OidRepository.getPair(DEFAULT_DIGEST_ALGORITHM);
			MessageDigest digest = MessageDigest.getInstance(oid[1]);
			digest.update(pwd);
			pwd = digest.digest();
		}
		AceDataItemImpl item2 = new AceDataItemImpl(PASSWORD_HEADER, oid[0], pwd);
		if(version == null) {
			version = "";
		}
		AceDataItemImpl item3 = new AceDataItemImpl(VERSION_HEADER, "", version.getBytes());
		this.setConstructedContent(item1, item2, item3);
		this.setMessageType(REGISTRATION_REQUEST);
	}
	
}