package org.pabk.emanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.pabk.emanager.routing.Distribution;

public class Loader {
	
	private static Logger mainLog;
	//private static Properties mainPro;
	
	private static Properties DEFAULT_PROPERTIES=new Properties();
	private static final String PROPERTIES_PATH="conf\\general.properties.xml";
	
	private static final String LOG_PREFIX = "log";
	private static final String LOG_PATH_NAME_KEY="LogDirectory";
	private static final String LOG_PATH_NAME_VAL=LOG_PREFIX+"s";
	private static final String MAIN_LOGGER_NAME_KEY="MainLoggerName";
	private static final String MAIN_LOGGER_NAME_VAL="main";
	

	private static final String LOG_SIZE_LIMIT_KEY = "limit";
	private static final String LOG_SIZE_LIMIT_VALUE = "5000000";
	private static final String LOG_COUNTER_KEY = "count";
	private static final String LOG_COUNTER_VALUE = "10";
	private static final String LOG_APPEND_KEY = "append";
	private static final String LOG_APPEND_VALUE = "false";
	private static final String LOG_FORMATTER_CLASSNAME_KEY = "formatter";
	private static final String LOG_FORMATTER_CLASSNAME_VALUE = "java.util.logging.SimpleFormatter";
	private static final String LOG_LEVEL_KEY = "level";
	private static final String LOG_LEVEL_VALUE = "ALL";
	
	private static final String[] DEFAULT_PROPERTIES_VALUES=new String[]{
		Loader.LOG_PATH_NAME_KEY,Loader.LOG_PATH_NAME_VAL,
		Loader.MAIN_LOGGER_NAME_KEY,Loader.MAIN_LOGGER_NAME_VAL,
		Const.MAIN_CONF_PATH_KEY,Const.MAIN_CONF_PATH_VALUE,
		"applications","conf\\Applications.xml",
		Loader.LOG_SIZE_LIMIT_KEY,Loader.LOG_SIZE_LIMIT_VALUE,
		Loader.LOG_COUNTER_KEY,Loader.LOG_COUNTER_VALUE,
		Loader.LOG_APPEND_KEY,Loader.LOG_APPEND_VALUE,
		Loader.LOG_FORMATTER_CLASSNAME_KEY,Loader.LOG_FORMATTER_CLASSNAME_VALUE,
		Loader.LOG_LEVEL_KEY,Loader.LOG_LEVEL_VALUE
	};
	
	
	private Loader() {}
	
	public static void main(String args[]) throws InterruptedException, IOException, NullPointerException, ClassNotFoundException, SQLException {
		//String s="1207110711C4,42S1033238141404//0000000385484941\r\nCITIGB2LXXX/072358/CITIFRPPXXX";
		//String s="121212DEUR10,";
		//System.out.println(FINBodyParser.getValues(s, "60F"));
		/*Properties pro=new Properties();
		DBConnector db=new DBConnector();
		db.pro=pro;
		FileInputStream in=new FileInputStream("conf/dbconnector.properties.xml");
		pro.loadFromXML(in);
		in.close();
		db.getInstance();
		SAAPrtMsgParser.getMsgFromDB("10.1.4.41", "FIN_MSGS", null);
		
		/*
		String path="C:\\BB\\ARCHIVE\\BATCH\\DN_queue2\\00390001.prt";
		File f=new File(path);
		try {
			SAAPrtMsgParser.loadMessages(mainLog, f);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ArrayList<String [][]> tmp = new ArrayList<String[][]>();
		try {
			while(SAAPrtMsgParser.hasMoreElements()) {
				tmp.add(SAAPrtMsgParser.parsePrintMessage((String[]) SAAPrtMsgParser.next(), path, "ss"));
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println(tmp);
		System.out.println("OK");
		
		/*
		String original="jaæöËùû˝·";
		try {
		    byte[] utf8Bytes = original.getBytes("UTF8");
		    //byte[] defaultBytes = original.getBytes();

		    String roundTrip = new String(utf8Bytes, "utf-8");
		    System.out.println("roundTrip = " + roundTrip);
		    System.out.println(new String(utf8Bytes));
		    
		} 
		catch (UnsupportedEncodingException e) {
		    e.printStackTrace();
		}
		*/
		/*
		String host = "ses01ba";
		String from = "branislav.brandys@pabk.sk";
		String to = "branislav.brandys@pabk.sk";

		// Get system properties
		Properties props = System.getProperties();

		// Setup mail server
		props.put("mail.smtp.host", host);

		// Get session
		Session session = Session.getDefaultInstance(props, null);

		// Define message
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(from));
		message.addRecipient(Message.RecipientType.TO, 
		  new InternetAddress(to));
		message.setSubject("Hello");
		message.setText("Welcome");

		// Send message
		Transport.send(message);
		
		
		
		new Sleeper().sleep(2000);
		*//*
		String s="V prÌpade ak˝chkoævek nejasnostÌ";
		
		String b=TextParser.parse(s, new Hashtable<String, Object>(),"utf-8");
		
		System.out.println(b);
		*/
		//System.exit(0);
		
		loadDefaultProperties();
		Properties pro=null;
		if(args.length>0) {
			try {pro=loadProperties(getDefaultProperties(),args[0]);}
			catch(IOException e) {e.printStackTrace();pro=null;}
		}
		if(pro==null) {
			try {pro=loadProperties(getDefaultProperties(),Loader.PROPERTIES_PATH);}
			catch(IOException e) {e.printStackTrace();pro=null;}
		}
		if(pro==null) {
			System.err.println("Failed to load main properties");
			System.exit(1);
		}
		DEFAULT_PROPERTIES=pro;
				
		mainLog=Logger.getLogger(pro.getProperty(MAIN_LOGGER_NAME_KEY));
		mainLog.setUseParentHandlers(false);
		LogManager.getLogManager().addLogger(mainLog);
		initLogger(mainLog,pro);
		mainLog.info("Logger "+mainLog.getName()+" was successfully initialized");
		mainLog.info("Application Manager will be initialized!");
		try {
			Distribution.init();
		} catch (Exception e) {
			mainLog.severe("Failed to load distribution list. Application stopped!\r\n");
			e.printStackTrace();
			System.exit(1);
		}
		EventManager em=EventManager.getEventManager();
		em.init(null);
		em.join();
	}
	
	
	private static void loadDefaultProperties() {
		for(int i=0;i<DEFAULT_PROPERTIES_VALUES.length;i+=2) {
			getDefaultProperties().setProperty(DEFAULT_PROPERTIES_VALUES[i], DEFAULT_PROPERTIES_VALUES[i+1]);
		}
		
		
	}
	
