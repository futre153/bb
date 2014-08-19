package com.acepricot.finance.sync.share.sql;

import java.io.Serializable;
import java.sql.SQLException;

public interface SQLSyntax extends Serializable {
	public final PreparedBuffer psb = new PreparedBuffer();
	public String toSQLString() throws SQLException;
}
