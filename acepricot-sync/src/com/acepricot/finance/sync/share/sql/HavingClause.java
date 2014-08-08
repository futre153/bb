package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public class HavingClause extends WhereClause {

	public HavingClause(Object[] objs) throws SQLException {
		super(objs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toSQLString() throws SQLException {
		return super.toSQLString().replaceFirst("WHERE", "HAVING");
	}

}
