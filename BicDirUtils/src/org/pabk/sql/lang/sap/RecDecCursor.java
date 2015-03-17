package org.pabk.sql.lang.sap;

import java.sql.SQLException;

public class RecDecCursor extends SQLSyntaxImpl {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Identifier identifier;
	protected Identifier refName;
	protected Identifier[] aliasName;
	protected QuerySpec initSelect;
	protected QuerySpec recSelect;
	protected Select finSelect;
	
	public RecDecCursor(Identifier id, Identifier ref, Identifier[] aliases, QuerySpec initialSelect, QuerySpec recursiveSelect, Select finalSelect) {
		this.identifier = id;
		this.refName = ref;
		this.aliasName = aliases;
		this.initSelect = initialSelect;
		this.recSelect = recursiveSelect;
		this.finSelect = finalSelect;
	}
	
	@Override
	public String toSQLString(PreparedBuffer psb) throws SQLException {
		if(identifier == null) {
			throw new SQLException ("Result table name cannot be null in context of recursive declared query");
		}
		if(refName == null) {
			throw new SQLException ("Reference name cannot be null in context of recursive declared query");
		}
		if(aliasName == null || aliasName.length == 0) {
			throw new SQLException ("alias name table name cannot be null or void in context of recursive declared query");
		}
		if(initSelect == null) {
			throw new SQLException ("Initial select cannot be null in context of recursive declared query");
		}
		if(recSelect == null) {
			throw new SQLException ("Recursive select cannot be null in context of recursive declared query");
		}
		if(finSelect == null) {
			throw new SQLException ("Final select name cannot be null in context of recursive declared query");
		}
		return "DECLARE " + identifier.toSQLString(psb) + " CURSOR FOR WITH RECURSIVE " + refName.toSQLString(psb) + "(" + Predicate.join(psb, aliasName) + ") AS (" + initSelect + " UNION ALL " + recSelect.toSQLString(psb) + ") "	+ finSelect.toSQLString(psb);
	}

}
