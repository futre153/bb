package org.pabk.sql.lang.sap;

import java.sql.SQLException;

abstract class RowNoExp extends SQLSyntaxImpl {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Object exp;
	
	protected RowNoExp(Object obj) {
		this.exp = obj;
	}
	
	@Override
	public String toSQLString(PreparedBuffer psb) throws SQLException {
		if(exp == null) {
			throw new SQLException("Row number cannot be null");
		}
		if(!(exp instanceof UnsInt || exp instanceof ParameterName)) {
			throw new SQLException("Row number accept only unsigned integer or parameter");
		}
		return Predicate.toSQLString(psb, exp);
	}

}
