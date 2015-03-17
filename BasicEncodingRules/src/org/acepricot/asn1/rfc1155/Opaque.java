package org.acepricot.asn1.rfc1155;

import org.acepricot.asn1.Application;
import org.acepricot.asn1.impl.ImpOctetString;

public class Opaque extends Application {

	private static final int OPAQUE_APP = 4;
	
	protected Opaque(int optional, String name) {
		super(OPAQUE_APP, optional, name, new ImpOctetString(null));
		// TODO Auto-generated constructor stub
	}

}

