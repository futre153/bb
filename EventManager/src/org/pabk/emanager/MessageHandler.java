package org.pabk.emanager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Logger;

import org.pabk.emanager.parser.SAAEventParser;
import org.pabk.emanager.parser.SAAPrtMsgParser;
import org.pabk.emanager.routing.Distribution;
import org.pabk.emanager.util.Sys;


public class MessageHandler extends HandlerImpl {

	private static final String FIN5XX = "fin5XX";
	private static final String FIN_DN = "finDN";
	private static final String FIN_DN_SHORT = "_NoMsg";
	private static MessageHandler msgHandler;
	private static String mt5XXKey;
	private static String mt5XXMask;
	private static String dnKey;
	private static String dnMask;
	private static String statKey;
	private static String statMask;
	private static String[] saaServers=null;
	@Override
	public void businessLogic() {
		setMessageHandler(this);
		
		//HandlerImpl.getString
		
		
		String tmp=pro.getProperty(Const.ALLIANCE_ACCESS_SERVERS_KEY);
		if(tmp==null) {
			tmp=Const.DEFAULT_ALLIANCE_ACCESS_SERVERS;
			//tmp="10.1.4.41";
			log.info("Used Default Alliance Access servers: "+tmp);
		}
		else {
			log.info("Load Alliance Access servers: "+tmp);
		}
		try {
			saaServers=tmp.split(pro.getProperty(Const.SAA_SERVERS_SEPARATOR_KEY));
		}
		catch(Exception e) {
			saaServers=tmp.split(Const.SAA_SERVERS_SEPARATOR);
		}
		
		mt5XXKey=pro.getProperty(Const.MT5XX_KEY_KEY);
		
		if(mt5XXKey==null) {
			mt5XXKey=Const.DEFAULT_MT5XX_KEY;
			log.info("Used default keyword for MT5XX messages: "+mt5XXKey);
		}
		else {
			log.info("Defined keyword for MT5XX messages: "+mt5XXKey);
			//pro=System.getProperties();
			//pro.put(Const.MT5XX_KEY_KEY, mt5XXKey);
		}
		
		mt5XXMask=pro.getProperty(Const.MT5XX_MASK_KEY);
		if(mt5XXMask==null) {
			mt5XXMask=Const.DEFAULT_MT5XX_MASK;
			log.info("Used default mask for MT5XX messages: "+mt5XXMask);
		}
		else {
			log.info("Defined mask for MT5XX messages: "+mt5XXMask);
			//pro=System.getProperties();
			//pro.put(Const.MT5XX_MASK_KEY, mt5XXMask);
		}
		dnKey=pro.getProperty(Const.DELIVERY_NOTIFICATION_KEY_KEY);
		if(dnKey==null) {
			dnKey=Const.DEFAULT_DELIVERY_NOTIFICATION_KEY;
			log.info("Used default keyword for Delivey Notification messages: "+dnKey);
		}
		else {
			log.info("Defined keyword for Delivey Notification messages: "+dnKey);
			
			//pro=System.getProperties();
			//pro.put(Const.DELIVERY_NOTIFICATION_KEY_KEY, dnKey);
		}
		dnMask=pro.getProperty(Const.DELIVERY_NOTIFICATION_MASK_KEY);
		
		if(dnMask==null) {
			dnMask=Const.DEFAULT_DELIVERY_NOTIFICATION_MASK;
			log.info("Used default mask for Delivey Notification messages: "+dnMask);
		}
		else {
			log.info("Defined mask for Delivey Notification messages: "+dnMask);
			
			//pro=System.getProperties();
			//pro.put(Const.DELIVERY_NOTIFICATION_MASK_KEY, dnMask);
		}
		statKey=pro.getProperty(Const.STATEMENT_KEY_KEY);
		if(statKey==null) {
			statKey=Const.DEFAULT_STATEMENT_KEY;
			log.info("Used default keyword for Statement messages: "+statKey);
			
		}
		else {
			log.info("Defined keyword for Statement messages: "+statKey);
			//pro=System.getProperties();
			//pro.put(Const.MT5XX_KEY_KEY, mt5XXKey);
		}
		
		statMask=pro.getProperty(Const.STATEMENT_MASK_KEY);
		if(statMask==null) {
			statMask=Const.DEFAULT_STATEMENT_MASK;
			log.info("Used default mask for Statement messages: "+statMask);
		}
		else {
			log.info("Defined mask for Statement messages: "+statMask);
			//pro=System.getProperties();
			//pro.put(Const.MT5XX_MASK_KEY, mt5XXMask);
		}
		
		
		sleep=new Sleeper();
		while(true) {
			log.info(this.getClass().getSimpleName()+" is working");
			if(shutdown)break;
			//String[] saaServer=DatagramCollector.getSAAServers();
			String tableName=MessageCollector.getTableName();
			log.info(this.getClass().getSimpleName()+" found "+saaServers.length+" Alliance Access servers");
			for(int i=0;i<saaServers.length;i++) {
				execute(log,SAAPrtMsgParser.getMsgFromDB(saaServers[i],tableName,this,SAAPrtMsgParser.NOT_EXECUTED),saaServers[i]);
				execute(log,SAAPrtMsgParser.getMsgFromDB(saaServers[i],tableName,this,SAAPrtMsgParser.STATEMENT),saaServers[i]);
			}
			log.info(this.getClass().getSimpleName()+" is SLEEPING");
			sleep.sleep(0);
			if(shutdown)break;
		}
		
	}

