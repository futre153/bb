package org.pabk.sql.lang.sap;

public class DistinctSpec extends SQLSyntaxImpl {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String DISTINCT = "DISTINCT";
	private static final String ALL = "ALL";
	
	public static DistinctSpec Distinct = new DistinctSpec(true, true);
	public static DistinctSpec All = new DistinctSpec(false, true);
	
	private boolean distinct = false;
	private boolean set = false;
	
	private DistinctSpec(boolean d, boolean s) {
		this.distinct = d;
		this.set = s;
	}

	@Override
	public String toSQLString(PreparedBuffer psb) {
		return distinct ? DISTINCT : (set ? ALL : EMPTY);
	}

}
