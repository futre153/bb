package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public class SoundsPred extends Predicate {
	boolean not = false;
	boolean like = false;
	public SoundsPred(boolean not, boolean like, Object ...objs) throws SQLException {
		super(objs);
		if(objs.length != 2) {
			throw new SQLException("Sounds predicate must have two expressions");
		}
		this.not = not;
		this.like = like;
	}
	
	public String toSQLString() throws SQLException {
		return Predicate.toSQLString(psb, objs[0]) + (not ? " NOT" : EMPTY) + " SOUNDS " + (like ? "LIKE " : EMPTY) + Predicate.toSQLString(psb, objs[1]);
	}
}
