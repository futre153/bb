package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public class SelectColumn extends SQLSyntaxImpl {
	
	private SQLSyntaxImpl id;
	private SQLSyntaxImpl[] ids;
	private Identifier[] cols;
	private Identifier[] derivedAs;
	
	public SelectColumn(SQLSyntaxImpl id, String ... cols) throws SQLException {
		if(id != null) {
			this.id = id;
		}
		if(cols.length > 0) {
			this.cols = new Identifier[cols.length];
			for (int i = 0; i < cols.length; i ++) {
				this.cols[i] = new Identifier(cols[i]);
			}
		}
	}
	
	public void setIdentifiers(SQLSyntaxImpl ... ids) throws SQLException {
		if(cols == null) {
			throw new SQLException ("Cannot set the identifiers when the columns expresions is null");
		}
		this.ids = new SQLSyntaxImpl [cols.length];
		for(int i = 0; i < cols.length; i ++) {
			if(i <= ids.length) {
				this.ids[i] = ids[i];
			}
			else {
				this.ids[i] = null;
			}
		}
	}
	
	public void setDerived(String ... ders) throws SQLException {
		if(cols == null) {
			throw new SQLException ("Cannot set the derived columns when the columns expresions is null");
		}
		derivedAs = new Identifier[cols.length];
		for(int i = 0; i < cols.length; i ++) {
			if(i <= ders.length && ders[i] != null) {
				derivedAs[i] = new Identifier(ders[i]);
				continue;
			}
			derivedAs[i] = null;
		}
	}

	@Override
	public String toSQLString() throws SQLException {
		if(cols == null) {
			return id == null ? "*" : id.toSQLString() + ".*";
		}
		else {
			StringBuffer sb = new StringBuffer();
			for(int i = 0; i < cols.length; i++) {
				sb.append(i > 0 ? "," : EMPTY);
				sb.append(ids == null && ids[i] == null ? cols[i].toSQLString() : ids[i].toSQLString() + "." + cols[i].toSQLString());
				sb.append(derivedAs == null && derivedAs[i] == null ? EMPTY : " AS " + derivedAs[i].toSQLString());
			}
			return sb.toString();
		}
	}
}
