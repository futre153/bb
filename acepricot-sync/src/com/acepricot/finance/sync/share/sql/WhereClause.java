package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public class WhereClause extends SQLSyntaxImpl {
	
	protected SearchCon searchCon;
	
	public WhereClause(SQLSyntaxImpl ...s) {
		super(s);
	}

	@Override
	public String toSQLString() throws SQLException {
		return searchCon == null ? EMPTY : "WHERE " + searchCon.toSQLString();
	}

}
