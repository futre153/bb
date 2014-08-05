package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public class SearchCon extends SQLSyntaxImpl {
	protected BoolTerm boolTerm;
	protected SearchCon searchCon;
	public SearchCon(SQLSyntaxImpl ...s) {
		super(s);
	}
	@Override
	public String toSQLString() throws SQLException {
		if(boolTerm == null) {
			throw new SQLException("Boolean term cannot be null under search condition");
		}
		return (searchCon == null ? EMPTY : searchCon.toSQLString() + " OR ") + boolTerm.toSQLString();
	}
		
}
