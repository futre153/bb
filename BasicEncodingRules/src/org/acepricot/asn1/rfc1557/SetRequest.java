package org.acepricot.asn1.rfc1557;

import org.acepricot.asn1.Application;
import org.acepricot.ber.BERConst;

public class SetRequest extends Application {

	private static final int SET_REQUEST_PDU_TAG = 3;

	protected SetRequest(String name) {
		super(SET_REQUEST_PDU_TAG, BERConst.FALSE, name, new PDU(null));
		// TODO Auto-generated constructor stub
	}

}
