package com.acepricot.finance.sync.client;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;

import org.h2.api.Trigger;

import com.acepricot.finance.sync.Where;

public class DBSchemas {
	
	private static final String SYNC_CHANGES = "SYNC_CHANGES";
	private static final String SYNC_TYPE = "SYNC_TYPE";
	private static final String SYNC_INSERT = "SYNC_INSERT";
	private static final String SYNC_SCHEMA = "SYNC_SHEMA";
	private static final String SYNC_TABLE = "SYNC_TABLE";
	private static final String[] SYNC_COLS_EXTENSIONS = {
		SYNC_CHANGES, SYNC_TYPE, SYNC_INSERT, SYNC_SCHEMA, SYNC_TABLE
	};
	private static final String[] SYNC_DATATYPES_EXTENSIONS = {
		"VARCHAR(256)", "TINYINT", "BIGINT", "VARCHAR(256)", "VARCHAR(256)"
	};
	private static final String[] SYNC_CONSTRAINTS_EXTENSIONS = {
		"NOT NULL",
		"DEFAULT " + Trigger.INSERT + " OR " + Trigger.UPDATE + " OR " + Trigger.DELETE + " OR " + Trigger.SELECT + " NOT NULL",
		"NOT NULL",
		"NOT NULL",
		"NOT NULL"
	};
	private static final String INFORMATION_SCHEMA = "INFORMATION_SCHEMA";
	private static final String IS_TABLES = "TABLES";
	private static final String IS_TABLES_TABLE_NAME = "TABLE_NAME";
	private static final String IS_TABLES_TABLE_SCHEMA = "TABLE_SCHEMA";
	private static final String USER_SCMEMA = "USER";
	private static final String IS_COLUMNS_COLUMN_NAME = "COLUMN_NAME";
	private static final String IS_COLUMNS = "COLUMNS";
	private static final String IS_COLUMNS_CHARACTER_MAXIMUM_LENGTH = "CHARACTER_MAXIMUM_LENGTH";
	private static final String IS_COLUMNS_NUMERIC_PRECISION = "NUMERIC_PRECISION";
	private static final String IS_COLUMNS_DATA_TYPE = "DATA_TYPE";
	private static final String IS_COLUMNS_NUMERIC_SCALE = "NUMERIC_SCALE";
	private static final String IS_TYPE_INFO = "TYPE_INFO";
	private static final String IS_TYPE_INFO_PARAMS = "PARAMS";
	private static final String IS_TYPE_INFO_PARAMS_LENGTH = "LENGTH";
	private static final String IS_COLUMNS_TYPE_NAME = "TYPE_NAME";
	private static final String SYNC_SCHEMA_NAME = "SYNC";
	private static final String IS_COLUMNS_IS_NULLABLE = "IS_NULLABLE";
	private static final String IS_IS_NULLABLE_NO = "NO";
	private static final String USER_SCHEMA = "USER";

	private static String[] tables;
	private static String[][] columns;
	private static String[][] dataTypes;
	private static String[][] constraints;
	
	private static boolean trigger = true;
	
	private DBSchemas() {}

	public static String[] getColumns(Connection con, String tableName) throws SQLException {
		return DBSchemas.getColumnsForTableIndex(con, DBSchemas.getIndexOfTable(con, tableName));
	}

	private static String[] getColumnsForTableIndex(Connection con, int index) throws SQLException {
		if(columns == null) {
			loadSchemas(con);
			if(columns == null) {
				throw new SQLException("Failed to load database schemas");
			}
		}
		if(index >= columns.length) {
			throw new SQLException ("Index out of bounds while columns retrieving");
		}
		if(columns[index] == null) {
			throw new SQLException ("Columns are null for this table");
		}
		return columns[index];
	}

	private static int getIndexOfTable(Connection con, String tableName) throws SQLException {
		if(tables == null) {
			loadSchemas(con);
			if(tables == null) {
				throw new SQLException("Failed to load database schemas");
			}
		}
		for (int i = 0; i < tables.length; i ++) {
			if(tableName.equals(tables[i])) {
				return i;
			}
		}
		throw new SQLException("Failed to retrieve tableName");
	}

