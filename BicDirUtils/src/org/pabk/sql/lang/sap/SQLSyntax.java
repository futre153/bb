package org.pabk.sql.lang.sap;

import java.io.Serializable;
import java.sql.SQLException;

public interface SQLSyntax extends Serializable {
	public String toSQLString(PreparedBuffer psb) throws SQLException;
}
