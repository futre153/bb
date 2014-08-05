package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public interface SQLSyntax {
	public String toSQLString() throws SQLException;
}
