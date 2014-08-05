package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public class Identifier extends SQLSyntaxImpl {
	
	private static String LETTER = "[a-zA-Z]";
	private static String EXTENDED_LETTER = "[\\#\\@\\$]";
	
	//private static String SPECIFIC_CHARACTER = null;
	private static String DIGIT = "[0-9]";
	private static String SIMPLE_ID_MASK = LETTER + "(" + LETTER + "|" + EXTENDED_LETTER + "|" + DIGIT + ")*";
	
	private String identifier;
	
	public Identifier(String id) throws SQLException {
		if(id == null) {
			throw new SQLException("Identifier cannot be null value");
		}
		identifier = id;
		if(!id.matches(SIMPLE_ID_MASK)) {
			identifier = "\"" + identifier + "\"";
		}
	}
	
	public String toSQLString() {
		return identifier;
	}
}
