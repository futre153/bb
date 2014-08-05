package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public class RownoPred extends Predicate {
	boolean equ = false;
	public RownoPred(boolean equ, Object ...objs) throws SQLException {
		super(objs);
		if(objs.length != 1) {
			throw new SQLException("Roqno predicate must have only one xpression");
		}
		if(!(objs[0] instanceof UnsInt || objs[0] instanceof ParamSpec)) {
			throw new SQLException ("Rowno predicate accept only unsigned integer or parameter");
		}
		this.equ = equ;
	}
	
	public String toSQLString() throws SQLException {
		return "ROWNO <" + (equ ? "=" : EMPTY) + Predicate.toSQLString(objs[0]) ;
	}
}
