package org.pabk.sql.lang.sap;

import java.sql.SQLException;


public class Select extends SQLSyntaxImpl {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected QueryExp queryExp;
	protected OrderClause orderClause;
	protected LimitClause limitClause;
	protected UpdateClause updateClause;
	protected LockOption lockOption;
	private boolean forReuse = false;
	private PreparedBuffer psb = new PreparedBuffer();
	public PreparedBuffer getPreparedBuffer() {
		return psb;
	}
	public Select(boolean reuse, SQLSyntaxImpl ...impls) {
		psb = new PreparedBuffer();
		forReuse = reuse;
	}
	public Select(SQLSyntaxImpl ...impls) {
		super(impls);
	}
	
	@Override
	public String toSQLString(PreparedBuffer psb) throws SQLException {
		if(queryExp == null) {
			throw new SQLException("Query expression cannot be null in context of select statement");
		}
		return queryExp.toSQLString(psb) + (orderClause == null ? EMPTY : " " + orderClause.toSQLString(psb))
				+ (limitClause == null ? EMPTY : " " + limitClause.toSQLString(psb))
				+ (updateClause == null ? EMPTY : " " + updateClause.toSQLString(psb))
				+ (lockOption == null ? EMPTY : " " + lockOption.toSQLString(psb))
				+ (forReuse ? " FOR REUSE" : EMPTY);
	}
	
}
