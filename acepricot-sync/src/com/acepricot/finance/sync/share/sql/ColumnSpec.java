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

	public static ColumnSpec[] getColSpecArray(TableName tableName, String ...strings) {
		ColumnSpec[] cols = new ColumnSpec[strings.length];
		for(int i = 0; i < cols.length; i ++) {
			cols[i] = new ColumnSpec(tableName, new Identifier(strings[i]));
		}
		return cols;
	}

}
