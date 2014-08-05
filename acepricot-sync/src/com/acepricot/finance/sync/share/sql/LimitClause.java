package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public class LimitClause extends SQLSyntaxImpl {
	
	private RowCount rowCount;
	private Offset offset;
	
	@Override
	public String toSQLString() throws SQLException {
		if(rowCount == null) {
			throw new SQLException("Row number cannot be null in contaxt of limit clause");
		}
		return "LIMIT " + (offset == null ? EMPTY : offset.toSQLString() + ",") + rowCount.toSQLString();
	}

}
