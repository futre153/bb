package com.acepricot.finance.sync;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;

import org.apache.catalina.connector.Connector;
import org.h2.api.Trigger;

import com.acepricot.finance.sync.share.JSONMessage;
import com.acepricot.finance.sync.share.sql.Identifier;
import com.acepricot.finance.sync.share.sql.Insert;
import com.acepricot.finance.sync.share.sql.SchemaName;
import com.acepricot.finance.sync.share.sql.TableName;

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
	private DBSchema schema = null;

	public GroupNode(Properties p) throws SQLException {
		this.status = STARTUP;
		this.props  = p;
		String dsn = p.getProperty(DBConnector.DB_DSN_KEY);
		DBConnector.bind(p, dsn);
		Connection con2 = DBConnector.lookup(dsn);
		schema = DBSchema.getInstance().loadSchemas(con2);
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
			this.status = BUSY;
			Connection con = null;
			try {
				con = DBConnector.lookup(props.getProperty(DBConnector.DB_DSN_KEY));
				return action(con ,params);
			}
			catch (IOException | SQLException e) {
				return new JSONMessage().sendAppError(e);
			}
			finally {
				this.status = ACTIVE;
				try {
					con.close();
				} catch (SQLException e) {}
			}
		}	
	}
	
	private JSONMessage action(Connection con, Object[] params) throws IOException {
		if(params.length > 0 && params[0] != null) {
			if(params[0] instanceof Row) {
				Row row = (Row) params[0];
				String devName = (String) row.get(JSONMessageProcessor.LOCAL_LABEL + JSONMessageProcessor.REGISTERED_DEVICES.DEVICE_NAME);
				DeviceNode devNode = this.get(devName);
				if(devNode == null) {
					throw new IOException("Device node " + this.getName() + ":" + devName + " is not started");
				}
				System.out.println(row);
				return checkForPendingOperation(row, devNode);
			}
		}
		throw new IOException ("Group node " + this.getName() + ": missing parameter for synchronization action");
	}

	private JSONMessage checkForPendingOperation(Row row, DeviceNode devNode) {
		int opid  = JSONMessageProcessor.getPendingOperation(this.getGroupId(), devNode.getDeviceId());
		if(opid < 0) {
			opid = JSONMessageProcessor.getWaitingOperation(this.getGroupId(), devNode.getDeviceId());
			if(opid < 0) {
				return checkForSyncRequest(row, devNode);
			}
			else {
				return sendWaitingOperation(opid);
			}
		}
		else {
			switch(((Double) row.get(DBSchema.SYNC_TYPE)).intValue()) {
			case JSONMessage.RESPONSE_FOR_PENDING:
				if(opid == (int) row.get(DBSchema.SYNC_OPERATION_ID)) {
					switch((int) row.get(DBSchema.SYNC_OPERATION_RESPONSE_RESULT)) {
					case DBSchema.SYNC_OPERATION_RESULT_NO_OPERATION:
						return retryOperation(opid);
					case DBSchema.SYNC_OPERATION_RESULT_FAILED:
						/*
						 * TODO force download database file ????
						 */
					case DBSchema.SYNC_OPERATION_RESULT_OK:
						removePendingOperation(opid);
						opid = JSONMessageProcessor.getWaitingOperation(this.getGroupId(), devNode.getDeviceId());
						if(opid < 0) {
							return new JSONMessage().returnOK();
						}
						else {
							return sendWaitingOperation(opid);
						}
					}
				}
			default:
				return requestForPendingResponse(opid);
			}
		}
	}

	private JSONMessage requestForPendingResponse(int l) {
		return new JSONMessage().returnOK(JSONMessage.REQUEST_PENDING_RESPONSE, l);
	}

	private void removePendingOperation(int l) {
		JSONMessageProcessor.deleteOperation(l);
	}

	private JSONMessage sendWaitingOperation(long l) {
		// TODO Auto-generated method stub
		return null;
	}

	private JSONMessage checkForSyncRequest(Row row, DeviceNode devNode) {
		switch(((Double) row.get(DBSchema.SYNC_TYPE)).intValue()) {
		case Trigger.INSERT:
			return insertOperation(row, devNode);
		case Trigger.UPDATE:
			return updateOperation(row, devNode);
		case Trigger.DELETE:
			return deleteOperation(row, devNode);
		default:
			return new JSONMessage().sendAppError("Sync request type " + DBSchema.SYNC_TYPE + " is not defined");
		}
	}

	private JSONMessage deleteOperation(Row row, DeviceNode devNode) {
		// TODO Auto-generated method stub
		return new JSONMessage().returnOK();
	}

	private JSONMessage updateOperation(Row row, DeviceNode devNode) {
		// TODO Auto-generated method stub
		return new JSONMessage().returnOK();
	}

	private JSONMessage insertOperation(Row row, DeviceNode devNode) {
		String dsn = this.getDSN();
		String schema = (String) row.remove(DBSchema.SYNC_SCHEMA);
		String table = (String) row.remove(DBSchema.SYNC_TABLE);
		String[] cols = JSONMessageProcessor.getSyncColumnNames(row);
		Object[] values = JSONMessageProcessor.getSyncColumnValues(cols, row);
		try {
			String[] pks = this.schema.getPrimaryKeys(table);
			if(!GroupNode.checkColumns(cols, this.schema.getColumns(table))) {
				throw new SQLException("Columns failed to check. May versions of databases are not equals");
			}
			if(!GroupNode.checkColumns(pks, cols)) {
				throw new SQLException("Primary keys failed to check. May versions of databases are not equals");
			}
			if(JSONMessageProcessor.checkAll(dsn, schema, table, cols, values)) {
				return new JSONMessage().returnOK();
			}
			else {
				DeviceNode[] devNodes = getOtherDevNodes(devNode);
				ArrayList<Object> pksNew = new ArrayList<Object>();
				boolean[] b = JSONMessageProcessor.checkPartial(dsn, schema, table, cols, values, pks);
				if(b[b.length-1]) {
					for(int i = 0; i < b.length; i ++) {
						if(b[i]) {
							pksNew.add(pks[i]);
							int index = JSONMessageProcessor.getIndexOfArray(cols, pks[i]);
							pksNew.add(values[index]);
							values[index] = JSONMessageProcessor.getNewPKValue(dsn, schema, table, cols, values, pks[i]);
							pksNew.add(values[index]);
						}
					}
					JSONMessageProcessor.insertOperation(dsn, schema, table, cols, values, devNodes, this.getGroupId(), pksNew, devNode.getDeviceId());
					pksNew.add(0, JSONMessage.INSERT_UPDATE_PK);
					return new JSONMessage().returnOK(pksNew.toArray());
					
				}
				else {
					JSONMessageProcessor.insertOperation(dsn, schema, table, cols, values, devNodes, this.getGroupId(), pksNew, devNode.getDeviceId());
					return new JSONMessage().returnOK(JSONMessage.INSERT_NO_ACTION);
				}
			}
		}
		catch (SQLException e) {
			return new JSONMessage().sendAppError(e);
		}
	}

	

	private DeviceNode[] getOtherDevNodes(DeviceNode devNode) {
		DeviceNode[] devNodes = new DeviceNode[this.size() -1];
		Iterator<String> i = this.keySet().iterator();
		int j = 0;
		while (i.hasNext()) {
			DeviceNode node = this.get(i.next());
			if(node.getDeviceId() != devNode.getDeviceId()) {
				devNodes[j] = node;
				j ++;
			}
		}
		return devNodes;
	}

	
	private static boolean checkColumns(String[] cols, String[] order) {
		for(int i = 0; i < cols.length; i ++) {
			if(GroupNode.ArrayContain(order, cols[i])) {
				continue;
			}
			return false;
		}
		return true;
	}

	private static boolean ArrayContain(String[] order, String s) {
		for(int i = 0; i < order.length; i ++) {
			if(order[i].equals(s)) {
				return true;
			}
		}
		return false;
	}

	private String getDSN() {
		return this.props.getProperty(DBConnector.DB_DSN_KEY);
	}

	private JSONMessage retryOperation(long l) {
		// TODO Auto-generated method stub
		return null;
	}

	private JSONMessage checkForWaitingOperation(Row row, DeviceNode devNode) {
		long l = JSONMessageProcessor.getWaitingOperation(this.getGroupId(), devNode.getDeviceId());
		if(l < 0) {
			return checkForWaitingOperation(row, devNode);
		}
		return new JSONMessage().returnOK(JSONMessage.REQUEST_PENDING_RESPONSE, l);
		//return null;
	}

	private String getName() {
		return this.props.getProperty(JSONMessageProcessor.REGISTERED_GROUPS.GROUP_NAME);
	}

	int getGroupId() {
		return Integer.parseInt(this.props.getProperty(JSONMessageProcessor.REGISTERED_DEVICES.GROUP_ID));
	}
	
}
