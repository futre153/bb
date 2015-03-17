package org.pabk.rpc.datatypes;

public class RpcUnsigned32 extends RpcUnsigned8 {

	private static final long MAX_VALUE = 0xFFFFFFFFL;

	public RpcUnsigned32(Number nr) {
		super(nr);
	}

	@Override
	long getMaxValue() {
		return MAX_VALUE;
	}
}
