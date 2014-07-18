package org.acepricot.asn1;

import java.io.IOException;

import org.acepricot.ber.BER;
import org.acepricot.ber.BERConst;

public abstract class SetOf extends ASN1NodeImpl {

	protected SetOf(int implicit, int optional, String name) {
		super (	
				BERConst.SET_TAG_NUMBER,
				BERConst.UNIVERSAL_CLASS,
				BERConst.CONSTRUCTED_ENCODING,
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
