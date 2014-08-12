package com.acepricot.finance.sync.share.sql;

public class ParameterName extends SQLSyntaxImpl {
	protected Identifier identifier;
	public String toSQLString () {
		return identifier == null || identifier.toSQLString() == null ? "?" : ":" + identifier.toSQLString(); 
	}
}
