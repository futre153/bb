package com.acepricot.finance.sync.client;

import java.util.ArrayList;
import java.util.Properties;

import com.acepricot.finance.sync.share.JSONMessage;

public class SyncClientEngine extends Thread {
	
	//final static Logger logger = LoggerFactory.getLogger(SyncClientEngine.class);
	
	private static final ArrayList<SyncClientEngine> engines = new ArrayList<SyncClientEngine>();
	private static final String INTERVAL_KEY = "interval";
	private static final String INTERVAL_DEF = "1000";
	private static final String ACTION_COUNTER_KEY = "actionCounter";
	private static final String ACTION_COUNTER_DEF = "10";
	private static boolean shutdown = false;
	
	public static void start(Properties p) {
		int i = 0;
		for(; i < engines.size(); i ++) {
			if(!engines.get(i).isAlive()) {
				break;
			}
		}
		engines.add(i, new SyncClientEngine(p));
		engines.get(i).setDaemon(true);
		engines.get(i).start();
	}

	private Properties props;
	private SyncClientEngine(Properties p) {
		this.props = p;
	}
	
	public void run() {
		
		String url = props.getProperty(JSONMessageProcessorClient.DEFAULT_URL_KEY);
		Sleeper s = new Sleeper();
		long interval = Long.parseLong(props.getProperty(INTERVAL_KEY, INTERVAL_DEF));
		int counter = Integer.parseInt(props.getProperty(ACTION_COUNTER_KEY, ACTION_COUNTER_DEF));
		JSONMessage inMsg = null;
		JSONMessage outMsg = null;
		while(!isShutdown()) {
			try {
				inMsg = JSONMessageProcessorClient.process(Heartbeat.getInstance(), url, null);
				if(!inMsg.isError()) {
					inMsg = SyncRequest.getInstance(props);
					if (inMsg != null) {
						outMsg = JSONMessageProcessorClient.process(inMsg, url, null);
					}
				}
			} catch (Exception e) {
				// spracuj vynimku;
				e.printStackTrace();
			}
			finally {
				// TODO spracuj JSONMessage
				System.out.println("INPUT MSG -----------------------------------------------------------");
				System.out.println(inMsg);
				System.out.println("OUTPUT MSG -----------------------------------------------------------");
				System.out.println(outMsg);
			}
			for(int i = 0; (i < counter && (!isShutdown())); i ++) {
				s.sleep(interval);
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
