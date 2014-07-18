package org.acepricot;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Properties;

import org.acepricot.finance.client.MessageProcessor;
import org.jumpmind.symmetric.ClientSymmetricEngine;
import org.jumpmind.symmetric.ISymmetricEngine;

public class EasyStarter {
	
	private static final String ERROR = "ERROR";
	private static final Object NOFILE = "NOFILE";
	
	private static final String PROPATH = "org.acepricot.sync.propath";
	private static final String EMAIL = "org.acepricot.sync.email";
	private static final String PASS = "org.acepricot.sync.password";
	private static final String DBVER = "org.acepricot.sync.dbVersion";
	private static final String SVID = "org.acepricot.sync.serverID";
	private static final String CLID = "org.acepricot.sync.clientID";
	private static final String DBPATH = "org.acepricot.sync.dbpath";
	private static final String DBURL = "db.url";
	private static final String DBUSER = "db.user";
	private static final String DBPASS = "db.password";

	public static void main(String[] args) throws IOException {
		if(args.length < 5 || args.length > 6) {
			System.out.println("Usage:");
			System.out.println("EasyStarter proPath action dburl user password [dbFilePath]");
			System.out.println("where");
			System.out.println("proPath - path of synchronization properties for this device");
			System.out.println("action - action Id as follows:");
			System.out.println("dburl - url of the local DB");
			System.out.println("user - user of the local DB");
			System.out.println("password - user password");
			System.out.println("dbFilePath - path of DB file");
			System.out.println("\t1 - register + synchronize first device");
			System.out.println("\t2 - login + synchronize next device");
			System.out.println("\t3 - start engine");
			System.exit(1);
		}
		/*
		final String krbfile = "D:/Temp/krb5.conf";
	    final String loginfile = "D:/Temp/login.conf";
	    
	    System.setProperty("java.security.krb5.conf", krbfile);
	    System.setProperty("java.security.auth.login.config", loginfile);
		*/
		Properties prop = loadProps(args[0]);
		
		prop.setProperty(DBURL, args[2]);
		if(args[3].equals("@")) args[3] = "";
		prop.setProperty(DBUSER, args[3]);
		if(args[4].equals("@")) args[4] = "";
		prop.setProperty(DBPASS, args[4]);
		switch(Integer.parseInt(args[1])) {
		case 1:
			if(args.length != 6) throw new IOException("Path of DB file is mandatory for this case");
			prop.setProperty(DBPATH, args[5]);
			syncFirstDevice(prop);
			break;
		case 2:
			syncNextDevice(prop);
			break;
		case 3:
			break;
		default:
			throw new IOException("unknown action");
		}
		
		System.setProperty("java.io.tmpdir", "C:\\BB\\temp");
		System.setProperty("log4j.configuration", "file:C:\\BB\\symetric-ds\\conf\\log4j.xml");
		
		ISymmetricEngine engine = new ClientSymmetricEngine(loadProps(args[0]), true);
		System.out.println("Client symmetric engine start");
		engine.start();
		
		readLine("");
		
		//engine.stop();
		//System.out.println("Client symmetric engine stopped");
	}
	
	private static void syncNextDevice(Properties prop) throws IOException {
		loginAndInitSynchronization(prop);
		//uploadDBFile(prop);
		getConfig(prop);		
	}

	private static void loginAndInitSynchronization(Properties prop) throws IOException {
		String email = null;
		while((email = readLine("Enter registering e-mail")).length() == 0);
		String pass = readLine("Enter password");
		String svid = null;
		while((svid = readLine("Enter server ID for " + email)).length() == 0);
		String dbVer = "1";
		MessageProcessor.initSynchronization(svid, email, pass, dbVer);
		processError();
		prop.setProperty(EMAIL, email);
		prop.setProperty(PASS, pass);
		prop.setProperty(DBVER, dbVer);
		prop.setProperty(SVID,  svid);
		prop.setProperty(CLID,  MessageProcessor.getMessage());
		saveProps(prop);
		System.out.println("Client ID " + MessageProcessor.getMessage() + " has been"
				+ " assigned for this device. Synchronization was initiated for this device.");
	}

	private static Properties loadProps(String filename) throws IOException {
		Properties p = new Properties();
		File f = new File(filename);
		if(f.exists()) {
			FileInputStream in = new FileInputStream(f);
			p.load(in);
			in.close();
		}
		p.setProperty(PROPATH, f.getAbsolutePath());
		return p;
	}

