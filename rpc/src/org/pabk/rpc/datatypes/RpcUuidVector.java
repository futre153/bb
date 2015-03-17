package org.pabk.rpc.datatypes;

public class RpcUuidVector extends RpcArray {

	public RpcUuidVector(RpcUUID ...uuids) {
		super(RpcUUID.class, uuids);
	}

}
