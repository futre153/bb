package com.acepricot.finance.sync;

import java.sql.SQLException;

interface Constraint {
	
	void setName(String name);
	String getName();
	void setContent(Object[] objs) throws SQLException;
	void getSQLText(StringBuffer sb, char env) throws SQLException;
}
