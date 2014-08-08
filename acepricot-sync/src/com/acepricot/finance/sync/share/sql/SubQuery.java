package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public class SubQuery extends Query {
	public String toSQLString() throws SQLException {
		return "(" + super.toSQLString() + ")";
	}
}
