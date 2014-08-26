package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public class GroupClause extends SQLSyntaxImpl {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Object[] exps; 
	
	public GroupClause(Object ... objs) throws SQLException {
		if(objs.length < 1) {
			throw new SQLException("Group clause must have at least one expression");
		}
		exps = objs;
	}
	
	@Override
	public String toSQLString(PreparedBuffer psb) throws SQLException {
		return "GROUP BY " + Predicate.join(psb, exps);
	}

}
