package org.pabk.rpc.datatypes;

public class RpcIfIdVector extends RpcArray {
	public RpcIfIdVector(RpcIfId ... interfaceIds) {
		super(RpcIfId.class, interfaceIds);
	}
}
