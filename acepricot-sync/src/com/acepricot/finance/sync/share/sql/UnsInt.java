package com.acepricot.finance.sync.share.sql;

public class UnsInt extends SQLSyntaxImpl {
	private FixedPointLiteral fixedPointLiteral;
	private FloatingPointLiteral floatingPointLiteral;
	@Override
	public String toSQLString () {
		return fixedPointLiteral == null ? (fixedPointLiteral == null ? EMPTY : floatingPointLiteral.toString()) : fixedPointLiteral.toString();
	} 
}
