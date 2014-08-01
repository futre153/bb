package com.acepricot.finance.sync.sql;

public class ParameterName extends SQLSyntaxImpl {
	private Identifier identifier;
	public String toSQLString () {
		return identifier == null || identifier.toSQLString() == null ? "?" : ":" + identifier.toSQLString(); 
	}
}
