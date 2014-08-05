package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public class QueryTerm extends SQLSyntaxImpl {

	protected QueryTerm queryTerm;
	protected QueryPrimary queryPrimary;
	boolean all = false;
	
	public QueryTerm(boolean all, SQLSyntaxImpl ...impls) {
		super(impls);
		this.all = all;
	}

	public QueryTerm(SQLSyntaxImpl ...s) {
		super(s);
	}

	@Override
	public String toSQLString() throws SQLException {
		if(queryPrimary == null) {
			throw new SQLException("Primary query cannot be null");
		}
		return (queryTerm == null ? EMPTY : (queryTerm.toSQLString() + " INTERSECT ")) + (all ? "ALL " : EMPTY) + queryPrimary.toSQLString();
	}
}
