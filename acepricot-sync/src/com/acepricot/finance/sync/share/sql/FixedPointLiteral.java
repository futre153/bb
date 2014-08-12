package com.acepricot.finance.sync.share.sql;

import java.math.BigDecimal;

public class FixedPointLiteral extends SQLSyntaxImpl {
	private BigDecimal value = BigDecimal.ZERO;
	
	public FixedPointLiteral(double d) {
		value = new BigDecimal(Math.abs(d));
	}

	@Override
	public String toSQLString() {
		if(SQLSyntaxImpl.isPrepared()) {
			getPreparedBuffer().append(value);
			return "?";
		}
		else {
			return value.toString();
		}
	}
	
}
