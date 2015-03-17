package org.pabk.emanager.sql.sap;

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
	private PreparedBuffer psb;
	
	public Delete (SQLSyntaxImpl ...impls) throws SQLException {
		super(impls);
		psb = new PreparedBuffer();
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
	
	public PreparedBuffer getPreparedBuffer() {
		return psb;
	}
	@Override
	public String toSQLString(PreparedBuffer psb) throws SQLException {
		if(psb == null) {
			psb = this.getPreparedBuffer();
		}
		try {
			return "DELETE " + (from ? " FROM " : EMPTY) + tableName.toSQLString(psb) +
			(identifier == null ? EMPTY : identifier.toSQLString(psb)) +
			(whereCurrent == null ? EMPTY : (" WHERE CURRENT OF " + whereCurrent.toSQLString(psb))) +
			(whereClause == null ? EMPTY : (whereCurrent == null ? (" " + whereClause.toSQLString(psb)) : EMPTY)) +
			(ignoreTrigger ? (whereCurrent == null ? " IGNORE TRIGGER" : EMPTY) : EMPTY) + (nowait ? " NOWAIT" : EMPTY);
		}
		catch(Exception e) {
			throw new SQLException(e);
		}
	}
}
