package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public class Identifier extends SQLSyntaxImpl {
	
	private static String LETTER = "[a-zA-Z]";
	private static String EXTENDED_LETTER = "[\\#\\@\\$\\_]";
	
	//private static String SPECIFIC_CHARACTER = null;
	private static String DIGIT = "[0-9]";
	private static String SIMPLE_ID_MASK = LETTER + "(" + LETTER + "|" + EXTENDED_LETTER + "|" + DIGIT + ")*";
	
	private String identifier;
	
	public Identifier(String id) throws SQLException {
		if(id == null) {
			throw new SQLException("Identifier cannot be null value");
		}
		identifier = id;
	}
	
	public String getValue() {
		return identifier;
	}
	
	public String toSQLString() {
		return getValue().matches(SIMPLE_ID_MASK) ? getValue() : ("\"" + getValue() + "\"");
	}
}
