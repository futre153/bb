package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public class RecDecCursor extends SQLSyntaxImpl {
	
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
	public String toSQLString() throws SQLException {
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
		return "DECLARE " + identifier.toSQLString() + " CURSOR FOR WITH RECURSIVE " + refName.toSQLString() + "(" + Predicate.join(psb, aliasName) + ") AS (" + initSelect + " UNION ALL " + recSelect.toSQLString() + ") "	+ finSelect.toSQLString();
	}

}
