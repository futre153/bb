package com.acepricot.finance.sync.client;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;

import org.h2.api.Trigger;

import com.acepricot.finance.sync.ConstraintImpl;

public class DBConnectorLt  {
	
	private static boolean prepared = true;
			
	private DBConnectorLt(){}
	
	
	final public static int count(Connection con, String table, Object[] where) throws SQLException {
		Hashtable<String, Object> row = getFirstRowOf (con, table, (char) 0, new String[]{"COUNT(*)"}, where);
		if(row == null) {
			throw new SQLException("Cannot get count of rows because ResultSet is null");
		}
		return ((BigDecimal) row.get(row.keys().nextElement())).intValue();
	}

	public static Hashtable<String, Object> getFirstRowOf(Connection con, String table, char env, String[] cols, Object[] where) throws SQLException {
		ArrayList<Hashtable<String, Object>> rows = select(con, table, cols, env, where);
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
	
	
	private static Hashtable<String, Object> nextRow (ResultSet rs) throws SQLException {
		if(next(rs)) {
			Hashtable<String, Object> row = new Hashtable<String, Object>();
			ResultSetMetaData rsmd = rs.getMetaData();
			int j = rsmd.getColumnCount();
			String[] cols = new String[j];
			for(int i = 0; i < j; i ++) {
				cols[i] = rsmd.getColumnName(i + 1); 
			}
			for(int i = 0; i < cols.length; i ++) {
				Object obj = rs.getObject(cols[i]);
				if(obj != null) {
					row.put(cols[i], obj);
				}
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
	
	static int createTable(Connection con, String schemaName, String tableName, String[] cols, String[] dataTypes, String[] cons, boolean ifNotExists, char env) throws SQLException {
		StringBuffer sb = new StringBuffer("CREATE TABLE ");
		if(ifNotExists) {
			sb.append("IF NOT EXISTS ");
		}
		envelope(sb, schemaName + "." + tableName, env);
		sb.append(" (");
		for(int i = 0; i < cols.length; i ++) {
			if(i > 0) {
				sb.append(',');
			}
			envelope(sb, cols[i], env);
			sb.append(' ');
			sb.append(dataTypes[i]);
			if(cons[i] != null) {
				sb.append(' ');
				sb.append(cons[i]);
			}
		}
		sb.append(')');
		System.out.println(sb);
		con.setAutoCommit(false);
		PreparedStatement ps = con.prepareStatement(sb.toString());
		int status = ps.executeUpdate();
		con.commit();
		con.setAutoCommit(true);
		ps.close();
		return status;
	}
	
	static int createSchema(Connection con, String schemaName, boolean ifNotExists, char env) throws SQLException {
		StringBuffer sb = new StringBuffer("CREATE SCHEMA ");
		if(ifNotExists) {
			sb.append("IF NOT EXISTS ");
		}
		envelope(sb, schemaName, env);
		System.out.println(sb);
		con.setAutoCommit(false);
		PreparedStatement ps = con.prepareStatement(sb.toString());
		int status = ps.executeUpdate();
		con.commit();
		con.setAutoCommit(true);
		ps.close();
		return status;
	}
	
	static int update(Connection con, String table, String[] cols, String[] values, Object[] where, char env) throws SQLException {
		StringBuffer sb = new StringBuffer("UPDATE ");
		envelope(sb, table, '"');
		sb.append(" SET ");
		DBConnectorLt.join(sb, cols, values, env, prepared);
		DBConnectorLt.applyWhere(sb, where, prepared);
		System.out.println(sb);
		//con.setAutoCommit(false);
		PreparedStatement ps = con.prepareStatement(sb.toString());
		if(prepared) {
			DBConnectorLt.setPrepared(ps, where, DBConnectorLt.setValues(values, ps, 1));
		}		
		int status = ps.executeUpdate();
		//con.commit();
		//con.setAutoCommit(true);
		ps.close();
		return status;
	}
	
	static int insert (Connection con, String table, String[] cols, String[] values, char env) throws SQLException {
		StringBuffer sb = new StringBuffer("INSERT INTO ");
		envelope(sb, table, env);
		sb.append(' ');
		sb.append('(');
		DBConnectorLt.join(sb, null, cols, env, false);
		sb.append(')');
		sb.append(" VALUES (");
		DBConnectorLt.join(sb, null, values, env , true);
		sb.append(')');
		System.out.println(sb);
		//con.setAutoCommit(false);
		PreparedStatement ps = con.prepareStatement(sb.toString());
		DBConnectorLt.setValues(values, ps, 1);
		int status = ps.executeUpdate();
		//con.commit();
		//con.setAutoCommit(true);
		ps.close();
		return status;
	}
	
	static ArrayList<Hashtable<String, Object>> select(Connection con, String table, String[] cols, char env, Object[] where) throws SQLException {
		StringBuffer sb = new StringBuffer("SELECT ");
		if(cols == null) {
			sb.append('*');
		}
		else {
			DBConnectorLt.join(sb, null, cols, env, false);
		}
		sb.append(" FROM ");
		envelope(sb, table, env);
		DBConnectorLt.applyWhere(sb, where, prepared);
		System.out.println(sb);
		//con.setAutoCommit(false);
		PreparedStatement ps = con.prepareStatement(sb.toString());
		if(prepared) {
			setPrepared(ps, where, 1);
		}
		ResultSet rs = ps.executeQuery();
		//con.commit();
		//con.setAutoCommit(true);
		ArrayList<Hashtable<String, Object>> rows = new ArrayList<Hashtable<String, Object>>();
		Hashtable<String, Object> row;
		while((row = nextRow(rs)) != null) {
			rows.add(row);
		};
		ps.close();
		rs.close();
		return rows;
	}

	private static void setPrepared(PreparedStatement ps, Object[] where, int index) throws SQLException {
		if(where != null) {
			for(int i = 1; i < where.length; i ++) {
				ps.setObject(index, where[i]);
				index ++;
			}
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
	
	private static void envelope(StringBuffer sb, String s, char c) {
		if(c > 0) {
			sb.append(c);
		}
		sb.append(s);
		if(c > 0) {
			sb.append(c);
		}
	}


	public static int createTrigger(Connection con, boolean ifNotExists, String schemaName, String tableName, boolean before, int type, boolean forEachRow, Class<?> _class, char env) throws SQLException {
		StringBuffer sb = new StringBuffer("CREATE TRIGGER ");
		if(ifNotExists) {
			sb.append("IF NOT EXISTS ");
		}
		envelope(sb, schemaName + "_" + tableName + "_" + type, env);
		if(before) {
			sb.append(" BEFORE ");
		}
		else {
			sb.append(" AFTER ");
		}
		switch(type) {
		case Trigger.INSERT:
			sb.append(" INSERT ");
			break;
		case Trigger.UPDATE:
			sb.append(" UPDATE ");
			break;
		case Trigger.DELETE:
			sb.append(" DELETE ");
			break;
		case Trigger.SELECT:
			sb.append(" SELECT ");
			break;
		default:
			throw new SQLException ("Trigger type not allowed");
		}
		sb.append("ON ");
		envelope(sb, schemaName + "." + tableName, env);
		if(forEachRow) {
			sb.append(" FOR EACH ROW");
		}
		sb.append(" CALL ");
		envelope(sb, _class.getName(), '"');
		System.out.println(sb);
		con.setAutoCommit(false);
		PreparedStatement ps = con.prepareStatement(sb.toString());
		int status = ps.executeUpdate();
		con.commit();
		con.setAutoCommit(true);
		ps.close();
		return status;
	}


	public static int dropTable(Connection con, boolean ifExists, String schemaName, String tableName, char env) throws SQLException {
		StringBuffer sb = new StringBuffer("DROP TABLE ");
		if(ifExists) {
			sb.append("IF EXISTS ");
		}
		envelope(sb, schemaName + "." + tableName, env);
		System.out.println(sb);
		con.setAutoCommit(false);
		PreparedStatement ps = con.prepareStatement(sb.toString());
		int status = ps.executeUpdate();
		con.commit();
		con.setAutoCommit(true);
		ps.close();
		return status;
	}


	public static int dropSchema(Connection con, boolean ifExists, String schemaName, char env) throws SQLException {
		StringBuffer sb = new StringBuffer("DROP SCHEMA ");
		if(ifExists) {
			sb.append("IF EXISTS ");
		}
		envelope(sb, schemaName, env);
		System.out.println(sb);
		con.setAutoCommit(false);
		PreparedStatement ps = con.prepareStatement(sb.toString());
		int status = ps.executeUpdate();
		con.commit();
		con.setAutoCommit(true);
		ps.close();
		return status;
	}


	public static int dropTrigger(Connection con, boolean ifExists, String schemaName, String triggerName, char env) throws SQLException {
		StringBuffer sb = new StringBuffer("DROP TRIGGER ");
		if(ifExists) {
			sb.append("IF EXISTS ");
		}
		envelope(sb, schemaName + "." + triggerName, env);
		System.out.println(sb);
		con.setAutoCommit(false);
		PreparedStatement ps = con.prepareStatement(sb.toString());
		int status = ps.executeUpdate();
		con.commit();
		con.setAutoCommit(true);
		ps.close();
		return status;
	}


	public static int alterTable(Connection con, ConstraintImpl cons, char env) throws SQLException {
		StringBuffer sb = new StringBuffer("ALTER TABLE ");
		envelope(sb, cons.getTableName(), env);
		sb.append(' ');
		cons.getSQLText(sb, env);
		System.out.println(sb);
		con.setAutoCommit(false);
		PreparedStatement ps = con.prepareStatement(sb.toString());
		int status = ps.executeUpdate();
		con.commit();
		con.setAutoCommit(true);
		ps.close();
		return status;
		
	}	
}
