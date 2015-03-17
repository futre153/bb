package org.acepricot.asn1.rfc1155;

import org.acepricot.asn1.Choice;
import org.acepricot.ber.BERConst;

public class ApplicationSyntax extends Choice {

	private static final String COUNTER = "counter";
	private static final String ADDRESS = "address";
	private static final String GAUGE = "gauge";
	private static final String TICKS = "ticks";
	private static final String ARBITRARY = "arbitrary";

	protected ApplicationSyntax(int optional, String name) {
		super(optional, name, new NetworkAddress(BERConst.FALSE, ADDRESS), new Counter(BERConst.FALSE, COUNTER), new Gauge(BERConst.FALSE, GAUGE), new TimeTicks(BERConst.FALSE, TICKS), new Opaque(BERConst.FALSE, ARBITRARY));
		// TODO Auto-generated constructor stub
	}

}
