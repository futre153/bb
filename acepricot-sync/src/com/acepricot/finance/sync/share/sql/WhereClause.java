package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public class WhereClause extends SQLSyntaxImpl {
	
	public static final int OR = 1;
	public static final int AND = 2;
	public static final int NOT = 3;
	public static final String RIGHT_BRACKET = ")";
	public static final String LEFT_BRACKET = "(";
	protected SearchCon searchCon;
	
	public WhereClause(Object ...objs) throws SQLException {
		this.searchCon = addCondition(objs);
	}
	
	private static SearchCon addCondition(Object ...objs) throws SQLException {
		SearchCon se = null; 
		if(objs.length > 0) {
			int s = WhereClause.firstIndexOf("(", objs);
			int e = WhereClause.lastIndexOf(")", objs);
			if((s < 0 && e >= 0) || (s >= 0 && e < 0) || (e < s)) {
				throw new SQLException("Where clause bracket error");
			}
			if(s >= 0) {
				Object[] a = new Object[e - s - 1];
				System.arraycopy(objs, s + 1, a, 0, a.length);
				Object obj = addCondition(a);
				a = new Object[s - e + objs.length - (obj == null ? 1 : 0)];
				System.arraycopy(objs, 0, a, 0, s);
				System.arraycopy(objs, e + 1, a, s + (obj == null ? 0 : 1), objs.length - e -1);
				a[s] = obj;
				objs = a;
			}
			int index = 0;
			while(index < objs.length) {
				boolean not = false;
				boolean or = false;
				boolean and = false;
				Predicate p = null;
				SearchCon in = null;
				try {
					not = objs[index].equals(Integer.toString(NOT));
					if(not) {
						index ++;
					}
					else {
						or = objs[index].equals(Integer.toString(OR));
						if(or) {
							index ++;
						}
						else {
							and = objs[index].equals(Integer.toString(AND));
							if(and) {
								index ++;
							}
						}
						if ((or | and) & (!not)) {
							not = objs[index].equals(Integer.toString(NOT));
							if(not) {
								index ++;
							}
						}
					}
					if(objs[index] instanceof Predicate) {
						p = (Predicate) objs[index];
						index ++;
					}
					else {
						if(objs[index] instanceof SearchCon) {
							in = (SearchCon) objs[index];
							index ++;
						}
					}
					if(p == null && in == null) {
						throw new SQLException("Missing predicate or search condition in contaxt of search");
					}
					if(or | and) {
						if(se == null) {
							throw new SQLException("Operators AND or OR are not expected at this time");
						}
						if(or) {
							se.getFreeSearchCon().addFields(new SearchCon(new BoolTerm(new BoolFactor(not, p == null ? in : p))));
						}
						else {
							se.getFreeBoolTerm().boolTerm.addFields(new BoolTerm(new BoolFactor(not, p == null ? in : p)));
						}
					}
					else {
						se = new SearchCon(new BoolTerm(new BoolFactor(not, p == null ? in : p)));
					}
				}
				catch (IndexOutOfBoundsException err) {
					break;
				}
			}
		}
		return se;
	}
	
	private static int lastIndexOf(String key, Object[] objs) {
		return WhereClause.indexOf(key, objs, false);
	}

	private static int firstIndexOf(String key, Object[] objs) {
		return WhereClause.indexOf(key, objs, true);
	}

	private static int indexOf(String key, Object[] objs, boolean first) {
		int index = -1;
		for(int i = 0; i < objs.length; i ++) {
			if(objs[i].equals(key)) {
				index = i;
				if(first) {
					break;
				}
			}
		}
		return index;
	}

	
	@Override
	public String toSQLString() throws SQLException {
		return searchCon == null ? EMPTY : "WHERE " + searchCon.toSQLString();
	}

}
