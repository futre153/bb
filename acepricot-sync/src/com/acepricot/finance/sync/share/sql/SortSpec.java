package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public class SortSpec extends SQLSyntaxImpl {
	
	private Object exp;
	private boolean display = false;
	private boolean asc = true;
		
	public SortSpec(Object exp) {
		this.display = false;
		this.exp = exp;
	}
	
	public SortSpec(boolean asc, Object exp) {
		this.display = true;
		this.asc = asc;
		this.exp = exp;
	}
	
	@Override
	public String toSQLString() throws SQLException {
		return Predicate.toSQLString(psb, exp) + (display ? (asc ? " ASC" : " DESC") : EMPTY);
	}

	public boolean hasColumnSpec() {
		return exp instanceof ColumnSpec;
	}

}
