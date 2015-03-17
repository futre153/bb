package org.pabk.emanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;

import org.pabk.emanager.parser.SAAPrtMsgParser;

public class MessageCollector extends HandlerImpl {
	private static final String FIN_MSG_TABLE_NAME = "FIN_MSGS";
	@SuppressWarnings("unused")
	private static MessageCollector collector;
	private static String[] saaServers=null;
	private static String[] prtFolders=null;
	@SuppressWarnings("unused")
	private static String[] finFolders=null;
	private static long msgLoopInterval=Const.DEFAULT_MESSAGE_COLLECTOR_LOOP_INTERVAL;
		
	@Override
	public void businessLogic() {
		
		String tmp=pro.getProperty(Const.ALLIANCE_ACCESS_SERVERS_KEY);
		if(tmp==null) {
			tmp=Const.DEFAULT_ALLIANCE_ACCESS_SERVERS;
			tmp="P3600X006";
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
					
		tmp=pro.getProperty(Const.PRT_FOLDERS_KEY);
		if(tmp==null) {
			tmp=Const.DEFAULT_PRT_FOLDERS;
			log.info("Used default print messages store folders:"+ tmp);
		}
		else {
			log.info("Load print messages store folders: "+tmp);
		}
		try {
			prtFolders=tmp.split(pro.getProperty(Const.PRT_FOLDERS_SEPARATOR_KEY));
		}
		catch(Exception e) {
			prtFolders=tmp.split(Const.PRT_FOLDERS_SEPARATOR);
		}
		
		tmp=pro.getProperty(Const.FIN_FOLDERS_KEY);
		if(tmp==null) {
			tmp=Const.DEFAULT_FIN_FOLDERS;
			log.info("Used default fin messages store folders:"+ tmp);
		}
		else {
			log.info("Load fin messages store folders: "+tmp);
		}
		try {
			finFolders=tmp.split(pro.getProperty(Const.FIN_FOLDERS_SEPARATOR_KEY));
		}
		catch(Exception e) {
			finFolders=tmp.split(Const.FIN_FOLDERS_SEPARATOR);
		}
		
		try {
			msgLoopInterval=Long.parseLong(pro.getProperty(Const.MESSAGE_COLLECTOR_LOOP_INTERVAL_KEY));
		}
		catch (Exception e) {
			
		}
		
		collector=this;
		while(true) {
			log.info("MessageCollector is WORKING");
			if(shutdown)break;
			addMessages(false, prtFolders);
			//addMessages(false, finFolders);
			this.sleep=new Sleeper();
			//SAATrapHandler handler=SAATrapHandler.getInstance();
			//if(handler!=null)handler.wakeUp();
			log.info("MessageCollector goes to SLEEP");
			MessageHandler.getMessageHandler().wakeUp();
			if(shutdown)break;
			sleep.sleep(msgLoopInterval);
		}

	}

	private void addMessages(boolean fin, String[] p)  {
		for(int i=0;i<saaServers.length;i++) {
			for(int j=0;j<p.length;j++) {
				//log.info("Trying to connect to "+p[j]);
				//log.info("Trying to connect to "+prtFolders[j]);
				String path=System.getProperty("file.separator")+System.getProperty("file.separator")+saaServers[i]+System.getProperty("file.separator")+p[j];
				File dir=getFolder(path);
				if(dir!=null) {
					log.info("Found directory "+path);
					File[] files=dir.listFiles();
					for(int k=0;k<files.length;k++) {
						if(files[k].isDirectory()) continue;
						try {
							int l=0;
							if(fin){}
							else {
								SAAPrtMsgParser.loadMessages(log, new FileInputStream(files[k]));
								l=SAAPrtMsgParser.length();
							}
							log.info("File "+ files[k]+" was successfully loaded. "+l+ " messages found");
							boolean del=files[k].delete();
							if(del) {
								log.info("File "+ files[k]+" was deleted from");
								if(fin) {}
								else {
									while(SAAPrtMsgParser.hasMoreElements()) {
										String[][] parsed=SAAPrtMsgParser.parsePrintMessage((String[]) SAAPrtMsgParser.next(),files[k].getAbsolutePath(),saaServers[i]);
										try {
											DBConnector.getDb(false, null).insert(FIN_MSG_TABLE_NAME, parsed[1], parsed[0]);
										} catch (SQLException e) {
											throw new IOException (e);
										}
										
									}
								}
								log.info("File "+ files[k]+" was successfully parsed");
							}
							else {
								log.warning("File "+ files[k]+" cannot be deleted and it not being parsed");
							}
						}
						catch (IOException e) {
							//e.printStackTrace();
							log.warning ("Error while processing file " + files[k]+" ("+e.getMessage()+")");
						}
					}
				}
				else {
					log.warning ("Directory " + path + " not found");
				}
			}
		}
		
	}
	
	
	private static File getFolder(String path) {
		File f=new File(path);
		if(f.exists()) if(f.isDirectory())return f;
		return null;
	}

	static String getTableName() {return FIN_MSG_TABLE_NAME;}

}
