package com.acepricot.finance.sync.client;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;

import org.h2.api.Trigger;
import org.pabk.util.Base64Coder;
import org.pabk.util.Huffman;

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
			Object[] row;
			//StringBuffer sb = new StringBuffer();
			HashMap<String, Object> changes = new HashMap<String, Object>();
			switch(this.getType()) {
			case Trigger.INSERT:
				row = newRow;
				this.insert(con, row, changes);
				break;
			case Trigger.UPDATE:
				row = newRow;
				String[] colNames = DBSchemas.getColumns(con, tableName);
				for(int i = 0; i < newRow.length; i ++) {
					if(((oldRow[i] == null) && (newRow[i] != null)) || ((oldRow[i] != null) && ((newRow[i] == null) || (!oldRow[i].equals(newRow[i]))))) {
						changes.put(colNames[i], oldRow[i]);
					}
				}
				if(changes.size() > 0) {
					this.insert(con, row, changes);
				}
				break;
			case Trigger.DELETE:
				row = oldRow;
				this.insert(con, row, changes);
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

	private void insert(Connection con, Object[] row, HashMap<String, Object> changes) throws SQLException {
		Object[] temp = new Object[row.length + DBSchemas.getSyncExtensionValuableCols().length];
		String c = "";
		if(changes.size() > 0) {
			try {
				c = save(changes);
			} catch (Exception e) {
				c = "";
			}
		}
		temp[row.length] = c;
		temp[row.length + 1] = this.getType();
		temp[row.length + 2] = new Date().getTime();
		temp[row.length + 3] = this.getSchemaName();
		temp[row.length + 4] = this.getTableName();
		String cols[] = new String[temp.length]; 
		System.arraycopy(DBSchemas.getColumns(con, this.getTableName()), 0, cols, 0, DBSchemas.getColumns(con, this.getTableName()).length);
		System.arraycopy(DBSchemas.getSyncExtensionValuableCols(), 0, cols, DBSchemas.getColumns(con, this.getTableName()).length, DBSchemas.getSyncExtensionValuableCols().length);
		System.arraycopy(row, 0, temp, 0, row.length);
		DBConnectorLt.insert(con, SYNC_SCHEMA + "." + this.getTableName(), cols, toStringArray(temp), (char) 0);
	}
	
	private static String save(Object obj) throws Exception {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bout);
		out.writeObject(obj);
		return new String(Base64Coder.encode(Huffman.encode(bout.toByteArray(), null)));
	}
	
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
