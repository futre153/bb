package com.acepricot.finance.sync;

import com.acepricot.finance.sync.share.JSONMessage;

public class DeviceNode {
	private static final int ACTIVE = 0x01;
	private static final int STARTUP = 0x02;
	private static final int PAUSED = 0x10;
	
	private Object locker;
	private volatile int status;
	private String devName;
	private String grpName;
	private int devId;

	public DeviceNode(Row row) {
		this.status = STARTUP;
		this.setName((String) row.get(JSONMessageProcessor.REGISTERED_GROUPS.GROUP_NAME));
		this.setGroupName((String) row.get(JSONMessageProcessor.REGISTERED_DEVICES.DEVICE_NAME));
		this.setDeviceId((int) row.get(JSONMessageProcessor.UNIVERSAL_ID));
		this.status = ACTIVE;
	}

	
	int getStatus() {
		return this.status;
	}

	public JSONMessage pause(Object lock) {
		locker = lock;
		status = PAUSED;
		return new JSONMessage().returnOK("Device node " + this.getGroupName() + ": " + this.getName() + " sucessfully paused");
	}
	
	public JSONMessage activate(Object lock) {
		if (locker.equals(lock)) {
			status = ACTIVE;
			locker = null;
			return new JSONMessage().returnOK("Device node " + this.getGroupName() + ": " + this.getName() + " sucessfully activated");
		}
		return new JSONMessage().sendAppError("Device node " + this.getGroupName() + ": " + this.getName() + " failed to activate");
		
	}
	
	final String getName() {
		return devName;
	}

	private final void setName(String devName) {
		this.devName = devName;
	}

	private final String getGroupName() {
		return grpName;
	}

	private final void setGroupName(String grpName) {
		this.grpName = grpName;
	}

	public JSONMessage action(Object[] params) {
		return new JSONMessage().returnOK(params);
	}
	
	public int getDeviceId() {
		return this.devId;
	}
	private void setDeviceId(int deviceId) {
		this.devId = deviceId;
	}

}
