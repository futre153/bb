package com.acepricot.finance.sync.sql;

public class ParameterName extends SQLSyntaxImpl {
	private Identifier identifier;
	public String toString () {
		return identifier == null || identifier.toString() == null ? "?" : ":" + identifier.toString(); 
	}
}
