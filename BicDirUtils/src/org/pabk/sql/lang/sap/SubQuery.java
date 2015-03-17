package org.pabk.sql.lang.sap;

import java.sql.SQLException;

public class SubQuery extends Query {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String toSQLString(PreparedBuffer psb) throws SQLException {
		return "(" + super.toSQLString(psb) + ")";
	}
}
