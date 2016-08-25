package org.pabk.web.db;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.pabk.emanager.sql.sap.Delete;
import org.pabk.emanager.sql.sap.Identifier;
import org.pabk.emanager.sql.sap.Insert;
import org.pabk.emanager.sql.sap.Query;
import org.pabk.emanager.sql.sap.QueryExp;
import org.pabk.emanager.sql.sap.QueryPrimary;
import org.pabk.emanager.sql.sap.QuerySpec;
import org.pabk.emanager.sql.sap.QueryTerm;
import org.pabk.emanager.sql.sap.SQLSyntaxImpl;
import org.pabk.emanager.sql.sap.Select;
import org.pabk.emanager.sql.sap.SelectColumn;
import org.pabk.emanager.sql.sap.TableExp;
import org.pabk.emanager.sql.sap.TableName;
import org.pabk.emanager.sql.sap.Update;
import org.pabk.emanager.sql.sap.WhereClause;

public class DBConnector {

	private static final int DEFAULT_INT_VALUE = -1;
	private static final BigDecimal DEFAULT_BIGDECIMAL_VALUE = new BigDecimal(-1);
	private static final boolean DEBUG = true;
	private static final String COUNT_FUNCTION = "COUNT";
	private static final String JAVA_CONTEXT = "java:comp/env";
	@SuppressWarnings("unused")
	private static boolean prepared = true;

	private static Context context;
	private static BasicDataSource ds;
	
	private DBConnector(){}
		
	
	/*
	final public static void bind(Properties pro, String name) throws IOException {
		try {
			//load settings
			String driver = (String) Sys.getProperty(pro, Const.DB_DRIVER_CLASS_KEY, null, true, String.class, null);
			String url = (String) Sys.getProperty(pro, Const.DB_URL_KEY, null, true, String.class, null);
			String username = (String) Sys.getProperty(pro, Const.DB_USERNAME_KEY, null, true, String.class, null);
			String password = (String) Sys.getProperty(pro, Const.DB_PASSWORD_KEY, null, true, String.class, null);*/
			/*int initialSize = (int) Sys.getProperty(pro, Const.DB_POOL_INIT_SIZE_KEY, Const.DB_POOL_INIT_SIZE_VALUE, true, int.class, null);
			int maxTotal = (int) Sys.getProperty(pro, Const.DB_POOL_MAX_TOTAL_KEY, Const.DB_POOL_MAX_TOTAL_VALUE, true, int.class, null);
			int minIdle = (int) Sys.getProperty(pro, Const.DB_POOL_MIN_IDLE_KEY, Const.DB_POOL_MIN_IDLE_VALUE, true, int.class, null);
			int maxIdle = (int) Sys.getProperty(pro, Const.DB_POOL_MAX_IDLE_KEY, Const.DB_POOL_MAX_IDLE_VALUE, true, int.class, null);
			int maxWaitMillis = (int) Sys.getProperty(pro, Const.DB_POOL_MAX_WAIT_MILLIS_KEY, Const.DB_POOL_MAX_WAIT_MILLIS_VALUE, true, int.class, null);*/
			//load drivers
		/*	if(!loadedDrivers.contains(driver)) {
				Class.forName(driver);
			}
			
			PoolableConnectionFactory poolableFactory = new PoolableConnectionFactory(new DriverManagerConnectionFactory(url, username, Huffman.decode(password, null)), null);
			ObjectPool<PoolableConnection> connectionPool = new GenericObjectPool<>(poolableFactory);
			poolableFactory.setPool(connectionPool);
			PoolingDataSource<PoolableConnection> dataSource = new PoolingDataSource<>(connectionPool);
			DBConnector.connector.put(name, dataSource);
		}
		catch(Exception e) {
			throw(new IOException(e));
		}
	}
	*/
	/*
	public static void unbind(String dsn) {
		PoolingDataSource<PoolableConnection> ds = connector.remove(dsn);
		if(ds != null) {
			try {
				ds.close();
			} catch (Exception e) {}
		}
	}	
	*/
	final public static Connection lookup(String dsn) throws SQLException {
		try {
			if(context == null) {
				context = new InitialContext();
				context = (Context) context.lookup(JAVA_CONTEXT);
			}
			if (ds == null) {
				ds = (BasicDataSource) context.lookup(dsn);
			}
			Connection con = ds.getConnection();
			con.setAutoCommit(false);
			return con;
		}
		catch(Exception e) {
			throw new SQLException (e);
		}
	}
	
