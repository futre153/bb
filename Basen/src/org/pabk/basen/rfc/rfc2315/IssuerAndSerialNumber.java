package org.pabk.basen.rfc.rfc2315;

import org.pabk.basen.asn1.ASN1Impl;
import org.pabk.basen.asn1.BERInteger;
import org.pabk.basen.asn1.Sequence;
import org.pabk.basen.rfc.Name;

public class IssuerAndSerialNumber extends Sequence {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String ISSUER_NAME = "issuer";
	private static final boolean ISSUER_OPT = false;
	private static final String SN_NAME = "serialNumber";
	private static final boolean SN_OPT = false;
	
	private final ASN1Impl[] ISS_SN_SEQ = {
		new Name (ISSUER_NAME, ISSUER_OPT),
		new BERInteger (SN_NAME, SN_OPT)
	};

	public IssuerAndSerialNumber(String name, boolean optional) {
		super(name, optional);
		this.setSequences(ISS_SN_SEQ);
	}
	private IssuerAndSerialNumber() {}
	@Override
	public IssuerAndSerialNumber clone() {
		return (IssuerAndSerialNumber) ASN1Impl.clone(new IssuerAndSerialNumber(), this);
	}
}
