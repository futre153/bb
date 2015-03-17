package org.acepricot.asn1.rfc1155;

import org.acepricot.asn1.Application;
import org.acepricot.asn1.impl.ImpInteger;

public class TimeTicks extends Application {

	private static final int TIME_TICKS_APP = 3;

	protected TimeTicks (int optional, String name) {
		super(TIME_TICKS_APP, optional, name, new ImpInteger(null));
		// TODO Auto-generated constructor stub
	}

}
