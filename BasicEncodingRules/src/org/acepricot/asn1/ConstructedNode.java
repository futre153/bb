package org.acepricot.asn1;

import java.io.IOException;

import org.acepricot.ber.BER;
import org.acepricot.ber.BERConst;

abstract class ConstructedNode extends ASN1NodeImpl {

	protected ConstructedNode(int tag, int css, int imp, int opt, String mne, ASN1NodeImpl ... sequences) {
		super(tag, css, BERConst.TRUE, imp, opt, mne);
		this.setSeq(sequences);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void loadFromExisting(BER ber) throws IOException {
		ASN1NodeImpl.checkConstructed(this, ber);
	}

}
