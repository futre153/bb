package org.pabk.rpc.datatypes;

public class RpcUnsigned16 extends RpcUnsigned8 {

	private static final long MAX_VALUE = 0xFFFFL;

	public RpcUnsigned16(Number nr) {
		super(nr);
	}

	@Override
	long getMaxValue() {
		return MAX_VALUE;
	}
}
