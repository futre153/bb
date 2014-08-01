package com.acepricot.finance.sync.sql;

import java.sql.SQLException;

public class ExistsPred extends Predicate {
	
	public String toSQLString() throws SQLException {
		Predicate.checkClass(this, QueryExp.class);
		return "EXISTS " + Predicate.join(objs);
	}
	
}
