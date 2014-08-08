package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public interface SQLSyntax {
	public final PreparedBuffer psb = new PreparedBuffer();
	public String toSQLString() throws SQLException;
}
