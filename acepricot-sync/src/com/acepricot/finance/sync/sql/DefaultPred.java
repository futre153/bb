package com.acepricot.finance.sync.sql;

import java.sql.SQLException;

public class DefaultPred extends Predicate {
	private String comp;
	public DefaultPred (String comp, Object ... objs) throws SQLException {
		super(objs);
		Predicate.checkCopmarison(comp);
	}
	
	public String toSQLString() throws SQLException {
		super.toSQLString();
		Predicate.checkClass(this, ColumnSpec.class);
		return ((ColumnSpec) objs[0]).toSQLString() + " " + comp + " DEFAULT";
	}
	
}
