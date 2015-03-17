package org.acepricot.asn1.rfc1557;

import org.acepricot.asn1.Application;
import org.acepricot.ber.BERConst;

public class GetNextRequest extends Application {

	private static final int GET_NEXT_REQUEST_PDU_TAG = 1;

	protected GetNextRequest(String name) {
		super(GET_NEXT_REQUEST_PDU_TAG, BERConst.FALSE, name, new PDU(null));
		// TODO Auto-generated constructor stub
	}

}
