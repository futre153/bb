package com.acepricot.finance.sync;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

public class DBConnector extends Hashtable<String, DataSource> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String DB_URL_KEY = "com.acepricot.finance.db.url";
	private static final String DB_DRIVER_CLASS_KEY = "com.acepricot.finance.db.driver";
	private static final String DB_USERNAME_KEY = "com.acepricot.finance.db.user";
	private static final String DB_PASSWORD_KEY = "com.acepricot.finance.db.pass";
	private static final String DB_MAX_ACTIVE_KEY = "com.acepricot.finance.db.maxactive";
	private static final String DB_INIT_SIZE_KEY = "com.acepricot.finance.db.intisize";
	private static final String DB_MAX_WAIT_KEY = "com.acepricot.finance.db.maxwait";
	private static final String DB_REM_ABANDONED_KEY = "com.acepricot.finance.db.maxabandoned";
	private static final String DB_MIN_IDLE_KEY = "com.acepricot.finance.db.minidle";
	static final String DB_DSN_KEY = "com.acepricot.finance.db.dsn";
	private static final DBConnector db = new DBConnector();
	private static final int DEFAULT_INT_VALUE = -1;
	private static final BigDecimal DEFAULT_BIGDECIMAL_VALUE = new BigDecimal(-1);
	private static final boolean DEBUG = false;
	private static boolean prepared = true;
	
		
	private DBConnector(){}
	
	final public static void bind(Properties pro, String name) {
		PoolProperties p = new PoolProperties();
		p.setUrl(pro.getProperty(DB_URL_KEY));
		p.setDriverClassName(pro.getProperty(DB_DRIVER_CLASS_KEY));
		p.setUsername(pro.getProperty(DB_USERNAME_KEY));
		p.setPassword(pro.getProperty(DB_PASSWORD_KEY));
		p.setMaxActive(Integer.parseInt(pro.getProperty(DB_MAX_ACTIVE_KEY)));
		p.setInitialSize(Integer.parseInt(pro.getProperty(DB_INIT_SIZE_KEY)));
		p.setMaxWait(Integer.parseInt(pro.getProperty(DB_MAX_WAIT_KEY)));
		p.setRemoveAbandonedTimeout(Integer.parseInt(pro.getProperty(DB_REM_ABANDONED_KEY)));
		p.setMinEvictableIdleTimeMillis(Integer.parseInt(pro.getProperty(DB_MIN_IDLE_KEY)));
		DataSource ds = new DataSource();
		ds.setPoolProperties(p);
		DBConnector.db.put(name, ds);
	}
	
	final public static Connection lookup(String dsn) throws SQLException {
		return db.get(dsn).getConnection();
	}
	
	final public static int count(Connection con, String table, Object[] where) throws SQLException {
		HashMap<String, Object> row = getFirstRowOf (con, table, (char) 0, new String[]{"COUNT(*)"}, where);
		if(row == null) {
			throw new SQLException("Cannot get count of rows because ResultSet is null");
		}
		return ((BigDecimal) row.get(row.keySet().iterator().next())).intValue();
	}

	public static HashMap<String, Object> getFirstRowOf(Connection con, String table, char env, String[] cols, Object[] where) throws SQLException {
		ArrayList<HashMap<String, Object>> rows = select(con, table, cols, env, where);
		if(rows.size() == 0) {
			return null;
		}
		else {
			return rows.get(0);
		}
	}
	
	private static final void close(ResultSet rs) {
		try {
			rs.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			rs = null;
		}
	}
	
	
	private static HashMap<String, Object> nextRow (ResultSet rs) throws SQLException {
		if(next(rs)) {
			HashMap<String, Object> row = new HashMap<String, Object>();
			ResultSetMetaData rsmd = rs.getMetaData();
			int j = rsmd.getColumnCount();
			String[] cols = new String[j];
			for(int i = 0; i < j; i ++) {
				cols[i] = rsmd.getColumnName(i + 1); 
			}
			for(int i = 0; i < cols.length; i ++) {
				row.put(cols[i], rs.getObject(cols[i]));
			}
			return row;
		}
		else {
			return null;
		}
	}
	
	
	private static boolean next(ResultSet rs) {
		boolean status = false;
		if(rs != null) {
			try {
				status  = rs.next();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
			if(!status) {
				close(rs);
			}
		}
		return status;
	}
	
	static int update(Connection con, String table, String[] cols, String[] values, Object[] where) throws SQLException {
		StringBuffer sb = new StringBuffer("UPDATE ");
		envelope(sb, table, '"');
		sb.append(" SET ");
		DBConnector.join(sb, cols, values, '"', prepared);
		DBConnector.applyWhere(sb, where, prepared);
		if(DEBUG)System.out.println(sb);
		con.setAutoCommit(false);
		PreparedStatement ps = con.prepareStatement(sb.toString());
		if(prepared) {
			DBConnector.setPrepared(ps, where, DBConnector.setValues(values, ps, 1));
		}		
		int status = ps.executeUpdate();
		con.commit();
		con.setAutoCommit(true);
		return status;
	}
	
	static int insert (Connection con, String table, String[] cols, String[] values) throws SQLException {
		StringBuffer sb = new StringBuffer("INSERT INTO ");
		envelope(sb, table, '"');
		sb.append(' ');
		sb.append('(');
		DBConnector.join(sb, null, cols, '"', false);
		sb.append(')');
		sb.append(" VALUES (");
		DBConnector.join(sb, null, values, '"' , true);
		sb.append(')');
		if(DEBUG)System.out.println(sb);
		con.setAutoCommit(false);
		PreparedStatement ps = con.prepareStatement(sb.toString());
		DBConnector.setValues(values, ps, 1);
		int status = ps.executeUpdate();
		con.commit();
		con.setAutoCommit(true);
		return status;
	}
	
	static ArrayList<HashMap<String, Object>> select(Connection con, String table, String[] cols, char env, Object[] where) throws SQLException {
		StringBuffer sb = new StringBuffer("SELECT ");
		if(cols == null) {
			sb.append('*');
		}
		else {
			DBConnector.join(sb, null, cols, env, false);
		}
		sb.append(" FROM ");
		envelope(sb, table, '"');
		DBConnector.applyWhere(sb, where, prepared);
		if(DEBUG)System.out.println(sb);
		con.setAutoCommit(false);
		PreparedStatement ps = con.prepareStatement(sb.toString());
		if(prepared) {
			setPrepared(ps, where, 1);
		}
		ResultSet rs = ps.executeQuery();
		con.commit();
		con.setAutoCommit(true);
		ArrayList<HashMap<String, Object>> rows = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> row;
		while((row = nextRow(rs)) != null) {
			rows.add(row);
		};
		ps.close();
		rs.close();
		return rows;
	}

	private static void setPrepared(PreparedStatement ps, Object[] where, int index) throws SQLException {
		for(int i = 1; i < where.length; i ++) {
			ps.setObject(index, where[i]);
			index ++;
		}
		
	}

	private static int setValues(String[] values, PreparedStatement ps, int index) throws SQLException {
		for(int i = 0; i < values.length; i ++) {
			ps.setString(index, values[i]);
			index ++;
		}
		return index;
	}
	
	private static void applyWhere(StringBuffer sb, Object[] where, boolean ps) {
		if(where != null) {
			sb.append(" WHERE ");
			if(!ps) {
				for(int i = 1; i < where.length; i ++) {
					((String) where[0]).replaceFirst("\\?", "\"" + where[i].toString() + "\"");
				}
			}
			sb.append(where[0]);
		}
	}

	private static void join(StringBuffer sb, String[] cols, String[] val, char c, boolean ps) {
		for(int i = 0; i < val.length; i ++) {
			if(i > 0) {
				sb.append(',');
			}
			if(cols != null) {
				sb.append(cols[i]);
				sb.append('=');
			}
			if(ps) {
				sb.append('?');
			}
			else {
				if(c > 0) {
					envelope(sb, val[i], c);
				}
				else {
					sb.append(val[i]);
				}
			}
		}
	}
	
	static void envelope(StringBuffer sb, String s, char c) {
		if(c > 0) {
			sb.append(c);
		}
		sb.append(s);
		if(c > 0) {
			sb.append(c);
		}
	}

	public static int intValue(Object obj) {
		if(obj == null || (!(obj instanceof Integer))) {
			return DEFAULT_INT_VALUE;
		}
		return (int) obj;
	}

	public static BigDecimal bigDecimalValue(Object obj) {
		if(obj == null || (!(obj instanceof BigDecimal))) {
			return DEFAULT_BIGDECIMAL_VALUE;
		}
		return (BigDecimal) obj;
	}

	public static String toString(Object obj) {
		if(obj == null) {
			return null;
		}
		return obj.toString();
	}	
}
