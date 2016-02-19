package org.pabk.basen.ber.encoders;

import org.pabk.basen.ber.BERImpl;

public class UniversalTagEncoder extends OctetStringEncoder {
	public String[] toString(BERImpl ber) {
		try {
			switch(ber.getTag()) {
			case BERImpl.BOOLEAN_TAG:
				return EncoderUtil.parseBoolean(ber.getContentOctets());
			case BERImpl.INTEGER_TAG:
				return EncoderUtil.parseInteger(ber.getContentOctets());
			case BERImpl.BITSTRING_TAG:
				return EncoderUtil.parseBitString(ber.getContentOctets());
			case BERImpl.OBJECT_IDENTIFIER_TAG:
				return EncoderUtil.parseOid(ber);
			default:
				return super.toString(ber);
			}
		}
		catch (Exception e) {
			return new String[0];
		}
	}
}
