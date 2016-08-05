package org.pabk.emanager.sql.sap;

import java.sql.SQLException;

public class LimitClause extends SQLSyntaxImpl {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected RowCount rowCount;
	protected Offset offset;
	
	public LimitClause (SQLSyntaxImpl ...impls) {
		super(impls);
	}
	
	@Override
	public String toSQLString(PreparedBuffer psb) throws SQLException {
		if(rowCount == null) {
			throw new SQLException("Row number cannot be null in contaxt of limit clause");
		}
		return "LIMIT " + (offset == null ? EMPTY : offset.toSQLString(psb) + ",") + rowCount.toSQLString(psb);
	}

}
