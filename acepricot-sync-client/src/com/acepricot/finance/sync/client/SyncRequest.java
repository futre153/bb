package com.acepricot.finance.sync.client;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import javax.xml.bind.DatatypeConverter;

import com.acepricot.finance.sync.DBConnector;
import com.acepricot.finance.sync.Row;
import com.acepricot.finance.sync.Rows;
import com.acepricot.finance.sync.share.JSONMessage;
import com.acepricot.finance.sync.share.sql.ColumnSpec;
import com.acepricot.finance.sync.share.sql.CompPred;
import com.acepricot.finance.sync.share.sql.FixedPointLiteral;
import com.acepricot.finance.sync.share.sql.Identifier;
import com.acepricot.finance.sync.share.sql.OrderClause;
import com.acepricot.finance.sync.share.sql.Predicate;
import com.acepricot.finance.sync.share.sql.Query;
import com.acepricot.finance.sync.share.sql.SchemaName;
import com.acepricot.finance.sync.share.sql.SortSpec;
import com.acepricot.finance.sync.share.sql.TableName;
import com.acepricot.finance.sync.share.sql.TopSpec;
import com.acepricot.finance.sync.share.sql.UnsInt;
import com.acepricot.finance.sync.share.sql.WhereClause;

public class SyncRequest {

	static final int STATUS_NEW = 0;
	static final int STATUS_PENDING_OK = 1;
	static final int STATUS_PENDING_FAILED = 2;
	static final int STATUS_ARCHIVED_OK = 3;
	private static final int STATUS_ARCHIVED_FAILED = 4;
	private static final int UNANSWERED = 0;
	private static final int ANSWERED = 1;
	static final int ALL = 2;
	
