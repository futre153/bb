package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public class ParameterName extends SQLSyntaxImpl {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Identifier identifier;
	public String toSQLString (PreparedBuffer psb) throws SQLException {
		return identifier == null || identifier.toSQLString(psb) == null ? "?" : ":" + identifier.toSQLString(psb); 
	}
}
