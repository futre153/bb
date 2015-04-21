package org.pabk.emanager;

import java.sql.SQLException;
import java.util.ArrayList;

import org.pabk.ber.BERSequence;
import org.pabk.emanager.parser.SAAEventParser;
import org.pabk.emanager.snmp.DatagramIOStream;
import org.pabk.emanager.snmp.DatagramStream;
import org.pabk.emanager.snmp.SAATrap;

public class DatagramCollector extends HandlerImpl {

	private static DatagramCollector collector;
	private static String[] saaServers={"10.1.129.251"};
	private static final SAATrap trap=new SAATrap("saatrap");
	private static final String SAA_TRAP_TABLE_NAME = "SAA_TRAPS";
		
	private boolean stopped=false;
	
	public static DatagramCollector getInstance() {return collector;}
	
	@Override
	public void businessLogic() {
		
		
		String tmp=pro.getProperty(Const.ALLIANCE_ACCESS_SERVERS_KEY);
		if(tmp==null) {
			tmp=Const.DEFAULT_ALLIANCE_ACCESS_SERVERS;
			//TODO ymayat
			tmp="10.1.129.251";
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
		/*
		
		
		saaServers=Const.DEFAULT_ALLIANCE_ACCESS_SERVERS.split(Const.SAA_SERVERS_SEPARATOR);
		try {
			saaServers=pro.getProperty(Const.ALLIANCE_ACCESS_SERVERS_KEY).split(Const.SAA_SERVERS_SEPARATOR_KEY);
			log.info("Defined Alliance Access servers: "+Sys.concatenate(saaServers, ','));
		}
		catch(Exception e) {
			log.info("Used Default Alliance Access servers: "+Sys.concatenate(saaServers, ','));
		}*/
		collector=this;
		while(true) {
			stopped=false;
			log.info("DatagramCollector is working");
			if(shutdown)break;
			ArrayList<DatagramIOStream> list=DatagramStream.getStreams();
			
			System.out.println(list);
			
			for(int i=0;i<list.size();i++) {
	
				if(check(list.get(i).getHostAddress(),saaServers)) {
					log.info("Found datagram from "+list.get(i).getHostAddress());
					try {
						addSaaEvent(list.get(i));
					}
					catch (Exception e) {
						e.printStackTrace();
						if(e instanceof SQLException) continue;
					}
				}
				list.remove(i);
				i--;
			}
			stopped=true;
			this.sleep=new Sleeper();
			//SAATrapHandler handler=SAATrapHandler.getInstance();
			//if(handler!=null)handler.wakeUp();
			log.info("DatagramCollector goes to sleep");
			sleep.sleep(0);
		}
		
	}

	private void addSaaEvent(DatagramIOStream io) throws Exception {
		
		
		while(true) {
			try {
				BERSequence msg=trap.getMessage().clone();
				if(io.available()==0)break;
				io.lock();
				msg.decode(io,-1);
				log.info("Successfully decoded saatrap from "+io.getHostAddress());
				String[][] value=SAAEventParser.parse(msg);
				log.info("Successfully parsed saatrap from "+io.getHostAddress());
				DBConnector.getDb(false, null).insert(SAA_TRAP_TABLE_NAME, value[0], value[1]);
				log.info("Saatrap from "+io.getHostAddress()+" successfully added to database");
				//System.exit(0);
				io.unlock();
			}
			catch (Exception e) {
				log.severe(e.getMessage());
				if(e instanceof SQLException)io.restore();
				io.unlock();
				throw e;
			}
		}
		
	}
	

	private boolean check(String hostAddress, String[] saa) {
		for(int i=0;i<saa.length;i++) {
			if(hostAddress.equals(saa[i]))return true;
		}
		return false;
	}

	public void wakeUp() {
		this.sleep.wakeup();
	}

	public boolean isStopped() {
		return stopped;
	}

	public static String[] getSAAServers() {return DatagramCollector.saaServers;}

	public static String getTableName() {return DatagramCollector.SAA_TRAP_TABLE_NAME;}


}
