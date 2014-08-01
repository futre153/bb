package com.acepricot.finance.sync.sql;

import java.math.BigDecimal;

public class FixedPointLiteral extends SQLSyntaxImpl {
	private BigDecimal value = BigDecimal.ZERO;
	
	public FixedPointLiteral(double d) {
		value = new BigDecimal(Math.abs(d));
	}

	@Override
	public String toSQLString() {
		return value.toString();
	}
	
}
