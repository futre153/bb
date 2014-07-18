package org.acepricot.asn1;

import java.io.IOException;

import org.acepricot.ber.BER;
import org.acepricot.ber.BERConst;

public abstract class Sequence extends ASN1NodeImpl {
	protected Sequence(int implicit, int optional, String name) {
		super (	
				BERConst.SEQUENCE_TAG_NUMBER,
				BERConst.UNIVERSAL_CLASS,
				BERConst.CONSTRUCTED_ENCODING,
				implicit,
				optional,
				name
				);
	}
	
	
	public final void loadFromExisting(BER ber) throws IOException {
		ASN1NodeImpl.checkConstructed(this, ber);
		int index = 0;
		for(int i = 0; i < this.getSeq().length; i++) {
			BER child = ber.getCHildNode(index);
			try {
				this.getSeq()[i].loadFromExisting(child);
			}
			catch(IOException e) {
				if(this.getOpt() == BERConst.TRUE) {
					continue;
				}
				throw e;
			}
			index++;
		}
	}
	
}
