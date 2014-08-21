package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public class Delete extends SQLSyntaxImpl {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected TableName tableName;
	protected Identifier identifier;
	private Identifier whereCurrent;
	protected WhereClause whereClause;
	private boolean from = false;
	private boolean ignoreTrigger = false;
	private boolean nowait = false;
	
	public Delete (SQLSyntaxImpl ...impls) throws SQLException {
		super(impls);
	}
		
	public final void setWhereCurrent(Identifier whereCurrent) {
		this.whereCurrent = whereCurrent;
	}

	final void setIgnoreTrigger(boolean ignoreTrigger) {
		this.ignoreTrigger = ignoreTrigger;
	}

	final void setNowait(boolean nowait) {
		this.nowait = nowait;
	}
	
	
	@Override
	public String toSQLString() throws SQLException {
		try {
			return "DELETE " + (from ? " FROM " : EMPTY) + tableName.toSQLString() +
			(identifier == null ? EMPTY : identifier.toSQLString()) +
			(whereCurrent == null ? EMPTY : (" WHERE CURRENT OF " + whereCurrent.toSQLString())) +
			(whereClause == null ? EMPTY : (whereCurrent == null ? (" " + whereClause.toSQLString()) : EMPTY)) +
			(ignoreTrigger ? (whereCurrent == null ? " IGNORE TRIGGER" : EMPTY) : EMPTY) + (nowait ? " NOWAIT" : EMPTY);
		}
		catch(Exception e) {
			throw new SQLException(e);
		}
	}
}
