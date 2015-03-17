package org.pabk.emanager.sql.sap;

import java.sql.SQLException;

public class QuerySpec extends SQLSyntaxImpl {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected DistinctSpec distinctSpec;
	protected TopSpec topSpec;
	protected SelectColumn selectColumn;
	protected TableExp tableExp;
	public QuerySpec(SQLSyntaxImpl ...s) {
		super(s);
	}
	@Override
	public String toSQLString(PreparedBuffer psb) throws SQLException {
		if(selectColumn == null) {
			throw new SQLException ("Columns specification cannot be null on query specification");
		}
		if(tableExp == null) {
			throw new SQLException ("Table specification cannot be null on query specification");
		}
		return "SELECT " + (distinctSpec == null ? EMPTY : distinctSpec.toSQLString(psb) + " ") + (topSpec == null ? EMPTY : topSpec.toSQLString(psb) + " ") + selectColumn.toSQLString(psb) + " " + tableExp.toSQLString(psb);
	}
}
