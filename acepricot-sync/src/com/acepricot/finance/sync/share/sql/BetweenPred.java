package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public class BetweenPred extends Predicate {
	
	private boolean not;
	
	public BetweenPred(boolean not, Object ... objs) {
		super(objs);
		this.not = not;
	}
	
	@Override
	public String toSQLString() throws SQLException {
		super.toSQLString();
		if(objs.length != 3) {
			throw new SQLException ("Predicate between requires three expressions");
		}
		return SQLSyntaxImpl.toSQLString(objs[0]) + (not ? " NOT " : " ") + SQLSyntaxImpl.toSQLString(objs[1]) + "AND " + SQLSyntaxImpl.toSQLString(objs[2]);
	}
	
}