	private static final void initLogger(Logger log, Properties pro) {
		try {
			log.setUseParentHandlers(false);
			String dir=pro.getProperty(LOG_PATH_NAME_KEY)+"/"+log.getName().toLowerCase();
			File folder=new File(dir);
			if(!folder.exists()) {System.out.println("Directory "+dir+" does not exists!");return;}
			if(!folder.isDirectory()) {System.err.println(dir+" is not directory!");return;}
			String pattern=dir+"/"+LOG_PREFIX+"_"+log.getName().toLowerCase()+"%g.log";
			int limit=Integer.parseInt(pro.getProperty(LOG_SIZE_LIMIT_KEY));
			try {limit=Integer.parseInt(pro.getProperty(log.getName()+"."+LOG_SIZE_LIMIT_KEY));}catch(Exception e){}
			int count=Integer.parseInt(pro.getProperty(LOG_COUNTER_KEY));
			try {count=Integer.parseInt(pro.getProperty(log.getName()+"."+LOG_COUNTER_KEY));}catch(Exception e){}
			boolean append=Boolean.parseBoolean(pro.getProperty(LOG_APPEND_KEY));
			try{append=Boolean.parseBoolean(pro.getProperty(log.getName()+"."+LOG_APPEND_KEY));}catch(Exception e){}
			Handler handler=new FileHandler(pattern,limit,count,append);
			String formatter=pro.getProperty(LOG_FORMATTER_CLASSNAME_KEY);
			String formatter2=null;
			try{formatter2=pro.getProperty(log.getName()+"."+LOG_FORMATTER_CLASSNAME_KEY);}catch(Exception e){}
			formatter=(formatter2==null?formatter:formatter2);
			handler.setFormatter((Formatter) Class.forName(formatter).newInstance());
			String level=pro.getProperty(LOG_LEVEL_KEY).toUpperCase();
			try{level=pro.getProperty(log.getName()+"."+LOG_LEVEL_KEY).toUpperCase();}catch(Exception e){}
			handler.setLevel((Level) Class.forName(Const.LEVEL_CLASS_NAME).getField(level).get(Class.forName(Const.LEVEL_CLASS_NAME)));
			log.addHandler(handler);
		}
		catch(Exception e) {e.printStackTrace();return;}
	}
	
	static final Properties loadProperties(Properties pro, String path) throws IOException {
		if(pro==null) {pro=new Properties();}
		else{pro=new Properties(pro);}
		FileInputStream in=new FileInputStream(path);
		pro.loadFromXML(in);
		in.close();
		return pro;
	}
	
	static final Logger initLogger(String name, Properties pro) {
		try {
			Logger log=Logger.getLogger(name);
			LogManager.getLogManager().addLogger(mainLog);
			initLogger(log,pro);
			return log;
		}
		catch (Exception e) {return mainLog;}
	}

	static String getProperty(String key) {
		return DEFAULT_PROPERTIES.getProperty(key);
	}

	static Logger getMainLog() {
		return mainLog;
	}

	public static Properties getDefaultProperties() {
		return DEFAULT_PROPERTIES;
	}
	
}
