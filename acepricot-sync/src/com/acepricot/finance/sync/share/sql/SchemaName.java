package com.acepricot.finance.sync.share.sql;

public class SchemaName extends SQLSyntaxImpl {
	protected Identifier identifier;
	
	public SchemaName(SQLSyntaxImpl...impls) {
		super(impls);
	}

	public String toSQLString() {
		return identifier == null ? EMPTY: identifier.toSQLString();
	}
}
