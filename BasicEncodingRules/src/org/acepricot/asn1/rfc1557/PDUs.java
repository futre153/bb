package org.acepricot.asn1.rfc1557;

import org.acepricot.asn1.Choice;
import org.acepricot.ber.BERConst;

public class PDUs extends Choice {

	private static final String GET_REQUEST = "get-request";
	private static final String GET_NEXT_REQUEST = "get-next-request";
	private static final String GET_RESPONSE = "get-response";
	private static final String SET_REQUEST = "set-request";
	private static final String TRAP = "trap";

	protected PDUs(String name) {
		super(BERConst.TRUE, name, new GetRequest(GET_REQUEST), new GetNextRequest(GET_NEXT_REQUEST), new GetResponse(GET_RESPONSE), new SetRequest(SET_REQUEST), new Trap(TRAP));
		// TODO Auto-generated constructor stub
	}

}
