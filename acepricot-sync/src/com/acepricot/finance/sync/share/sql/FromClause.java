package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public class FromClause extends SQLSyntaxImpl {
	protected SQLSyntaxImpl sQLSyntaxImpl;
	protected Identifier identifier;
	
	public FromClause(SQLSyntaxImpl ...s) {
		super(s);
	}
	
	@Override
	public String toSQLString() throws SQLException {
		if(sQLSyntaxImpl == null && identifier == null) {
			throw new SQLException("Table name cannot be null");
		}
		
		return (sQLSyntaxImpl == null ? identifier.toSQLString() : (sQLSyntaxImpl instanceof QueryExp ? "(" + sQLSyntaxImpl.toSQLString() + ")" : sQLSyntaxImpl.toSQLString()));
	}
	
	
		
}
