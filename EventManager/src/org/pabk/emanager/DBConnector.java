package org.pabk.emanager;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.InvalidPropertiesFormatException;

import org.pabk.emanager.util.Sys;

public final class DBConnector extends HandlerImpl {
	
	private static String[] tables=null;
	private static String tableBatchFormat;
	private static final int DEFAULT_ATTEMPTS = 10;
	private static final long DEFAULT_TIMEWAIT_INTERVAL = 500;
	private static final String TABLE_EXIST = " Duplicate table name";
	private Connection con;
	//private Properties prop;
	private static Statement stat;
	private static ResultSet res;
	private static DBConnector db;
	private static boolean lock=false;
	private static Object locker;
	
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
	
	public void init(Object[] args) {
		super.init(args);
		try {
			getInstance();
		} catch (NullPointerException | IOException
				| ClassNotFoundException | SQLException e) {
			log.severe(this.getClass().getSimpleName()+" failed to connect to database");
			shutdown=true;
			lock=false;
		}
		
	}
	
	private void getInstance() throws NullPointerException,
	InvalidPropertiesFormatException, ClassNotFoundException, SQLException {
		db=this;
		Class.forName(db.pro.getProperty("Driver"));
		db.getConnection();
	}

	private synchronized void getConnection() throws SQLException {
		if(con==null) {
			con=DriverManager.getConnection(pro.getProperty("DatabaseURL"),pro);
			stat=con.createStatement();
		}
		else if(con.isClosed()) {
			con=DriverManager.getConnection(pro.getProperty("DatabaseURL"),pro);
			stat=con.createStatement();
		}
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
		log.info(sql);
		int status=stat.executeUpdate(sql);
		return status; 
	}
	
	public synchronized int delete (String tableName, String where) throws SQLException {
		String sql="DELETE FROM "+tableName;
		sql+=(where==null?"":(" "+where));
		log.info(sql);
		return stat.executeUpdate(sql);
	}
	
	
	public synchronized int insert (String tableName, String[] cName, String[] value) throws SQLException {
		String sql="INSERT INTO "+tableName;
		sql+=((cName==null)?(""):(" ("+Sys.concatenate(cName, ',')+")"));
		sql+=" VALUES";
		sql+=((value==null)?(" ()"):(" ("+Sys.concatenate(Sys.envelope(value,'\''), ',')+")"));
		//System.out.println("INSERT string\r\n"+sql+"\r\n----------------------------------------------------------");
		log.info(sql);
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
		log.info(sql);
		stat.execute(sql);
		res=stat.getResultSet();
	}
	
	public void wait(int a, long t, Object requestor) throws SQLException {
		for(int i=0;i>a;i--) {
			if(!lock) break;
			try {wait(t);} catch (InterruptedException e) {throw new SQLException(e.getMessage());}
		}
		if(lock) {
			log.warning(requestor+" trying to access "+this+" but access is locked by "+locker);
			throw new SQLException("Result set is in use");
		}
		getConnection();
	}
	
	public synchronized void next() throws SQLException {
		res.next();
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
			lock=false;
			log.info(locker+" UNLOCK the access to "+db);
			locker=null;
		}
		
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
	
	public synchronized static DBConnector getDb(boolean lock, Object locker) throws SQLException {
		if(!lock)return db;
		db.wait(DEFAULT_ATTEMPTS,DEFAULT_TIMEWAIT_INTERVAL,locker);
		DBConnector.lock=true;
		DBConnector.locker=locker;
		db.log.info(locker+" LOCK the access to "+db);
		return db;
	}
	
	
	@Override
	public void businessLogic() {
		tableBatchFormat=pro.getProperty(Const.TABLE_BATCH_FORMAT_KEY);
		
		if(tableBatchFormat==null) {
			tableBatchFormat=Const.DEFAULT_TABLE_BATCH_FORMAT;
			log.info("Used Default table batch format: "+tableBatchFormat);
		}
		else {
			log.info("Defined table batch format server: "+tableBatchFormat);
		}
		
		String tmp=pro.getProperty(Const.TABLES_KEY);
		if(tmp==null) {
			tmp=Const.DEFAULT_TABLES;
			log.info("Used Default tables: "+tmp);
		}
		else {
			log.info("Load tables: "+tmp);
		}
		try {
			tables=tmp.split(pro.getProperty(Const.TABLES_SEPARATOR_KEY));
		}
		catch(Exception e) {
			tables=tmp.split(Const.TABLES_SEPARATOR);
		}
		for(int i=0;i<tables.length;i++) {
			try {
				executeBatch(String.format(tableBatchFormat, tables[i]));
				log.info("table "+tables[i]+" has been loaded successfully");
			} catch (IOException | SQLException e) {
				if(!e.getMessage().contains(TABLE_EXIST)) {
					log.severe(this.getClass().getSimpleName()+" failed to load tables");
					shutdown=true;
					lock=false;
				}
				else {
					log.info("table "+tables[i]+" already exixts and will not be loaded");
				}
			}
		}
		
		while(!shutdown) {
			sleep=new Sleeper();
			log.info("Module "+this.getClass().getName()+" goes to SLEEP");
			sleep.sleep(0);
			log.info("Module "+this.getClass().getName()+" WAKE UP now");
		}
		
	}
}
