package org.pabk.sql.lang.sap;

import java.sql.SQLException;

public class BoolPred extends Predicate {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean not = false;
	private boolean t = true;
	private boolean d = false;
	
	public BoolPred(boolean t, Object ... objs) {
		this(false, t, objs);
	}
	
	public BoolPred(boolean not, boolean t, Object ... objs) {
		super(objs);
		this.not = not;
		this.t = t;
		d = true;
	}
	
	public String toSQLString(PreparedBuffer psb) throws SQLException {
		super.toSQLString(psb);
		Predicate.checkClass(this, ColumnSpec.class);
		return ((ColumnSpec) objs[0]).toSQLString(psb) + (d ? " IS " + (not ? "NOT " : EMPTY) + (t ? " TRUE" : " FALSE") : EMPTY);
	}
	
}
