package com.acepricot.finance.sync;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;

import com.acepricot.finance.sync.share.JSONMessage;

public class GroupNode extends Hashtable <String, DeviceNode> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int ACTIVE = 0x01;
	public static final int STARTUP = 0x02;
	public static final int PAUSED = 0x03;
	public static final int BUSY = 0x04;
	
	public static final int NODE_PAUSE = 0x10;
	public static final int NODE_ACTIVATE = 0x11;

	public static final int DEVICE_NODE_PAUSE = 0x20;
	public static final int DEVICE_NODE_ACTIVATE = 0x21;

	public static final int DEVICE_NODE_STOP = 0x30;
	public static final int DEVICE_NODE_START = 0x31;

	public static final String[] STATUS = {"UNKNOWN", "ACTIVE", "STARTUP", "PAUSED", "BUSY"};

	

	
	
	private volatile int status;

	private Properties props = null;
	private Object locker = null;

	public GroupNode(Properties p) throws SQLException {
		this.status = STARTUP;
		this.props  = p;
		String dsn = p.getProperty(DBConnector.DB_DSN_KEY);
		DBConnector.bind(p, dsn);
		Connection con2 = DBConnector.lookup(dsn);
		con2.close();
		con2 = null;
		this.status = ACTIVE;
	}

	public int getStatus() {
		return this.status;
	}

	public DeviceNode getDeviceNode(String devName) {
		return this.get(devName);
	}
	public DeviceNode getDeviceNode(int devId) {
		Iterator<String> i = this.keySet().iterator();
		while(i.hasNext()) {
			DeviceNode devNode = this.get(i.next());
			if(devNode.getDeviceId() == devId) {
				return devNode;
			}
		}
		return null;
		
	}

	public synchronized JSONMessage action(int actionId, Object lock, Object ...params) {
		switch(actionId) {
		case NODE_PAUSE:
			this.status = PAUSED;
			this.locker = lock;
			return new JSONMessage().returnOK("Group node " + this.getName() + " successfully paused");
		case NODE_ACTIVATE:
			if(this.locker.equals(lock)) {
				this.locker = null;
				this.status = ACTIVE;
				return new JSONMessage().returnOK("Group node " + this.getName() + " successfully activated");
			}
			return new JSONMessage().sendAppError("Group node " + this.getName() + " failed to activate");
		case DEVICE_NODE_PAUSE:
			if(params.length > 0 && params[0] != null) {
				return this.getDeviceNode((String) params[0]).pause(lock);
			}
			else {
				return new JSONMessage().sendAppError("Group node " + this.getName() + ": missing device name parameter for pause action");
			}
		case DEVICE_NODE_ACTIVATE:
			if(params.length > 0 && params[0] != null) {
				return this.getDeviceNode((String) params[0]).activate(lock);
			}
			else {
				return new JSONMessage().sendAppError("Group node " + this.getName() + ": missing device name parameter for pause action");
			}
		case DEVICE_NODE_STOP:
			if(params.length > 0 && params[0] != null) {
				DeviceNode node = this.remove(params[0]);
				if(node == null) {
					return new JSONMessage().sendAppError("Device node " + this.getName() + ": " + params[0] + " does not exists");
				}
				else {
					return new JSONMessage().returnOK("Device node " + this.getName() + ": " + node.getName() + " stopped successfully ");
				}
			}
			else {
				return new JSONMessage().sendAppError("Group node " + this.getName() + ": missing device name parameter for stop action");
			}
		case DEVICE_NODE_START:
			if(params.length > 0 && params[0] != null) {
				DeviceNode node = new DeviceNode((Row) params[0]);
				this.put(node.getName(), node);
				return new JSONMessage().returnOK("Device node " + this.getName() + ": " + node.getName() + " started successfully ");
			}
			else {
				return new JSONMessage().sendAppError("Group node " + this.getName() + ": missing device name parameter for start action");
			}
		default:
			if(params.length > 0 && params[0] != null) {
				this.status = BUSY;
				try {
					System.out.println(params[0].toString());
					return this.getDeviceNode((String) (((Row) params[0]).get(JSONMessageProcessor.REGISTERED_DEVICES.DEVICE_NAME))).action(params);
				}
				catch (Exception e) {
					return new JSONMessage().sendAppError(e);
				}
				finally {
					this.status = ACTIVE;
				}
			}
			else {
				return new JSONMessage().sendAppError("Group node " + this.getName() + ": missing parameter for synchronization action");
			}
		}
	}
	
	private String getName() {
		return this.props.getProperty(JSONMessageProcessor.REGISTERED_GROUPS.GROUP_NAME);
	}

	int getGroupId() {
		return (int) this.props.get(JSONMessageProcessor.REGISTERED_DEVICES.GROUP_ID);
	}
	
}
