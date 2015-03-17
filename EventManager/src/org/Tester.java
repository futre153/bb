package org;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class Tester {

	/**
	 * @param args
	 * @throws MessagingException 
	 * @throws AddressException 
	 */
	public static void main(String[] args) throws AddressException, MessagingException {
		Properties pro=new Properties();
		pro.put("mail.smtp.host", "10.1.1.59");
		pro.put("mail.smtp.port", "25");
		pro.put("mail.smtp.from", "SWIFTServer.pabk.sk");
		MimeMessage email=new MimeMessage(Session.getDefaultInstance(pro,null));
		email.addRecipient(Message.RecipientType.TO, new InternetAddress("branislav.brandys@pabk.sk"));
		email.setSubject("ºubovoæn˝ poËeù", "utf-8");
		MimeMultipart mp=new MimeMultipart();
		MimeBodyPart mbp=new MimeBodyPart();
		mbp.setText("ºubovoæn˝ poËeù","utf-8");
		mp.addBodyPart(mbp);
		email.setContent(mp);
		Transport.send(email);
	}

}
