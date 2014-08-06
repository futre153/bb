package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public class WhereClause extends SQLSyntaxImpl {
	
	private static final int OR = 1;
	private static final int AND = 2;
	private static final int NO_OPERATION = 0;
	private static final int NOT = 3;
	protected SearchCon searchCon;
	
	public WhereClause(SQLSyntaxImpl ...s) {
		super(s);
		if(searchCon == null) {
			searchCon = new SearchCon(); 
		}
	}
	
	public static SearchCon addCondition(Object ...objs) throws SQLException {
		SearchCon se = new SearchCon(); 
		if(objs.length > 0) {
			int s = WhereClause.firstIndexOf("(", objs);
			int e = WhereClause.lastIndexOf(")", objs);
			if((s > 0 && e >= 0) || (s >= 0 && e < 0) || (e < s)) {
				throw new SQLException("Where clause bracket error");
			}
			if(s >= 0) {
				Object[] a = new Object[s - e + objs.length];
				Object[] b = new Object[e - s - 1];
				System.arraycopy(objs, 0, a, 0, s);
				System.arraycopy(objs, s + 1, b, 0, b.length);
				System.arraycopy(objs, e + 1, a, s + 1, objs.length - e -1);
				a[s] = addCondition(b);
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
					not = objs[index].toString().equals(Integer.toString(NOT));
					if(not) {
						index ++;
					}
					else {
						or = objs[index].toString().equals(Integer.toString(OR));
						if(or) {
							index ++;
						}
						else {
							and = objs[index].toString().equals(Integer.toString(AND));
							if(and) {
								index ++;
							}
						}
						if ((or | and) & (!not)) {
							not = objs[index].toString().equals(Integer.toString(NOT));
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
						if(in != null) {
							
							se.boolTerm = new BoolTerm(new BoolTerm(new BoolFactor(not, in)));
						}
					}
					else {
						if(se.boolTerm != null) {
							throw new SQLException("If operator OR or AND is used then boolean term cannot be null in search");
						}
						if(in != null) {
							se.boolTerm = new BoolTerm(new BoolFactor(not, in));
						}
						else {
							se.boolTerm = new BoolTerm(new BoolFactor(not, p));
						}
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
			if(objs[i].toString().equals(key)) {
				index = i;
				if(first) {
					break;
				}
			}
		}
		return index;
	}

	private WhereClause addCondition2(Object[] objs) {
		
	}
	
	
	@Override
	public String toSQLString() throws SQLException {
		return searchCon == null ? EMPTY : "WHERE " + searchCon.toSQLString();
	}

}
