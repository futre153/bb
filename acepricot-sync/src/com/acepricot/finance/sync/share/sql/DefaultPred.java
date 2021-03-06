package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public class DefaultPred extends Predicate {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String comp;
	public DefaultPred (String comp, Object ... objs) throws SQLException {
		super(objs);
		Predicate.checkCopmarison(comp);
	}
	
	public String toSQLString(PreparedBuffer psb) throws SQLException {
		super.toSQLString(psb);
		Predicate.checkClass(this, ColumnSpec.class);
		return ((ColumnSpec) objs[0]).toSQLString(psb) + " " + comp + " DEFAULT";
	}
	
}
