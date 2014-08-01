package com.acepricot.finance.sync.sql;

import java.sql.SQLException;

public interface SQLSyntax {
	public String toSQLString() throws SQLException;
}
