package org.acepricot.finance.server.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.acepricot.finance.web.util.Sys;
import org.h2.jdbcx.JdbcDataSource;

public class DBConnector3 {
	
	@SuppressWarnings("unused")
	private static final String DRIVER_KEY = "Driver";
	@SuppressWarnings("unused")
	private static final String DEFAULT_DRIVER = "org.h2.Driver";
	private static final String URL_KEY = "DatabaseUrl";
	private static final String DEFAULT_URL = "jdbc:h2:tcp://localhost/~/aceserver;AUTO_SERVER=TRUE;LOCK_TIMEOUT=60000";
	private static final String USER_KEY = "user";
	private static final String DEFAULT_USER = "acesvradm";
	private static final String PASSWORD_KEY = "password";
	private static final String DEFAULT_PASSWORD = "";
	@SuppressWarnings("unused")
	private static final String NULL_USER = "";
	@SuppressWarnings("unused")
	private static final String USER_PASSWORD = "cnuewf092no ptraajtn39ln";
	@SuppressWarnings("unused")
	private static final String UNKNOWN_SQL_OPERATION = "Unknown SQL operation";
		
	private static Hashtable <String, DBConnector3> connectors = new Hashtable <String, DBConnector3>();
			
	private Context context = null;
	private JdbcDataSource dataSource = null;
	private Properties settings = null;
	
	
	public DBConnector3 (String name, Properties pro) throws NamingException {
		context = new InitialContext();
		if(pro == null) {
			settings = new Properties();
			settings.setProperty(URL_KEY, DEFAULT_URL);
			settings.setProperty(USER_KEY, DEFAULT_USER);
			settings.setProperty(PASSWORD_KEY, DEFAULT_PASSWORD);
		}
		else {
			settings = new Properties(pro);
		}
		dataSource = (JdbcDataSource) context.lookup(settings.getProperty(URL_KEY));
		connectors.put(name, this);
	}
	
	public static DBConnector3 getConnection(String name) {
		return connectors.get(name);
	}
	
	
	public int insert (String tableName, String[] cNames, Object[] values) throws SQLException {
		Connection con = null;
		PreparedStatement psmt = null;
		int status;
		try {
			con = dataSource.getConnection(settings.getProperty(USER_KEY), settings.getProperty(PASSWORD_KEY));
			con.setAutoCommit(false);
			StringBuilder sb = new StringBuilder();
			sb.append("INSERT INTO "+ tableName);
			sb.append(((cNames == null)?(""):(" ("+Sys.concatenate(cNames, ',')+")")));
			sb.append(" VALUES");
			String[] a = new String [values.length];
			for(int i = 0; i<values.length; i++) {
				a[i] = "?";
			}
			sb.append(((values==null)?(" ()"):(" ("+Sys.concatenate(a, ',')+")")));
			//System.out.println("INSERT string\r\n"+sb.toString()+"\r\n----------------------------------------------------------");
			psmt = con.prepareStatement(sb.toString());
			for(int i = 0; i < values.length; i++) {
				psmt.setObject(i + 1, values[i]);
			}
			status = psmt.executeUpdate();
			con.commit();
		}
		finally {
			if (con != null) {
				con.rollback();
				con.close();
				status = -1;
			}
		}
		return status;
	}
	
}
