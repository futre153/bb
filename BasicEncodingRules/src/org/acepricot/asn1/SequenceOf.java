package org.acepricot.asn1;

import java.io.IOException;

import org.acepricot.ber.BER;
import org.acepricot.ber.BERConst;

public abstract class SequenceOf extends ASN1NodeImpl {
	
	protected SequenceOf(int implicit, int optional, String name) {
		super (	
				BERConst.SEQUENCE_TAG_NUMBER,
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
