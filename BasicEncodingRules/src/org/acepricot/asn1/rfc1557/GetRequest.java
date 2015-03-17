package org.acepricot.asn1.rfc1557;

import org.acepricot.asn1.Application;
import org.acepricot.ber.BERConst;

public class GetRequest extends Application {

	private static final int GET_REQUEST_PDU_TAG = 0;

	protected GetRequest(String name) {
		super(GET_REQUEST_PDU_TAG, BERConst.FALSE, name, new PDU(null));
		// TODO Auto-generated constructor stub
	}

}
