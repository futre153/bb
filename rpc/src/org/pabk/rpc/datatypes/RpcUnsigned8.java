package org.pabk.rpc.datatypes;

public class RpcUnsigned8 extends RpcUnsInteger {

	private static final long MAX_VALUE = 0xFFL;
	private Encoder encoder = LENrEncoder.getDefaultEncoder();

	public RpcUnsigned8(Number nr) {
		super(nr);
	}

	@Override
	long getMaxValue() {
		return MAX_VALUE;
	}

	@Override
	Encoder getEncoder() {
		return this.encoder;
	}

	@Override
	void setEncoder(Encoder enc) {
		this.encoder = enc;	
	}
}
