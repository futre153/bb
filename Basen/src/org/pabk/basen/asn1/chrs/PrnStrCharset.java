package org.pabk.basen.asn1.chrs;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.BitSet;

public class PrnStrCharset extends Charset {

	private static final long[] LONG = {0xa7fffb8100000000L, 0xffffffe0ffffffeL, 0x0L, 0x0L};
	private static final BitSet charset = BitSet.valueOf(LONG);
	
	protected PrnStrCharset() {
		super("printable-string", new String[0]);
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
