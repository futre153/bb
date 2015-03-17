package org.acepricot.asn1;

import java.io.IOException;

import org.acepricot.ber.BER;
import org.acepricot.ber.BERConst;

abstract class UniversalConstructed extends ConstructedNode {

	protected UniversalConstructed(int tag, int imp, int opt, String mne, ASN1NodeImpl[] sequences) {
		super(tag, BERConst.UNIVERSAL_CLASS, imp, opt, mne, sequences);
		// TODO Auto-generated constructor stub
	}
	
	protected void loadFromExisting(BER ber) throws IOException {
		super.loadFromExisting(ber);
		int l = 
	}
	
	protected void loadSequences() {
		int 
	}
	
}
