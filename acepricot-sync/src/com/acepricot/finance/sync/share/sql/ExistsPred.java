package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public class ExistsPred extends Predicate {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String toSQLString(PreparedBuffer psb) throws SQLException {
		Predicate.checkClass(this, QueryExp.class);
		return "EXISTS " + Predicate.join(psb, objs);
	}
	
}
