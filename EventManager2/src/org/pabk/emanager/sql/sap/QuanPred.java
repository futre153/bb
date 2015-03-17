package org.pabk.emanager.sql.sap;

import java.sql.SQLException;

public class QuanPred extends Predicate {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Object[] objs;
	private Object[] objs2;
	private String comp;
	private String quantifier;
	
	public QuanPred(Object[] objs, Object[] objs2, String comp, String quan) throws SQLException {
		if(objs == null || objs2 == null || objs.length ==0 || objs2.length == 0) {
			throw new SQLException ("Comparison expressions cannot be null");
		}
		if(objs.length != objs2.length) {
			throw new SQLException ("Comparison expressions must have equal count on left and right side");
		}
		if(!((objs2.length ==1) && (objs2[0] instanceof QueryExp))) {
			throw new SQLException ("If expression on figth side is Query expression than right side mus have only one expression");
		}
		Predicate.checkCopmarison(comp);
		if(!(objs.length > 1 && Predicate.isEqualOrNot(comp))) {
			throw new SQLException("Comparison operator " + comp + " is not allowed for multiple expressions");
		}
		Predicate.checkQuantifier(quan);
		this.objs = objs;
		this.objs2 = objs2;
		this.comp = comp;
		this.quantifier = quan;
	}
	
	public String toSQLString(PreparedBuffer psb) throws SQLException {
		super.toString();
		return Predicate.join(psb, objs) + " " + comp + " " + quantifier + " " + Predicate.join(psb, objs2); 
	}
	
}