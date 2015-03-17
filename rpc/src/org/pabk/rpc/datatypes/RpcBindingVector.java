package org.pabk.rpc.datatypes;

public class RpcBindingVector extends RpcArray {
	
	public RpcBindingVector(RpcBindingHandle... bindings) {
		super(RpcBindingHandle.class, bindings);
	}
}
