package org.pabk.emanager.sql.sap;

import java.sql.SQLException;

public class InPred extends Predicate {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean not = false;
	private Object[] objs;	
	private Object[] objs2;
		
	public InPred(Object[] objs, Object[] objs2, boolean not) throws SQLException {
		if(objs == null || objs2 == null || objs.length ==0 || objs2.length == 0) {
			throw new SQLException ("In expressions cannot be null");
		}
		if(objs.length > objs2.length) {
			throw new SQLException ("In expressions must have equal or less count of element on left side");
		}
		if(!((objs2.length ==1) && (objs2[0] instanceof QueryExp))) {
			throw new SQLException ("If expression on figth side is Query expression than right side mus have only one expression");
		}
		this.objs = objs;
		this.objs2 = objs2;
		this.not = not;
	}
	
	public String toSQLString(PreparedBuffer psb) throws SQLException {
		super.toSQLString(psb);
		return Predicate.join(psb, objs) + (not ? EMPTY : " NOT") + " IN " + (objs.length != objs2.length ? "(" : EMPTY) + Predicate.join(psb, objs2) + (objs.length != objs2.length ? ")" : EMPTY); 
	}
}
