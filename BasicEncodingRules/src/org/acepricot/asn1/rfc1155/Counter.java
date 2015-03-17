package org.acepricot.asn1.rfc1155;

import org.acepricot.asn1.Application;
import org.acepricot.asn1.impl.ImpInteger;

public class Counter extends Application {

	private static final int COUNTER_APP = 1;

	protected Counter(int optional, String name) {
		super(COUNTER_APP, optional, name, new ImpInteger(null));
		// TODO Auto-generated constructor stub
	}

}
