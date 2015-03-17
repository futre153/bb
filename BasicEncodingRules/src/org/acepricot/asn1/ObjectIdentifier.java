package org.acepricot.asn1;

import java.io.IOException;

import org.acepricot.ber.BER;
import org.acepricot.ber.BERConst;

public abstract class ObjectIdentifier extends ASN1NodeImpl {
	protected ObjectIdentifier (int implicit, int optional, String name) {
		super (
				BERConst.OBJECTIDENTIFIER_TAG_NUMBER,
				BERConst.UNIVERSAL_CLASS,
				BERConst.PRIMITIVE_ENCODING,
				implicit,
				optional,
				name
				);
	}
	
	protected final void loadFromExisting(BER ber) throws IOException {
		ASN1NodeImpl.checkPrimitive(this, ber);
		this.setBer(ber);
	}
}
