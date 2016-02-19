package org.pabk.basen.asn1.chrs;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.BitSet;

public class NumStrCharset extends Charset {
	
	private static final long[] LONG = {0x3ff000100000000L,	0x0L, 0x0L, 0x0L};
	private static final BitSet charset = BitSet.valueOf(LONG);
	
	protected NumStrCharset() {
		super("numeric-string", new String[0]);
	}

	@Override
	public boolean contains(Charset arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public CharsetDecoder newDecoder() {
		return new BasenCharDecoder(this, charset, null);
	}

	@Override
	public CharsetEncoder newEncoder() {
		return new BasenCharEncoder(this, charset, null);
	}

}
