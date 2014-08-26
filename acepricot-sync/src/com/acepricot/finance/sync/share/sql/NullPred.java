package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public class NullPred extends Predicate {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean not = false;
	public NullPred(boolean not, Object ...objs) throws SQLException {
		super(objs);
		if(objs.length != 1) {
			throw new SQLException("Null predicate must have only one expression");
		}
		this.not = not;
	}
	
	public String toSQLString(PreparedBuffer psb) throws SQLException {
		super.toSQLString(psb);
		return Predicate.toSQLString(psb, objs[0]) + (not ? " NOT" : EMPTY + " NULL");
	}
}
