package org.acepricot.asn1.rfc1557;

import org.acepricot.asn1.Application;
import org.acepricot.ber.BERConst;

public class Trap extends Application {

	private static final int TRAP_PDU_TAG = 4;

	protected Trap(String name) {
		super(TRAP_PDU_TAG, BERConst.FALSE, name, new TrapPDU(null));
		// TODO Auto-generated constructor stub
	}

}
