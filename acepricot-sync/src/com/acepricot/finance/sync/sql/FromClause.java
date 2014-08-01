package com.acepricot.finance.sync.sql;

import java.sql.SQLException;

public class FromClause extends SQLSyntaxImpl {
	private SQLSyntaxImpl sQLSyntaxImpl;
	private Identifier indetifier;
	/*
	public FromClause(SQLSyntaxImpl table, Identifier ref) {
		sQLSyntaxImpl = table;
		indetifier = ref;
	}
	*/
	@Override
	public String toSQLString() throws SQLException {
		if(sQLSyntaxImpl == null) {
			throw new SQLException("Table name cannot be null");
		}
		return (sQLSyntaxImpl instanceof QueryExp ? "(" + sQLSyntaxImpl.toSQLString() + ")" : sQLSyntaxImpl.toSQLString()) +
				(indetifier == null && sQLSyntaxImpl instanceof JoinedTable ? EMPTY : indetifier.toSQLString());
	}
	
	
		
}
