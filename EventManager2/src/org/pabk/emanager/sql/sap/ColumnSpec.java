package org.pabk.emanager.sql.sap;

import java.sql.SQLException;

public class ColumnSpec extends SQLSyntaxImpl {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected SQLSyntaxImpl sQLSyntaxImpl;
	protected Identifier identifier;
	
	public ColumnSpec(SQLSyntaxImpl ...impls) {
		super(impls);
	}
	
	public ColumnSpec(SQLSyntaxImpl ref, Identifier name) {
		sQLSyntaxImpl = ref;
		identifier = name;
	} 
		
	@Override
	public String toSQLString(PreparedBuffer psb) throws SQLException {
		if(identifier == null) {
			throw new SQLException("Identifier cannot be null in context of column specification");
		}
		return (sQLSyntaxImpl == null ? EMPTY : sQLSyntaxImpl.toSQLString(psb) + ".") + identifier.toSQLString(psb);
	}

	public static ColumnSpec[] getColSpecArray(SQLSyntaxImpl reference, String ...strings) {
		ColumnSpec[] cols = new ColumnSpec[strings.length];
		for(int i = 0; i < cols.length; i ++) {
			cols[i] = new ColumnSpec(reference, new Identifier(strings[i]));
		}
		return cols;
	}

}
