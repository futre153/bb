package org.pabk.sql.lang.sap;

import java.sql.SQLException;

public class RownoPred extends Predicate {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean equ = false;
	public RownoPred(boolean equ, Object ...objs) throws SQLException {
		super(objs);
		if(objs.length != 1) {
			throw new SQLException("Roqno predicate must have only one xpression");
		}
		if(!(objs[0] instanceof UnsInt || objs[0] instanceof ParamSpec || objs[0] instanceof Integer)) {
			throw new SQLException ("Rowno predicate accept only unsigned integer or parameter");
		}
		this.equ = equ;
	}
	
	public String toSQLString(PreparedBuffer psb) throws SQLException {
		return "ROWNO <" + (equ ? "=" : EMPTY) + " " + Predicate.toSQLString(psb, objs[0]) ;
	}
}
