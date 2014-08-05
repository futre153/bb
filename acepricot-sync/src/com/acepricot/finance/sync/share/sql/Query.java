package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public class Query extends SQLSyntaxImpl {
	
	protected DecCursor decCursor;
	protected RecDecCursor recDecCursor;
	protected Select select;
	
	public Query(SQLSyntaxImpl ...s) {
		super(s);
	}

	@Override
	public String toSQLString() throws SQLException {
		if(select == null && decCursor == null && recDecCursor == null) {
			throw new SQLException("At least one of query statements must not be null");
		}
		return select == null ? (decCursor == null ? recDecCursor.toSQLString() : decCursor.toSQLString()) : select.toSQLString();
	}

}
