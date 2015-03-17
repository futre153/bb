package org.pabk.emanager.sql.sap;

import java.sql.SQLException;

public class CompPred extends Predicate {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Object[] objs;
	private Object[] objs2;
	private String comp;
	
	public CompPred(Object[] objs, Object[] objs2, String comp) throws SQLException {
		if(objs == null || objs2 == null || objs.length ==0 || objs2.length == 0) {
			throw new SQLException ("Comparison expressions cannot be null");
		}
		if(objs.length != objs2.length) {
			throw new SQLException ("Comparison expressions must have equal count on left and right side");
		}
		if((objs2.length == 1) && (objs2[0] instanceof QueryExp)) {
			throw new SQLException ("If expression on figth side is Query expression than right side mus have only one expression");
		}
		Predicate.checkCopmarison(comp);
		if(objs.length > 1 && (!Predicate.isEqualOrNot(comp))) {
			throw new SQLException("Comparison operator " + comp + " is not allowed for multiple expressions");
		}
		this.objs = objs;
		this.objs2 = objs2;
		this.comp = comp;
	}
	
	public String toSQLString(PreparedBuffer psb) throws SQLException {
		super.toSQLString(psb);
		return (objs.length > 1 ? Predicate.join(psb, objs, '(', ')') : Predicate.join(psb, objs)) + " " + comp + " " + (objs2.length > 1 ? ("(" + Predicate.join(psb, objs2, '(', ')') + ")") : Predicate.join(psb, objs2)); 
	}
	
}
