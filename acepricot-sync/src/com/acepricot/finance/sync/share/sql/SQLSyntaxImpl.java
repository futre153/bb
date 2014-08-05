package com.acepricot.finance.sync.share.sql;

import java.lang.reflect.Field;
import java.sql.SQLException;

public abstract class SQLSyntaxImpl implements SQLSyntax {
	
	protected static final String EMPTY = "";
	
	protected SQLSyntaxImpl(SQLSyntaxImpl ... impls) {
		for(int i = 0; i < impls.length; i++) {
			try {
				String className = impls[i].getClass().getSimpleName();
				className = className.substring(0, 1).toLowerCase() + className.substring(1);
				Field field = this.getClass().getDeclaredField(className);
				field.set(this, impls[i]);
			}
			catch(NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
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

	public static String toSQLString(Object object) throws SQLException {
		if (object instanceof SQLSyntax) {
			return ((SQLSyntax) object).toSQLString();
		}
		return object.toString();
	}
	
}
