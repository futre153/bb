package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public class ColumnSpec extends SQLSyntaxImpl {
	
	protected SQLSyntaxImpl sQLSyntaxImpl;
	protected Identifier identifier;
	
	@Override
	public String toSQLString() throws SQLException {
		if(identifier == null) {
			throw new SQLException("Identifier cannot be null in context of column specification");
		}
		return (sQLSyntaxImpl == null ? EMPTY : sQLSyntaxImpl.toSQLString() + ".") + identifier.toSQLString();
	}

}
