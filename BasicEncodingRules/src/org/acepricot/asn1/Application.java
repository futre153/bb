package org.acepricot.asn1;

import java.io.IOException;

import org.acepricot.ber.BER;
import org.acepricot.ber.BERConst;

public class Application extends ASN1NodeImpl {
	
	protected ASN1NodeImpl nde;
	
	protected Application(int appTag, int optional, String name, ASN1NodeImpl node) {
		super(appTag, BERConst.APPLICATION_CLASS, BERConst.TRUE, BERConst.TRUE, optional, name);
		this.nde = node;
	}

	@Override
	protected void loadFromExisting(BER ber) throws IOException {
		ASN1NodeImpl.checkConstructed(this, ber);
		this.setBer(ber);
		this.nde.loadFromExisting(ber);
	}

	@Override
	protected ASN1NodeImpl clone() {
		return new Application(this.getTag(), this.getOpt(), this.getNme(), this.nde);
	}

}
