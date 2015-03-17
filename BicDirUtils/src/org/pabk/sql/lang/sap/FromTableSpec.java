package org.pabk.sql.lang.sap;

import java.sql.SQLException;

public class FromTableSpec extends SQLSyntaxImpl {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SQLSyntaxImpl[] fromClause;
	
	public FromTableSpec(SQLSyntaxImpl ... clauses) {
		addTableSpecs(clauses);
	}
	
	void addTableSpecs(SQLSyntaxImpl ...clauses) {
		if(clauses.length > 0) {
			if(fromClause == null) {
				fromClause = new FromClause[0];
			}
			FromClause[] fc = new FromClause[fromClause.length + clauses.length];
			System.arraycopy(fromClause, 0, fc, 0, fromClause.length);
			System.arraycopy(clauses, 0, fc, fromClause.length, clauses.length);
			fromClause = fc;
		}
		
	}
	
	boolean hasLength() {
		return fromClause != null;
	}
	
	@Override
	public String toSQLString(PreparedBuffer psb) throws SQLException {
		StringBuffer sb = new StringBuffer();
		if(fromClause == null) {
			throw new SQLException("Freom table specification cannot be null");
		}
		boolean comma = false;
		for(int i = 0; i < fromClause.length; i ++) {
			if(!(fromClause[i] instanceof FromClause) || (fromClause[i] instanceof FromTableSpec)) {
				throw new SQLException("From table specification cannot be instance of " + fromClause[i].getClass().getSimpleName());
			}
			if((fromClause[i] instanceof FromTableSpec) && (!((FromTableSpec) fromClause[i]).hasLength())) {
				continue;
			}
			if(comma) {
				sb.append(", ");
			}
			else {
				comma = true;
			}
			sb.append(fromClause[i].toSQLString(psb));
		}
		return sb.toString();
	}

}
