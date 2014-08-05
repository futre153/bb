package com.acepricot.finance.sync.share.sql;

import java.sql.SQLException;

public class ParamSpec extends SQLSyntaxImpl {
	
	private ParameterName parameterName;
	private IndicatorName indicatorName;
	
	@Override
	public String toSQLString() throws SQLException {
		if(parameterName == null) {
			throw new SQLException("Parameter name cannot be null");
		}
		return  parameterName.toSQLString() + (indicatorName == null ? EMPTY : " " + indicatorName.toSQLString());
	}

}
