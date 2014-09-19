package com.acepricot.finance.sync.client;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import org.pabk.util.Huffman;

import com.acepricot.finance.sync.share.JSONMessage;

public class SyncClientEngine extends Thread {
	
	//final static Logger logger = LoggerFactory.getLogger(SyncClientEngine.class);
	
	private static final ArrayList<SyncClientEngine> engines = new ArrayList<SyncClientEngine>();
	private static final String INTERVAL_KEY = "interval";
	private static final String INTERVAL_DEF = "1000";
	private static final String ACTION_COUNTER_KEY = "actionCounter";
	private static final String ACTION_COUNTER_DEF = "3";
	private static boolean shutdown = false;
	
	private String password;
	private String user;
	private boolean propsLoaded = false;
	private String dbUrl;
	private Class<?> _class;
	
	public static void start(Properties p) throws SQLException {
		int i = 0;
		for(; i < engines.size(); i ++) {
			if(!engines.get(i).isAlive()) {
				break;
			}
		}
		SyncClientEngine engine = new SyncClientEngine(p); 
		DBSchemas.setTrigger(false);
		p.setProperty(JSONMessageProcessorClient.DB_USER_KEY, JSONMessageProcessorClient.LOCAL_DB_USER);
		DBSchemas.loadSchemas(engine.getConnection(p));
		p.setProperty(JSONMessageProcessorClient.DB_USER_KEY, JSONMessageProcessorClient.SYNC_ADMIN);
		engine.user = JSONMessageProcessorClient.SYNC_ADMIN;
		DBSchemas.setTrigger(true);
		engines.add(i, engine);
		engines.get(i).setDaemon(true);
		engines.get(i).start();
	}

	private Properties props;
	private SyncClientEngine(Properties p) {
		this.props = p;
	}
	
	private Connection getConnection(Properties p) throws SQLException {
		try {
			if(!propsLoaded) {
				loadProperties(p);
			}
			if(propsLoaded) {
				return DriverManager.getConnection(dbUrl, user, Huffman.decode(password, null));
			}
			throw new SQLException("Properties not loaded");
		}
		catch (Exception e) {
			throw new SQLException (e);
		}
	}
	
	private void loadProperties(Properties p) throws ClassNotFoundException {
		if(_class == null) {
			_class = Class.forName(p.getProperty(JSONMessageProcessorClient.JDBC_DRIVER_KEY));
		}
		if(dbUrl == null) {
			dbUrl = String.format(p.getProperty(JSONMessageProcessorClient.URL_STRING_KEY), p.getProperty(JSONMessageProcessorClient.DB_NAME_KEY).replaceAll("\\\\", "/").replaceAll(p.getProperty(JSONMessageProcessorClient.EXT_REPL_KEY), ""));			
		}
		if(user == null) {
			user = p.getProperty(JSONMessageProcessorClient.DB_USER_KEY);
		}
		if(password == null) {
			password = p.getProperty(JSONMessageProcessorClient.DB_PSWD_KEY);
		}
		propsLoaded = true;
	}
	
	
	public void run() {
		Connection con = null;
		String url = props.getProperty(JSONMessageProcessorClient.DEFAULT_URL_KEY);
		Sleeper s = new Sleeper();
		long interval = Long.parseLong(props.getProperty(INTERVAL_KEY, INTERVAL_DEF));
		int counter = Integer.parseInt(props.getProperty(ACTION_COUNTER_KEY, ACTION_COUNTER_DEF));
		JSONMessage heartbeat = null;
		JSONMessage inMsg = null;
		JSONMessage outMsg = null;
		long _interval = 1;
		while(!isShutdown()) {
			boolean alive = CommonTrigger.isAlive();
			JSONMessageProcessorClient.getLogger().info("Trigger is " + (alive ? "" : "not ") + "alive");
			if(alive) {
				for(int i = 0; (i < counter && (!isShutdown())); i ++) {
					s.sleep(_interval);
				}
			}
			else {
				Exception error = null;
				try {
					con = getConnection(props);
					System.out.println(con);
					if(outMsg != null) {
						inMsg = JSONMessageProcessorClient.processIncomming(con, props, outMsg, inMsg);
						outMsg = null;
					}
				}
				catch (Exception e) {
					error = e;
				}
				for(int i = 0; (i < counter && (!isShutdown())); i ++) {
					s.sleep(_interval);
				}
				_interval = interval;
				try {
					if(error != null) {
						throw error;
					}
					heartbeat = JSONMessageProcessorClient.process(Heartbeat.getInstance(), url, null);
					if(heartbeat.isError()) {
						/*
						 * TODO spracuj chybu spojenia
						 */
					}
					else {
						if(inMsg == null) {
							inMsg = SyncRequest.getResponseForUnasweredPending(con, props);
							if(inMsg == null) {
								inMsg = SyncRequest.getRequestForWaiting(con, props);
							}
							if(inMsg == null) {
								inMsg = SyncRequest.getEmptyRequest(props);
							}
						}
						System.out.println("--- Outgoing message ---");
						System.out.println(inMsg);
						outMsg = JSONMessageProcessorClient.process(inMsg, url, null);
						System.out.println("--- Incomming message ---");
						System.out.println(outMsg);
						if(outMsg.isError()) {
							outMsg = null;
						}
					}
				} catch (Exception e) {
					outMsg = null;
					inMsg = null;
					// TODO spracuj vynimku;
					e.printStackTrace();
				}
				finally {
					try {con.close();} catch (SQLException e) {	e.printStackTrace();}
				}
			}
			
		};
		
	}

	private static boolean isShutdown() {
		return shutdown;
	}

	public static void setShutdown() {
		SyncClientEngine.shutdown = true;
		System.out.println("ENGINES STOPPED");
	}

	public static void joinTo() throws InterruptedException {
		for(int i = 0; i < engines.size(); i ++) {
			engines.get(i).join();
		}
	}
}
