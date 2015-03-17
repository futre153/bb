package org.pabk.emanager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.mail.MessagingException;

import org.pabk.emanager.parser.EMessageParser;


public class MailHandler extends HandlerImpl {

	private static MailHandler mail;
	private static String SNMPServer;
	private static String SNMPServerPort;
	private static String sender;
	
	@Override
	public void businessLogic() {
		
		SNMPServer=pro.getProperty(Const.MAIL_SMTP_HOST_KEY);
		
		if(SNMPServer==null) {
			SNMPServer=Const.DEFAULT_MAIL_SMTP_HOST;
			log.info("Used Default SNMP server: "+SNMPServer);
			pro.put(Const.MAIL_SMTP_HOST_KEY, SNMPServer);
		}
		else {
			log.info("Defined SNMP server: "+SNMPServer);
		}
		
		SNMPServerPort=pro.getProperty(Const.MAIL_SMTP_PORT_KEY);
		
		
		if(SNMPServerPort==null) {
			SNMPServerPort=Const.DEFAULT_MAIL_SMTP_PORT;
			log.info("Used Default SNMP server port: "+SNMPServerPort);
			pro.put(Const.MAIL_SMTP_PORT_KEY, SNMPServerPort);
		}
		else {
			log.info("Defined SNMP server port to: "+SNMPServerPort);
		}
		sender=pro.getProperty(Const.MAIL_SMPT_FROM_KEY);
		
		if(sender==null) {
			sender=Const.DEFAULT_MAIL_SMTP_FROM;
			log.info("Used default sender name "+sender);
			pro.put(Const.MAIL_SMPT_FROM_KEY, sender);
		}
		else {
			log.info("Defined sender name as "+sender);
		}
		
		//System.out.println("Mailhandler="+pro);
		setMail(this);
		//this.pro.setProperty(Const, value)
		sleep=new Sleeper();
		while(true) {
			log.info(this.getClass().getSimpleName()+" is working");
			if(shutdown)break;
			
			ArrayList<Hashtable<String, Object>> msgs=EMessageParser.loadMessages(this);
			log.info("Found "+msgs.size()+" message/es for sending");
			for(int i=0;i<msgs.size();i++) {
				try {
					EMessageParser.createEMailMsg(msgs.get(i),pro);
					EMessageParser.setStatus(msgs.get(i),1);
					//break;
				}
				catch (MessagingException | IOException | SQLException e) {e.printStackTrace();}
			}
			//System.exit(1);
			log.info(this.getClass().getSimpleName()+" is SLEEPING");
			sleep.sleep(10000);
			if(shutdown)break;
		}

	}

	public static MailHandler getMail() {return mail;}

	private static void setMail(MailHandler mail) {MailHandler.mail = mail;}
	
	public void wakeUp(){sleep.wakeup();}

}
