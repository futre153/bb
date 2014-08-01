package com.acepricot.finance.sync.sql;

import java.sql.SQLException;

public class TableName extends SQLSyntaxImpl {
	private SchemaName schemaName;
	private Identifier identifier;
	
	
	public String toSQLString() throws SQLException {
		if(identifier == null) {
			throw new SQLException ("Table name identifier cannot be null");
		}
		return schemaName == null ? identifier.toSQLString() : schemaName.toSQLString() + "." + identifier.toSQLString();
	}
}
