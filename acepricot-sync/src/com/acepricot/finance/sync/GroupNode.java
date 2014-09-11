package com.acepricot.finance.sync;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;

import org.h2.api.Trigger;

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
	private DBSchema schema = null;

	final DBSchema getDBSchema() {
		return schema;
	}

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
			try {
				return action(params);
			}
			catch (IOException | SQLException e) {
				return new JSONMessage().sendAppError(e);
			}
			finally {
				this.status = ACTIVE;
			}
		}	
	}
	
	private JSONMessage action(Object[] params) throws IOException, SQLException {
		if(params.length > 0 && params[0] != null) {
			if(params[0] instanceof Operation) {
				return checkForForceOperation((Operation) params[0]);
			}
		}
		throw new IOException ("Group node " + this.getName() + ": missing parameter for synchronization action");
	}

	private JSONMessage checkForForceOperation(Operation op) throws IOException, SQLException {
		switch(op.getType()) {
		case JSONMessage.FORCE_OPERATION:
			throw new IOException ("Force operation is not allowed for this version");
		default:
			return checkForPendingOperation(op);
		}
	}

	private JSONMessage checkForPendingOperation(Operation op) throws SQLException, IOException {
		int opid  = JSONMessageProcessor.getPendingOperation(op);
		if(opid == 0) {
			opid = JSONMessageProcessor.getWaitingOperation(op);
			if(opid == 0) {
				switch(op.getType()) {
				case JSONMessage.RESPONSE_FOR_PENDING_NO_OPERATION:
				case JSONMessage.RESPONSE_FOR_PENDING_RESULT_FAILED:
				case JSONMessage.RESPONSE_FOR_PENDING_RESULT_OK:
					return PhantomPendingResult(op);
				default:
					return checkForSyncRequest(op);
				}
			}
			else {
				return sendWaitingOperation(op);
			}
		}
		else {
			switch(op.getType()) {
			case JSONMessage.RESPONSE_FOR_PENDING_NO_OPERATION:
				return PendingNoOperation(op);
			case JSONMessage.RESPONSE_FOR_PENDING_RESULT_FAILED:
				return PendingResultFailed(op);
			case JSONMessage.RESPONSE_FOR_PENDING_RESULT_OK:
				return PendingResultOK(op);
			default:
				return requestForPendingResponse(op);
			}
		}
	}

	private JSONMessage PhantomPendingResult(Operation op) throws IOException {
		op.setType(JSONMessage.NO_ACTION);
		return op.constructJSONMessage();
	}

	private JSONMessage PendingResultOK(Operation op) throws SQLException, IOException {
		int opid = JSONMessageProcessor.findAffectedOperation(op);
		if(opid < 0) {
			return requestForPendingResponse(op);
		}
		else {
			removePendingOperation(opid);
			opid = JSONMessageProcessor.getWaitingOperation(op);
			if(opid == 0) {
				op.setType(JSONMessage.NO_ACTION);
				return op.constructJSONMessage();
			}
			else {
				return sendWaitingOperation(op);
			}
		}
	}

	private JSONMessage PendingResultFailed(Operation op) throws IOException {
		op.setType(JSONMessage.REQUEST_FOR_FORCE);
		return op.constructJSONMessage();
	}

	private JSONMessage PendingNoOperation(Operation op) throws SQLException, IOException {
		int opid = JSONMessageProcessor.findAffectedOperation(op);
		if(opid < 0) {
			return requestForPendingResponse(op);
		}
		return retryOperation(op);
	}

	private JSONMessage requestForPendingResponse(Operation op) throws SQLException {
		JSONMessageProcessor mp = op.getMessageProcessor();
		mp.sync_responses.reset();
		if(mp.sync_responses.next()) {
			return new JSONMessage().returnOK(JSONMessage.REQUEST_PENDING_RESPONSE, mp.sync_responses.sync_id, mp.sync_responses.table_name);
		}
		else {
			throw new SQLException("Failed to retrieve data for penging response request");
		}
	}

	private void removePendingOperation(int l) {
		JSONMessageProcessor.deleteOperation(l);
	}

	private JSONMessage sendWaitingOperation(Operation op) throws IOException, SQLException {
		if (op.getMessageProcessor().sync_responses.next()) {
			JSONMessageProcessor.updateWaitingToPending(op);
			return new Operation(op.getMessageProcessor()).constructJSONMessage();
		}
		else {
			throw new IOException("Failed to retrieve date for waiting operation");
		}
	}

	private JSONMessage checkForSyncRequest(Operation op) throws IOException {
		switch(op.getType()) {
		case Trigger.INSERT:
			return insertOperation(op);
		case Trigger.UPDATE:
			return updateOperation(op);
		/*case Trigger.DELETE:
			return deleteOperation(op);*/
		case JSONMessage.EMPTY_REQUEST:
			op.setType(JSONMessage.NO_ACTION);
			return op.constructJSONMessage();
		default:
			throw new IOException ("Sync request type " + op.getType() + " is not defined");
		}
	}

	@SuppressWarnings("unused")
	private JSONMessage deleteOperation(Row row, DeviceNode devNode) {
		// TODO Auto-generated method stub
		return new JSONMessage().returnOK();
	}

	private JSONMessage updateOperation(Operation op) throws IOException {
		// TODO Auto-generated method stub
		String dsn = this.getDSN();
		try {
			op.setPrimaryKeys(this.schema.getPrimaryKeys(op.getTableName()));
			if(!GroupNode.checkColumns(op.getColumns(), this.schema.getColumns(op.getTableName()))) {
				throw new SQLException("Columns failed to check. May versions of databases are not equals");
			}
			if(!GroupNode.checkColumns(op.getPrimaryKeys(), op.getColumns())) {
				throw new SQLException("Primary keys failed to check. May versions of databases are not equals");
			}
			if(JSONMessageProcessor.checkAll(dsn, op)) {
				op.setType(JSONMessage.UPDATE_NO_ACTION);
			}
			else {
				/*
				boolean[] b = JSONMessageProcessor.checkUpdatePartial(dsn, op);
				if(b[b.length-1]) {
					for(int i = 0; i < (b.length - 1); i ++) {
						if(b[i]) {
							op.setNewPrimaryKeysValues(i, JSONMessageProcessor.getNewPKValue(dsn, op, i)); 
						}
					}
					JSONMessageProcessor.updateOperation(dsn, op);
					op.setType(JSONMessage.UPDATE_UPDATE_PK);
				}
				else {*/
					JSONMessageProcessor.updateOperation(dsn, op);
					op.setType(JSONMessage.UPDATE_NO_ACTION);
				//}
			}
			return op.constructJSONMessage();
		}
		catch (SQLException e) {
			return new JSONMessage().sendAppError(e);
		}
	}

	private JSONMessage insertOperation(Operation op) throws IOException {
		String dsn = this.getDSN();
		try {
			op.setPrimaryKeys(this.schema.getPrimaryKeys(op.getTableName()));
			if(!GroupNode.checkColumns(op.getColumns(), this.schema.getColumns(op.getTableName()))) {
				throw new SQLException("Columns failed to check. May versions of databases are not equals");
			}
			if(!GroupNode.checkColumns(op.getPrimaryKeys(), op.getColumns())) {
				throw new SQLException("Primary keys failed to check. May versions of databases are not equals");
			}
			if(JSONMessageProcessor.checkAll(dsn, op)) {
				op.setType(JSONMessage.INSERT_NO_ACTION);
			}
			else {/*
				boolean[] b = JSONMessageProcessor.checkPartial(dsn, op);
				if(b[b.length-1]) {
					for(int i = 0; i < (b.length - 1); i ++) {
						if(b[i]) {
							op.setNewPrimaryKeysValues(i, JSONMessageProcessor.getNewPKValue(dsn, op, i)); 
						}
					}
					JSONMessageProcessor.insertOperation(dsn, op);
					op.setType(JSONMessage.INSERT_UPDATE_PK);
				}
				else {*/
					JSONMessageProcessor.insertOperation(dsn, op);
					op.setType(JSONMessage.INSERT_NO_ACTION);
				//}
			}
			return op.constructJSONMessage();
		}
		catch (SQLException e) {
			return new JSONMessage().sendAppError(e);
		}
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

	private JSONMessage retryOperation(Operation op) throws IOException {
		return new Operation(op.getMessageProcessor()).constructJSONMessage();
	}


	String getName() {
		return this.props.getProperty(JSONMessageProcessor.REGISTERED_GROUPS.GROUP_NAME);
	}

	int getGroupId() {
		return Integer.parseInt(this.props.getProperty(JSONMessageProcessor.REGISTERED_DEVICES.GROUP_ID));
	}
	
}
