package org.pabk.emanager.sql.sap;

import java.sql.SQLException;

public class HavingClause extends WhereClause {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public HavingClause(Object[] objs) throws SQLException {
		super(objs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toSQLString(PreparedBuffer psb) throws SQLException {
		return super.toSQLString(psb).replaceFirst("WHERE", "HAVING");
	}

}
