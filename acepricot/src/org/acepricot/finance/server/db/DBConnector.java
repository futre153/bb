package org.acepricot.finance.server.db;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import org.acepricot.finance.server.ws.MessageProcessor;
import org.acepricot.finance.web.msgs.AceData;
import org.acepricot.finance.web.util.Sys;

public final class DBConnector {
	
	private static final String DRIVER_KEY = "Driver";
	
	private static final String USER_DEFAULT_DRIVER = "org.h2.Driver";
	private static final String DEFAULT_DRIVER = "com.mysql.jdbc.Driver";
	
	
	private static final String URL_KEY = "DatabaseUrl";
	
	//private static final String DEFAULT_URL = "jdbc:h2:tcp://localhost/~/aceserver;AUTO_SERVER=TRUE;LOCK_TIMEOUT=60000";
	private static final String DEFAULT_URL = "jdbc:mysql://acepricot/aceserver";	
	
	private static final String USER_KEY = "user";
	private static final String DEFAULT_USER = "acesvradm";
	private static final String PASSWORD_KEY = "password";
	
	//private static final String DEFAULT_PASSWORD = "";
	private static final String DEFAULT_PASSWORD = "nahradnik06";
	
	private static final String NULL_USER = "";
	private static final String USER_PASSWORD = "cnuewf092no ptraajtn39ln";
	private static final String UNKNOWN_SQL_OPERATION = "Unknown SQL operation";
	//private static String[] tables=null;
	//private static String tableBatchFormat;
	//private static final int DEFAULT_ATTEMPTS = 10;
	//private static final long DEFAULT_TIMEWAIT_INTERVAL = 500;
	//private static final String TABLE_EXIST = " Duplicate table name";
	private Connection con;
	//private Properties prop;
	private Statement stat;
	private ResultSet res;
	private Properties pro;
	private static DBConnector db;
	private static Hashtable<String, DBConnector> dbs = new Hashtable<String, DBConnector>();
	
	
	
	public int[] executeBatch(String sqlBatch) throws SQLException, IOException {
		FileInputStream in=new FileInputStream(sqlBatch);
		byte[] b=new byte[in.available()];
		int i=in.read(b);
		in.close();
		String sql=new String(b,0,i);
		String[] s=sql.split(System.getProperty("line.separator"));
		//System.out.println(Arrays.toString(s));
		for(int j=0;j<s.length;j++) {
			stat.addBatch(s[j]);
		}
		return stat.executeBatch();
	}
	
	private static DBConnector getInstance() throws SQLException {
		DBConnector db = new DBConnector();
		db.pro = new Properties();
		db.pro.setProperty(DRIVER_KEY, DEFAULT_DRIVER);
		db.pro.setProperty(URL_KEY, DEFAULT_URL);
		db.pro.setProperty(USER_KEY, DEFAULT_USER);
		db.pro.setProperty(PASSWORD_KEY, DEFAULT_PASSWORD);
		try {
			Class.forName(db.pro.getProperty(DRIVER_KEY));
		}
		catch (ClassNotFoundException e) {
			throw new SQLException("JDBC Driver not found");
		}
		db.getConnection(db.pro);
		return db;
	}

	private synchronized void getConnection(Properties pro2) throws SQLException {
		if(con==null) {
			con = DriverManager.getConnection(pro.getProperty(URL_KEY), pro);
			//con=DriverManager.getConnection("jdbc:h2:tcp://localhost/~/aceserver;AUTO_SERVER=TRUE;LOCK_TIMEOUT=60000","acesvradm","");
			stat = null;
			stat=con.createStatement();
		}
		else if(con.isClosed()) {
			try {
				con=DriverManager.getConnection(pro.getProperty("DatabaseURL"),pro);
				//con=DriverManager.getConnection("jdbc:h2:tcp://localhost/~/aceserver;AUTO_SERVER=TRUE;LOCK_TIMEOUT=60000","acesvradm","");
				stat = null;
				stat=con.createStatement();
			}
			catch(SQLException e) {
				try {
					close();
					con.close();
				}
				catch (Exception e1) {
					//log here
				}
				res = null;
				con = null;
				getConnection(pro2);
			}
		}
	}
	
