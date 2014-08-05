package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public class OrderClause extends SQLSyntaxImpl {
	
	private SortSpec[] sortSpec;
	
	public OrderClause (SortSpec ... specs) {
		this.sortSpec = specs;
	}
	
	@Override
	public String toSQLString() throws SQLException {
		if(sortSpec != null && sortSpec.length > 0) {
			if(sortSpec.length > 1) {
				for(int i = 0; i < sortSpec.length; i ++) {
					if(!sortSpec[i].hasColumnSpec()) {
						throw new SQLException("if multiple order clause is used then only column expression is allowed");
					}
				}
			}
			return "SORT BY " + Predicate.join(sortSpec);
		}
		else {
			return EMPTY;
		}
	}

}
