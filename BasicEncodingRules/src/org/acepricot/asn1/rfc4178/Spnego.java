package org.acepricot.asn1.rfc4178;

import java.io.IOException;

import org.acepricot.ber.BER;
import org.acepricot.ber.BERConst;

public class Spnego extends BER {

	public Spnego() throws IOException {
		this.setIdOctets(BERConst.UNIVERSAL_CLASS, BERConst.CONSTRUCTED_ENCODING, BERConst.SEQUENCE_TAG_NUMBER);
		this.setDefiniteLength(true);
		this.getContentDecoder().setDefinite(true);
		this.getContentEncoder().setDefinite(true);
		this.setConstructedContent();
	}
}
