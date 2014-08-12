package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public class Query extends SQLSyntaxImpl {
	
	protected DecCursor decCursor;
	protected RecDecCursor recDecCursor;
	protected Select select;
	
	public Query(SQLSyntaxImpl ...s) {
		super(s);
	}

	@Override
	public String toSQLString() throws SQLException {
		if(select == null && decCursor == null && recDecCursor == null) {
			throw new SQLException("At least one of query statements must not be null");
		}
		return select == null ? (decCursor == null ? recDecCursor.toSQLString() : decCursor.toSQLString()) : select.toSQLString();
	}

	public void addFromClause(SQLSyntaxImpl from) {
		if(this.decCursor != null) {
			this.decCursor.select.queryExp.queryTerm.queryPrimary.querySpec.tableExp.addFromClause(new FromClause(from));
		}
		if(this.recDecCursor != null) {
			this.recDecCursor.finSelect.queryExp.queryTerm.queryPrimary.querySpec.tableExp.addFromClause(new FromClause(from));
		}
		if(this.select != null) {
			this.select.queryExp.queryTerm.queryPrimary.querySpec.tableExp.addFromClause(new FromClause(from));
		}
	}

	public void addColumns(ColumnSpec ...specs) throws SQLException {
		if(this.decCursor != null) {
			this.decCursor.select.queryExp.queryTerm.queryPrimary.querySpec.selectColumn.setColumns(specs);
		}
		if(this.recDecCursor != null) {
			this.recDecCursor.finSelect.queryExp.queryTerm.queryPrimary.querySpec.selectColumn.setColumns(specs);
		}
		if(this.select != null) {
			this.select.queryExp.queryTerm.queryPrimary.querySpec.selectColumn.setColumns(specs);
		}
	}

	public void addColumns(String ...strings) throws SQLException {
		if(this.decCursor != null) {
			this.decCursor.select.queryExp.queryTerm.queryPrimary.querySpec.selectColumn.setColumns(strings);
		}
		if(this.recDecCursor != null) {
			this.recDecCursor.finSelect.queryExp.queryTerm.queryPrimary.querySpec.selectColumn.setColumns(strings);
		}
		if(this.select != null) {
			this.select.queryExp.queryTerm.queryPrimary.querySpec.selectColumn.setColumns(strings);
		}
		
	}

	public void addDerived(String ...strings) throws SQLException {
		if(this.decCursor != null) {
			this.decCursor.select.queryExp.queryTerm.queryPrimary.querySpec.selectColumn.setDerived(0, strings);
		}
		if(this.recDecCursor != null) {
			this.recDecCursor.finSelect.queryExp.queryTerm.queryPrimary.querySpec.selectColumn.setDerived(0, strings);
		}
		if(this.select != null) {
			this.select.queryExp.queryTerm.queryPrimary.querySpec.selectColumn.setDerived(0, strings);
		}
		
	}

	public void addSelectColumnFunction(String string) {
		if(this.decCursor != null) {
			this.decCursor.select.queryExp.queryTerm.queryPrimary.querySpec.selectColumn.setFunction(string);
		}
		if(this.recDecCursor != null) {
			this.recDecCursor.finSelect.queryExp.queryTerm.queryPrimary.querySpec.selectColumn.setFunction(string);
		}
		if(this.select != null) {
			this.select.queryExp.queryTerm.queryPrimary.querySpec.selectColumn.setFunction(string);
		}		
	}

	public void setDerived(String string) throws SQLException {
		if(this.decCursor != null) {
			this.decCursor.select.queryExp.queryTerm.queryPrimary.querySpec.selectColumn.setDerived(string);
		}
		if(this.recDecCursor != null) {
			this.recDecCursor.finSelect.queryExp.queryTerm.queryPrimary.querySpec.selectColumn.setDerived(string);
		}
		if(this.select != null) {
			this.select.queryExp.queryTerm.queryPrimary.querySpec.selectColumn.setDerived(string);
		}
	}
	
	public void addQuerySpec(SQLSyntaxImpl ...impls) {
		if(this.decCursor != null) {
			this.decCursor.select.queryExp.queryTerm.queryPrimary.querySpec.addFields(impls);
		}
		if(this.recDecCursor != null) {
			this.recDecCursor.finSelect.queryExp.queryTerm.queryPrimary.querySpec.addFields(impls);
		}
		if(this.select != null) {
			this.select.queryExp.queryTerm.queryPrimary.querySpec.addFields(impls);
		}	
	}
	
	public void addSelectSpec(SQLSyntaxImpl ...impls) {
		if(this.decCursor != null) {
			this.decCursor.select.addFields(impls);
		}
		if(this.recDecCursor != null) {
			this.recDecCursor.finSelect.addFields(impls);
		}
		if(this.select != null) {
			this.select.addFields(impls);
		}	
	}
	
	public void addTableSpec(SQLSyntaxImpl ...impls) {
		if(this.decCursor != null) {
			this.decCursor.select.queryExp.queryTerm.queryPrimary.querySpec.tableExp.addFields(impls);
		}
		if(this.recDecCursor != null) {
			this.recDecCursor.finSelect.queryExp.queryTerm.queryPrimary.querySpec.tableExp.addFields(impls);
		}
		if(this.select != null) {
			this.select.queryExp.queryTerm.queryPrimary.querySpec.tableExp.addFields(impls);
		}
		
	}

}
