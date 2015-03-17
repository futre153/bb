package org.acepricot.asn1;

import java.io.IOException;

import org.acepricot.ber.BER;
import org.acepricot.ber.BERConst;

public class Choice extends ASN1NodeImpl {
	
	private ASN1NodeImpl[] cho;
	protected int chu = -1;
	
	protected Choice(int optional, String name, ASN1NodeImpl ... cho) {
		super (
				BERConst.CHOICE,
				BERConst.NOT_DEFINED,
				BERConst.NOT_DEFINED,
				BERConst.NOT_DEFINED,
				optional,
				name
				);
		this.cho = cho == null ? new ASN1NodeImpl[]{} : cho;
	}
	
	@Override
	protected void loadFromExisting(BER ber) throws IOException {
		for(int i = 0; i < cho.length; i ++) {
			if(ber.getTagNumber() == cho[i].getTag()) {
				this.chu = i;
			}
		}
		if(chu > 0) {
			throw new IOException ("No object choosen for choice");
		}
		this.cho[chu].loadFromExisting(ber);
	}

	protected final ASN1NodeImpl[] getCho() {
		return cho;
	}

	protected final void setCho(ASN1NodeImpl[] cho) {
		this.cho = cho;
	}

	@Override
	protected ASN1NodeImpl clone() {
		// TODO Auto-generated method stub
		return new Choice(this.getOpt(), this.getNme(), this.getCco());
	}

	
}
