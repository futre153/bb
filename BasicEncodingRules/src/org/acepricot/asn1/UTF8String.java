package org.acepricot.asn1;

import java.io.IOException;

import org.acepricot.ber.BER;
import org.acepricot.ber.BERConst;

public class UTF8String extends ASN1NodeImpl {
	public UTF8String (int implicit, int optional, String name) {
		super (
				BERConst.T61STRING_TAG_NUMBER,
				BERConst.UNIVERSAL_CLASS,
				BERConst.NOT_DEFINED,
				implicit,
				optional,
				name
				);
	}

	@Override
	protected void loadFromExisting(BER ber) throws IOException {
		// TODO Auto-generated method stub
		
	};
}
