package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;
import java.util.Arrays;

public class Insert extends SQLSyntaxImpl {
	public static final int NO_DUPLICATE_VALUE = 0;
	public static final int REJECT_DUPLICATES = 1;
	public static final int IGNORE_DUPLICATES = 2;
	public static final int UPDATE_DUPLICATES = 3;
	private static final int[] DUPLICATES = {NO_DUPLICATE_VALUE, REJECT_DUPLICATES, IGNORE_DUPLICATES, UPDATE_DUPLICATES};
	private static final String[] DUPLICATE_STRINGS = {EMPTY, " REJECT DUPLICATES", " IGNORE DUPLICATES", " UPDATE DUPLICATES"};
	private TableName tableName;
	private Identifier[] cols;
	private Object values;
	private int duplicate = NO_DUPLICATE_VALUE;
	private boolean into = true;
	private boolean ignoreTrigger = false;
	private boolean nowait = false;
	
	public Insert(TableName tableName, Object values, String ...cols) throws SQLException {
		this.tableName = tableName;
		setColumns(cols);
		setValues(values);
	}
	
	final void setValues(Object ...objs) throws SQLException {
		if(objs.length == 1 && ((objs[0] instanceof QueryExp) || (objs[0] instanceof Object[]))) {
				values = objs[0];
		}
		else {
			values = objs;
		}
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

	@Override
	public String toSQLString() throws SQLException {
		try {
			if(!(values instanceof QueryExp)) {
				if(((Object[]) values).length != cols.length) {
					throw new SQLException("Columns and values must have equal counts of elements in context of insert statement");
				}
			}
			return "INSERT " + (into ? " INTO " : EMPTY) + tableName.toSQLString() +
			(cols == null ? EMPTY : " " + Predicate.join(psb, cols, '(', ')')) +
			" " + (values instanceof QueryExp ? ((QueryExp) values).toSQLString() : ("VALUES " + Predicate.join(psb, (Object[]) values, '(', ')'))) +
			DUPLICATE_STRINGS[duplicate] +
			(ignoreTrigger ? " IGNORE TRIGGER" : EMPTY) + (nowait ? " NOWAIT" : EMPTY);
		}
		catch(Exception e) {
			throw new SQLException(e);
		}
	}

	public void setDuplicate(int duplicate) {
		if(Arrays.binarySearch(DUPLICATES, duplicate) < 0) {
			duplicate = Insert.NO_DUPLICATE_VALUE;
		}
		this.duplicate = duplicate;
	}
}
