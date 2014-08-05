package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public class HavingClause extends WhereClause {

	@Override
	public String toSQLString() throws SQLException {
		return super.toSQLString().replaceFirst("WHERE", "HAVING");
	}

}
