package org.pabk.rpc.datatypes;

public class RpcStatsVector extends RpcArray {

	public RpcStatsVector(RpcUnsigned32... stats) {
		super(RpcUnsigned32.class, stats);
	}

}
