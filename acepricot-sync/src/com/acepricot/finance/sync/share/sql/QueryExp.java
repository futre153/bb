package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public class QueryExp extends SQLSyntaxImpl {
	
	protected QueryExp queryExp;
	protected QueryTerm queryTerm;
	private boolean union = false;
	private boolean except = false;
	private boolean all = false;
	
	
	public QueryExp(boolean union, boolean except, boolean all, SQLSyntaxImpl ...impls) {
		super(impls);
		this.union = union;
		this.except = except;
		this.all = all;
	}
	
	public QueryExp(SQLSyntaxImpl ...s) {
		super(s);
	}

	public String toSQLString() throws SQLException {
		if(queryTerm == null) {
			throw new SQLException("Query term cannot be null in context of query expression");
		}
		return (((union | except) && (queryExp != null)) ? queryExp.toSQLString() + " " + (union ? "UNION " : "EXCEPT ") + (all ? "ALL " : EMPTY) : EMPTY) + queryTerm.toSQLString();
	}
	

}
