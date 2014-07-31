package com.acepricot.finance.sync;

import java.sql.SQLException;
import java.util.Arrays;

public class RefConstraint extends ConstraintImpl {
	
	private static final int CASCADE = 0x00;
	private static final int RESTRICT = 0x01;
	private static final int SET_NULL = 0x02;
	private static final int SET_DEFAULT = 0x04;
	private static final String CASCADE_SQL = "CASCADE";
	private static final String RESTRICT_SQL = "RESTRICT";
	private static final String SET_NULL_SQL = "SET NULL";
	private static final String SET_DEFAULT_SQL = "SET DEFAULT";
	private String schema;
	private String table;
	private String column;
	
	private String refSchema;
	private String refTable;
	private String refColumn;
	
	private short onUpdate;
	private short onDelete;
	
	private boolean ifExists;
	private boolean ifNotExists;
	private boolean add;
	
	public RefConstraint(String name, Object[] objs, boolean add, boolean exists) throws SQLException {
		super(name);
		System.out.println(Arrays.toString(objs));
		setContent(objs);
		this.add = add;
		this.ifNotExists = exists;
		this.ifExists = exists;
	}
	
	@Override
	public void setContent(Object[] objs) throws SQLException {
		try {
			refSchema = (String) objs[0];
			refTable = (String) objs[1];
			refColumn = (String) objs[2];
			schema = (String) objs[3];
			table = (String) objs[4];
			column = (String) objs[5];
			onUpdate = (short) objs[6];
			onDelete = (short) objs[7];
		}
		catch(Exception e) {
			throw new SQLException(e);
		}
	}
	
	public String getTableName() {
		return schema + "." + table;
	}
	
	@Override
	public void getSQLText(StringBuffer sb, char e) throws SQLException {
		if(add) {
			sb.append("ADD CONSTRAINT ");
		}
		else {
			sb.append("DROP CONSTRAINT ");
		}
		if(add & ifNotExists) {
			sb.append("IF NOT EXISTS ");
		}
		if((!add) & ifExists) {
			sb.append("IF EXISTS ");
		}
		DBConnector.envelope(sb, null, this.getName(), e);
		if(add) {
			sb.append(" FOREIGN KEY (");
			DBConnector.envelope(sb, null, column, e);
			sb. append(") REFERENCES ");
			DBConnector.envelope(sb, null, refSchema + "." + refTable, e);
			sb.append(" (");
			DBConnector.envelope(sb, null, refColumn, e);
			sb.append(')');
			sb.append(" ON DELETE ");
			sb.append(getAction(onDelete));
			sb.append(" ON UPDATE ");
			sb.append(getAction(onUpdate));
		}
	}

	private static String getAction(int action) throws SQLException {
		switch(action) {
		case CASCADE:
			return CASCADE_SQL;
		case RESTRICT:
			return RESTRICT_SQL;
		case SET_NULL:
			return SET_NULL_SQL;
		case SET_DEFAULT:
			return SET_DEFAULT_SQL;
		default:
			throw new SQLException("Referential action is not supported");
		}
	}

}
