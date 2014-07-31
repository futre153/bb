package com.acepricot.finance.sync.sql;

public class FloatingPointLiteral extends SQLSyntaxImpl {
	private float value;

	@Override
	public String toString () {
		return Float.toString(value);
	}
	public FloatingPointLiteral (double d) {
		value = new Float(d);
	}
}
