package org.acepricot.asn1.rfc1155;

import org.acepricot.asn1.Application;
import org.acepricot.asn1.impl.ImpOctetString;

public class IPAddress extends Application {

	private static final String INTERNET = "internet";
	private static final int IP_ADDRESS_APP = 0;
	

	protected IPAddress(int optional) {
		super(IP_ADDRESS_APP, optional, INTERNET, new ImpOctetString(null));
		// TODO Auto-generated constructor stub
	}

}
