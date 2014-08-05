package com.acepricot.finance.sync.share.sql;

public class FloatingPointLiteral extends SQLSyntaxImpl {
	private float value;

	@Override
	public String toSQLString () {
		return Float.toString(value);
	}
	public FloatingPointLiteral (double d) {
		value = new Float(d);
	}
}
