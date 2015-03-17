package org.pabk.rpc.datatypes;

public class RpcUUID extends RpcStruct {
	private RpcUnsigned32 timeLow;
	private RpcUnsigned16 timeMid;
	private RpcUnsigned16 timeHiAndVersion;
	private RpcUnsigned8 clockSeqHiAndReserved;
	private RpcUnsigned8 clockSeqLow;
	private RpcUnsignedChar node;
	public RpcUUID(RpcUnsigned32 timeLow, RpcUnsigned16 timeMid, RpcUnsigned16 timeHiAndVersion, RpcUnsigned8 clockSeqHiAndReserved, RpcUnsigned8 clockSeqLow, RpcUnsignedChar node) {
		super(timeLow, timeMid, timeHiAndVersion, clockSeqHiAndReserved, clockSeqLow, node);
		this.setTimeLow(timeLow);
		this.setTimeMid(timeMid);
		this.setTimeHiAndVersion(timeHiAndVersion);
		this.setClockSeqHiAndReserved(clockSeqHiAndReserved);
		this.setClockSeqLow(clockSeqLow);
		this.setNode(node);
	}
	protected final RpcUnsigned32 getTimeLow() {
		return timeLow;
	}
	protected final void setTimeLow(RpcUnsigned32 timeLow) {
		this.timeLow = timeLow;
	}
	protected final RpcUnsigned16 getTimeMid() {
		return timeMid;
	}
	protected final void setTimeMid(RpcUnsigned16 timeMid) {
		this.timeMid = timeMid;
	}
	protected final RpcUnsigned16 getTimeHiAndVersion() {
		return timeHiAndVersion;
	}
	protected final void setTimeHiAndVersion(RpcUnsigned16 timeHiAndVersion) {
		this.timeHiAndVersion = timeHiAndVersion;
	}
	protected final RpcUnsigned8 getClockSeqHiAndReserved() {
		return clockSeqHiAndReserved;
	}
	protected final void setClockSeqHiAndReserved(RpcUnsigned8 clockSeqHiAndReserved) {
		this.clockSeqHiAndReserved = clockSeqHiAndReserved;
	}
	protected final RpcUnsigned8 getClockSeqLow() {
		return clockSeqLow;
	}
	protected final void setClockSeqLow(RpcUnsigned8 clockSeqLow) {
		this.clockSeqLow = clockSeqLow;
	}
	protected final RpcUnsignedChar getNode() {
		return node;
	}
	protected final void setNode(RpcUnsignedChar node) {
		this.node = node;
	}
	
	public String toString() {
		String mask = "%d-%d-%d-%d%d-%s";
		return String.format(mask, this.getTimeLow().getValue(), this.getTimeMid().getValue(), this.getTimeHiAndVersion().getValue(), this.getClockSeqHiAndReserved().getValue(), this.getClockSeqLow().getValue(), this.getNode().getValue());
	}
	
}
