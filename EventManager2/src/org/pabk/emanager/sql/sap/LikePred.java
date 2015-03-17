package org.pabk.emanager.sql.sap;

import java.sql.SQLException;

public class LikePred extends Predicate {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean not = false;
	public LikePred(boolean not, Object ...objs) throws SQLException {
		super(objs);
		if(objs.length < 2 || objs.length > 3) {
			throw new SQLException("Like must have two or three expresions");
		}
		this.not = not;
	}
	
	public String toSQLString(PreparedBuffer psb) throws SQLException {
		return Predicate.toSQLString(psb, objs[0]) + (not ? " NOT" : EMPTY) + " LIKE " + Predicate.toSQLString(psb, objs[1]) + (objs.length == 3 ? " ESCAPE " + Predicate.toSQLString(psb, objs[2]) : EMPTY);
	}
}
