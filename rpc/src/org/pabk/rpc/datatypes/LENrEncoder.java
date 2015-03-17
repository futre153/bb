package org.pabk.rpc.datatypes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class LENrEncoder implements Encoder {
	
	private static Encoder _default = new LENrEncoder();
	
	private LENrEncoder() {}
	
	@Override
	public byte[] encode(Object obj) throws IOException {
		if(obj instanceof RpcUnsInteger) {
			return encodeNumber((RpcUnsInteger) obj);
		}
		throw new IOException("Class " + obj.getClass().getSimpleName() + " is not supported");
	}

	private static byte[] encodeNumber(RpcUnsInteger nr) {
		long max = nr.getMaxValue();
		long val = nr.getValue();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		while(max > 0) {
			out.write((int) (val & 0xFF));
			val >>= Byte.SIZE;
			max >>= Byte.SIZE;
		}
		return out.toByteArray();
	}

	@Override
	public void decode(Object obj, byte[] b) throws IOException {
		if(obj instanceof RpcUnsInteger) {
			decodeNumber((RpcUnsInteger) obj, b);
		}
		throw new IOException("Class " + obj.getClass().getSimpleName() + " is not supported");		
	}

	private static void decodeNumber(RpcUnsInteger obj, byte[] b) {
		long l = 0L;
		for(int i = 0; i < b.length; i ++) {
			l |= (b[b.length - 1 - i] & 0xFF);
			l <<= Byte.SIZE;
		}
		obj.setValue(l);
	}

	public static Encoder getDefaultEncoder() {
		return _default;
	}

}
