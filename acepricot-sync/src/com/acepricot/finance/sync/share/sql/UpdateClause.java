package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public class UpdateClause extends SQLSyntaxImpl {
	
	private ColumnSpec[] cols;
	boolean nowait = false;
	
	public UpdateClause(ColumnSpec ...specs) {
		this(false, specs);
	}
	
	public UpdateClause(boolean nowait, ColumnSpec ...specs) {
		cols = specs;
		this.nowait = nowait;
	}
	
	public String toSQLString() throws SQLException {
		return "FOR UPDATE" + ((cols != null && cols.length > 0) ? " OF " + Predicate.join(psb, cols) : EMPTY) + (nowait ? " NOWAIT" : EMPTY);
	}

}