	final public static int count(Connection con, TableName table, WhereClause where) throws SQLException {
		Query q = DBConnector.createSelect();
		q.addFromClause(table);
		q.addTableSpec(where);
		q.addSelectColumnFunction(COUNT_FUNCTION);
		q.setDerived(COUNT_FUNCTION);
		Rows rows = DBConnector.select(con, q);
		if(rows == null || rows.size() == 0) {
			throw new SQLException("Cannot get count of rows because ResultSet is null");
		}
		return ((BigDecimal) rows.get(0).get(COUNT_FUNCTION)).intValue();
	}
	/*
	final public static int count(Connection con, String table, Object[] where) throws SQLException {
		HashMap<String, Object> row = getFirstRowOf (con, table, (char) 0, new String[]{"COUNT(*)"}, where);
		if(row == null) {
			throw new SQLException("Cannot get count of rows because ResultSet is null");
		}
		return ((BigDecimal) row.get(row.keySet().iterator().next())).intValue();
	}
*/
	public static Row getFirstRowOf(Connection con, Query select) throws SQLException {
		Rows rows = select(con, select);
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
				cols[i] = rsmd.getColumnLabel(i + 1);
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
	
	public static int update(Connection con, Update update) throws SQLException {
		return DBConnector.update(con, update, true);
	}
	
	public static int update(Connection con, Update update, boolean commit) throws SQLException {
		String sql = update.toSQLString(null);
		if(DEBUG) System.out.println(sql);
		PreparedStatement ps = con.prepareStatement(sql);
		if(SQLSyntaxImpl.isPrepared()) {
			update.getPreparedBuffer().setAll(ps);
		}
		int status = ps.executeUpdate();
		if(commit) {
			con.commit();
		}
		ps.close();
		return status;
	}
	/*
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
	*/
	public static int insert (Connection con, Insert insert) throws SQLException {
		return DBConnector.insert(con, insert, true);
	}
	
	public static int insert (Connection con, Insert insert, boolean commit) throws SQLException {
		String sql = insert.toSQLString(null);
		if(DEBUG) System.out.println(sql);
		PreparedStatement ps = con.prepareStatement(sql);
		if(SQLSyntaxImpl.isPrepared()) {
			insert.getPreparedBuffer().setAll(ps);
		}
		int status = ps.executeUpdate();
		if(commit) {
			con.commit();
		}
		ps.close();
		return status;
	}
	
	/*
	
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
	*/
	public static int delete(Connection con, Delete delete) throws SQLException {
		return delete(con, delete, true);
	}
	
	public static int delete(Connection con, Delete delete, boolean commit) throws SQLException {
		String sql = delete.toSQLString(null);
		if(DEBUG) System.out.println(sql);
		PreparedStatement ps = con.prepareStatement(sql);
		if(SQLSyntaxImpl.isPrepared()) {
			delete.getPreparedBuffer().setAll(ps);
		}
		int status = ps.executeUpdate();
		if(commit) {
			con.commit();
		}
		ps.close();
		return status;
	}
	
	
	/*
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
	*/
	public static Query createSelect() throws SQLException {
		Query q = new Query(new Select(new QueryExp(new QueryTerm(new QueryPrimary(new QuerySpec(new SelectColumn(), new TableExp()))))));
		return q;
	}
	
	public static Rows select(Connection con, Query select) throws SQLException {
		String sql = select.toSQLString(null);
		if(DEBUG) System.out.println(sql);
		PreparedStatement ps = con.prepareStatement(sql);
		if(SQLSyntaxImpl.isPrepared()) {
			select.getPreparedBuffer().setAll(ps);
		}
		ResultSet rs = ps.executeQuery();
		Rows rows = new Rows();
		Row row;
		while((row = nextRow(rs)) != null) {
			rows.add(row);
		};
		ps.close();
		rs.close();
		return rows;

	}
	/*
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
	*/
	/*
	private static void setPrepared(PreparedStatement ps, Object[] where, int index) throws SQLException {
		for(int i = 1; i < where.length; i ++) {
			ps.setObject(index, where[i]);
			index ++;
		}
		
	}*/
	/*
	private static int setValues(String[] values, PreparedStatement ps, int index) throws SQLException {
		for(int i = 0; i < values.length; i ++) {
			ps.setString(index, values[i]);
			index ++;
		}
		return index;
	}
	*/
	/*
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
	}*/
	/*
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
	*/
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

	public static Insert createInsert(TableName tableName, Object values, String ...strings) throws SQLException {
		Insert insert = new Insert(tableName, values, strings);
		return insert;
	}

	public static Update createUpdate(TableName tableName, String[] cols, Object[] values, WhereClause whereClause) throws SQLException {
		Update update = new Update(tableName, values, cols);
		update.setWhereClause(whereClause);
		return update;
	}
	
	public static Delete createDelete(TableName tableName, String referenceName, WhereClause where) throws SQLException {
		Delete delete;
		if(referenceName == null) {
			delete = new Delete(tableName, where);
		}
		else {
			delete = new Delete(tableName, new Identifier(referenceName), where);
		}
		return delete;
	}

}
