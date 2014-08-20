package com.acepricot.finance.sync.client;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import javax.xml.bind.DatatypeConverter;

import org.pabk.util.Huffman;

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

public class SyncRequest extends JSONMessage {

	private static final int STATUS_NEW = 0;
	private static String password;
	private static String user;
	private static Class<?> _class;
	private static boolean propsLoaded = false;
	private static String dbUrl;
	
	static Connection getConnection(Properties p) throws SQLException {
		try {
			if(!propsLoaded) {
				loadProperties(p);
			}
			if(propsLoaded) {
				return DriverManager.getConnection(dbUrl, user, Huffman.decode(password, null));
			}
			throw new SQLException("Properties not loaded");
		}
		catch (Exception e) {
			throw new SQLException (e);
		}
	}
	
	private static void loadProperties(Properties p) throws ClassNotFoundException {
		if(_class == null) {
			_class = Class.forName(p.getProperty(JSONMessageProcessorClient.JDBC_DRIVER_KEY));
		}
		if(dbUrl == null) {
			dbUrl = String.format(p.getProperty(JSONMessageProcessorClient.URL_STRING_KEY), p.getProperty(JSONMessageProcessorClient.DB_NAME_KEY).replaceAll("\\\\", "/").replaceAll(p.getProperty(JSONMessageProcessorClient.EXT_REPL_KEY), ""));			
		}
		if(user == null) {
			user = p.getProperty(JSONMessageProcessorClient.DB_USER_KEY);
		}
		if(password == null) {
			password = p.getProperty(JSONMessageProcessorClient.DB_PSWD_KEY);
		}
		propsLoaded = true;
	}

	public static JSONMessage getInstance(Properties props) {
		JSONMessage msg = new JSONMessage(
				JSONMessageProcessorClient.SYNC_REQUEST_HEADER,
				new Object[] {
						props.getProperty(JSONMessageProcessorClient.GRP_NAME_KEY),
						DatatypeConverter.parseHexBinary(props.getProperty(JSONMessageProcessorClient.GRP_PSWD_KEY)),
						props.getProperty(JSONMessageProcessorClient.DEV_NAME_KEY),
						JSONMessageProcessorClient.DEFAULT_DB_VERSION});;
		Connection con = null;
		Object[] array;
		try {
			con = getConnection(props);
			String[] tables = DBSchemas.getTableNames(con);
			array = checkForPending(con, tables, props);
			if(msg == null) {
				array = checkForOperation(con, tables, props);
			}
			
		
		}
		catch (Exception e) {
			//TODO spracuj vynimku
			e.printStackTrace();
		}
		finally {
			try {con.close();} catch (SQLException e) {}
		}
		return msg;
	}

	private static Object[] checkForOperation(Connection con, String[] tables, Properties props) throws SQLException {
		Row row = checkFor(con, tables, props, STATUS_NEW);
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

	private static Row checkFor (Connection con, String[] tables, Properties props, int status) throws SQLException {
		JSONMessage msg = null;
		for(int i = 0; i < tables.length; i ++) {
			TableName tableName = new TableName(new Identifier(tables[i]), new SchemaName(new Identifier(DBSchemas.getSyncSchemaName())));
			TopSpec top = new TopSpec(new UnsInt(new FixedPointLiteral(1)));
			Query select = DBConnector.createSelect();
			select.addQuerySpec(top);
			select.addFromClause(tableName);
			select.addSelectSpec(new OrderClause(new SortSpec(new ColumnSpec(tableName, new Identifier(DBSchemas.SYNC_INSERT)))));
			Object com1 = new CompPred(new Object[]{new ColumnSpec(tableName, new Identifier(DBSchemas.SYNC_STATUS))}, new Object[]{status}, Predicate.EQUAL);
			Rows rows = DBConnector.select(con, select.addTableSpec(new WhereClause(com1)));
			System.out.println(rows);
			if(rows.size() == 1) {
				return rows.get(0);
			}
			else {
				continue;
			}
		}
		return null;
	}
	
	private static Object[] checkForPending(Connection con, String[] tables, Properties props) {
		Row row = checkFor(con, tables, props, STATUS_NEW);
		if(row != null) {
			row.put(DBSchemas., value)
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

}
