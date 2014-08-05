package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public class ColumnSpec extends SQLSyntaxImpl {
	
	private SQLSyntaxImpl sQLSyntaxImpl;
	private Identifier identifier;
	
	@Override
	public String toSQLString() throws SQLException {
		return (sQLSyntaxImpl == null ? EMPTY : sQLSyntaxImpl.toSQLString() + ".") + identifier.toSQLString();
	}

}