	public static JSONMessage getRequestForWaiting(Connection con, Properties prop) {
		try {
			Object[] objs = SyncRequest.checkForNewOperation(con);
			if(objs != null) {
				return SyncRequest.getInstance(prop).appendBody(objs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			/*
			 * TODO spracuj chybu
			 */
		}
		return null;
	}
	
	public static JSONMessage getResponseForUnasweredPending(Connection con, Properties prop) {
		try {
			Object[] objs = SyncRequest.checkForPending(con, UNANSWERED, -1, null);
			if(objs != null) {
				return SyncRequest.getInstance(prop).appendBody(objs);
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
			/*
			 * TODO spracuj chybu
			 */
		}
		return null;
	}
	
	static JSONMessage getInstance(Properties props) {
		return new JSONMessage(
				JSONMessageProcessorClient.SYNC_REQUEST_HEADER,
				new Object[] {
						props.getProperty(JSONMessageProcessorClient.GRP_NAME_KEY),
						DatatypeConverter.parseHexBinary(props.getProperty(JSONMessageProcessorClient.GRP_PSWD_KEY)),
						props.getProperty(JSONMessageProcessorClient.DEV_NAME_KEY),
						JSONMessageProcessorClient.DEFAULT_DB_VERSION});
	}
	
	
	private static Object[] checkForNewOperation(Connection con) throws SQLException {
		Object com1 = new CompPred(new Object[]{new ColumnSpec(new Identifier(DBSchemas.SYNC_LABEL), new Identifier(DBSchemas.SYNC_STATUS))}, new Object[]{STATUS_NEW}, Predicate.EQUAL);
		Row row = checkFor(con, com1);
		if(row != null) { 
			Iterator<String> iterator = row.keySet().iterator();
			ArrayList<Object> objs = new ArrayList<Object>();
			while(iterator.hasNext()) {
				String key = iterator.next();
				Object value = row.get(key);
				if(value != null) {
					objs.add(key);
					objs.add(value);
				}
			}
			return objs.toArray();
		}
		return null;
	}

	private static Row checkFor (Connection con, Object ... predicates) throws SQLException {
		String[] tables = DBSchemas.getTableNames(con);
		SchemaName schemaName = new SchemaName(new Identifier(DBSchemas.getSyncSchemaName()));
		TableName tableName = new TableName(new Identifier(tables[0]), schemaName);
		ColumnSpec[] cols = ColumnSpec.getColSpecArray(new Identifier(DBSchemas.SYNC_LABEL), DBSchemas.SYNC_ID, DBSchemas.SYNC_TABLE, DBSchemas.SYNC_INSERT);
		//Query query = DBConnector.createSelect().addColumns(cols).addFromClause(tableName, new Identifier(DBSchemas.SYNC_LABEL)).addQuerySpec(top).addSelectSpec(orderClause).addTableSpec(new WhereClause(predicates));
		Query query = null;
		for(int i = 1; i < tables.length; i ++) {
			tableName = new TableName(new Identifier(tables[i]), schemaName);
			Query select = DBConnector.createSelect();
			//select.addQuerySpec(top);
			select.addFromClause(tableName, new Identifier(DBSchemas.SYNC_LABEL));
			//select.addSelectSpec(orderClause);
			select.addColumns(cols);
			select.addTableSpec(new WhereClause(predicates));
			if(query == null) {
				query = select;
			}
			else {
				query = select.unionTo(query);
			}
		}
		TopSpec top = new TopSpec(new UnsInt(new FixedPointLiteral(1)));
		OrderClause orderClause = new OrderClause(new SortSpec(new ColumnSpec(new Identifier(DBSchemas.SYNC_INSERT))));
		query.addQuerySpec(top).addSelectSpec(orderClause);
		Rows rows = DBConnector.select(con, query);
		System.out.println(rows);
		if(rows.size() == 1) {
			tableName = new TableName(schemaName, new Identifier((String) rows.get(0).get(DBSchemas.SYNC_TABLE)));
			Object com1 = new CompPred(
					ColumnSpec.getColSpecArray(tableName, DBSchemas.SYNC_ID, DBSchemas.SYNC_TABLE),
					new Object[]{rows.get(0).get(DBSchemas.SYNC_ID), rows.get(0).get(DBSchemas.SYNC_TABLE)},
					Predicate.EQUAL);
			return DBConnector.select(con, DBConnector.createSelect().addFromClause(tableName).addTableSpec(new WhereClause(com1))).get(0);
		}
		else {
			return null;
		}
		
	}
	
	static Object[] checkForPending(Connection con, int pType, int id, String tableName) throws SQLException {
		ArrayList<Object> objs = new ArrayList<Object>();
		Object[] com = new CompPred[4];
		if(pType == UNANSWERED || pType == ALL) {
			com[0] = new CompPred(new Object[]{new ColumnSpec(new Identifier(DBSchemas.SYNC_LABEL), new Identifier(DBSchemas.SYNC_STATUS))}, new Object[]{STATUS_PENDING_OK}, Predicate.EQUAL);
			com[1] = new CompPred(new Object[]{new ColumnSpec(new Identifier(DBSchemas.SYNC_LABEL), new Identifier(DBSchemas.SYNC_STATUS))}, new Object[]{STATUS_PENDING_FAILED}, Predicate.EQUAL);
		}
		if(pType == ANSWERED || pType == ALL) {
			com[2] = new CompPred(new Object[]{new ColumnSpec(new Identifier(DBSchemas.SYNC_LABEL), new Identifier(DBSchemas.SYNC_STATUS))}, new Object[]{STATUS_ARCHIVED_OK}, Predicate.EQUAL);
			com[3] = new CompPred(new Object[]{new ColumnSpec(new Identifier(DBSchemas.SYNC_LABEL), new Identifier(DBSchemas.SYNC_STATUS))}, new Object[]{STATUS_ARCHIVED_FAILED}, Predicate.EQUAL);
		}
		for(int i = 0; i < com.length; i ++) {
			if(com[i] != null) {
				if(objs.size() > 0) {
					objs.add(WhereClause.OR);
				}
				objs.add(com[i]);
			}
		}
		if(id >= 0) {
			objs.add(0, WhereClause.LEFT_BRACKET);
			objs.add(WhereClause.RIGHT_BRACKET);
			objs.add(WhereClause.AND);
			objs.add(new CompPred(new Object[]{new ColumnSpec(new Identifier(DBSchemas.SYNC_LABEL), new Identifier(DBSchemas.SYNC_ID))}, new Object[]{id}, Predicate.EQUAL));
			objs.add(WhereClause.AND);
			objs.add(new CompPred(new Object[]{new ColumnSpec(new Identifier(DBSchemas.SYNC_LABEL), new Identifier(DBSchemas.SYNC_TABLE))}, new Object[]{tableName}, Predicate.EQUAL));
		}
		
		Row row = checkFor(con, objs.toArray());
		objs.clear();
		if(row != null) {
			objs.add(DBSchemas.SYNC_TYPE);
			byte i = (byte) row.get(DBSchemas.SYNC_STATUS);
			if(i == STATUS_PENDING_OK || i == STATUS_ARCHIVED_OK) {
				objs.add(JSONMessage.RESPONSE_FOR_PENDING_RESULT_OK);
			}
			else if (i == STATUS_PENDING_FAILED || i == STATUS_ARCHIVED_FAILED) {
				objs.add(JSONMessage.RESPONSE_FOR_PENDING_RESULT_FAILED);
			}
			objs.add(DBSchemas.SYNC_TABLE);
			objs.add(row.get(DBSchemas.SYNC_TABLE));
			objs.add(DBSchemas.SYNC_ID);
			objs.add(row.get(DBSchemas.SYNC_ID));
			return objs.toArray();
		}
		else {
			if(id >= 0) {
				objs.add(DBSchemas.SYNC_TYPE);
				objs.add(JSONMessage.RESPONSE_FOR_PENDING_NO_OPERATION);
				objs.add(DBSchemas.SYNC_TABLE);
				objs.add(tableName);
				objs.add(DBSchemas.SYNC_ID);
				objs.add(id);
				return objs.toArray();
			}
			return null;
		}
	}

	public static JSONMessage getEmptyRequest(Properties props) {
		return getInstance(props).appendBody(DBSchemas.SYNC_TYPE, JSONMessage.EMPTY_REQUEST);
	}

}