	private void execute(Logger log, ArrayList<Hashtable<String, Object>> list, String server) {
		log.info("Found "+list.size()+" unprocessed message/s");
		//MT5XX handler
		executeMT5XX(log,findForType(list,mt5XXKey,"\\\\\\\\"+server+"\\\\"+mt5XXMask));
		//DN handler
		executeDeliveryNots(log,findForType(list,dnKey,"\\\\\\\\"+server+"\\\\"+dnMask));
		//Statement Handler
		executeStatements(log,findForType(list,statKey,"\\\\\\\\"+server+"\\\\"+statMask));
	}
	
	private void executeMT5XX(Logger log,ArrayList<Hashtable<String, Object>> list) {
		log.info("Found "+list.size()+" unprocessed message/s for MT5XX messaging");
		for(int i=0;i<list.size();i++) {
			Hashtable<String, Object> tab=list.get(i);
			tab.put(Distribution.MESSAGE_ID_KEY, FIN5XX);
			Distribution.createMessage(tab);
			try {
				DBConnector.getDb(false,null).update(
						MessageCollector.getTableName(),
						new String[]{SAAPrtMsgParser.STATUS},
						new String[]{SAAPrtMsgParser.EXECUTED},
						"WHERE "+SAAPrtMsgParser.ID+" = '"+tab.get(SAAEventParser.ID)+"'");
			}
			catch (SQLException e) {e.printStackTrace();}
			
		}
		
	}
	
	private void executeStatements(Logger log,	ArrayList<Hashtable<String, Object>> list) {
		log.info("Found "+list.size()+" unprocessed message/s for statement messaging");
		if(list.size()>0) {
			Hashtable<String, Object> tab=list.get(0);
			if(tab.get(SAAPrtMsgParser.STATUS).equals(SAAPrtMsgParser.STATEMENT)) {
				list=SAAPrtMsgParser.joinStatements(list,MessageCollector.getTableName());
			}
			else {
				for(int i=0;i<list.size();i++) {
					tab=list.get(i);
					//System.out.println(tab);
					//try {
					//	SAAPrtMsgParser.addStatement(log,tab);
					//} catch (IOException e) {
					//	e.printStackTrace();
					//}
				}
			}
		}
	}
	
	private void executeDeliveryNots(Logger log, ArrayList<Hashtable<String, Object>> list) {
		log.info("Found "+list.size()+" unprocessed message/s for delivery notification messaging");
		for(int i=0;i<list.size();i++) {
			Hashtable<String, Object> tab=list.get(i);
			Hashtable<String, Object> tab2=SAAPrtMsgParser.findTwinsMessage(list,tab);
			//System.out.println(tab);
			//System.out.println(tab2);
			if(tab2==null)continue;	else tab2=tab2.size()>0?tab2:null;
			tab.put(Distribution.MESSAGE_ID_KEY, FIN_DN);
			SAAPrtMsgParser.setShortMT(tab);
			if(tab2!=null) {tab=Sys.join(tab, tab2);}
			else {
				tab.put(Distribution.MESSAGE_ID_KEY, ((String)(tab.get(Distribution.MESSAGE_ID_KEY)))+FIN_DN_SHORT);
			}
			Distribution.createMessage(tab);
			//System.out.println(tab);
			try {
				DBConnector.getDb(false,null).update(
						MessageCollector.getTableName(),
						new String[]{SAAPrtMsgParser.STATUS},
						new String[]{SAAPrtMsgParser.EXECUTED},
						"WHERE "+SAAPrtMsgParser.ID+" = '"+tab.get(SAAEventParser.ID)+"'");
			}
			catch (SQLException e) {e.printStackTrace();}
		}
	}
	
	
	
	
	

	private ArrayList<Hashtable<String,Object>> findForType(ArrayList<Hashtable<String, Object>> tab,
			String key, String regExp) {
		//System.out.println(regExp);
		ArrayList<Hashtable<String,Object>> tmp=new ArrayList<Hashtable<String,Object>>();
		for(int i=0;i<tab.size();i++) {
			Object item=tab.get(i).get(key);
			//System.out.println(item);
			if(item!=null) {
				if(item instanceof String) {
					//System.out.println(((String) item).matches(regExp));
					if(((String) item).matches(regExp))tmp.add(tab.get(i));
				}
			}
		}
		return tmp;
	}
	public void wakeUp() {this.sleep.wakeup();}
	private void setMessageHandler(MessageHandler messageHandler) {msgHandler=messageHandler;}
	static MessageHandler getMessageHandler() {return msgHandler;}
}
