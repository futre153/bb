package com.acepricot.finance.sync.client;

import java.sql.SQLException;

public class DBSchemas {
	
	private static String[] tables;
	private static String[][] columns;
	
	private DBSchemas() {}

	public static String[] getColumns(String tableName) throws SQLException {
		return DBSchemas.getColumnsForTableIndex(DBSchemas.getIndexOfTable(tableName));
	}

	private static String[] getColumnsForTableIndex(int index) throws SQLException {
		if(columns == null) {
			loadSchemas();
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

	private static int getIndexOfTable(String tableName) throws SQLException {
		if(tableName == null) {
			loadSchemas();
			if(tableName == null) {
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

	private static void loadSchemas() {
		// TODO Auto-generated method stub
		
	}

	public static Object getSyncExtensionCols() {
		return SYNC_COLS_EXCEPTIONS;
	};
	
	
	
}
