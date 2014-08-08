package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public class DecCursor extends SQLSyntaxImpl {
	
	protected Identifier identifier;
	protected Select select;
	
	@Override
	public String toSQLString() throws SQLException {
		if(select == null) {
			throw new SQLException("Select cannot be null in context of declared cursor statement");
		}
		if( identifier == null) {
			throw new SQLException("Identifier cannot be null in context of declared cursor statement");
		}
		return "DECLARE " + identifier.toSQLString() + " CURSOR FOR " + select.toSQLString();
	}

}