	private static String readLine(String text) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print(text + ": ");
        String output = reader.readLine();
        if(output == null) throw new IOException("input failed");
        return output;
	}
	
	public static void syncFirstDevice(Properties prop) throws IOException {
		registerSubject(prop);
		initSynchronization(prop);
		uploadDBFile(prop);
		getConfig(prop);
	}
	
	private static void getConfig(Properties prop) throws IOException {
		String clid = prop.getProperty(CLID);
		MessageProcessor.requestSyncConfig(clid);
		processError();
		String config = String.format(MessageProcessor.getConfigFile(),
				prop.getProperty(DBURL), prop.getProperty(DBUSER), prop.getProperty(DBPASS));
		Properties newp = new Properties();
		ByteArrayInputStream in = new ByteArrayInputStream(config.getBytes());
		newp.load(in);
		prop.putAll(newp);
		saveProps(prop);
		System.out.println("Synchronization was started on server. Config file:");
		System.out.println(config);
	}

	private static void uploadDBFile(Properties prop) throws IOException {
		File dbfile = new File(prop.getProperty(DBPATH));
		if(!dbfile.exists()) throw new IOException("DB File does not exists!");
		String svid = prop.getProperty(SVID);
		MessageProcessor.upload(svid, dbfile);
		processError();
		if(!MessageProcessor.getMessage().equals(NOFILE)) {
			String clid = prop.getProperty(CLID);
			int max = 16*1024;
			Date t1 = new Date();
			byte[] b = new byte[max];
			FileInputStream in = new FileInputStream(dbfile);
			int i;
			int counter = 1;
			System.out.print("file upload progress ... ");
			while((i = in.read(b)) >=0) {
				byte[] a = new byte[i];
				System.arraycopy(b, 0, a, 0, i);
				MessageProcessor.uploadFilepart(clid, svid,  counter, a);
				processError();
				counter++;
				System.out.print(">");
			}
			System.out.println(" 100% ");
			in.close();
			System.out.print("File " + dbfile + " was successfully upload to the server."
					+ " Upload time was ");
			System.out.println(((new Date().getTime() - t1.getTime())/1000) + " sec");
		}
		else {
			System.out.println("Database file is not need to upload. "
					+ "Please remove all data from database as synchronization "
					+ "fill it with datas from server database.");
		}
	}

	private static void initSynchronization(Properties prop) throws IOException {
		String svid = prop.getProperty(SVID);
		String email = prop.getProperty(EMAIL);
		String pass = prop.getProperty(PASS);
		String dbVer = prop.getProperty(DBVER);
		MessageProcessor.initSynchronization(svid, email, pass, dbVer);
		processError();
		prop.setProperty(CLID,  MessageProcessor.getMessage());
		saveProps(prop);
		System.out.println("Client ID " + MessageProcessor.getMessage() + " has been"
				+ " assigned for this device. Synchronization was initiated for this device.");
	}

	public static void registerSubject(Properties pro) throws IOException {
		String email = null;
		while((email = readLine("Enter registering e-mail")).length() == 0);
		String pass = readLine("Enter password");
		String dbVer = "1";
		MessageProcessor.register(email, pass, dbVer);
		processError();
		pro.setProperty(EMAIL, email);
		pro.setProperty(PASS, pass);
		pro.setProperty(DBVER, dbVer);
		pro.setProperty(SVID,  MessageProcessor.getMessage());
		saveProps(pro);
		System.out.println("Registration for " + email +
				" was successfull. Server ID " + MessageProcessor.getMessage() +
				" was assigned to " + email + ".");
	}

	private static void saveProps(Properties pro) throws IOException {
		String filename = pro.getProperty(PROPATH);
		File tmp = new File(filename + ".tmp");
		FileOutputStream out = new FileOutputStream(tmp);
		pro.store(out, "Synchronzation properties");
		out.close();
		File f = new File(filename);
		if(f.exists()) {
			if(!f.delete()) {
				throw new IOException("Failed to save properties");
			}
		}
		tmp.renameTo(f);
	}

	private static void processError() throws IOException {
		if(MessageProcessor.getStatus().equals(ERROR)) {
			System.err.println();
			System.err.println("Error while run application");
			System.err.println("Error message: " + MessageProcessor.getMessage());
			System.err.println();
			throw new IOException(MessageProcessor.getMessage());
		}
	}
}
