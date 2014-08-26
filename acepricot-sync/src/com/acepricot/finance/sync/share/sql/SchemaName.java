package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public class SchemaName extends SQLSyntaxImpl {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Identifier identifier;
	
	public SchemaName(SQLSyntaxImpl...impls) {
		super(impls);
	}

	public String toSQLString(PreparedBuffer psb) throws SQLException {
		return identifier == null ? EMPTY: identifier.toSQLString(psb);
	}
}
