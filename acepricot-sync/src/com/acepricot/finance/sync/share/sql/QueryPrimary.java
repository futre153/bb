package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public class QueryPrimary extends SQLSyntaxImpl {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected QuerySpec querySpec;
	protected QueryExp queryExp;
	
	public QueryPrimary(SQLSyntaxImpl ...s) {
		super(s);
	}
	@Override
	public String toSQLString() throws SQLException {
		if(querySpec == null && queryExp == null) {
			throw new SQLException("Query apecification and query expression cannot be null both");
		}
		return querySpec == null ? "(" + queryExp.toSQLString() + ")" : querySpec.toSQLString();
	}
	
}
