package org.acepricot.asn1;

import java.io.IOException;

import org.acepricot.asn1.rfc2986.Attributes;
import org.acepricot.ber.BER;
import org.acepricot.ber.BERConst;

public class ContextSpecific extends ASN1NodeImpl {
	
	public ContextSpecific(int tagNumber, int implicit, int optional, String name, Attributes attributes) {
		super (
				tagNumber,
				BERConst.CONTEXT_SPECIFIC_CLASS,
				BERConst.NOT_DEFINED,
				implicit,
				optional,
				name
				);
		this.setSeq(new ASN1NodeImpl[]{attributes});
	}

	@Override
	protected void loadFromExisting(BER ber) throws IOException {
		// TODO Auto-generated method stub

	}

}