	public static void loadSchemas(Connection con) throws SQLException {
		// TODO Auto-generated method stub
		String table = INFORMATION_SCHEMA + "." + IS_TABLES;
		String[] cols = new String[]{IS_TABLES_TABLE_NAME};
		Where w = new Where();
		Object[] where = w.set(w.equ(IS_TABLES_TABLE_SCHEMA, USER_SCMEMA));
		ArrayList<Hashtable<String, Object>> rows = DBConnectorLt.select(con, table, cols, (char)0, where);
		tables = new String[rows.size()];
		columns = new String[rows.size()][];
		dataTypes = new String[rows.size()][];
		constraints = new String[rows.size()][];
		for(int i = 0; i < tables.length; i ++) {
			tables[i] = (String) rows.get(i).get(IS_TABLES_TABLE_NAME);
		}
		table = INFORMATION_SCHEMA + "." + IS_TYPE_INFO;
		ArrayList<Hashtable<String, Object>> typesRows = DBConnectorLt.select(con, table, null, (char) 0, null);		
		table = INFORMATION_SCHEMA + "." + IS_COLUMNS;
		cols = new String[]{IS_COLUMNS_COLUMN_NAME, IS_COLUMNS_DATA_TYPE, IS_COLUMNS_CHARACTER_MAXIMUM_LENGTH, IS_COLUMNS_NUMERIC_PRECISION, IS_COLUMNS_NUMERIC_SCALE, IS_COLUMNS_TYPE_NAME, IS_COLUMNS_IS_NULLABLE};
		for(int i = 0; i < tables.length; i ++) {
			w.clear();
			where = w.set(w.and(w.equ(IS_TABLES_TABLE_SCHEMA, USER_SCMEMA), w.equ(IS_TABLES_TABLE_NAME, tables[i])));
			rows = DBConnectorLt.select(con, table, cols, (char)0, where);
			columns[i] = new String[rows.size()];
			dataTypes[i] = new String[rows.size()];
			constraints[i] = new String[rows.size()];
			for(int j = 0; j < columns[i].length; j ++) {
				Hashtable<String, Object> row = rows.get(j);
				columns[i][j] = (String) row.get(IS_COLUMNS_COLUMN_NAME);
				dataTypes[i][j] = (String) row.get(IS_COLUMNS_TYPE_NAME);
				switch(getDataType(typesRows, row.get(IS_COLUMNS_DATA_TYPE))) {
				case 0:
					break;
				case 1:
					dataTypes[i][j] += ("(" + row.get(IS_COLUMNS_CHARACTER_MAXIMUM_LENGTH) + ")");
					break;
				case 2:
					dataTypes[i][j] += ("(" + row.get(IS_COLUMNS_NUMERIC_PRECISION) + "," + row.get(IS_COLUMNS_NUMERIC_SCALE) + ")");
					break;
				default:
					throw new SQLException("Unknown data type");
				}
				Object nullable = row.get(IS_COLUMNS_IS_NULLABLE);
				if(nullable != null && nullable.equals(IS_IS_NULLABLE_NO)) {
					constraints[i][j] = "NOT NULL";
				}
			}
		}
		
		
		if(isTrigger()) return;
		
		
		
		DBConnectorLt.createSchema(con, SYNC_SCHEMA_NAME, true, (char) 0);
		/*System.out.println(status);
		for(int i = 0; i < dataTypes.length; i ++) {
			System.out.println(Arrays.toString(columns[i]));
			System.out.println(Arrays.toString(dataTypes[i]));
			System.out.println(Arrays.toString(constraints[i]));
		}*/
		for(int i = 0; i < tables.length; i ++) {
			cols = new String[columns[i].length + SYNC_COLS_EXTENSIONS.length];
			System.arraycopy(columns[i], 0, cols, 0, columns[i].length);
			System.arraycopy(SYNC_COLS_EXTENSIONS, 0, cols, columns[i].length, SYNC_COLS_EXTENSIONS.length);
			String[] types = new String[dataTypes[i].length + SYNC_DATATYPES_EXTENSIONS.length];
			System.arraycopy(dataTypes[i], 0, types, 0, dataTypes[i].length);
			System.arraycopy(SYNC_DATATYPES_EXTENSIONS, 0, types, dataTypes[i].length, SYNC_DATATYPES_EXTENSIONS.length);
			String[] cons = new String[constraints[i].length + SYNC_CONSTRAINTS_EXTENSIONS.length];
			System.arraycopy(constraints[i], 0, cons, 0, constraints[i].length);
			System.arraycopy(SYNC_CONSTRAINTS_EXTENSIONS, 0, cons, constraints[i].length, SYNC_CONSTRAINTS_EXTENSIONS.length);
			//System.out.println(Arrays.toString(cols));
			//System.out.println(Arrays.toString(types));
			//System.out.println(Arrays.toString(cons));
			DBConnectorLt.createTable(con, SYNC_SCHEMA_NAME, tables[i], cols, types, cons, true, (char) 0);
			
			DBConnectorLt.createTrigger(con, true, USER_SCHEMA, tables[i], false, Trigger.INSERT, true, CommonTrigger.class, (char) 0); 
			DBConnectorLt.createTrigger(con, true, USER_SCHEMA, tables[i], false, Trigger.UPDATE, true, CommonTrigger.class, (char) 0);
			DBConnectorLt.createTrigger(con, true, USER_SCHEMA, tables[i], false, Trigger.DELETE, true, CommonTrigger.class, (char) 0);
			
		}
	}

	private static int getDataType(ArrayList<Hashtable<String, Object>> typesRows, Object dataType) throws SQLException {
		for(int i = 0; i < typesRows.size(); i ++) {
			Hashtable<String, Object> row = typesRows.get(i);
			if(row.get(IS_COLUMNS_DATA_TYPE).equals(dataType)) {
				Object obj = row.get(IS_TYPE_INFO_PARAMS);
				if(obj == null) {
					return 0;
				}
				else if(obj.equals(IS_TYPE_INFO_PARAMS_LENGTH)) {
					return 1;
				}
				else {
					return 2;
				}
			}
		}
		throw new SQLException("Unknown data type");
	}

	public static String[] getSyncExtensionCols() {
		return SYNC_COLS_EXTENSIONS;
	}

	public static boolean isTrigger() {
		return trigger;
	}

	public static void setTrigger(boolean trigger) {
		DBSchemas.trigger = trigger;
	};
	
	
	
}
