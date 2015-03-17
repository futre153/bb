package org.pabk.sql.lang.sap;

import java.sql.SQLException;

public class SelectColumn extends SQLSyntaxImpl {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SQLSyntaxImpl id;
	private SQLSyntaxImpl[] ids;
	private Identifier[] cols;
	private Identifier[] derivedAs;
	private Identifier derived;
	private String function;
	
	public SelectColumn() throws SQLException {
		this(null);
	}
	
	public SelectColumn(SQLSyntaxImpl id, String ... cols) throws SQLException {
		setId(id);
		setColumns(cols);
	}
	
	void setFunction(String function) {
		this.function = function;
	}
	
	void setDerived(String der) throws SQLException {
		this.derived = new Identifier(der);
	}
	
	void setId(SQLSyntaxImpl id) {
		if(id != null) {
			this.id = id;
		}
	}
	
	int setColumns(Identifier ...ids) {
		int index = 0;
		if(ids.length > 0) {
			Identifier[] c;
			if(this.cols == null) {
				c = new Identifier[ids.length];
			}
			else {
				c = new Identifier[ids.length + this.cols.length];
				System.arraycopy(this.cols, 0, c, 0, this.cols.length);
				index = ids.length;
			}
			System.arraycopy(ids, 0, c, index, ids.length);
			this.cols = c;
		}
		return this.cols.length;
	}
	
	int setColumns(String ...cols) throws SQLException {
		int index = 0;
		if(cols.length > 0) {
			Identifier[] c;
			if(this.cols == null) {
				c = new Identifier[cols.length];
			}
			else {
				c = new Identifier[cols.length + this.cols.length];
				System.arraycopy(this.cols, 0, c, 0, this.cols.length);
				index = cols.length;
			}
			for (int i = index; i < c.length; i ++) {
				c[i] = new Identifier(cols[i]);
			}
			this.cols = c;
		}
		return index;
	}
	
	void setIdentifier(int index, SQLSyntaxImpl ... ids) throws SQLException {
		if(cols == null) {
			throw new SQLException ("Cannot set the identifiers when the columns expresions is null");
		}
		SQLSyntaxImpl[] s = new SQLSyntaxImpl [cols.length];
		if(this.ids != null) {
			System.arraycopy(this.ids, 0, s, 0, this.ids.length <= s.length ? this.ids.length: s.length);
		}
		int j = 0;
		for(int i = index; i < cols.length; i ++) {
			if(j >= ids.length) {
				break;
			}
			s[i] = ids[j];
			j ++;
		}
		this.ids = s;
	}
	
	
	void setDerived(int index, String ... ders) throws SQLException {
		if(cols == null) {
			throw new SQLException ("Cannot set the derived columns when the columns expresions is null");
		}
		Identifier[] s = new Identifier [cols.length];
		if(this.derivedAs != null) {
			System.arraycopy(this.derivedAs, 0, s, 0, this.derivedAs.length <= s.length ? this.derivedAs.length: s.length);
		}
		derivedAs = new Identifier[cols.length];
		int j = 0;
		for(int i = 0; i < cols.length; i ++) {
			if(j >= ders.length) {
				break;
			}
			s[i] = new Identifier(ders[j]);
			j ++;
		}
		this.derivedAs = s;
	}

	@Override
	public String toSQLString(PreparedBuffer psb) throws SQLException {
		StringBuffer sb = new StringBuffer();
		if(cols == null) {
			sb.append(function == null ? EMPTY : (function + "("));
			sb.append(id == null ? "*" : id.toSQLString(psb) + ".*");
			sb.append(function == null ? EMPTY : (")"));
			sb.append(derived == null ? EMPTY : (" AS " + derived.toSQLString(psb)));
		}
		else {
			for(int i = 0; i < cols.length; i++) {
				sb.append(i > 0 ? ", " : EMPTY);
				sb.append(function == null ? EMPTY : (function + "("));
				sb.append(ids == null || ids[i] == null ? cols[i].toSQLString(psb) : ids[i].toSQLString(psb) + "." + cols[i].toSQLString(psb));
				sb.append(function == null ? EMPTY : (")"));
				sb.append(derivedAs == null || derivedAs[i] == null ? EMPTY : " AS " + derivedAs[i].toSQLString(psb));
			}
		}
		return sb.toString();
	}
	
	void setColumns(ColumnSpec ...specs) throws SQLException {
		String[] d = new String[specs.length];
		SQLSyntaxImpl[] s = new SQLSyntaxImpl[specs.length];
		for(int i = 0; i < specs.length; i ++) {
			d[i] = specs[i].identifier.getValue();
			s[i] = specs[i].sQLSyntaxImpl;
		}
		this.setIdentifier(this.setColumns(d), s);
	}
}
