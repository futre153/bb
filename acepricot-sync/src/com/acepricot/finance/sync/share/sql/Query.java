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
	
	public Query addFromClause(FromClause ...from) {
		if(this.decCursor != null) {
			this.decCursor.select.queryExp.queryTerm.queryPrimary.querySpec.tableExp.addFromClause(from);
		}
		if(this.recDecCursor != null) {
			this.recDecCursor.finSelect.queryExp.queryTerm.queryPrimary.querySpec.tableExp.addFromClause(from);
		}
		if(this.select != null) {
			this.select.queryExp.queryTerm.queryPrimary.querySpec.tableExp.addFromClause(from);
		}
		return this;
	}
	
	public Query addFromClause(SQLSyntaxImpl from) {
		return addFromClause(from, null);
	}
	
	public Query addFromClause(SQLSyntaxImpl from, Identifier ref) {
		return addFromClause(new FromClause[]{new FromClause(from, ref)});
	}
	
	public Query addColumns(ColumnSpec ...specs) throws SQLException {
		if(this.decCursor != null) {
			this.decCursor.select.queryExp.queryTerm.queryPrimary.querySpec.selectColumn.setColumns(specs);
		}
		if(this.recDecCursor != null) {
			this.recDecCursor.finSelect.queryExp.queryTerm.queryPrimary.querySpec.selectColumn.setColumns(specs);
		}
		if(this.select != null) {
			this.select.queryExp.queryTerm.queryPrimary.querySpec.selectColumn.setColumns(specs);
		}
		return this;
	}

	public Query addColumns(String ...strings) throws SQLException {
		if(this.decCursor != null) {
			this.decCursor.select.queryExp.queryTerm.queryPrimary.querySpec.selectColumn.setColumns(strings);
		}
		if(this.recDecCursor != null) {
			this.recDecCursor.finSelect.queryExp.queryTerm.queryPrimary.querySpec.selectColumn.setColumns(strings);
		}
		if(this.select != null) {
			this.select.queryExp.queryTerm.queryPrimary.querySpec.selectColumn.setColumns(strings);
		}
		return this;
	}

	public Query addDerived(String ...strings) throws SQLException {
		if(this.decCursor != null) {
			this.decCursor.select.queryExp.queryTerm.queryPrimary.querySpec.selectColumn.setDerived(0, strings);
		}
		if(this.recDecCursor != null) {
			this.recDecCursor.finSelect.queryExp.queryTerm.queryPrimary.querySpec.selectColumn.setDerived(0, strings);
		}
		if(this.select != null) {
			this.select.queryExp.queryTerm.queryPrimary.querySpec.selectColumn.setDerived(0, strings);
		}
		return this;
	}

	public Query addSelectColumnFunction(String string) {
		if(this.decCursor != null) {
			this.decCursor.select.queryExp.queryTerm.queryPrimary.querySpec.selectColumn.setFunction(string);
		}
		if(this.recDecCursor != null) {
			this.recDecCursor.finSelect.queryExp.queryTerm.queryPrimary.querySpec.selectColumn.setFunction(string);
		}
		if(this.select != null) {
			this.select.queryExp.queryTerm.queryPrimary.querySpec.selectColumn.setFunction(string);
		}
		return this;
	}

	public Query setDerived(String string) throws SQLException {
		if(this.decCursor != null) {
			this.decCursor.select.queryExp.queryTerm.queryPrimary.querySpec.selectColumn.setDerived(string);
		}
		if(this.recDecCursor != null) {
			this.recDecCursor.finSelect.queryExp.queryTerm.queryPrimary.querySpec.selectColumn.setDerived(string);
		}
		if(this.select != null) {
			this.select.queryExp.queryTerm.queryPrimary.querySpec.selectColumn.setDerived(string);
		}
		return this;
	}
	
	public Query addQuerySpec(SQLSyntaxImpl ...impls) {
		if(this.decCursor != null) {
			this.decCursor.select.queryExp.queryTerm.queryPrimary.querySpec.addFields(impls);
		}
		if(this.recDecCursor != null) {
			this.recDecCursor.finSelect.queryExp.queryTerm.queryPrimary.querySpec.addFields(impls);
		}
		if(this.select != null) {
			this.select.queryExp.queryTerm.queryPrimary.querySpec.addFields(impls);
		}
		return this;
	}
	
	public Query addSelectSpec(SQLSyntaxImpl ...impls) {
		if(this.decCursor != null) {
			this.decCursor.select.addFields(impls);
		}
		if(this.recDecCursor != null) {
			this.recDecCursor.finSelect.addFields(impls);
		}
		if(this.select != null) {
			this.select.addFields(impls);
		}
		return this;
	}
	
	public Query addTableSpec(SQLSyntaxImpl ...impls) {
		if(this.decCursor != null) {
			this.decCursor.select.queryExp.queryTerm.queryPrimary.querySpec.tableExp.addFields(impls);
		}
		if(this.recDecCursor != null) {
			this.recDecCursor.finSelect.queryExp.queryTerm.queryPrimary.querySpec.tableExp.addFields(impls);
		}
		if(this.select != null) {
			this.select.queryExp.queryTerm.queryPrimary.querySpec.tableExp.addFields(impls);
		}
		return this;
	}
}
