package com.acepricot.finance.sync.share.sql;

public class SchemaName extends SQLSyntaxImpl {
	private Identifier identifier;
	
	public String toSQLString() {
		return identifier == null ? EMPTY: identifier.toSQLString();
	}
}
