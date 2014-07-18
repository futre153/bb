package com.acepricot.finance.sync;

import java.util.ArrayList;

public class Where extends ArrayList<Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String EQU = "%s = %s";
	private static final String AND = "(%s) AND (%s)";
	private static final Object QUESTION_MARK = "?";
	
	public Object[] set(Object obj) {
		this.add(0, obj);
		Object[] objs = this.toArray();
		this.clear();
		return objs;
	}
	
	public Object equ(Object a, Object b) {
		this.add(b);
		return simpleOperation(Where.EQU, a, QUESTION_MARK);
	}
	
	private static Object simpleOperation(String format, Object ... s) {
		return String.format(format, s);
	}

	public Object and(Object ... s) {
		return multipleOperation(Where.AND, s);		
	}

	private static Object multipleOperation(String format, Object ... s) { 
		String ret = (String) s[0];
		for(int i = 1; i < s.length; i ++) {
			ret = String.format(format, ret, s[i]);
		}
		return ret;
	}
	
	
	
}
