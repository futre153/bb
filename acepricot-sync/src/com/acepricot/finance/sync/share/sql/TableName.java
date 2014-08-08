package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public class TableName extends SQLSyntaxImpl {
	protected SchemaName schemaName;
	protected Identifier identifier;
	
	public TableName(SQLSyntaxImpl ...impls) {
		super(impls);
	}
	
	public String toSQLString() throws SQLException {
		if(identifier == null) {
			throw new SQLException ("Table name identifier cannot be null");
		}
		return schemaName == null ? identifier.toSQLString() : schemaName.toSQLString() + "." + identifier.toSQLString();
	}
}
