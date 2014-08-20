package com.acepricot.finance.sync;

import java.sql.Connection;
import java.sql.SQLException;

import com.acepricot.finance.sync.share.sql.ColumnSpec;
import com.acepricot.finance.sync.share.sql.CompPred;
import com.acepricot.finance.sync.share.sql.Identifier;
import com.acepricot.finance.sync.share.sql.Predicate;
import com.acepricot.finance.sync.share.sql.SchemaName;
import com.acepricot.finance.sync.share.sql.TableName;
import com.acepricot.finance.sync.share.sql.WhereClause;

public class DBSchema {
	static final String SYNC_LABEL = "SYNC_";
	private static final String SYNC_CHANGES = SYNC_LABEL + "CHANGES";
	public static final String SYNC_TYPE = SYNC_LABEL + "TYPE";
	public static final String SYNC_INSERT = SYNC_LABEL + "INSERT";
	static final String SYNC_SCHEMA = SYNC_LABEL + "SCHEMA";
	static final String SYNC_TABLE = SYNC_LABEL + "TABLE";
	static final String SYNC_ID = SYNC_LABEL + "ID";
	private static final String SYNC_STATUS = SYNC_LABEL + "STATUS";
	private static final String[] SYNC_COLS_EXTENSIONS = {
		SYNC_CHANGES, SYNC_TYPE, SYNC_INSERT, SYNC_SCHEMA, SYNC_TABLE, SYNC_ID, SYNC_STATUS
	};
	
	private static final String INFORMATION_SCHEMA = "INFORMATION_SCHEMA";
	private static final String IS_TABLES = "TABLES";
	private static final String IS_TABLES_TABLE_NAME = "TABLE_NAME";
	private static final String IS_TABLES_TABLE_SCHEMA = "TABLE_SCHEMA";
	//private static final String USER_SCMEMA = "USER";
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
	static final String USER_SCHEMA = "USER";
	
	private static final String IS_CONSTRAINTS = "CONSTRAINTS";
	private static final String IS_CONSTRAINTS_CONSTRAINT_SCHEMA = "CONSTRAINT_SCHEMA";
	//private static final String IS_CONSTRAINTS_CONSTRAINT_NAME = "CONSTRAINT_NAME";
	private static final String IS_CONSTRAINTS_TABLE_SCHEMA = "TABLE_SCHEMA";
	private static final String IS_CONSTRAINTS_TABLE_NAME = "TABLE_NAME";
	private static final String IS_CONSTRAINTS_COLUMN_LIST = "COLUMN_LIST";
	private static final String IS_CONSTRAINTS_CONSTRAINT_TYPE = "CONSTRAINT_TYPE";
	private static final String PRIMARY_KEY_VALUE = "PRIMARY KEY";
	private static final String UNIQUE_VALUE = "UNIQUE";
	//public static final String SYNC_OPERATION_ID = "SYNC_OPERATION_ID";
	public static final String SYNC_OPERATION_RESPONSE_RESULT = "SYNC_OPERATION_RESPONSE_RESULT";
	public static final int SYNC_OPERATION_RESULT_OK = 0;
	public static final int SYNC_OPERATION_RESULT_FAILED = 1;
	public static final int SYNC_OPERATION_RESULT_NO_OPERATION = 2;
	
	
	private String[] tables;
	private String[][] columns;
	private String[][] dataTypes;
	private String[][] nullables;
	private Object[][] constraints;
	
	private static boolean trigger = true;
	
	private DBSchema() {}
	
	static DBSchema getInstance() {
		return new DBSchema();
	}
	
	public String[] getColumns(Connection con, String tableName) throws SQLException {
		return getColumnsForTableIndex(con, getIndexOfTable(con, tableName));
	}

