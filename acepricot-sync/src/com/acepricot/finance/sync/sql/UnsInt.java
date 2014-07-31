package com.acepricot.finance.sync.sql;

public class UnsInt extends SQLSyntaxImpl {
	private FixedPointLiteral fixedPointLiteral;
	private FloatingPointLiteral floatingPointLiteral;
	@Override
	public String toString () {
		return fixedPointLiteral == null ? (fixedPointLiteral == null ? EMPTY : floatingPointLiteral.toString()) : fixedPointLiteral.toString();
	} 
}
