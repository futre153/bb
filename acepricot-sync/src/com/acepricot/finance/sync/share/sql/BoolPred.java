package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public class BoolPred extends Predicate {
	
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
	
	public String toSQLString() throws SQLException {
		super.toSQLString();
		Predicate.checkClass(this, ColumnSpec.class);
		return ((ColumnSpec) objs[0]).toSQLString() + (d ? " IS " + (not ? "NOT " : EMPTY) + (t ? " TRUE" : " FALSE") : EMPTY);
	}
	
}
