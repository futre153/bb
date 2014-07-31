package com.acepricot.finance.sync;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
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
	static final String DB_URL_KEY = "com.acepricot.finance.db.url";
	static final String DB_DRIVER_CLASS_KEY = "com.acepricot.finance.db.driver";
	static final String DB_USERNAME_KEY = "com.acepricot.finance.db.user";
	static final String DB_PASSWORD_KEY = "com.acepricot.finance.db.pass";
	private static final String DB_MAX_ACTIVE_KEY = "com.acepricot.finance.db.maxactive";
	private static final String DB_INIT_SIZE_KEY = "com.acepricot.finance.db.initsize";
	private static final String DB_MAX_WAIT_KEY = "com.acepricot.finance.db.maxwait";
	private static final String DB_REM_ABANDONED_KEY = "com.acepricot.finance.db.maxabandoned";
	private static final String DB_MIN_IDLE_KEY = "com.acepricot.finance.db.minidle";
	static final String DB_DSN_KEY = "com.acepricot.finance.db.dsn";
	private static final DBConnector db = new DBConnector();
	private static final int DEFAULT_INT_VALUE = -1;
	private static final BigDecimal DEFAULT_BIGDECIMAL_VALUE = new BigDecimal(-1);
	private static final boolean DEBUG = true;
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
		p.setMaxIdle(Integer.parseInt(pro.getProperty(DB_MAX_ACTIVE_KEY)));
		DataSource ds = new DataSource();
		ds.setPoolProperties(p);
		DBConnector.db.put(name, ds);
	}
	
	public static void unbind(String dsn) {
		DataSource ds = db.remove(dsn);
		if(ds != null) {
			ds.close();
		}
	}	
	
	final public static Connection lookup(String dsn) throws SQLException {
		try {
			return db.get(dsn).getConnection();
		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	final public static int count(Connection con, String table, Object[] where) throws SQLException {
		HashMap<String, Object> row = getFirstRowOf (con, table, (char) 0, new String[]{"COUNT(*)"}, where);
		if(row == null) {
			throw new SQLException("Cannot get count of rows because ResultSet is null");
		}
		return ((BigDecimal) row.get(row.keySet().iterator().next())).intValue();
	}

	public static Row getFirstRowOf(Connection con, String table, char env, String[] cols, Object[] where) throws SQLException {
		Rows rows = select(con, table, cols, env, where);
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
	
	
	private static Row nextRow (ResultSet rs) throws SQLException {
		if(next(rs)) {
			Row row = new Row();
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
		return DBConnector.update(con, table, cols, values, where, true);
	}
	
	static int update(Connection con, String table, String[] cols, String[] values, Object[] where, boolean commit) throws SQLException {
		StringBuffer sb = new StringBuffer("UPDATE ");
		envelope(sb, null, table, '"');
		sb.append(" SET ");
		DBConnector.join(sb, table, cols, values, '"', prepared);
		DBConnector.applyWhere(sb, where, prepared);
		if(DEBUG)System.out.println(sb);
		PreparedStatement ps = con.prepareStatement(sb.toString());
		if(prepared) {
			DBConnector.setPrepared(ps, where, DBConnector.setValues(values, ps, 1));
		}		
		int status = ps.executeUpdate();
		if(commit) {
			con.commit();
		}
		return status;
	}
	
	static int insert (Connection con, String table, String[] cols, String[] values) throws SQLException {
		return DBConnector.insert(con, table, cols, values, true);
	}
	
	static int insert (Connection con, String table, String[] cols, String[] values, boolean commit) throws SQLException {
		StringBuffer sb = new StringBuffer("INSERT INTO ");
		envelope(sb, null, table, '"');
		sb.append(' ');
		sb.append('(');
		DBConnector.join(sb, table, null, cols, '"', false);
		sb.append(')');
		sb.append(" VALUES (");
		DBConnector.join(sb, null, null, values, '"' , true);
		sb.append(')');
		if(DEBUG)System.out.println(sb);
		PreparedStatement ps = con.prepareStatement(sb.toString());
		DBConnector.setValues(values, ps, 1);
		int status = ps.executeUpdate();
		if(commit) {
			con.commit();
		}
		ps.close();
		return status;
	}
	
	static int delete(Connection con, String table, Object[] where, char env) throws SQLException {
		return delete(con, table, where, env, true);
	}
	
	static int delete(Connection con, String table, Object[] where, char env, boolean commit) throws SQLException {
		StringBuffer sb = new StringBuffer("DELETE FROM ");
		envelope(sb, null, table, env);
		DBConnector.applyWhere(sb, where, prepared);
		PreparedStatement ps = con.prepareStatement(sb.toString());
		if(prepared) {
			setPrepared(ps, where, 1);
		}
		int status = ps.executeUpdate();
		if(commit) {
			con.commit();
		}
		ps.close();
		return status;
	}
	
	
	static Rows select(Connection con, String table, String[] cols, char env, Object[] where) throws SQLException {
		StringBuffer sb = new StringBuffer("SELECT ");
		if(cols == null) {
			sb.append('*');
		}
		else {
			DBConnector.join(sb, table, null, cols, env, false);
		}
		sb.append(" FROM ");
		envelope(sb, null, table, '"');
		DBConnector.applyWhere(sb, where, prepared);
		if(DEBUG)System.out.println(sb);
		PreparedStatement ps = con.prepareStatement(sb.toString());
		if(prepared) {
			setPrepared(ps, where, 1);
		}
		ResultSet rs = ps.executeQuery();
		//con.commit();
		//con.setAutoCommit(true);
		Rows rows = new Rows();
		Row row;
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

	private static void join(StringBuffer sb, String table, String[] cols, String[] val, char c, boolean ps) {
		for(int i = 0; i < val.length; i ++) {
			if(i > 0) {
				sb.append(',');
			}
			if(cols != null) {
				if(table != null) {
					sb.append(table);
					sb.append('.');
				}
				sb.append(cols[i]);
				sb.append('=');
			}
			if(ps) {
				sb.append('?');
			}
			else {
				if(c > 0) {
					envelope(sb, table, val[i], c);
				}
				else {
					sb.append(val[i]);
				}
			}
		}
	}
	
	static void envelope(StringBuffer sb, String table, String s, char c) {
		if(c > 0) {
			sb.append(c);
		}
		if(table != null) {
			sb.append(table);
			sb.append('.');
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
