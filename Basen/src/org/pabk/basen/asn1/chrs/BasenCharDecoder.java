package org.pabk.basen.asn1.chrs;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.util.BitSet;

public class BasenCharDecoder extends CharsetDecoder {

	private BitSet bs;
	private int[] map;

	protected BasenCharDecoder(Charset cs, BitSet bs, int[] map) {
		super(cs, 1, 1);
		this.bs  = bs;
		this.map = map;
	}

	@Override
	protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
		while(true) {
			if(in.remaining() == 0) {
				return CoderResult.UNDERFLOW;
			}
			if(!out.hasRemaining()) {
				return CoderResult.OVERFLOW;
			}
			int i = in.get() & 0xFF;
			if (bs.get(i)) {
				out.append((char) (map != null ? map[i] : i));
				continue;
			}
			break;
		}
		return CoderResult.malformedForLength(in.position());
	}

}
