package com.acepricot.finance.sync.share.sql;

import java.lang.reflect.Field;
import java.sql.SQLException;

public abstract class SQLSyntaxImpl implements SQLSyntax {
	
	protected static final String EMPTY = "";
	private static boolean prepared = true;
	public static final String PREPARED_MARK = "?";
	
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
	
	public static final String QUAN_ALL = "All";
	public static final String QUAN_SOME = "SOME";
	public static final String QUAN_ANY = "ANY";
	
	protected SQLSyntaxImpl(SQLSyntaxImpl ... impls) {
		addFields(impls);
	}
	
	protected void addFields(SQLSyntaxImpl ...impls) {
		for(int i = 0; i < impls.length; i++) {
			try {
				Class<?> _class = impls[i].getClass();
				NoSuchFieldException e1 = new NoSuchFieldException();
				Field field = null;
				do {
					if(_class == null) {
						throw e1 ;
					}
					String className = _class.getSimpleName();
					className = className.substring(0, 1).toLowerCase() + className.substring(1);
					try {
						field = this.getClass().getDeclaredField(className);
					}
					catch (NoSuchFieldException e) {
						e1 = e;
					}
					_class = _class.getSuperclass();
				}
				while(field == null);
				field.set(this, impls[i]);
			}
			catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	public final String toString() {
		try {
			return this.toSQLString();
		} catch (SQLException e) {
			return null;
		}
	}

	public static String toSQLString(PreparedBuffer psb, Object object) throws SQLException {
		if (object instanceof SQLSyntax) {
			return ((SQLSyntax) object).toSQLString();
		}
		if(SQLSyntaxImpl.isPrepared()) {
			psb.append(object);
			return PREPARED_MARK;
		}
		else {
			return object.toString();
		}
	}

	public static boolean isPrepared() {
		return prepared;
	}

	public static void setPrepared(boolean prepared) {
		SQLSyntaxImpl.prepared = prepared;
	}
	
	public PreparedBuffer getPreparedBuffer() {
		return psb;
	}
	
	public void closePSBuffer() {
		psb.close();
	}
}
