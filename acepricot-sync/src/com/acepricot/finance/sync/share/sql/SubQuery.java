package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public class SubQuery extends Query {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String toSQLString() throws SQLException {
		return "(" + super.toSQLString() + ")";
	}
}
