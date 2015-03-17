package org.pabk.emanager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Logger;

import org.pabk.emanager.parser.SAAEventParser;
import org.pabk.emanager.routing.Distribution;
import org.pabk.emanager.util.Sys;


public class SAATrapHandler extends HandlerImpl {

	
	private static SAATrapHandler trapHandler;

	@Override
	public void businessLogic() {
		trapHandler=this;
		sleep=new Sleeper();
		while(true) {
			log.info(this.getClass().getSimpleName()+" is working");
			if(shutdown)break;
			String[] saaServer=DatagramCollector.getSAAServers();
			String tableName=DatagramCollector.getTableName();
			log.info(this.getClass().getSimpleName()+" found "+saaServer.length+" Alliance Access servers");
			
			for(int i=0;i<saaServer.length;i++) {
				execute(log,SAAEventParser.getTrapsFromDB(saaServer[i],tableName,this));
			}
			log.info(this.getClass().getSimpleName()+" is SLEEPING");
			sleep.sleep(10000);
			if(shutdown)break;
		}

	}

	private static void execute(Logger log, ArrayList<Hashtable<String,Object>> obj) {
		String svr=(obj.size()>0?((String)(obj.get(0).get(SAAEventParser.SVRI))):null);
		log.info("Trap execution started"+(svr==null?".":(" for "+svr+"."))+" Entries found: "+obj.size());
		for(int i=0;i<obj.size();i++) {
			switch((int)(obj.get(i).get(SAAEventParser.getFieldName(SAAEventParser.ENRI)))) {
			case SAAEventParser.SESSION_CLOSED:execute10023(log, obj, obj.get(i));break;
			case SAAEventParser.VALIDATION_ERROR_FROM:execute10117(log, obj, obj.get(i));break;
			case SAAEventParser.NACKED:execute8005(log, obj, obj.get(i));break;
			case SAAEventParser.QUEUE_OVERFLOW:execute2010(log, obj, obj.get(i));break;
			case SAAEventParser.POSSIBLE_DUPLICATE_EMISSION:execute8135(log, obj, obj.get(i));break;
			case SAAEventParser.POSSIBLE_DUPLICATE_MESSAGE:execute8026(log, obj, obj.get(i));break;
			case SAAEventParser.DISPOSE_ERROR:execute10050(log, obj, obj.get(i));break;
			case SAAEventParser.SNF_FILE_RECEIVED:execute28116(log, obj, obj.get(i));break;
			case SAAEventParser.START_SESSION:break;
			default:
				/*
				try {
					DBConnector.getDb(false,null).update(
						DatagramCollector.getTableName(),
						new String[]{SAAEventParser.getFieldName(SAAEventParser.TSTI)},
						new String[]{SAAEventParser.EXECUTED},
						"WHERE "+SAAEventParser.ID+" = '"+obj.get(i).get(SAAEventParser.ID)+"'");
				}
				catch (SQLException e) {e.printStackTrace();}
				*/
			}
			log.info("Trying to wake-up MailHandler: "+MailHandler.getMail().isAlive());
			MailHandler.getMail().wakeUp();
		}
		log.info("Trap execution ended"+(svr==null?".":(" for "+svr+"."))+" Entries processed: "+obj.size());
	}
	private static void execute10050(Logger log, ArrayList<Hashtable<String,Object>> obj, Hashtable<String, Object> tab) {
		log.info("Found event nr. 10050 Parsing started.");
		tab.put(Distribution.MESSAGE_ID_KEY, "trap"+tab.get(SAAEventParser.getFieldName(SAAEventParser.ENRI)));
		Distribution.createMessage(tab);
		try {
			DBConnector.getDb(false,null).update(
					DatagramCollector.getTableName(),
					new String[]{SAAEventParser.getFieldName(SAAEventParser.TSTI)},
					new String[]{SAAEventParser.EXECUTED},
					"WHERE "+SAAEventParser.ID+" = '"+tab.get(SAAEventParser.ID)+"'");
		}
		catch (SQLException e) {e.printStackTrace();}
	}
	
	private static void execute8026(Logger log, ArrayList<Hashtable<String,Object>> obj, Hashtable<String, Object> tab) {
		log.info("Found event nr 8135. Parsing started.");
		tab.put(Distribution.MESSAGE_ID_KEY, "trap"+tab.get(SAAEventParser.getFieldName(SAAEventParser.ENRI)));
		Distribution.createMessage(tab);
		try {
			DBConnector.getDb(false,null).update(
					DatagramCollector.getTableName(),
					new String[]{SAAEventParser.getFieldName(SAAEventParser.TSTI)},
					new String[]{SAAEventParser.EXECUTED},
					"WHERE "+SAAEventParser.ID+" = '"+tab.get(SAAEventParser.ID)+"'");
		}
		catch (SQLException e) {e.printStackTrace();}
	}
	
	
	private static void execute8135(Logger log, ArrayList<Hashtable<String,Object>> obj, Hashtable<String, Object> tab) {
		log.info("Found event nr 8135. Parsing started.");
		tab.put(Distribution.MESSAGE_ID_KEY, "trap"+tab.get(SAAEventParser.getFieldName(SAAEventParser.ENRI)));
		Distribution.createMessage(tab);
		try {
			DBConnector.getDb(false,null).update(
					DatagramCollector.getTableName(),
					new String[]{SAAEventParser.getFieldName(SAAEventParser.TSTI)},
					new String[]{SAAEventParser.EXECUTED},
					"WHERE "+SAAEventParser.ID+" = '"+tab.get(SAAEventParser.ID)+"'");
		}
		catch (SQLException e) {e.printStackTrace();}
	}
	
