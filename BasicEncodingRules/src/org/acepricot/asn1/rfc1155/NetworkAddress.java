package org.acepricot.asn1.rfc1155;

import org.acepricot.asn1.Choice;
import org.acepricot.ber.BERConst;

public class NetworkAddress extends Choice {

	protected NetworkAddress(int optional, String name) {
		super(optional, name, new IPAddress(BERConst.FALSE));
		// TODO Auto-generated constructor stub
	}

}
