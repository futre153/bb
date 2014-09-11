package com.acepricot.finance.sync;

import java.io.IOException;
import java.util.HashMap;

import com.acepricot.finance.sync.share.JSONMessage;

class Operation {
	private int type;
	private GroupNode grpNode;
	private DeviceNode devNode;
	private String[] columns;
	private String tableName;
	private String schemaName;
	private Object[] values;
	private int id;
	private JSONMessageProcessor messageProcessor;
	private String query;
	private String[] primaryKeys;
	private Object[] primaryKeysValues;
	private Object[] newPrimaryKeysValues;
	private HashMap<?, ?> syncChanges;
	
	Operation(JSONMessageProcessor mp, Row row, String grpName, String devName) throws IOException {
		this.setType(((Double) row.get(DBSchema.SYNC_TYPE)).intValue());
		GroupNode grpNode = SyncEngine.getGroupNode(grpName);
		if(grpNode == null) {
			throw new IOException("Grop node for group name " + grpName + " does not exists");
		}
		this.setGroupNode(grpNode);
		DeviceNode devNode = grpNode.getDeviceNode(devName);
		if(devNode == null) {
			throw new IOException("Device node " + grpNode.getName() + ":" + devName + " is not started");
		}
		this.setDeviceNode(devNode);
		this.setSchemaName((String) row.remove(DBSchema.SYNC_SCHEMA));
		this.setTableName((String) row.remove(DBSchema.SYNC_TABLE));
		this.setSyncChanges(row.remove(DBSchema.SYNC_CHANGES));
		this.setColumns(JSONMessageProcessor.getSyncColumnNames(row));
		this.setValues(JSONMessageProcessor.getSyncColumnValues(this.getColumns(), row));
		Object id = row.remove(DBSchema.SYNC_ID);
		this.setId(id == null ? -1 : ((Double) id).intValue());
		this.setMessageProcessor(mp);
	}

	private void setSyncChanges(Object c) {
		if(c != null) {
			String s = ((String) c);
			if(s.length() > 0) {
				try {
					Object obj = JSONMessageProcessor.load(s);
					this.syncChanges = (HashMap<?, ?>) obj ;
				} catch (Exception e) {
					this.syncChanges = null;
				}
			}
		}
	}
	
	HashMap<?, ?> getSyncChanges() {
		return this.syncChanges;
	}
	
	public Operation(JSONMessageProcessor mp) {
		this.setMessageProcessor(mp);
		this.setGroupNode(SyncEngine.getGroupNode(mp.sync_responses.group_id));
		this.setDeviceNode(this.getGroupNode().getDeviceNode(mp.sync_responses.device_id));
		this.setType(mp.sync_responses.type);
		this.setTableName(mp.sync_responses.table_name);
		this.setId(mp.sync_responses.sync_id);
		Object query = mp.sync_responses.query;
		this.setQuery(query == null ? null : (String) query);
	}

	final int getType() {
		return type;
	}

	final void setType(int type) {
		this.type = type;
	}

	final GroupNode getGroupNode() {
		return grpNode;
	}

	final void setGroupNode(GroupNode grpNode) {
		this.grpNode = grpNode;
	}

	final DeviceNode getDeviceNode() {
		return devNode;
	}

	final void setDeviceNode(DeviceNode devNode) {
		this.devNode = devNode;
	}

	final String[] getColumns() {
		return columns;
	}

	final void setColumns(String[] columns) {
		this.columns = columns;
	}

	final String getTableName() {
		return tableName;
	}

	final void setTableName(String tableName) {
		this.tableName = tableName;
	}

	final String getSchemaName() {
		return schemaName;
	}

	final void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	final Object[] getValues() {
		return values;
	}

	final void setValues(Object[] values) {
		this.values = values;
	}

	final int getId() {
		return id;
	}

	final void setId(int id) {
		this.id = id;
	}

	final String getQuery() {
		return query;
	}

	final void setQuery(String query) {
		this.query = query;
	}

	final JSONMessageProcessor getMessageProcessor() {
		return messageProcessor;
	}

	final void setMessageProcessor(JSONMessageProcessor messageProcessor) {
		this.messageProcessor = messageProcessor;
	}

	final String[] getPrimaryKeys() {
		return primaryKeys;
	}

	final void setPrimaryKeys(String[] primaryKeys) {
		this.primaryKeys = primaryKeys;
		Object[] values = new Object[primaryKeys.length];
		for(int i = 0; i < primaryKeys.length; i ++) {
			int	index = JSONMessageProcessor.getIndexOfArray(this.getColumns(), primaryKeys[i]);
			if(index >= 0) {
				values[i] = this.getValues()[index];
			}
		}
		this.setPrimaryKeysValues(values);
		//this.setNewPrimaryKeysValues(new Object[this.getPrimaryKeysValues().length]);
	}

	final Object[] getPrimaryKeysValues() {
		return primaryKeysValues;
	}

	private final void setPrimaryKeysValues(Object[] primaryKeysValues) {
		this.primaryKeysValues = primaryKeysValues;
	}
	final Object[] getNewPrimaryKeysValues() {
		return newPrimaryKeysValues;
	}

	final void setNewPrimaryKeysValues(int i, Object newPrimaryKeysValue) {
		if(this.getNewPrimaryKeysValues() == null) {
			this.newPrimaryKeysValues = new Object[this.getPrimaryKeysValues().length];
		}
		if(i < this.getNewPrimaryKeysValues().length) {
			this.newPrimaryKeysValues[i] = newPrimaryKeysValue;
		}
	}

	public JSONMessage constructJSONMessage() throws IOException {
		JSONMessage msg = new JSONMessage().returnOK(this.getType());
		switch(this.getType()) {
		case JSONMessage.NO_ACTION:
			return msg;
		case JSONMessage.BUSY_RESPONSE:
			return msg.appendBody("Grop node " + this.getGroupNode().getName() + " is not in active state (Current state is " + GroupNode.STATUS[this.getGroupNode().getStatus()] + ")");
		case JSONMessage.REQUEST_FOR_FORCE:
			return msg.appendBody("Server request for force synchronization action due to unrecoverable error");
		case JSONMessage.INSERT_UPDATE_PK:
			return msg.appendBody(this.getId(), this.getTableName(), this.getQuery());
		case JSONMessage.INSERT_NO_ACTION:
			return msg.appendBody(this.getId(), this.getTableName());
		case JSONMessage.INSERT_OPERATION:
			return msg.appendBody(this.getId(), this.getTableName(), this.getQuery());
		case JSONMessage.UPDATE_NO_ACTION:
			return msg.appendBody(this.getId(), this.getTableName());
		case JSONMessage.UPDATE_UPDATE_PK:
			return msg.appendBody(this.getId(), this.getTableName(), this.getQuery());
		case JSONMessage.UPDATE_OPERATION:
			return msg.appendBody(this.getId(), this.getTableName(), this.getQuery());
		default: throw new IOException("Type " + this.getType() + " is not defined");	
		}
	}
}
