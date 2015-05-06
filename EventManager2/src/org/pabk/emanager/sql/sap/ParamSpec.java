package org.pabk.emanager.sql.sap;

import java.sql.SQLException;

public class ParamSpec extends SQLSyntaxImpl {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ParameterName parameterName;
	private IndicatorName indicatorName;
	
	@Override
	public String toSQLString(PreparedBuffer psb) throws SQLException {
		if(parameterName == null) {
			throw new SQLException("Parameter name cannot be null");
		}
		return  parameterName.toSQLString(psb) + (indicatorName == null ? EMPTY : " " + indicatorName.toSQLString(psb));
	}

}