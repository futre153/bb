package org.acepricot.finance.web.util;

import java.io.IOException;
import java.util.Properties;

import org.acepricot.ber.BERFormatException;

public class OidRepository {
	
	private static final String[][] repository = {
			new String[]{"SHA-256", "2.16.840.1.101.3.4.2.1"},
	};
	
	private static Properties oids = null;
	private static Properties desc = null;
	
	private OidRepository(){}
	
	public static String getOid(String key) {
		if(oids == null) {
			loadRepository();
		}
		return oids.getProperty(key);
	}
	
	public static String getDesc(String key) {
		if(desc == null) {
			loadRepository();
		}
		return desc.getProperty(key);
	}
	
	private static void loadRepository() {
		oids = new Properties();
		desc = new Properties();
		for(int i = 0; i < repository.length; i++) {
			oids.setProperty(repository[i][0], repository[i][1]);
			desc.setProperty(repository[i][1], repository[i][0]);
		}
	}
	
	public  static String[] getPair(String key) throws IOException {
		String oid, desc = null;
		oid = OidRepository.getOid(key);
		if(oid != null) {
			desc = OidRepository.getDesc(oid);
		}
		else {
			desc = OidRepository.getDesc(key);
			if(desc == null) {
				throw new BERFormatException(String.format(BERFormatException.NO_SUCH_OID, key));
			}
			else {
				oid = OidRepository.getOid(desc);
			}
		}
		return new String []{oid, desc};
	}
	
	
}
