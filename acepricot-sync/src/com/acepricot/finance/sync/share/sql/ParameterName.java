package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public class ParameterName extends SQLSyntaxImpl {
	protected Identifier identifier;
	public String toSQLString () throws SQLException {
		return identifier == null || identifier.toSQLString() == null ? "?" : ":" + identifier.toSQLString(); 
	}
}
