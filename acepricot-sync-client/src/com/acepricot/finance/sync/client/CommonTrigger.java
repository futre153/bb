package com.acepricot.finance.sync.client;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import org.h2.api.Trigger;

public class CommonTrigger implements Trigger {
	
	private static final String SYNC_SCHEMA = "SYNC";
	private int type;
	private String schemaName;
	private String triggerName;
	private String tableName;
	private boolean before;
	
	@Override
	public void close() throws SQLException {
		// TODO Auto-generated method stub
	}

	@Override
	public void fire(Connection con, Object[] oldRow, Object[] newRow)	throws SQLException {
		String user = getUser(con);
		if(user == null || user.length() == 0 || !(user.equals(JSONMessageProcessorClient.SYNC_ADMIN))) {
			switch(this.getType()) {
			case Trigger.INSERT:
				this.insert(con, newRow, newRow);
				break;
			case Trigger.UPDATE:
				this.insert(con, oldRow, newRow);
				break;
			case Trigger.DELETE:
				this.insert(con, oldRow, oldRow);
				break;
			default:
			}
		}
	}

	private static String getUser(Connection con) {
		String[] a = con.toString().split(" ");
		for(int i = 0; i < a.length; i ++) {
			if(a[i].contains("user")) {
				return a[i].split("=", 2)[1];
			}
		}
		return null;
	}

	private void insert(Connection con, Object[] oldRow, Object[] newRow) throws SQLException {
		int rl = oldRow.length + newRow.length;
		Object[] temp = new Object[oldRow.length + newRow.length + DBSchemas.getSyncExtensionValuableCols().length];
		temp[rl + 0] = this.getType();
		temp[rl + 1] = new Date().getTime();
		temp[rl + 2] = this.getSchemaName();
		temp[rl + 3] = this.getTableName();
		String cols[] = DBSchemas.getSyncTableValsCols(con, this.tableName); 
		System.arraycopy(oldRow, 0, temp, 0, oldRow.length);
		System.arraycopy(newRow, 0, temp, oldRow.length, newRow.length);
		DBConnectorLt.insert(con, SYNC_SCHEMA + "." + this.getTableName(), cols, toStringArray(temp), (char) 0);
	}
	/*
	private static String save(Object obj) throws Exception {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bout);
		out.writeObject(obj);
		return new String(Base64Coder.encode(Huffman.encode(bout.toByteArray(), null)));
	}
	*/
	private static String[] toStringArray(Object[] temp) {
		String[] tmp = new String[temp.length];
		for(int i = 0; i < temp.length; i ++) {
			tmp[i] = temp[i].toString();
		}
		return tmp;
	}

	@Override
	public void init(Connection con, String schema, String trigger, String table, boolean before, int type) throws SQLException {
		this.setSchemaName(schema);
		this.setTriggerName(trigger);
		this.setTableName(table);
		this.setBefore(before);
		this.setType(type);
	}

	@Override
	public void remove() throws SQLException {
		// TODO Auto-generated method stub
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public String getTriggerName() {
		return triggerName;
	}

	public void setTriggerName(String triggerName) {
		this.triggerName = triggerName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public boolean isBefore() {
		return before;
	}

	public void setBefore(boolean before) {
		this.before = before;
	}

}
