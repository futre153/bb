package org.pabk.emanager.sql.sap;

import java.sql.SQLException;

public class FromClause extends SQLSyntaxImpl {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SQLSyntaxImpl tableName;
	private Identifier ref;
	
	public FromClause(SQLSyntaxImpl tableName, Identifier ref) {
		this.tableName = tableName;
		this.ref = ref;
	}
	
	public FromClause(SQLSyntaxImpl tableName) {
		this(tableName, null);
	}
	
	@Override
	public String toSQLString(PreparedBuffer psb) throws SQLException {
		if(tableName == null || ((tableName instanceof FromTableSpec) && (!((FromTableSpec) tableName).hasLength()))) {
			throw new SQLException("Table name cannot be null");
		}
		if(!((tableName instanceof Identifier) || (tableName instanceof TableName) || (tableName instanceof JoinedTable) || (tableName instanceof FromTableSpec))) {
			throw new SQLException("Table name cannot be instance of " + tableName.getClass().getSimpleName());
		}
		return (tableName instanceof QueryExp ? "(" + tableName.toSQLString(psb) + ")" : tableName.toSQLString(psb)) + (ref == null ? EMPTY : (((tableName instanceof JoinedTable) || (tableName instanceof FromTableSpec)) ? EMPTY : (" AS " + ref.toSQLString(psb))));
	}
	
	
		
}
