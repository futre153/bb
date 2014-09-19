package com.acepricot.finance.sync.client;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;

import org.h2.api.Trigger;

import com.acepricot.finance.sync.DBConnector;
import com.acepricot.finance.sync.Row;
import com.acepricot.finance.sync.Rows;
import com.acepricot.finance.sync.share.sql.CompPred;
import com.acepricot.finance.sync.share.sql.Identifier;
import com.acepricot.finance.sync.share.sql.Predicate;
import com.acepricot.finance.sync.share.sql.SchemaName;
import com.acepricot.finance.sync.share.sql.TableName;
import com.acepricot.finance.sync.share.sql.WhereClause;

public class CommonTrigger implements Trigger {
	
	private static int alive = 0;
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
		CommonTrigger.setAlive(true);
		try {
			String user = getUser(con);
			if(user == null || user.length() == 0 || !(user.equals(JSONMessageProcessorClient.SYNC_ADMIN))) {
				switch(this.getType()) {
				case Trigger.INSERT:
					this.insert(con, newRow, newRow);
					break;
				case Trigger.UPDATE:
					if(!Arrays.equals(oldRow, newRow)) {
						if(!this.update(con, oldRow, newRow)) {
							this.insert(con, oldRow, newRow);
						}
					}
					break;
				case Trigger.DELETE:
					Object[] row = this.delete(con, oldRow);
					if(row != null) {
						this.insert(con, row, row);
					}
					break;
				default:
				}
			}
		}
		catch(Exception e) {
			throw new SQLException(e);
		}
		finally {
			CommonTrigger.setAlive(false);
		}
	}
	
	private Object[] delete(Connection con, Object[] oldRow) throws SQLException {
		Object[] retValue = oldRow;
		String[] cols = DBSchemas.getColumns(con, tableName);
		String[] oldCols = JSONMessageProcessorClient.addPrefix(cols, DBSchemas.OLD_ROW_PREFIX);
		String[] newCols = JSONMessageProcessorClient.addPrefix(cols, DBSchemas.NEW_ROW_PREFIX);
		SchemaName schemaName = new SchemaName(new Identifier(DBSchemas.getSyncSchemaName()));
		TableName tableName = new TableName(schemaName, new Identifier(this.tableName));
		Object com1 = JSONMessageProcessorClient.getComparisonPredicate(newCols, new String[]{DBSchemas.SYNC_TYPE, DBSchemas.SYNC_STATUS}, oldRow, new Object[]{Trigger.INSERT, 0});
		retValue = DBConnector.delete(con, DBConnector.createDelete(tableName, null, new WhereClause(com1)), false) > 0 ? null : retValue;
		com1 = JSONMessageProcessorClient.getComparisonPredicate(newCols, new String[]{DBSchemas.SYNC_TYPE, DBSchemas.SYNC_STATUS}, oldRow, new Object[]{Trigger.UPDATE, 0});
		Rows rows = DBConnector.select(con, DBConnector.createSelect().addFromClause(tableName).addTableSpec(new WhereClause(com1)));
		if(rows.size() > 0) {
			com1 = new CompPred(new Object[]{new Identifier(DBSchemas.SYNC_ID)}, new Object[]{rows.get(0).get(DBSchemas.SYNC_ID)}, Predicate.EQUAL);
			DBConnector.delete(con, DBConnector.createDelete(tableName, null, new WhereClause(com1)), false);
			retValue = new Object[oldCols.length];
			Row row = rows.get(0);
			for(int i = 0; i < retValue.length; i ++) {
				retValue[i] = row.get(oldCols[i]);
			}
		}
		return retValue;
	}
	
	private boolean update(Connection con, Object[] oldRow, Object[] newRow) throws SQLException {
		boolean retValue = false;
		String[] cols = DBSchemas.getColumns(con, tableName);
		String[] oldCols = JSONMessageProcessorClient.addPrefix(cols, DBSchemas.OLD_ROW_PREFIX);
		String[] newCols = JSONMessageProcessorClient.addPrefix(cols, DBSchemas.NEW_ROW_PREFIX);
		SchemaName schemaName = new SchemaName(new Identifier(DBSchemas.getSyncSchemaName()));
		TableName tableName = new TableName(schemaName, new Identifier(this.tableName));
		Object com1 = JSONMessageProcessorClient.getComparisonPredicate(oldCols, new String[]{DBSchemas.SYNC_TYPE, DBSchemas.SYNC_STATUS}, oldRow, new Object[]{Trigger.INSERT, 0});
		retValue = DBConnector.update(con, DBConnector.createUpdate(tableName, JSONMessageProcessorClient.joinArray(oldCols, newCols), JSONMessageProcessorClient.joinArray(newRow, newRow.clone()), new WhereClause(com1)), false) > 0;
		com1 = JSONMessageProcessorClient.getComparisonPredicate(newCols, new String[]{DBSchemas.SYNC_TYPE, DBSchemas.SYNC_STATUS}, oldRow, new Object[]{Trigger.UPDATE, 0});
		retValue = DBConnector.update(con, DBConnector.createUpdate(tableName, newCols, newRow, new WhereClause(com1)), false) > 0 ? true : retValue;
		Object[] pred = new Object[(oldCols.length + 1) * 2 - 1];
		int j = 0;
		for(int i = 0; i < oldCols.length; i ++) {
			pred[j] = new CompPred(new Object[]{new Identifier(oldCols[i])}, new Object[]{new Identifier(newCols[i])}, Predicate.EQUAL);
			j ++;
			pred[j] = WhereClause.AND;
			j ++;
		}
		pred[j] = new CompPred(new Object[]{new Identifier(DBSchemas.SYNC_TYPE), new Identifier(DBSchemas.SYNC_STATUS)}, new Object[]{Trigger.UPDATE, SyncRequest.STATUS_NEW}, Predicate.EQUAL);
		return DBConnector.delete(con, DBConnector.createDelete(tableName, null, new WhereClause(pred)), false) > 0 ? true : retValue;
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
		SchemaName schemaName = new SchemaName(new Identifier(DBSchemas.getSyncSchemaName()));
		TableName tableName = new TableName(schemaName, new Identifier(this.tableName));
		int rl = oldRow.length + newRow.length;
		Object[] temp = new Object[oldRow.length + newRow.length + DBSchemas.getSyncExtensionValuableCols().length];
		temp[rl + 0] = this.getType();
		temp[rl + 1] = new Date().getTime();
		temp[rl + 2] = this.getSchemaName();
		temp[rl + 3] = this.getTableName();
		String cols[] = DBSchemas.getSyncTableValsCols(con, this.tableName); 
		System.arraycopy(oldRow, 0, temp, 0, oldRow.length);
		System.arraycopy(newRow, 0, temp, oldRow.length, newRow.length);
		DBConnector.insert(con, DBConnector.createInsert(tableName, temp, cols), false);
	}
	/*
	private static String save(Object obj) throws Exception {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bout);
		out.writeObject(obj);
		return new String(Base64Coder.encode(Huffman.encode(bout.toByteArray(), null)));
	}
	*/
	/*
	private static String[] toStringArray(Object[] temp) {
		String[] tmp = new String[temp.length];
		for(int i = 0; i < temp.length; i ++) {
			tmp[i] = tmp[i] == null ? null : temp[i].toString();
		}
		return tmp;
	}
*/
	@Override
	public void init(Connection con, String schema, String trigger, String table, boolean before, int type) throws SQLException {
		JSONMessageProcessorClient.getLogger().info("Trigger is now " + alive);
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

	public static boolean isAlive() {
		JSONMessageProcessorClient.getLogger().info("Trigger is now " + alive);
		return alive != 0;
	}

	private static void setAlive(boolean b) {
		CommonTrigger.alive = (b ? CommonTrigger.alive + 1 : CommonTrigger.alive - 1);
		JSONMessageProcessorClient.getLogger().info("Trigger is now " + alive);
	}

}
