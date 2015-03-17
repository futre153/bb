package org.pabk.emanager;

import java.io.File;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.pabk.emanager.parser.EMessageParser;
import org.pabk.emanager.parser.SAAEventParser;
import org.pabk.emanager.parser.SAAPrtMsgParser;
import org.pabk.emanager.util.Base64Coder;
import org.pabk.emanager.util.Sys;

public class Cleaner extends HandlerImpl {
	
	private static long[] timeRunTrapCleaner;
	private static long nextTrapTime=-1;
	private static long trapRemaining=-1;
	
	private static long eMsgRemaining=-1;
	private static long nextEMsgTime=-1;
	private static long[] timeRunEMsgCleaner;
	
	private static long oldFilesRemaining=-1;
	private static long nextOldFilesTime=-1;
	private static long[] timeRunOldFilesCleaner;
	
	private static long msgsRemaining=-1;
	private static long nextMsgsTime=-1;
	private static long[] timeRunMsgsCleaner;
	private static long msgCleanerNonExecDelay;
	private static long msgCleanerStmtDelay;
	
	@Override
	public void businessLogic() {
		
		
		String separator=pro.getProperty(Const.CLEANER_SEPARATOR_KEY);
		if(separator==null) {
			separator=Const.CLEANER_SEPARATOR;
		}
		
		String tr=pro.getProperty(Const.TRAP_CLEANER_TIME_RUN_KEY);
		if(tr==null) {
			tr=Const.DEFAULT_TRAP_CLEANER_TIME_RUN;
			log.info("Used Default times for Trap cleaner: "+tr);
		}
		else {
			log.info("Defined times for Trap cleaner: "+tr);
		}
		
		String tr1=pro.getProperty(Const.TRAP_REMAINING_DAYS_KEY);
		if(tr1==null) {
			tr1=Const.DEFAULT_TRAP_REMAINING_TIME;
			log.info("Used Default remaining time for Trap cleaner: "+tr1);
		}
		else {
			log.info("Defined remaining time for Trap cleaner: "+tr1);
		}
		
		String tr2=pro.getProperty(Const.EMSG_REMAINING_DAYS_KEY);
		if(tr2==null) {
			tr2=Const.DEFAULT_EMSG_REMAINING_TIME;
			log.info("Used Default remaining time for e-mail cleaner: "+tr2);
		}
		else {
			log.info("Defined remaining time for e-mail cleaner: "+tr2);
		}
		
		String tr22=pro.getProperty(Const.EMSG_CLEANER_TIME_RUN_KEY);
		if(tr22==null) {
			tr22=Const.DEFAULT_EMSG_CLEANER_TIME_RUN;
			log.info("Used Default times for Trap cleaner: "+tr22);
		}
		else {
			log.info("Defined times for Trap cleaner: "+tr22);
		}
		
		String tr32=pro.getProperty(Const.OLDFILES_CLEANER_TIME_RUN_KEY);
		if(tr32==null) {
			tr32=Const.DEFAULT_OLDFILES_CLEANER_TIME_RUN;
			log.info("Used Default times for Old files cleaner: "+tr32);
		}
		else {
			log.info("Defined times for Old files cleaner: "+tr32);
		}
		
		String tr3=pro.getProperty(Const.OLDFILES_REMAINING_DAYS_KEY);
		if(tr3==null) {
			tr3=Const.DEFAULT_OLDFILES_REMAINING_TIME;
			log.info("Used Default remaining time for Old files cleaner: "+tr3);
		}
		else {
			log.info("Defined remaining time for Old files cleaner: "+tr3);
		}
		
		String tr42=pro.getProperty(Const.MSGS_CLEANER_TIME_RUN_KEY);
		if(tr42==null) {
			tr42=Const.DEFAULT_MSGS_CLEANER_TIME_RUN;
			log.info("Used Default times for fin messages cleaner: "+tr42);
		}
		else {
			log.info("Defined times for fin messages cleaner: "+tr42);
		}
		
		String tr4=pro.getProperty(Const.MSGS_REMAINING_DAYS_KEY);
		if(tr4==null) {
			tr4=Const.DEFAULT_MSGS_REMAINING_TIME;
			log.info("Used Default remaining time for fin messages cleaner: "+tr4);
		}
		else {
			log.info("Defined remaining time for fin messages cleaner: "+tr4);
		}
		
		String tr43=pro.getProperty(Const.MSGS_NOT_EXECUTED_DELAY_KEY);
		if(tr43==null) {
			tr43=Const.DEFAULT_MSGS_DELAY_DAYS;
			log.info("Used Default remove delay days for not executed fin messages cleaner: "+tr43);
		}
		else {
			log.info("Defined remove delays days time for not excecuted fin messages cleaner: "+tr43);
		}
		
		String tr44=pro.getProperty(Const.MSGS_NOT_EXECUTED_STATEMENT_DELAY_KEY);
		if(tr44==null) {
			tr44=Const.DEFAULT_MSGS_DELAY_DAYS;
			log.info("Used Default remove delay days for not executed statement fin messages cleaner: "+tr44);
		}
		else {
			log.info("Defined remove delays days time for not excecuted statement fin messages cleaner: "+tr44);
		}
		
		DateFormat dt=DateFormat.getTimeInstance();
		DateFormat ddt=DateFormat.getDateTimeInstance();
		sleep=new Sleeper();
		
		trapRemaining=Long.parseLong(tr1);
		timeRunTrapCleaner=Cleaner.setTimes(tr,separator);
		nextTrapTime=setNextTime(nextTrapTime, timeRunTrapCleaner);
		
		eMsgRemaining=Long.parseLong(tr2);
		timeRunEMsgCleaner=Cleaner.setTimes(tr22, separator);
		nextEMsgTime=setNextTime(nextEMsgTime, timeRunEMsgCleaner);
		
		oldFilesRemaining=Long.parseLong(tr3);
		timeRunOldFilesCleaner=Cleaner.setTimes(tr32, separator);
		nextOldFilesTime=setNextTime(nextOldFilesTime, timeRunOldFilesCleaner);
		
		msgsRemaining=Long.parseLong(tr4);
		timeRunMsgsCleaner=Cleaner.setTimes(tr42, separator);
		nextMsgsTime=setNextTime(nextMsgsTime, timeRunMsgsCleaner);
		msgCleanerNonExecDelay=Long.parseLong(tr43);;
		msgCleanerStmtDelay=Long.parseLong(tr44);;
		
		long l=getActualDay();
		for(int i=0;i<timeRunTrapCleaner.length;i++) {
			log.info("Clean trap execution time "+(i+1)+ " is set to "+dt.format(timeRunTrapCleaner[i]+l));
		}
		log.info("Next trap clean time is set to "+ddt.format(nextTrapTime));
		
		for(int i=0;i<timeRunEMsgCleaner.length;i++) {
			log.info("Clean e-mail execution time "+(i+1)+ " is set to "+dt.format(timeRunEMsgCleaner[i]+l));
		}
		log.info("Next e-mail clean time is set to "+ddt.format(nextEMsgTime));
		
		
		while(true) {
			log.info("Cleaner is WORKING");
			if(shutdown)break;
			//trap cleaner
			if(nextTrapTime>=0) {
				if(GregorianCalendar.getInstance().getTimeInMillis()>nextTrapTime) {
					cleanTrap();
					nextTrapTime=Cleaner.setNextTime(nextTrapTime, timeRunTrapCleaner);
					log.info("Next trap clean time is set to "+ddt.format(nextTrapTime));
				}
			}
			//e-mail cleaner
			if(nextEMsgTime>=0) {
				if(GregorianCalendar.getInstance().getTimeInMillis()>nextEMsgTime) {
					cleanMail();
					nextEMsgTime=Cleaner.setNextTime(nextEMsgTime, timeRunEMsgCleaner);
					log.info("Next e-mail clean time is set to "+ddt.format(nextEMsgTime));
				}
			}
			//fin msg cleaner
			if(nextMsgsTime>=0) {
				if(GregorianCalendar.getInstance().getTimeInMillis()>nextMsgsTime) {
					cleanMsgs();
					nextMsgsTime=Cleaner.setNextTime(nextMsgsTime, timeRunMsgsCleaner);
					log.info("Next FIN message clean time is set to "+ddt.format(nextMsgsTime));
				}
			}
			//old files cleaner
			if(nextOldFilesTime>=0) {
				if(GregorianCalendar.getInstance().getTimeInMillis()>nextOldFilesTime) {
					cleanOldFiles();
					nextOldFilesTime=Cleaner.setNextTime(nextOldFilesTime, timeRunOldFilesCleaner);
					log.info("Next old files clean time is set to "+ddt.format(nextOldFilesTime));
				}
			}
			
			log.info("Cleaner is SLEEPING");
			sleep.sleep(1000*60);
		}
	}
	
