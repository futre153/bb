package org.pabk.emanager.sql.sap;

import java.sql.SQLException;

public class TableName extends SQLSyntaxImpl {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected SchemaName schemaName;
	protected Identifier identifier;
		
	public TableName(SQLSyntaxImpl ...impls) {
		super(impls);
	}
	
	public String toSQLString(PreparedBuffer psb) throws SQLException {
		if(identifier == null) {
			throw new SQLException ("Table name identifier cannot be null");
		}
		return schemaName == null ? identifier.toSQLString(psb) : schemaName.toSQLString(psb) + "." + identifier.toSQLString(psb);
	}
}
