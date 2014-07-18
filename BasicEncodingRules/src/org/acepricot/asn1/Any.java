package org.acepricot.asn1;

import java.io.IOException;

import org.acepricot.ber.BER;
import org.acepricot.ber.BERConst;

public abstract class Any extends ASN1NodeImpl {
	protected Any (int optional, String name) {
		super (
				BERConst.ANY,
				BERConst.NOT_DEFINED,
				BERConst.NOT_DEFINED,
				BERConst.NOT_DEFINED,
				optional,
				name
				);
	}
	@Override
	protected void loadFromExisting(BER ber) throws IOException {
		// TODO Auto-generated method stub

	}

}