	private String[] getColumnsForTableIndex(Connection con, int index) throws SQLException {
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

	private int getIndexOfTable(Connection con, String tableName) throws SQLException {
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
	
	public String[] getTableNames(Connection con) throws SQLException {
		if(tables == null) {
			loadSchemas(con);
		}
		return tables;
	}
	/*
	public static void dropSyncSchema(Connection con) throws SQLException {
		loadSchemas(con);
		if(tables != null) {
			for(int i = 0; i < tables.length; i ++) {
				DBConnectorLt.dropTable(con, true, SYNC_SCHEMA_NAME, tables[i], (char) 0);
			}
		}
		else {
			throw new SQLException("Failed to find sync tables");
		}
		DBConnectorLt.dropSchema(con, true, SYNC_SCHEMA_NAME, (char) 0);
		String tableName = INFORMATION_SCHEMA + "." + IS_TRIGGERS;
		String[] cols = {IS_TRIGGERS_TRIGGER_SCHEMA, IS_TRIGGERS_TRIGGER_NAME};
		Where w = new Where();
		Object[] where = w.set(w.equ(IS_TRIGGERS_JAVA_CLASS, CommonTrigger.class.getName()));
		ArrayList<Hashtable<String, Object>> rows = DBConnectorLt.select(con, tableName, cols, (char) 0, where);
		for(int i = 0; i < rows.size(); i ++) {
			Hashtable<String, Object> row = rows.get(i);
			DBConnectorLt.dropTrigger(con, true,(String) row.get(IS_TRIGGERS_TRIGGER_SCHEMA),(String) row.get(IS_TRIGGERS_TRIGGER_NAME), (char) 0);
		}
	}
	
	*/
	public DBSchema loadSchemas(Connection con) throws SQLException {
		SchemaName is = new SchemaName(new Identifier(INFORMATION_SCHEMA));
		TableName tableName = new TableName(is, new Identifier(IS_TABLES));
		ColumnSpec[] cols = ColumnSpec.getColSpecArray(tableName, new String[]{IS_TABLES_TABLE_NAME});
		Object com1 = new CompPred(new Object[]{new Identifier(IS_TABLES_TABLE_SCHEMA)}, new Object[]{USER_SCHEMA}, Predicate.EQUAL);
		Rows rows = DBConnector.select(con, DBConnector.createSelect().addFromClause(tableName).addColumns(cols).addTableSpec(new WhereClause(com1)));
		tables = new String[rows.size()];
		columns = new String[rows.size()][];
		dataTypes = new String[rows.size()][];
		nullables = new String[rows.size()][];
		for(int i = 0; i < tables.length; i ++) {
			tables[i] = (String) rows.get(i).get(IS_TABLES_TABLE_NAME);
		}
		tableName = new TableName(is, new Identifier(IS_TYPE_INFO));
		Rows typesRows = DBConnector.select(con, DBConnector.createSelect().addFromClause(tableName));		
		tableName = new TableName(is, new Identifier(IS_COLUMNS));
		cols = ColumnSpec.getColSpecArray(tableName, new String[]{IS_COLUMNS_COLUMN_NAME, IS_COLUMNS_DATA_TYPE, IS_COLUMNS_CHARACTER_MAXIMUM_LENGTH, IS_COLUMNS_NUMERIC_PRECISION, IS_COLUMNS_NUMERIC_SCALE, IS_COLUMNS_TYPE_NAME, IS_COLUMNS_IS_NULLABLE});
		for(int i = 0; i < tables.length; i ++) {
			com1 = new CompPred(new Object[]{new Identifier(IS_TABLES_TABLE_SCHEMA), new Identifier(IS_TABLES_TABLE_NAME)}, new Object[]{USER_SCHEMA, tables[i]}, Predicate.EQUAL);
			rows = DBConnector.select(con, DBConnector.createSelect().addFromClause(tableName).addColumns(cols).addTableSpec(new WhereClause(com1)));
			columns[i] = new String[rows.size()];
			dataTypes[i] = new String[rows.size()];
			nullables[i] = new String[rows.size()];
			for(int j = 0; j < columns[i].length; j ++) {
				Row row = rows.get(j);
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
					nullables[i][j] = "NOT NULL";
				}
			}
		}
		tableName = new TableName(is, new Identifier(IS_CONSTRAINTS));
		cols = ColumnSpec.getColSpecArray(tableName, new String[]{IS_CONSTRAINTS_COLUMN_LIST});
		Object com2 = new CompPred(new Object[]{new Identifier(IS_CONSTRAINTS_CONSTRAINT_TYPE)}, new Object[]{DBSchema.PRIMARY_KEY_VALUE}, Predicate.EQUAL);
		Object com3 = new CompPred(new Object[]{new Identifier(IS_CONSTRAINTS_CONSTRAINT_TYPE)}, new Object[]{DBSchema.UNIQUE_VALUE}, Predicate.EQUAL);
		constraints = new Object[tables.length][2];
		for(int i = 0; i < tables.length; i ++) {
			com1 = new CompPred (
					new Object[]{new Identifier(IS_CONSTRAINTS_CONSTRAINT_SCHEMA),	new Identifier(IS_CONSTRAINTS_TABLE_SCHEMA),	new Identifier(IS_CONSTRAINTS_TABLE_NAME)},
					new Object[]{USER_SCHEMA,										USER_SCHEMA,									tables[i]},
					Predicate.EQUAL);
			rows = DBConnector.select(con, DBConnector.createSelect().addFromClause(tableName).addColumns(cols).addTableSpec(new WhereClause(com1, WhereClause.AND, com2)));
			constraints[i][0] = getList(rows);
			rows = DBConnector.select(con, DBConnector.createSelect().addFromClause(tableName).addColumns(cols).addTableSpec(new WhereClause(com1, WhereClause.AND, com3)));
			constraints[i][1] = getList(rows);
		}
		return this;
	}
	
	
	private static String[] getList(Rows rows) {
		StringBuffer sb = new StringBuffer();
		for(int j = 0; j < rows.size(); j ++) {
			Object list = rows.get(j).get(IS_CONSTRAINTS_COLUMN_LIST);
			if(list != null && (list instanceof String)) {
				if(sb.length() > 0) {
					sb.append(',');
				}
				sb.append(list);
			}
		}
		if(sb.length() > 0) {
			return sb.toString().split(",");
		}
		else {
			return null;
		}
	}
		
		
		//if(isTrigger()) return;
		
		/*
		
		DBConnectorLt.createSchema(con, SYNC_SCHEMA_NAME, true, (char) 0);
		System.out.println(status);
		for(int i = 0; i < dataTypes.length; i ++) {
			System.out.println(Arrays.toString(columns[i]));
			System.out.println(Arrays.toString(dataTypes[i]));
			System.out.println(Arrays.toString(constraints[i]));
		}
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
		table = INFORMATION_SCHEMA + "." + IS_CROSS_REFERENCES;
		cols = new String[] {
				IS_CROSS_REFERENCES_PKTABLE_SCHEMA, IS_CROSS_REFERENCES_PKTABLE_NAME, IS_CROSS_REFERENCES_PKCOLUMN_NAME,
				IS_CROSS_REFERENCES_FKTABLE_SCHEMA, IS_CROSS_REFERENCES_FKTABLE_NAME, IS_CROSS_REFERENCES_FKCOLUMN_NAME,
				IS_CROSS_REFERENCES_UPDATE_RULE, IS_CROSS_REFERENCES_DELETE_RULE};
		w.clear();
		where = w.set(w.equ(IS_CROSS_REFERENCES_PKTABLE_SCHEMA, USER_SCHEMA));
		rows = DBConnectorLt.select(con, table, cols, (char) 0, where);
		for(int i = 0; i < rows.size(); i ++) {
			String consName = SYNC_SCHEMA_NAME + "_CONSTRIANT_" + i;
			Hashtable<String, Object> row = rows.get(i);
			row.put(IS_CROSS_REFERENCES_FKTABLE_SCHEMA, SYNC_SCHEMA_NAME);
			DBConnectorLt.alterTable(con, new RefConstraint(consName, toArray(row, cols), true, true), (char) 0);
		}
		table = INFORMATION_SCHEMA + "." + IS_CONSTRAINTS;
		cols = new String[] {IS_CONSTRAINTS_CONSTRAINT_SCHEMA, IS_CONSTRAINTS_CONSTRAINT_NAME,
			IS_CONSTRAINTS_TABLE_SCHEMA, IS_CONSTRAINTS_TABLE_NAME, IS_CONSTRAINTS_COLUMN_LIST};
		
		w.clear();
		where = w.set(w.and(w.equ(IS_CONSTRAINTS_TABLE_SCHEMA, USER_SCHEMA), w.equ(IS_CONSTRAINTS_CONSTRAINT_TYPE, PRIMARY_KEY_VALUE)));
		rows = DBConnectorLt.select(con, table, cols, (char) 0, where);
		primaries = new Hashtable<String, String>();
		for(int i = 0; i < rows.size(); i ++) {
			Hashtable<String, Object> row = rows.get(i);
			String[] list = ((String) row.get(IS_CONSTRAINTS_COLUMN_LIST)).split(",");
			for(int j = 0; j < list.length; j ++) {
				primaries.put(row.get(IS_CONSTRAINTS_TABLE_SCHEMA) + "." + row.get(IS_CONSTRAINTS_TABLE_NAME) + "." + list[j], row.get(IS_CONSTRAINTS_CONSTRAINT_SCHEMA) + "." + row.get(IS_CONSTRAINTS_CONSTRAINT_NAME));
			}
		}
		System.out.println(primaries);
		*/
	

	private static int getDataType(Rows typesRows, Object dataType) throws SQLException {
		for(int i = 0; i < typesRows.size(); i ++) {
			Row row = typesRows.get(i);
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

	public String[] getSyncExtensionCols() {
		return SYNC_COLS_EXTENSIONS;
	}

	public static boolean isTrigger() {
		return trigger;
	}

	public static void setTrigger(boolean trigger) {
		DBSchema.trigger = trigger;
	}

	public static String getSchemaName() {
		return DBSchema.USER_SCHEMA;
	};
	
	public static String getSyncSchemaName() {
		return DBSchema.SYNC_SCHEMA_NAME;
	}

	public String[] getColumns(String table) throws SQLException {
		return this.getColumns(null, table);
	}

	public String[] getPrimaryKeys(String table) throws SQLException {
		int index = this.getIndexOfTable(null, table);
		Object obj = this.constraints[index][0];
		if(obj == null) {
			return new String[]{};
		}
		return (String[]) obj;
	};
	
}
