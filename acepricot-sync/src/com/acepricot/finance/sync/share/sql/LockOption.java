package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public class LockOption extends SQLSyntaxImpl {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean ignore = false;
	private boolean nowait = false;
	private boolean exclusive = false;
	private boolean optimistic = false;
	private UnsInt unsInt;
	
	public LockOption(boolean ignore, boolean nowait, boolean exclusive, boolean optimistic, UnsInt level) {
		this.ignore = ignore;
		this.nowait = nowait;
		this.exclusive = exclusive;
		this.optimistic = optimistic;
		unsInt = level;
	}
	
	@Override
	public String toSQLString(PreparedBuffer psb) throws SQLException {
		if(unsInt != null && (!Predicate.isLockLevel(unsInt.toSQLString(psb)))) {
			throw new SQLException ("Lock level " + unsInt.toSQLString(psb) + " is nor allowed");
		}
		return "WITH LOCK" + (ignore | nowait ? (ignore ? " IGNORE" : " NOWAIT") : EMPTY) + (exclusive | optimistic ? (exclusive ? " EXCLUSIVE" : " OPTIMISTIC") : EMPTY) + (unsInt != null ? " ISOLATION LEVEL " + unsInt.toSQLString(psb) : EMPTY);
	}

}
