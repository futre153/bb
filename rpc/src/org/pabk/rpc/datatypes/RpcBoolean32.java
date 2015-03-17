package org.pabk.rpc.datatypes;

public class RpcBoolean32 extends RpcUnsigned32 {

	public RpcBoolean32(Number nr) {
		super(nr);
	}
	
	public boolean isTrue() {
		return this.getValue() != 0;
	}
	
	public boolean isFalse() {
		return this.getValue() == 0;
	}
}
