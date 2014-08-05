package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;


public class Select extends SQLSyntaxImpl {
	protected QueryExp queryExp;
	protected OrderClause orderClause;
	protected LimitClause limitClause;
	protected UpdateClause updateClause;
	protected LockOption lockOption;
	private boolean forReuse = false;
	
	public Select(boolean reuse, SQLSyntaxImpl ...impls) {
		super(impls);
		forReuse = reuse;
	}
	public Select(SQLSyntaxImpl ...impls) {
		super(impls);
	}
	
	@Override
	public String toSQLString() throws SQLException {
		if(queryExp == null) {
			throw new SQLException("Query expression cannot be null in context of select statement");
		}
		return queryExp.toSQLString() + (orderClause == null ? EMPTY : " " + orderClause.toSQLString())
				+ (limitClause == null ? EMPTY : " " + limitClause.toSQLString())
				+ (updateClause == null ? EMPTY : " " + updateClause.toSQLString())
				+ (lockOption == null ? EMPTY : " " + lockOption.toSQLString())
				+ (forReuse ? " FOR REUSE" : EMPTY);
	}
	
}
