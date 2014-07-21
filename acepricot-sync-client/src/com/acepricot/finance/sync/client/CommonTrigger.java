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
		
		Object[] row;
		StringBuffer sb = new StringBuffer();
		switch(this.getType()) {
		case Trigger.INSERT:
			row = newRow;
			this.insert(con, row, sb);
			break;
		case Trigger.UPDATE:
			row = newRow;
			for(int i = 0; i < newRow.length; i ++) {
				if(!oldRow[i].equals(newRow[i])) {
					if(sb.length() > 0) {
						sb.append(',');
					}
					sb.append(i);
				}
			}
			if(sb.length() != 0) {
				this.insert(con, row, sb);
			}
			break;
		case Trigger.DELETE:
			row = oldRow;
			this.insert(con, row, sb);
			break;
		default:
		}
	}

	private void insert(Connection con, Object[] row, StringBuffer sb) throws SQLException {
		Object[] temp = new Object[row.length + DBSchemas.getSyncExtensionCols().length];
		temp[row.length] = sb.toString();
		temp[row.length + 1] = this.getType();
		temp[row.length + 2] = new Date().getTime();
		temp[row.length + 3] = this.getSchemaName();
		temp[row.length + 4] = this.getTableName();
		String cols[] = new String[temp.length]; 
		System.arraycopy(DBSchemas.getColumns(con, this.getTableName()), 0, cols, 0, DBSchemas.getColumns(con, this.getTableName()).length);
		System.arraycopy(DBSchemas.getSyncExtensionCols(), 0, cols, DBSchemas.getColumns(con, this.getTableName()).length, DBSchemas.getSyncExtensionCols().length);
		System.arraycopy(row, 0, temp, 0, row.length);
		DBConnectorLt.insert(con, SYNC_SCHEMA + "." + this.getTableName(), cols, toStringArray(temp), (char) 0);
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
