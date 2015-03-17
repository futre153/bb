package org.pabk.sql.lang.sap;

import java.sql.SQLException;

public class SearchCon extends SQLSyntaxImpl {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected BoolTerm boolTerm;
	protected SearchCon searchCon;
	public SearchCon(SQLSyntaxImpl ...s) {
		super(s);
	}
	@Override
	public String toSQLString(PreparedBuffer psb) throws SQLException {
		if(boolTerm == null) {
			throw new SQLException("Boolean term cannot be null under search condition");
		}
		return (searchCon == null ? EMPTY : searchCon.toSQLString(psb) + " OR ") + boolTerm.toSQLString(psb);
	}
	/*
	public SearchCon getFreeBoolTerm() {
		if(this.boolTerm.boolTerm == null) {
			return this.boolTerm;
		}
		return this.getFreeBoolTerm();
	}
	*/
	public BoolTerm getFreeBoolTerm() {
		return this.boolTerm.getFreeBoolTerm();
	}
	
	public SearchCon getFreeSearchCon() {
		if(searchCon == null) {
			return this;
		}
		else {
			return searchCon.getFreeSearchCon();
		}
	}
	
}
