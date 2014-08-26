package com.acepricot.finance.sync.share.sql;

import java.io.Serializable;
import java.sql.SQLException;

public interface SQLSyntax extends Serializable {
	public String toSQLString(PreparedBuffer psb) throws SQLException;
}
