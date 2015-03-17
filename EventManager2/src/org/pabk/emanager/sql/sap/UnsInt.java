package org.pabk.emanager.sql.sap;

public class UnsInt extends SQLSyntaxImpl {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected FixedPointLiteral fixedPointLiteral;
	protected FloatingPointLiteral floatingPointLiteral;
	
	
	public UnsInt(SQLSyntaxImpl ...impls) {
		super(impls);
	}
	@Override
	public String toSQLString (PreparedBuffer psb) {
		return fixedPointLiteral == null ? (fixedPointLiteral == null ? EMPTY : floatingPointLiteral.toSQLString(psb)) : fixedPointLiteral.toSQLString(psb);
	} 
}