	public synchronized int updateWithPreparedStatement(String tableName, String[] cName, String[] value, String where) throws SQLException {
		String sql="UPDATE "+tableName;
		for(int i=0;i<cName.length;i++) {
			if(i>0) {sql+=",";}
			else {sql+=" SET ";}
			sql+=(cName[i]+"=?");
		}
		sql+=(where==null?"":(" "+where));
		//System.out.println("UPDATE string\r\n"+sql+"\r\n----------------------------------------------------------");
		PreparedStatement psmt = con.prepareStatement(sql);
		for(int i = 0; i < value.length; i++) {
			psmt.setString(i + 1, value[i]);
		}
		return psmt.executeUpdate();
	}
	
	public synchronized int update (String tableName, String[] cName, String[] value, String where) throws SQLException {
		String sql="UPDATE "+tableName;
		for(int i=0;i<cName.length;i++) {
			if(i>0) {sql+=",";}
			else {sql+=" SET ";}
			sql+=(cName[i]+"='"+value[i]+"'");
		}
		sql+=(where==null?"":(" "+where));
		//System.out.println("UPDATE string\r\n"+sql+"\r\n----------------------------------------------------------");
		//log.info(sql);
		int status=stat.executeUpdate(sql);
		return status; 
	}
	
	public synchronized int delete (String tableName, String where) throws SQLException {
		String sql="DELETE FROM "+tableName;
		sql+=(where==null?"":(" "+where));
		//log.info(sql);
		return stat.executeUpdate(sql);
	}
	
	public synchronized int insertWithPreparedStatement (String tableName, String[] cName, String[] value) throws SQLException {
		String sql="INSERT INTO "+tableName;
		sql+=((cName==null)?(""):(" ("+Sys.concatenate(cName, ',')+")"));
		sql+=" VALUES";
		String[] a = new String [value.length];
		for(int i = 0; i<value.length; i++) {
			a[i] = "?";
		}
		sql+=((value==null)?(" ()"):(" ("+Sys.concatenate(a, ',')+")"));
		//System.out.println("INSERT string\r\n"+sql+"\r\n----------------------------------------------------------");
		//log.info(sql);
		PreparedStatement psmt = con.prepareStatement(sql);
		for(int i = 0; i < value.length; i++) {
			psmt.setString(i + 1, value[i]);
		}
		int status=psmt.executeUpdate();
		return status;
	}
	
	public synchronized int insert (String tableName, String[] cName, String[] value) throws SQLException {
		String sql="INSERT INTO "+tableName;
		sql+=((cName==null)?(""):(" ("+Sys.concatenate(cName, ',')+")"));
		sql+=" VALUES";
		sql+=((value==null)?(" ()"):(" ("+Sys.concatenate(Sys.envelope(value,'\''), ',')+")"));
		//System.out.println("INSERT string\r\n"+sql+"\r\n----------------------------------------------------------");
		//log.info(sql);
		int status=stat.executeUpdate(sql);
		return status;
	}
	
	/*
	public synchronized void select(String tableName, String[] cName, String where) throws SQLException {
		select(tableName, cName, where, DEFAULT_ATTEMPTS, DEFAULT_TIMEWAIT_INTERVAL);
	}
	*/
	public synchronized void select(String tableName, String[] cName, String where) throws SQLException {
		String sql="SELECT "+(cName==null?"*":Sys.concatenate(cName, ','))+" FROM "+tableName+(where==null?"":(" "+where));
		//System.out.println("SELECT string\r\n"+sql+"\r\n----------------------------------------------------------");
		//log.info(sql);
		stat.execute(sql);
		res=stat.getResultSet();
	}
	
