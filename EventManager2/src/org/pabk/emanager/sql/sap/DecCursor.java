package org.pabk.emanager.sql.sap;

import java.sql.SQLException;

public class DecCursor extends SQLSyntaxImpl {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Identifier identifier;
	protected Select select;
	
	@Override
	public String toSQLString(PreparedBuffer psb) throws SQLException {
		if(select == null) {
			throw new SQLException("Select cannot be null in context of declared cursor statement");
		}
		if( identifier == null) {
			throw new SQLException("Identifier cannot be null in context of declared cursor statement");
		}
		return "DECLARE " + identifier.toSQLString(psb) + " CURSOR FOR " + select.toSQLString(psb);
	}

}
