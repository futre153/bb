package org.acepricot.asn1.rfc1557;

import org.acepricot.asn1.Application;
import org.acepricot.ber.BERConst;

public class GetResponse extends Application {

	private static final int GET_RESPONSE_PDU = 2;

	protected GetResponse(String name) {
		super(GET_RESPONSE_PDU, BERConst.FALSE, name, new PDU(null));
		// TODO Auto-generated constructor stub
	}

}
