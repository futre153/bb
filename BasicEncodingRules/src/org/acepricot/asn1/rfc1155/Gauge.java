package org.acepricot.asn1.rfc1155;

import org.acepricot.asn1.Application;
import org.acepricot.asn1.impl.ImpInteger;

public class Gauge extends Application {

	private static final int GAUGE_APP = 2;

	protected Gauge(int optional, String name) {
		super(GAUGE_APP, optional, name, new ImpInteger(null));
		// TODO Auto-generated constructor stub
	}

}
