package org.acepricot;

import java.awt.AWTException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;


public class Test {
	
		
	public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException, AWTException {
		
		String[] x = {""};
		String[] y = {""};
		
		
		System.out.println(x.hashCode());
		System.out.println(y.hashCode());
		
		System.exit(0);
		
		
		System.out.println(System.getProperty("os.name"));
		System.out.println(System.getProperty("os.name").toUpperCase().contains(("LINUX")));
		/*String[] pgm = null;
		if(System.getProperty("os.name").toUpperCase().contains(("WINDOWS"))) {
			pgm = new String[] {"net", "start", "symmetricds"};
		}
		else if(System.getProperty("os.name").toUpperCase().contains(("LINUX"))) {
			pgm = new String[] {"sudo", "service", "sym_service", "restart"};
		}
		else {
			throw new IOException("Unknown OS");
		}
		try {
			exec(pgm);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pgm = null;
		
		System.out.println("Test file creating>");
		String filename = "/opt/h2/db/file.txt";
		File f = new File(filename);
		if(f.createNewFile()) {
			System.out.println(filename + " was successfully created");
		}
		else {
			System.out.println(filename + " did not created");
			throw new IOException(filename + " did not created");
		}
		FileOutputStream out = new FileOutputStream(f);
		out.write(filename.getBytes());
		out.close();*/
		
		final String DRIVER_KEY = "Driver";
		
		//private static final String DEFAULT_DRIVER = "org.h2.Driver";
		final String DEFAULT_DRIVER = "com.mysql.jdbc.Driver";
		
		
		final String URL_KEY = "DatabaseUrl";
		
		//private static final String DEFAULT_URL = "jdbc:h2:tcp://localhost/~/aceserver;AUTO_SERVER=TRUE;LOCK_TIMEOUT=60000";
		final String DEFAULT_URL = "jdbc:mysql://"+ args[0] +"/aceserver";	
		
		final String USER_KEY = "user";
		String DEFAULT_USER = "acesvradm";
		if (args.length > 1) {
			DEFAULT_USER += ("@" + args[1]); 
		}
		final String PASSWORD_KEY = "password";
		
		//private static final String DEFAULT_PASSWORD = "";
		final String DEFAULT_PASSWORD = "nahradnik06";
		
		@SuppressWarnings("unused")
		final String NULL_USER = "";
		@SuppressWarnings("unused")
		final String USER_PASSWORD = "cnuewf092no ptraajtn39ln";
		@SuppressWarnings("unused")
		final String UNKNOWN_SQL_OPERATION = "Unknown SQL operation";
		//private static String[] tables=null;
		//private static String tableBatchFormat;
		//private static final int DEFAULT_ATTEMPTS = 10;
		//private static final long DEFAULT_TIMEWAIT_INTERVAL = 500;
		//private static final String TABLE_EXIST = " Duplicate table name";
		Connection con = null;
		//private Properties prop;
		Statement stat = null;
		ResultSet res;
		Properties pro;
		
		
		pro = new Properties();
		pro.setProperty(DRIVER_KEY, DEFAULT_DRIVER);
		pro.setProperty(URL_KEY, DEFAULT_URL);
		pro.setProperty(USER_KEY, DEFAULT_USER);
		pro.setProperty(PASSWORD_KEY, DEFAULT_PASSWORD);
		Class.forName(pro.getProperty(DRIVER_KEY));
		con = DriverManager.getConnection(pro.getProperty(URL_KEY), pro);
		stat=con.createStatement();
		/*String sql="INSERT INTO REGISTERED_SUBJECTS ";
		sql+="(ID, EMAIL, PASSWORD, ENABLED, DB_VERSION)";
		sql+=" VALUES ";
		sql+="('SERV0000','futre@szm.sk','0102030405060708091011121314151617181920212223242526272829303132','1','1.0')";
		System.out.println("INSERT string\r\n"+sql+"\r\n----------------------------------------------------------");
		
		int status=stat.executeUpdate(sql);
		System.out.println("status = " + status);
		*/
		
		String sql = "SELECT * FROM REGISTERED_SUBJECTS WHERE ID = 'SERV0000'";
		System.out.println("SELECT string\r\n"+sql+"\r\n----------------------------------------------------------");
		boolean status = stat.execute(sql);
		System.out.println(status);
		res = stat.getResultSet();
		if(res.isBeforeFirst()) {
			res.next();
		}
		for(int i = 1; i < 6; i++) {
			Object obj = res.getObject(i);
			System.out.println("Object" + i + ": '" + obj.toString() + "': " + obj.getClass().getName());
		}
		res.close();
		con.close();
	}
	
	@SuppressWarnings("unused")
	private static void exec(String[] pgm) throws IOException {
		Process p = Runtime.getRuntime().exec(pgm);
		int status;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayOutputStream err = new ByteArrayOutputStream();
		Thread tins = new ChannelReader(p.getInputStream(), out);
		tins.setDaemon(true);
		Thread terr = new ChannelReader(p.getErrorStream(), err);
		terr.setDaemon(true);
		tins.start();
		terr.start();
		try {
			status = p.waitFor();
		} catch (InterruptedException e) {
			status = 1;
		}
		p.destroy();
		if(tins.isAlive()) {
			tins.interrupt();
		}
		if(terr.isAlive()) {
			terr.interrupt();
		}
		System.out.println(out.toString());
		System.err.println(err.toString());
		if(status != 0) {
			throw new IOException("Failed to load symmetric tables");
		}
		
	}

	
	
}
