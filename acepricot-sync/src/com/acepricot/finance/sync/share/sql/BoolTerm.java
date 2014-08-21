package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public class BoolTerm extends SQLSyntaxImpl {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected BoolFactor boolFactor;
	protected BoolTerm boolTerm;
	public BoolTerm(SQLSyntaxImpl ...s) {
		super(s);
	}
	@Override
	public String toSQLString() throws SQLException {
		if(boolFactor == null) {
			throw new SQLException("Boolean factor cannot be null under boolean term");
		}
		return (boolTerm == null ? EMPTY : boolTerm.toSQLString() + " AND ") + boolFactor.toSQLString();
	}
	public BoolTerm getFreeBoolTerm() {
		if(this.boolTerm == null) {
			return this;
		}
		else {
			return this.boolTerm.getFreeBoolTerm();
		}
	}
}
