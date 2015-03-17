package org.pabk.sql.lang.sap;

public class FloatingPointLiteral extends SQLSyntaxImpl {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private float value;

	@Override
	public String toSQLString (PreparedBuffer psb) {
		return Float.toString(value);
	}
	public FloatingPointLiteral (double d) {
		value = new Float(d);
	}
}
