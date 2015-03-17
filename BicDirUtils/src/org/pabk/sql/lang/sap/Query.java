package org.pabk.sql.lang.sap;

import java.sql.SQLException;

public class Query extends SQLSyntaxImpl {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected DecCursor decCursor;
	protected RecDecCursor recDecCursor;
	protected Select select;
	private PreparedBuffer psb = new PreparedBuffer();
	public Query(SQLSyntaxImpl ...s) {
		super(s);
		psb = new PreparedBuffer();
	}
	
	public PreparedBuffer getPreparedBuffer() {
		return psb;
	}
	@Override
	public String toSQLString(PreparedBuffer psb) throws SQLException {
		if(psb == null) {
			psb = getPreparedBuffer();
		}
		if(select == null && decCursor == null && recDecCursor == null) {
			throw new SQLException("At least one of query statements must not be null");
		}
		return select == null ? (decCursor == null ? recDecCursor.toSQLString(psb) : decCursor.toSQLString(psb)) : select.toSQLString(psb);
	}
	
	public Query addFromClause(FromClause ...from) {
		findFirstQE(this).queryTerm.queryPrimary.querySpec.tableExp.addFromClause(from);
		return this;
	}
	
	public Query addFromClause(SQLSyntaxImpl from) {
		return addFromClause(from, null);
	}
	
	public Query addFromClause(SQLSyntaxImpl from, Identifier ref) {
		return addFromClause(new FromClause[]{new FromClause(from, ref)});
	}
	
	public Query addColumns(ColumnSpec ...specs) throws SQLException {
		findFirstQE(this).queryTerm.queryPrimary.querySpec.selectColumn.setColumns(specs);
		return this;
	}
	
	public Query addColumns(Identifier ... ids) {
		findFirstQE(this).queryTerm.queryPrimary.querySpec.selectColumn.setColumns(ids);
		return this;
	}
	
	public Query addColumns(String ...strings) throws SQLException {
		findFirstQE(this).queryTerm.queryPrimary.querySpec.selectColumn.setColumns(strings);
		return this;
	}

	public Query addDerived(String ...strings) throws SQLException {
		findFirstQE(this).queryTerm.queryPrimary.querySpec.selectColumn.setDerived(0, strings);
		return this;
	}

	public Query addSelectColumnFunction(String string) {
		findFirstQE(this).queryTerm.queryPrimary.querySpec.selectColumn.setFunction(string);
		return this;
	}

	public Query setDerived(String string) throws SQLException {
		findFirstQE(this).queryTerm.queryPrimary.querySpec.selectColumn.setDerived(string);
		return this;
	}
	
	public Query addQuerySpec(SQLSyntaxImpl ...impls) {
		findFirstQE(this).queryTerm.queryPrimary.querySpec.addFields(impls);
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
	
	private static final QueryExp findFirstQE(Query q) {
		QueryExp tQE = q.decCursor != null ? q.decCursor.select.queryExp : (q.recDecCursor != null ? q.recDecCursor.finSelect.queryExp : (q.select != null ? q.select.queryExp : null));
		while(tQE.queryExp != null) {
			tQE = tQE.queryExp;
		}
		return tQE;
	}
	
	public Query addTableSpec(SQLSyntaxImpl ...impls) {
		findFirstQE(this).queryTerm.queryPrimary.querySpec.tableExp.addFields(impls);
		return this;
	}

	public Query unionTo(Query q) {
		Select qSelect = q.decCursor != null ? q.decCursor.select : (q.recDecCursor != null ? q.recDecCursor.finSelect : (q.select != null ? q.select : null));
		Select tSelect = this.decCursor != null ? this.decCursor.select : (this.recDecCursor != null ? this.recDecCursor.finSelect : (this.select != null ? this.select : null));
		tSelect.queryExp.addFields(qSelect.queryExp);
		tSelect.queryExp.setUnion(true);
		return this;
	}

}
