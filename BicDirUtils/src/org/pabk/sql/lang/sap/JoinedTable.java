package org.pabk.sql.lang.sap;

import java.sql.SQLException;

public class JoinedTable extends SQLSyntaxImpl {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int NO_OUTER = -0x01;
	private static final int LEFT = 0x00;
	private static final int RIGHT = 0x01;
	private static final int FULL = 0x02;
	
	private static final int[] OUTER = {LEFT, RIGHT, FULL};
	
	private TableExp table1;
	private TableExp table2;
	private SearchCon join;
	private boolean display = false;
	private boolean cross = true;
	private int outer = LEFT; 
	
	
	public JoinedTable(boolean cross, int outer, TableExp table1, TableExp table2, SearchCon join) {
		this.cross = cross;
		this.outer = outer >=0 && outer < OUTER.length ? outer : NO_OUTER;
		this.table1 = table1;
		this.table2 = table2;
		this.join = join;
	}
	
	@Override
	public String toSQLString(PreparedBuffer psb) throws SQLException {
		if(table1 == null || table2 == null) {
			throw new SQLException ("Joined table expressions cannot be null");
		}
		if((!cross) && join == null) {
			throw new SQLException ("Joined table search contition cannot be null when the tables is not specifies as cross");
		}		
		return table1.toSQLString(psb) + (cross ? " CROSS" : (outer == NO_OUTER ? (display ? " INNER" : EMPTY) : ((outer == LEFT ? " LEFT" : (outer == RIGHT ? "RIGHT" : "FULL")) + (display ? " OUTER" : EMPTY)))) + " JOIN " + table2.toSQLString(psb) + (cross ? EMPTY : " " + join.toSQLString(psb));
	}

}
