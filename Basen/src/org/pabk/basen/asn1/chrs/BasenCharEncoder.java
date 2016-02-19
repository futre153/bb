package org.pabk.basen.asn1.chrs;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.BitSet;

public class BasenCharEncoder extends CharsetEncoder {
	
	private BitSet bs;
	private int[] map;
	
	protected BasenCharEncoder(Charset cs, BitSet charset, int[] map) {
		super(cs, 1, 1);
		this.bs = charset;
		this.map = map;
	}

	@Override
	protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {
		while(true) {
			if(!in.hasRemaining()) {
				return CoderResult.UNDERFLOW;
			}
			if(!out.hasRemaining()) {
				return CoderResult.OVERFLOW;
			}
			int i = in.get() & 0xFFFF;
			if(map != null) {
				int j = 0;
				for(; j < map.length; j ++) {
					if(map[j] == i) {
						break;
					}
				}
				i = j;
			}
			if(i < 0x100 && bs.get(i)) {
				out.put((byte) i);
				continue;
			}
			break;
		}
		return CoderResult.malformedForLength(in.position());
	}

}
