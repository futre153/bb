package com.acepricot.finance.sync.sql;

import java.sql.SQLException;

public class LikePred extends Predicate {
	boolean not = false;
	public LikePred(boolean not, Object ...objs) throws SQLException {
		super(objs);
		if(objs.length < 2 || objs.length > 3) {
			throw new SQLException("Like must have two or three expresions");
		}
		this.not = not;
	}
	
	public String toSQLString() throws SQLException {
		return Predicate.toSQLString(objs[0]) + (not ? " NOT" : EMPTY) + " LIKE " + Predicate.toSQLString(objs[1]) + (objs.length == 3 ? " ESCAPE " + Predicate.toSQLString(objs[2]) : EMPTY);
	}
}
