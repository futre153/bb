package org.acepricot.asn1;

import java.io.IOException;

import org.acepricot.asn1.impl.ImpOctetString;
import org.acepricot.ber.BER;
import org.acepricot.ber.BERConst;

public abstract class OctetString extends ASN1NodeImpl {
	
	protected OctetString (int implicit, int optional, String name) {
		super (
				BERConst.OCTETSTRING_TAG_NUMBER,
				BERConst.UNIVERSAL_CLASS,
				BERConst.NOT_DEFINED,
				implicit,
				optional,
				name
				);
	}
	
	@Override
	protected void loadFromExisting(BER ber) throws IOException {
		ASN1NodeImpl.checkTagNumberAndClassType(this, ber);
		this.con = ber.isPrimitive() ? BERConst.FALSE : BERConst.TRUE;
		if(this.con == BERConst.TRUE) {
			int l = ber.getConstructedContentLength();
			this.setCco(new ImpOctetString[l]);
			for(int i = 0; i < this.getCco().length; i ++) {
				ASN1NodeImpl node = new ImpOctetString(null);
				node.loadFromExisting(ber.getCHildNode(i));
			}
		}
		this.setBer(ber);
	}

}
