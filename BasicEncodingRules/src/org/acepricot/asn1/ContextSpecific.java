package org.acepricot.asn1;

import java.io.IOException;

import org.acepricot.ber.BER;
import org.acepricot.ber.BERConst;

abstract class ContextSpecific extends ASN1NodeImpl {
	
	public ContextSpecific(int tagNumber, int implicit, int optional, String name) {
		super (
				tagNumber,
				BERConst.CONTEXT_SPECIFIC_CLASS,
				BERConst.NOT_DEFINED,
				implicit,
				optional,
				name
				);
	}

	@Override
	protected void loadFromExisting(BER ber) throws IOException {
		// TODO Auto-generated method stub

	}

}