	private void cleanOldFiles() {
		String path=Sys.getTmpFilesPath();
		File dir=new File(path);
		if(dir.isDirectory()) {
			File[] list=dir.listFiles();
			if(list!=null) {
				Date c=getExtDate(oldFilesRemaining);
				for(int i=0;i<list.length;i++) {
					if(list[i].isFile()) {
						log.info("Found file "+list[i]);
						Date d=new Date(list[i].lastModified());
						if(d.before(c)) {
							log.info("File "+list[i]+" was created more than "+oldFilesRemaining+" days and will be deleted");
							if(list[i].delete()) {
								log.info("File "+list[i]+" has been successfully deleted");
							}
							else {
								log.severe("Failed to delete file "+list[i]+". Cleaner will try to delete this file at a next time run.");
							}
						}
						else {
							log.info("File "+ list[i]+" was created less than "+oldFilesRemaining+" days and will be kept");
						}
					}
				}
			}
			else {
				log.severe("Temporary directory "+path+"does not exist");
			}
		}
		else {
			log.severe("Temparary directory "+path+" failed to open");
		}
	}
	
	private void cleanMsgs() {
		cleanMsgs(SAAPrtMsgParser.EXECUTED, 0);
		cleanMsgs(SAAPrtMsgParser.NOT_EXECUTED, msgCleanerNonExecDelay);
		cleanMsgs(SAAPrtMsgParser.STATEMENT, msgCleanerStmtDelay);
	}
	
