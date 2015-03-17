package org.pabk.sql.lang.sap;

import java.math.BigDecimal;

public class FixedPointLiteral extends SQLSyntaxImpl {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BigDecimal value = BigDecimal.ZERO;
	
	public FixedPointLiteral(double d) {
		value = new BigDecimal(Math.abs(d));
	}

	@Override
	public String toSQLString(PreparedBuffer psb) {
		if(SQLSyntaxImpl.isPrepared()) {
			psb.append(value);
			return "?";
		}
		else {
			return value.toString();
		}
	}
	
}
