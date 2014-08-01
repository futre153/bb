package com.acepricot.finance.sync.sql;

import java.sql.SQLException;

abstract class Predicate extends SQLSyntaxImpl {
	
	public static String LESS = "<";
	public static String GREATHER = ">";
	public static String LESS_OR_GREATHER = LESS + GREATHER;
	public static String EQUAL = "=";
	public static String NOT_EQUAL = "!" + EQUAL;
	public static String LESS_OR_EQUAL = LESS + EQUAL;
	public static String GREATHER_OR_EQUAL = GREATHER + EQUAL;
	public static String ASCII_LESS = "~" + LESS;
	public static String ASCII_EQUAL = "~" + EQUAL;
	public static String ASCII_GRATHER = "~" + GREATHER;
	
	private static String[] COMP_OP = {
		LESS, GREATHER, LESS_OR_GREATHER,
		EQUAL, NOT_EQUAL, LESS_OR_EQUAL,
		GREATHER_OR_EQUAL, ASCII_LESS, ASCII_EQUAL, ASCII_GRATHER};
	private static String[] EQUAL_OR_NOT = {LESS_OR_GREATHER, EQUAL, ASCII_EQUAL};
	
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
	
	public static boolean isEqualOrNot(String comp) {
		return getIndex(Predicate.EQUAL_OR_NOT, comp) >= 0;
	}
	
	public static boolean isComparisonOperator(String comp) {
		return getIndex(Predicate.COMP_OP, comp) >= 0;
	}

	public static String join(Object[] objs) throws SQLException {
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < objs.length; i ++) {
			boolean qe = objs[i] instanceof QueryExp;
			if(i > 0) {
				sb.append(',');
			}
			if(objs[i] instanceof SQLSyntaxImpl) {
				sb.append(qe ? "(" : EMPTY);
				sb.append(((SQLSyntaxImpl) objs[i]).toSQLString());
				sb.append(qe ? ")" : EMPTY);
			}
			else {
				objs[i].toString();
			}
		}
		return null;
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
	public static void checkClass(Predicate pred, Class<?> class1) throws SQLException {
		if(pred.objs.length != 1) {
			throw new SQLException ("Predicate " + pred.getClass().getSimpleName() + " requires one expression");
		}
		if(pred.objs[0].getClass().equals(class1)) {
			throw new SQLException ("Predicate " + pred.getClass().getSimpleName() + " allow only " + class1.getSimpleName() + " specification expression");
		}		
	}
	
}
