package com.acepricot.finance.sync.share.sql;

public class TopSpec extends SQLSyntaxImpl {
	
	private static final String TOP = "TOP ";
	protected UnsInt unsInt;
	protected ParameterName parameterName;
	public TopSpec (SQLSyntaxImpl ...impls) {
		super(impls);
	}
	
	public String toSQLString() {
		return unsInt == null ? (parameterName == null ? EMPTY : TOP + parameterName.toString()) : TOP + unsInt.toString();
	}
	
}