	public synchronized void next() {
		try {res.next();} catch (SQLException e) {}
	}
	
	public synchronized void close() {
		if(res!=null) {
			try {
				res.close();
			}
			catch (SQLException e) {
				
			}
			//System.out.print("RESULTSET RESET ");
			res=null;
		}
		
	}
	
	public synchronized Hashtable<String, Object> getRowAsHashtable(String[] label) throws SQLException {
		try {
			Hashtable<String, Object> row = new Hashtable<String, Object>();
			for(int i = 0; i < label.length; i++) {
				row.put(label[i], res.getObject(label[i]));
			}
			return row;
		}
		catch(Exception e) {throw new SQLException(e.getMessage());}
	}
	
	public synchronized Object[] getRow(String[] label) throws SQLException {
		Object[] result=new Object[label.length];
		try {
			for(int i=0;i<label.length;i++) {result[i]=res.getObject(label[i]);}
		}
		catch(Exception e) {throw new SQLException(e.getMessage());}
		return result;
	}
	
	public synchronized Object getObject(String label) throws SQLException {
		Object result=null;
		try {
			result=res.getObject(label);
		}
		catch (Exception e) {throw new SQLException(e.getMessage());}
		if(result == null) throw new SQLException("Result is void");
		return result;
	}
	
	public synchronized Object getObjectIgnoreNull(String label) throws SQLException {
		Object result=null;
		try {
			result=res.getObject(label);
		}
		catch (Exception e) {throw new SQLException(e.getMessage());}
		return result;
	}
	
	public synchronized static DBConnector getDb() throws SQLException {
		if(db == null) {
			db = getInstance();
		}
		db.getConnection(db.pro);
		return db;
	}

	public static void multiConnector(String svid, String operation, Hashtable<?, ?> table) throws ClassNotFoundException, SQLException {
		DBConnector db = findConnector(svid);
		String tableName = (String) table.get(AceData.SQL_TABLE_NAME_KEY);
		String where = (String) table.get(AceData.SQL_WHERE_KEY);
		if(operation.equals(AceData.SQL_OPERATION_DELETE)) {
			db.delete(tableName, where);
		}
		else {
			String[] cName = new String[table.size() - (where == null?1:2)];
			String[] value = new String[table.size() - (where == null?1:2)];
			Enumeration<?> e = table.keys();
			int i = 0;
			while(e.hasMoreElements()) {
				String key = (String) e.nextElement();
				if(!(key.equals(AceData.SQL_WHERE_KEY) || key.equals(AceData.SQL_TABLE_NAME_KEY))) {
					cName[i] = key;
					value[i] = (String) table.get(key);
				}
			}
			if(operation.equals(AceData.SQL_OPERATION_INSERT)) {
				db.insertWithPreparedStatement(tableName, cName, value);
			}
			else if(operation.equals(AceData.SQL_OPERATION_UPDATE)) {
				db.updateWithPreparedStatement(tableName, cName, value, where);
			}
			else {
				throw new SQLException(UNKNOWN_SQL_OPERATION);
			}
		}
		db.close();
	}

	private static DBConnector findConnector(String svid) throws ClassNotFoundException, SQLException {
		DBConnector db = dbs.get(svid);
		if(db == null) {
			db = new DBConnector();
			db.pro = new Properties();
			db.pro.setProperty(DRIVER_KEY, USER_DEFAULT_DRIVER);
			db.pro.setProperty(URL_KEY, MessageProcessor.getUrl(svid));
			db.pro.setProperty(USER_KEY, NULL_USER);
			db.pro.setProperty(PASSWORD_KEY, USER_PASSWORD);
			Class.forName(db.pro.getProperty(DRIVER_KEY));
			db.getConnection(db.pro);
			dbs.put(svid, db);
			return db;
		}
		else {
			db.getConnection(db.pro);
		}
		return db;
	}
	
}
