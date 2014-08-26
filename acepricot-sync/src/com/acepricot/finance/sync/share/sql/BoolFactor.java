package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public class BoolFactor extends SQLSyntaxImpl {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean not = false;
	protected Predicate predicate;
	protected SearchCon searchCon;
	
	protected BoolFactor(boolean not, SQLSyntaxImpl ... objs) {
		super(objs);
		this.not = not;
	}

	public BoolFactor(SQLSyntaxImpl ...s) {
		super(s);
	}

	@Override
	public String toSQLString(PreparedBuffer psb) throws SQLException {
		if(predicate == null && searchCon == null) {
			throw new SQLException("predicate and search conditions cannot be null both in context of boolean factor");
		}
		return (not ? "NOT " : EMPTY) + (predicate != null ? predicate.toSQLString(psb) : ("(" + searchCon.toSQLString(psb)) + ")");
	}
}
