package org.pabk.emanager.sql.sap;

import java.sql.SQLException;

public class QueryTerm extends SQLSyntaxImpl {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected QueryTerm queryTerm;
	protected QueryPrimary queryPrimary;
	boolean all = false;
	
	public QueryTerm(boolean all, SQLSyntaxImpl ...impls) {
		super(impls);
		this.all = all;
	}

	public QueryTerm(SQLSyntaxImpl ...s) {
		super(s);
	}

	@Override
	public String toSQLString(PreparedBuffer psb) throws SQLException {
		if(queryPrimary == null) {
			throw new SQLException("Primary query cannot be null");
		}
		return (queryTerm == null ? EMPTY : (queryTerm.toSQLString(psb) + " INTERSECT ")) + (all ? "ALL " : EMPTY) + queryPrimary.toSQLString(psb);
	}
}
