package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public class TableExp extends SQLSyntaxImpl {
	
	private FromClause[] fromClause;
	protected WhereClause whereClause;
	protected GroupClause groupClause;
	protected HavingClause havingClause;
	
	public TableExp(WhereClause where, GroupClause group, HavingClause having, FromClause ...clauses) {
		this.whereClause = where;
		this.havingClause = having;
		this.groupClause = group;
		this.fromClause = clauses;
	}
	
	@Override
	public String toSQLString() throws SQLException {
		if(fromClause == null || fromClause.length < 1) {
			throw new SQLException("Table expression must contains at least one table name definition"); 
		}
		return "FROM " + Predicate.join(fromClause) + (whereClause == null ? EMPTY : " " + whereClause.toSQLString()) + (groupClause == null ? EMPTY : " " + groupClause.toSQLString()) + (havingClause == null ? EMPTY : " " + havingClause.toSQLString());
	}

}
