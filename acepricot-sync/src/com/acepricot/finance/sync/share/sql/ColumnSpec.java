package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public class ColumnSpec extends SQLSyntaxImpl {
	
	protected SQLSyntaxImpl sQLSyntaxImpl;
	protected Identifier identifier;
	
	public ColumnSpec(SQLSyntaxImpl ...impls) {
		super(impls);
	}
	
	public ColumnSpec(Identifier ref, Identifier name) {
		sQLSyntaxImpl = ref;
		identifier = name;
	} 
	
	@Override
	public String toSQLString() throws SQLException {
		if(identifier == null) {
			throw new SQLException("Identifier cannot be null in context of column specification");
		}
		return (sQLSyntaxImpl == null ? EMPTY : sQLSyntaxImpl.toSQLString() + ".") + identifier.toSQLString();
	}

}
