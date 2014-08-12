package com.acepricot.finance.sync.share.sql;

public class UnsInt extends SQLSyntaxImpl {
	protected FixedPointLiteral fixedPointLiteral;
	protected FloatingPointLiteral floatingPointLiteral;
	
	
	public UnsInt(SQLSyntaxImpl ...impls) {
		super(impls);
	}
	@Override
	public String toSQLString () {
		return fixedPointLiteral == null ? (fixedPointLiteral == null ? EMPTY : floatingPointLiteral.toSQLString()) : fixedPointLiteral.toSQLString();
	} 
}
