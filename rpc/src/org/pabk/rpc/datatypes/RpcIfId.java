package org.pabk.rpc.datatypes;

public class RpcIfId extends RpcStruct {
	private RpcUUID uuid;
	private RpcUnsigned16 versMajor;
	private RpcUnsigned16 versMinor;
	public RpcIfId(RpcUUID uuid, RpcUnsigned16 versMajor, RpcUnsigned16 versMinor) {
		super(uuid, versMajor, versMinor);
		this.setUuid(uuid);
		this.setVersMajor(versMajor);
		this.setVersMinor(versMinor);
	}
	public RpcUUID getUuid() {
		return uuid;
	}
	public void setUuid(RpcUUID uuid) {
		this.uuid = uuid;
	}
	public RpcUnsigned16 getVersMajor() {
		return versMajor;
	}
	public void setVersMajor(RpcUnsigned16 versMajor) {
		this.versMajor = versMajor;
	}
	public RpcUnsigned16 getVersMinor() {
		return versMinor;
	}
	public void setVersMinor(RpcUnsigned16 versMinor) {
		this.versMinor = versMinor;
	}
}
