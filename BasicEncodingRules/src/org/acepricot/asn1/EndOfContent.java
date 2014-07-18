package org.acepricot.asn1;

import org.acepricot.ber.BER;
import org.acepricot.ber.BERConst;

public class EndOfContent extends ASN1NodeImpl {
	private static final String DEFAULT_EOC_NAME = "end-of-content";

	public EndOfContent(int implicit, int optional) {
		super (	
				BERConst.EOC_TAG_NUMBER,
				BERConst.UNIVERSAL_CLASS,
				BERConst.PRIMITIVE_ENCODING,
				implicit,
				optional,
				DEFAULT_EOC_NAME
				);
	}

	@Override
	protected void loadFromExisting(BER ber) {
		// TODO Auto-generated method stub
		
	}
}
