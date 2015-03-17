package org.acepricot.asn1;

import java.io.IOException;

import org.acepricot.ber.BER;
import org.acepricot.ber.BERConst;

public abstract class Null extends ASN1NodeImpl {
	
	protected Null(int implicit, int optional, String name) {
		super(
			BERConst.NULL_TAG_NUMBER,
			BERConst.UNIVERSAL_CLASS,
			BERConst.PRIMITIVE_ENCODING,
			implicit,
			optional,
			name);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void loadFromExisting(BER ber) throws IOException {
		ASN1NodeImpl.checkPrimitive(this, ber);
		this.setBer(ber);
	}

}
