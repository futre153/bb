package org.acepricot.asn1;

import java.io.IOException;

import org.acepricot.ber.BER;
import org.acepricot.ber.BERConst;

abstract class PrimitiveNode extends ASN1NodeImpl {

	protected PrimitiveNode(int tag, int css, int imp, int opt,	String mne) {
		super(tag, css, BERConst.FALSE, imp, opt, mne);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void loadFromExisting(BER ber) throws IOException {
		// TODO Auto-generated method stub
		ASN1NodeImpl.checkPrimitive(this, ber);
		this.setBer(ber);
	}

}