package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public class TopSpec extends SQLSyntaxImpl {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String TOP = "TOP ";
	protected UnsInt unsInt;
	protected ParameterName parameterName;
	public TopSpec (SQLSyntaxImpl ...impls) {
		super(impls);
	}
	
	public String toSQLString(PreparedBuffer psb) throws SQLException {
		return unsInt == null ? (parameterName == null ? EMPTY : TOP + parameterName.toSQLString(psb)) : TOP + unsInt.toSQLString(psb);
	}
	
}
