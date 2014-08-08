package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public class Update extends SQLSyntaxImpl {
	private TableName tableName;
	private Identifier reference;
	private Identifier whereCurrent;
	private WhereClause whereClause;
	private Identifier[] cols;
	private Object values;
	private boolean of = false;
	private boolean ignoreTrigger = false;
	private boolean nowait = false;
	
	public final void setReference(Identifier reference) {
		this.reference = reference;
	}

	public final void setWhereCurrent(Identifier whereCurrent) {
		this.whereCurrent = whereCurrent;
	}

	public final void setWhereClause(WhereClause whereClause) {
		this.whereClause = whereClause;
	}

	public Update(TableName tableName, Object values, String[] cols) throws SQLException {
		this.tableName = tableName;
		setColumns(cols);
		setValues(values);
	}
		
	final void setValues(Object ...objs) throws SQLException {
		values = objs;
	}
	
	final void setColumns(String ...cols) throws SQLException {
		if(cols.length > 0) {
			Identifier[] s;
			int index;
			if(this.cols == null) {
				 index = 0;
				 s = new Identifier[cols.length];
			}
			else {
				index = this.cols.length;
				s = new Identifier[cols.length + index];
				System.arraycopy(this.cols, 0, s, 0, index);
			}
			for (int i = index; i < s.length; i ++) {
				s[i] = new Identifier(cols[i - index]);
			}
			this.cols = s;
		}
	}
	
	final boolean isIgnoreTrigger() {
		return ignoreTrigger;
	}
	final void setIgnoreTrigger(boolean ignoreTrigger) {
		this.ignoreTrigger = ignoreTrigger;
	}
	final boolean isNowait() {
		return nowait;
	}
	final void setNowait(boolean nowait) {
		this.nowait = nowait;
	}
	
	static String joinSet(PreparedBuffer pb, Identifier[] cols, Object[] objs) throws SQLException {
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < objs.length; i ++) {
			boolean qe = objs[i] instanceof QueryExp;
			if(i > 0) {
				sb.append(", ");
			}
			sb.append(cols[i]);
			sb.append(" = ");
			if(objs[i] instanceof SQLSyntaxImpl) {
				sb.append(qe ? "(" : EMPTY);
				sb.append(((SQLSyntaxImpl) objs[i]).toSQLString());
				sb.append(qe ? ")" : EMPTY);
			}
			else {
				if(SQLSyntaxImpl.isPrepared()) {
					sb.append(PREPARED_MARK);
					pb.append(objs[i]);
				}
				else {
					if(objs[i] instanceof String) {
						sb.append("'");		
					}
					sb.append(objs[i]);
					if(objs[i] instanceof String) {
						sb.append("'");
					}
				}
			}
		}
		return sb.toString();
	}
	
	@Override
	public String toSQLString() throws SQLException {
		try {
			if(cols == null) {
				throw new SQLException("Column must be defined in contex of update statement");
			}
			if(values == null) {
				throw new SQLException("Values must be defined in contex of update statement");
			}
			if(((Object[]) values).length != cols.length) {
				throw new SQLException("Columns and values must have equal counts of elements in context of update statement");
			}
			return "UPDATE" + (of ? " OF " : EMPTY) + tableName.toSQLString() +
			(reference == null ? EMPTY : reference.toSQLString()) +
			" SET "	+ Update.joinSet(psb, cols, (Object[]) values) +
			(whereCurrent == null ? EMPTY : (" WHERE CURRENT OF " + whereCurrent.toSQLString())) +
			(whereClause == null ? EMPTY : (whereCurrent == null ? (" WHERE " + whereClause.toSQLString()) : EMPTY)) +
			(ignoreTrigger ? (whereCurrent == null ? " IGNORE TRIGGER" : EMPTY) : EMPTY) + (nowait ? " NOWAIT" : EMPTY);
		}
		catch(Exception e) {
			throw new SQLException(e);
		}
	}
	
}