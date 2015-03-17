package org.acepricot.asn1;

import org.acepricot.ber.BERConst;

public class UniversalPrimitive extends PrimitiveNode {

	protected UniversalPrimitive(int tag, int imp, int opt, String mne) {
		super(tag, BERConst.UNIVERSAL_CLASS, imp, opt, mne);
		// TODO Auto-generated constructor stub
	}

}