	private static void execute2010(Logger log, ArrayList<Hashtable<String,Object>> obj, Hashtable<String, Object> tab) {
		log.info("Found event nr 2010. Parsing started.");
		tab.put(Distribution.MESSAGE_ID_KEY, "trap"+tab.get(SAAEventParser.getFieldName(SAAEventParser.ENRI)));
		Distribution.createMessage(tab);
		try {
			DBConnector.getDb(false,null).update(
					DatagramCollector.getTableName(),
					new String[]{SAAEventParser.getFieldName(SAAEventParser.TSTI)},
					new String[]{SAAEventParser.EXECUTED},
					"WHERE "+SAAEventParser.ID+" = '"+tab.get(SAAEventParser.ID)+"'");
		}
		catch (SQLException e) {e.printStackTrace();}
	}
	
	private static void execute8005(Logger log, ArrayList<Hashtable<String,Object>> obj, Hashtable<String, Object> tab) {
		log.info("Found event nr 8005. Parsing started.");
		tab.put(Distribution.MESSAGE_ID_KEY, "trap"+tab.get(SAAEventParser.getFieldName(SAAEventParser.ENRI)));
		Distribution.createMessage(tab);
		try {
			DBConnector.getDb(false,null).update(
					DatagramCollector.getTableName(),
					new String[]{SAAEventParser.getFieldName(SAAEventParser.TSTI)},
					new String[]{SAAEventParser.EXECUTED},
					"WHERE "+SAAEventParser.ID+" = '"+tab.get(SAAEventParser.ID)+"'");
		}
		catch (SQLException e) {e.printStackTrace();}
	}
	
	private static void execute10117(Logger log, ArrayList<Hashtable<String,Object>> obj, Hashtable<String, Object> tab) {
		log.info("Found event nr 10117. Parsing started.");
		tab.put(Distribution.MESSAGE_ID_KEY, "trap"+tab.get(SAAEventParser.getFieldName(SAAEventParser.ENRI)));
		Distribution.createMessage(tab);
		try {
			DBConnector.getDb(false,null).update(
					DatagramCollector.getTableName(),
					new String[]{SAAEventParser.getFieldName(SAAEventParser.TSTI)},
					new String[]{SAAEventParser.EXECUTED},
					"WHERE "+SAAEventParser.ID+" = '"+tab.get(SAAEventParser.ID)+"'");
		}
		catch (SQLException e) {e.printStackTrace();}
	}
	
	private static void execute28116(Logger log, ArrayList<Hashtable<String,Object>> obj, Hashtable<String, Object> tab) {
		log.info("Found event nr 28116. Parsing started.");
		tab.put(Distribution.MESSAGE_ID_KEY, "trap"+tab.get(SAAEventParser.getFieldName(SAAEventParser.ENRI)));
		Distribution.createMessage(tab);
		try {
			DBConnector.getDb(false,null).update(
					DatagramCollector.getTableName(),
					new String[]{SAAEventParser.getFieldName(SAAEventParser.TSTI)},
					new String[]{SAAEventParser.EXECUTED},
					"WHERE "+SAAEventParser.ID+" = '"+tab.get(SAAEventParser.ID)+"'");
		}
		catch (SQLException e) {e.printStackTrace();}
	}
	
	private static void execute10023(Logger log, ArrayList<Hashtable<String,Object>> obj, Hashtable<String, Object> tab) {
		log.info("Found event nr 10023. Parsing started.");
		Hashtable<String, Object> twin = find(
			obj,
			new String[]{SAAEventParser.getFieldName(SAAEventParser.ENRI),SAAEventParser.SE},
			new Object[]{SAAEventParser.START_SESSION,tab.get(SAAEventParser.SE)});
		if(twin!=null) {
			/*
			 * to do business
			 */
			log.info("Twin 10018 for event 10023 found");
			Hashtable<String,Object> join=Sys.join(tab,twin);
			join.put(Distribution.MESSAGE_ID_KEY, "trap"+tab.get(SAAEventParser.getFieldName(SAAEventParser.ENRI)));
			SAAEventParser.set1023Status(join);
			Distribution.createMessage(join);
						
			try {
				DBConnector.getDb(false,null).update(
						DatagramCollector.getTableName(),
						new String[]{SAAEventParser.getFieldName(SAAEventParser.TSTI)},
						new String[]{SAAEventParser.EXECUTED},
						"WHERE "+SAAEventParser.ID+" = '"+tab.get(SAAEventParser.ID)+"'");
				DBConnector.getDb(false,null).update(
						DatagramCollector.getTableName(),
						new String[]{SAAEventParser.getFieldName(SAAEventParser.TSTI)},
						new String[]{SAAEventParser.EXECUTED},
						"WHERE "+SAAEventParser.ID+" = '"+twin.get(SAAEventParser.ID)+"'");
			}
			catch (SQLException e) {e.printStackTrace();}
			//System.exit(0);
		}
	}

	private static Hashtable<String, Object> find(ArrayList<Hashtable<String, Object>> obj,	String[] fieldName, Object[] value) {
		for(int i=0;i<obj.size();i++) {
			int j=0;		
			for(;j<fieldName.length;j++) {
				//System.out.println(i+","+j+" val1="+obj.get(i).get(fieldName[j])+" val2="+value[j]+" "+(!obj.get(i).get(fieldName[j]).equals(value[j])));
				if(!obj.get(i).get(fieldName[j]).equals(value[j]))break;
			}
			/*System.out.println(j);
			System.out.println(obj.get(i).get("ID"));
			System.out.println(obj.get(i).get("Session"));
			*/
			if(j==fieldName.length)return obj.get(i);
		}
		return null;
	}

	public static  SAATrapHandler getInstance() {
		return trapHandler;
	}

	public void wakeUp() {
		this.sleep.wakeup();
	}

}
