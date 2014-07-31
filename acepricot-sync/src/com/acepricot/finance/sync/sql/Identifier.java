package com.acepricot.finance.sync.sql;

public class Identifier extends SQLSyntaxImpl {
	
	private static String LETTER = "[a-zA-Z]";
	private static String EXTENDED_LETTER = "[\\#\\@\\$]";
	
	//private static String SPECIFIC_CHARACTER = null;
	private static String DIGIT = "[0-9]";
	private static String SIMLE_ID_MASK = LETTER + "(" + LETTER + "|" + EXTENDED_LETTER + "|" + DIGIT + ")";
	
	private String identifier;
	
	public Identifier(String id) {
		identifier = id;
		if(!id.matches(SIMLE_ID_MASK)) {
			identifier = "\"" + identifier + "\"";
		}
	}
	
	public String toString() {
		return identifier;
	}
}
