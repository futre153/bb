package org.pabk.sql.lang.sap;

import java.sql.SQLException;

public class BoolTerm extends SQLSyntaxImpl {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected BoolFactor boolFactor;
	protected BoolTerm boolTerm;
	public BoolTerm(SQLSyntaxImpl ...s) {
		super(s);
	}
	@Override
	public String toSQLString(PreparedBuffer psb) throws SQLException {
		if(boolFactor == null) {
			throw new SQLException("Boolean factor cannot be null under boolean term");
		}
		return (boolTerm == null ? EMPTY : boolTerm.toSQLString(psb) + " AND ") + boolFactor.toSQLString(psb);
	}
	public BoolTerm getFreeBoolTerm() {
		if(this.boolTerm == null) {
			return this;
		}
		else {
			return this.boolTerm.getFreeBoolTerm();
		}
	}
}
