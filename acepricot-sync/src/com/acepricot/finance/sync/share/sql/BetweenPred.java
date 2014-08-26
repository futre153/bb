package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public class BetweenPred extends Predicate {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean not;
	
	public BetweenPred(boolean not, Object ... objs) {
		super(objs);
		this.not = not;
	}
	
	@Override
	public String toSQLString(PreparedBuffer psb) throws SQLException {
		super.toSQLString(psb);
		if(objs.length != 3) {
			throw new SQLException ("Predicate between requires three expressions");
		}
		return SQLSyntaxImpl.toSQLString(psb, objs[0]) + (not ? " NOT " : " ") + "BETWEEN "+ SQLSyntaxImpl.toSQLString(psb, objs[1]) + " AND " + SQLSyntaxImpl.toSQLString(psb, objs[2]);
	}
	
}
