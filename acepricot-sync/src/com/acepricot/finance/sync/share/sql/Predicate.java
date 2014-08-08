package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public abstract class Predicate extends SQLSyntaxImpl {
	
	private static String[] COMP_OP = {
		LESS, GREATHER, LESS_OR_GREATHER,
		EQUAL, NOT_EQUAL, LESS_OR_EQUAL,
		GREATHER_OR_EQUAL, ASCII_LESS, ASCII_EQUAL, ASCII_GRATHER};
	private static String[] EQUAL_OR_NOT = {LESS_OR_GREATHER, EQUAL, ASCII_EQUAL};
	private static String[] QUAN_LIT = {QUAN_ALL, QUAN_SOME, QUAN_ANY};
	private static String[] LOCK_LEVELS = {"0", "1", "2", "3", "10", "15", "20", "30"};	
	
	protected Object[] objs;
	protected Predicate(Object ... objs) {
		this.objs = new Object[objs.length];
		if(objs != null) {
			for(int i = 0; i < objs.length; i++) {
				this.objs[i] = objs[i];
			}
		}
	}
	
	public String toSQLString() throws SQLException {
		if(objs == null) {
			throw new SQLException("Predicate expressions cannot be null");
		}
		return EMPTY;
	}
	
	private static int getIndex(String[] a, String key) {
		for(int i = 0; i < a.length; i++) {
			if(a[i].equals(key)) {
				return i;
			}
		}
		return -1;
	}
	
	static boolean isEqualOrNot(String comp) {
		return getIndex(Predicate.EQUAL_OR_NOT, comp) >= 0;
	}
	
	static boolean isComparisonOperator(String comp) {
		return getIndex(Predicate.COMP_OP, comp) >= 0;
	}
	
	static boolean isQuantifier(String quan) {
		return getIndex(Predicate.QUAN_LIT, quan) >= 0;
	}
	
	static boolean isLockLevel(String level) {
		return getIndex(Predicate.LOCK_LEVELS, level) >= 0;
	}
	
	static String join(PreparedBuffer pb, Object[] objs, char env1, char env2) throws SQLException {
		StringBuffer sb = new StringBuffer();
		sb.append(env1 == 0 ? EMPTY : env1);
		for(int i = 0; i < objs.length; i ++) {
			boolean qe = objs[i] instanceof QueryExp;
			if(i > 0) {
				sb.append(", ");
			}
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
		sb.append(env2 == 0 ? EMPTY : env2);
		return sb.toString();
	
	}
	
	static String join(PreparedBuffer pb, Object[] objs) throws SQLException {
		return join(pb, objs, (char) 0, (char) 0);
	}
	
	static void checkCopmarison(String comp) throws SQLException {
		if(comp == null) {
			throw new SQLException("Comparison operator cannot be null");
		}
		if(!Predicate.isComparisonOperator(comp)) {
			throw new SQLException("Comparison operator " + comp + " is not allowed");
		}
	}
	/*
	static void checkColumnSpec(Predicate pred) throws SQLException {
		if(pred.objs.length != 1) {
			throw new SQLException ("Predicate " + pred.getClass().getSimpleName() + " requires one expression");
		}
		if(pred.objs[0] instanceof ColumnSpec) {
			throw new SQLException ("Predicate " + pred.getClass().getSimpleName() + " allow only Column specification expression");
		}
		
	}
	*/
	
	static void checkClass(Predicate pred, Class<?> class1) throws SQLException {
		if(pred.objs.length != 1) {
			throw new SQLException ("Predicate " + pred.getClass().getSimpleName() + " requires one expression");
		}
		if(pred.objs[0].getClass().equals(class1)) {
			throw new SQLException ("Predicate " + pred.getClass().getSimpleName() + " allow only " + class1.getSimpleName() + " specification expression");
		}		
	}

	static void checkQuantifier(String quan) throws SQLException {
		if(quan == null) {
			throw new SQLException("Quantifier literal cannot be null");
		}
		if(!Predicate.isQuantifier(quan)) {
			throw new SQLException("Qunatifier literal " + quan + " is not allowed");
		}
	}
	
}
