package com.acepricot.finance.sync.share.sql;

public class TopSpec extends SQLSyntaxImpl {
	
	private static final String TOP = "TOP ";
	private UnsInt unsInt;
	private ParameterName parameterName;
	
	public String toSQLString() {
		return unsInt == null ? (parameterName == null ? EMPTY : TOP + parameterName.toString()) : TOP + unsInt.toString();
	}
	
}
