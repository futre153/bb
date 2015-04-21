package org;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.pabk.emanager.parser.fin.SAARJEParser;
import org.pabk.emanager.util.SimpleFileFilter;

public class Tester {

	/**
	 * @param args
	 * @throws MessagingException 
	 * @throws AddressException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws AddressException, MessagingException, IOException {
		File f = new File("D:\\TEMP\\01561531_20150420090805.txt.ARCH");
		FileInputStream in = new FileInputStream(f);
		new SAARJEParser().parse(in, null);
		in.close();
		/*File f = new File("C:\\ARCHIVE\\BATCH\\Broadcast_cz");
		SimpleFileFilter filter = new SimpleFileFilter("\\d{8}\\.[pt][rx][t]", 90);
		File[] list = f.listFiles(filter);
		System.out.println(list.length);
		System.out.println(Arrays.toString(list));
		*/
		/*
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
		*/
	}

}
