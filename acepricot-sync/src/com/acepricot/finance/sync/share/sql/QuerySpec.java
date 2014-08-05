package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public class QuerySpec extends SQLSyntaxImpl {
	protected DistinctSpec distinctSpec;
	protected TopSpec topSpec;
	protected SelectColumn selectColumn;
	protected TableExp tableExp;
	public QuerySpec(SQLSyntaxImpl ...s) {
		super(s);
	}
	@Override
	public String toSQLString() throws SQLException {
		if(selectColumn == null) {
			throw new SQLException ("Columns specification cannot be null on query specification");
		}
		if(tableExp == null) {
			throw new SQLException ("Table specification cannot be null on query specification");
		}
		return "SELECT " + (distinctSpec == null ? EMPTY : distinctSpec.toSQLString() + " ") + (topSpec == null ? EMPTY : topSpec.toSQLString() + " ") + selectColumn.toSQLString() + " " + tableExp.toSQLString();
	}
}