	private void cleanMsgs(String status, long dayDelay) {
		DBConnector db=null;
		String tableName=MessageCollector.getTableName();
		String[] cols=new String[] {
				SAAPrtMsgParser.ID,
				SAAPrtMsgParser.INSERT_TIME,
				SAAPrtMsgParser.MSG_TEXT };
		ArrayList<Object[]> dels=new ArrayList<Object[]>();
		String where="WHERE "+SAAPrtMsgParser.STATUS+"='"+status+"'";
		try {
			db=DBConnector.getDb(true, this);
			db.select(tableName, cols, where);
			Date c= getExtDate(msgsRemaining+dayDelay);
			while(true) {
				db.next();
				String id=db.getObject(cols[0]).toString();
				Timestamp date=(Timestamp) db.getObject(cols[1]);
				if(date.before(c)) {
					dels.add(new Object[]{id,db.getObjectIgnoreNull(cols[2])});
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		for(int i=0;i<dels.size();i++) {
			try {
				db.delete(tableName, "where "+SAAPrtMsgParser.ID+" = '"+dels.get(i)[0]+"'");
				deleteFile("File", (String) dels.get(i)[1]);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(db!=null)db.close();
	}
	
	private final void deleteFile(String fType, String filename) {
		if(Sys.deleteTmpFile(filename)) {
			log.info(fType+" "+filename+" was successfully deleted");
		}
		else {
			log.warning(fType+" "+filename+" was NOT deleted");
		}
	}
	
	private static final Date getExtDate(long ext) {
		return new Date(new Date().getTime()-ext*24*60*60*1000);
	}
	
	
	private void cleanMail() {
		DBConnector db=null;
		String tableName=EMessageParser.getMailTableName();
		String[] cols=new String[] {
				EMessageParser.ID,
				EMessageParser.TIME,
				EMessageParser.ATT1,
				EMessageParser.ATT2};
		ArrayList<Object[]> dels=new ArrayList<Object[]>();
		String where="WHERE "+EMessageParser.STAT+"='"+SAAEventParser.EXECUTED+"'";
		try {
			db=DBConnector.getDb(true, this);
			db.select(tableName, cols, where);
			Date c=getExtDate(eMsgRemaining);
			while(true) {
				db.next();
				String id=db.getObject(cols[0]).toString();
				Timestamp date=(Timestamp) db.getObject(cols[1]);
				if(date.before(c)) {
					dels.add(new Object[]{id,db.getObjectIgnoreNull(cols[2]),db.getObjectIgnoreNull(cols[3])});
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		for(int i=0;i<dels.size();i++) {
			try {
				db.delete (tableName,"where "+EMessageParser.ID+" = '"+dels.get(i)[0]+"'");
				
				for(int j=1;j<2;j++) {
					if(dels.get(i)[j]!=null) {
						String[] att=Base64Coder.decodeString((String) dels.get(i)[j]).split(",");
						if(att.length==2) {
							deleteFile("Attachment",att[1]);
						}
						else {
							log.warning("Attachment record is malformed");
						}
					}
				}
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(db!=null) db.close();
	}
	
	
	private void cleanTrap() {
		DBConnector db=null;
		String[] cols=new String[]{SAAEventParser.ID,SAAEventParser.SAA_TRAPS_COLUMN[SAAEventParser.DATI]};
		ArrayList<String> dels=new ArrayList<String>();
		try {
			db=DBConnector.getDb(true, this);
			db.select(
				DatagramCollector.getTableName(),
				cols,
				"where "+SAAEventParser.SAA_TRAPS_COLUMN[SAAEventParser.TSTI]+"='"+SAAEventParser.EXECUTED+"'");
			//int counter=0;
			long c=Calendar.getInstance().getTimeInMillis()-trapRemaining*24*60*60*1000;
			while(true) {
				db.next();
				String id=db.getObject(cols[0]).toString();
				long date=SAAEventParser.getDateFromISODate(db.getObject(cols[1]).toString());
				
				if(date<c) {
					dels.add(id);
				}
				
				//counter++;
				//System.out.println(counter);
			}
		}
		catch (SQLException e) {
			//e.printStackTrace();
		}
		for(int i=0;i<dels.size();i++) {
			try {
				db.delete (DatagramCollector.getTableName(),"where "+SAAEventParser.ID+" = '"+dels.get(i)+"'");
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(db!=null)db.close();
		
		//System.out.println("END");
		/*ArrayList<Hashtable<String,Object>> traps=SAAEventParser.getTrapsFromDB (
			DatagramCollector.getTableName(),
			this,
			"where "+SAAEventParser.SAA_TRAPS_COLUMN[SAAEventParser.TSTI]+"='"+SAAEventParser.EXECUTED+"' limit "+maxLines
		);*/
		//System.exit(1);
	}
	
	private static long getActualDay() {
		Calendar c=GregorianCalendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTimeInMillis();
	}
	
	private static long setNextTime(long next, long[] timeRun) {
		if(timeRun.length==0)return -1;
		long a=GregorianCalendar.getInstance().getTimeInMillis();
		long l=getActualDay();
		for(int i=0;i<timeRun.length;i++) {
			if(((l+timeRun[i])>a) && ((l+timeRun[i])>next)) return timeRun[i]+l;
		}
		return l+1000*60*60*24+timeRun[0];
	}

	private static long[] setTimes(String tr, String s) {
		String[] a = tr.split(s);
		long[] l=new long[a.length];
		int index=0;
		for(int i=0;i<a.length;i++) {
			String[] s1=a[i].split(":");
			if(s1.length==2) {
				try {
					l[index]=Long.parseLong(s1[0])*60*60*1000+Long.parseLong(s1[1])*60*1000;
					index++;
				}
				catch(Exception e) {}
			}
		}
		return Arrays.copyOf(l, index);
	}

}
