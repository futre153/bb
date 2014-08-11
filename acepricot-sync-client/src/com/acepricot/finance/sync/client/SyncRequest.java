package com.acepricot.finance.sync.client;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import org.pabk.util.Huffman;

import com.acepricot.finance.sync.DBConnector;
import com.acepricot.finance.sync.Rows;
import com.acepricot.finance.sync.share.JSONMessage;
import com.acepricot.finance.sync.share.sql.FixedPointLiteral;
import com.acepricot.finance.sync.share.sql.Identifier;
import com.acepricot.finance.sync.share.sql.Query;
import com.acepricot.finance.sync.share.sql.RownoPred;
import com.acepricot.finance.sync.share.sql.TableName;
import com.acepricot.finance.sync.share.sql.UnsInt;
import com.acepricot.finance.sync.share.sql.WhereClause;

public class SyncRequest extends JSONMessage {

	private static Class<?> _class;
	
	public static JSONMessage getInstance(Properties props) throws Exception {
		if(_class == null) {
			_class = Class.forName(props.getProperty(JSONMessageProcessorClient.JDBC_DRIVER_KEY));
		}

		Connection con = DriverManager.getConnection(
				String.format(
						props.getProperty(JSONMessageProcessorClient.URL_STRING_KEY),
						props.getProperty(JSONMessageProcessorClient.DB_NAME_KEY).replaceAll("\\\\", "/").replaceAll(props.getProperty(JSONMessageProcessorClient.EXT_REPL_KEY), "")),
						props.getProperty(JSONMessageProcessorClient.DB_USER_KEY),
						Huffman.decode(props.getProperty(JSONMessageProcessorClient.DB_PSWD_KEY),null));
		String[] tables = DBSchemas.getTableNames(con);
		for(int i = 0; i < tables.length; i ++) {
			TableName tableName = new TableName(new Identifier(tables[i]));
			Object rowno = new RownoPred(true, 1);
			Query select = DBConnector.createSelect();
			select.addTableSpec(tableName);
			select.addFromClause(new WhereClause(rowno));
			Rows rows = DBConnector.select(con, select);
			System.out.println(rows);
		}
		con.close();
		return new JSONMessage().returnOK();
	}

}
