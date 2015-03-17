package org.pabk.sql.lang.sap;

import java.sql.SQLException;

public class TableExp extends SQLSyntaxImpl {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private FromClause[] fromClause;
	protected WhereClause whereClause;
	protected GroupClause groupClause;
	protected HavingClause havingClause;
	
	public TableExp(SQLSyntaxImpl ...impls) {
		super(impls);
	}
	
	void addFromClause(FromClause ...froms) {
		if(fromClause == null) {
			fromClause = froms;
		}
		else {
			FromClause[] _new  = new FromClause[fromClause.length + froms.length];
			System.arraycopy(fromClause, 0, _new, 0, fromClause.length);
			System.arraycopy(froms, 0, _new, fromClause.length, froms.length);
			fromClause = null;
			fromClause = _new;
		}
	}
	
	public TableExp(WhereClause where, GroupClause group, HavingClause having, FromClause ...clauses) {
		this.whereClause = where;
		this.havingClause = having;
		this.groupClause = group;
		this.fromClause = clauses;
	}
	
	@Override
	public String toSQLString(PreparedBuffer psb) throws SQLException {
		if(fromClause == null || fromClause.length < 1) {
			throw new SQLException("Table expression must contains at least one table name definition"); 
		}
		return "FROM " + Predicate.join(psb, fromClause) + (whereClause == null ? EMPTY : " " + whereClause.toSQLString(psb)) + (groupClause == null ? EMPTY : " " + groupClause.toSQLString(psb)) + (havingClause == null ? EMPTY : " " + havingClause.toSQLString(psb));
	}

}
