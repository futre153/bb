package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public class SchemaName extends SQLSyntaxImpl {
	protected Identifier identifier;
	
	public SchemaName(SQLSyntaxImpl...impls) {
		super(impls);
	}

	public String toSQLString() throws SQLException {
		return identifier == null ? EMPTY: identifier.toSQLString();
	}
}
