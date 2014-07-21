package com.acepricot.finance.sync.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Date;

import org.pabk.net.http.DefaultContent;
import org.pabk.net.http.HttpClientConst;
import org.pabk.net.http.SimpleClient;

import com.acepricot.finance.sync.share.JSONMessage;
import com.google.gson.Gson;

public class Test {
	public static void main(final String[] a) throws Exception {
		
		Class.forName("org.h2.Driver");
		Connection con = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/DATABASE01;AUTO_SERVER=TRUE;LOCK_TIMEOUT=60000;CIPHER=AES", "", "cnuewf092no ptraajtn39ln");
		DBSchemas.setTrigger(false);
		DBSchemas.loadSchemas(con);
		DBSchemas.setTrigger(true);
		con.close();
		
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		byte[] digest = md.digest(new byte[]{});
		
		JSONMessage msg = new JSONMessage("heartbeat", new Object[]{new Date().getTime()});
		//String param = "?header="+msg.getHeader()+"&body="+new Gson().toJson(msg.getBody());
		msg = process(msg, "", false);
		/*Date x = new Date();
		System.out.println((double)x.getTime());
		System.out.println("Request time is "+ ((double)msg.getBody()[1] - (double)msg.getBody()[0])/1000 + "s");
		System.out.println("Response time is "+ (x.getTime() - (double)msg.getBody()[1])/1000 + "s");
		System.out.println("Full time is "+ (x.getTime() - (double)msg.getBody()[0])/1000 + "s");
		*/
		msg = new JSONMessage("login", new Object[]{"MyGroup",digest});
		msg = process(msg, "", false);
		
		msg = new JSONMessage("register", new Object[] {
				"MyGroup",
				digest,
				"futre@szm.sk",
				1
		});
		msg = process(msg, "", false);
		
		msg = new JSONMessage("registerDevice", new Object[]{
				"MyGroup",
				digest,
				"MyDevice4",
				1,
				1
		});
		
		msg = process(msg, "", false);
		/*
		FileInputStream fin = new FileInputStream(new File("D:\\Dokumenty\\My Documents.rar"));
		byte[] b = new byte[4096];
		md.reset();
		int i;
		while((i = fin.read(b)) >= 0) {
			md.update(b, 0, i);
		}
		byte[] digest2 = md.digest();
		md.reset();fin.close();
		msg = new JSONMessage("initUpload", new Object[] {
				"MyGroup",
				digest,
				digest2,
		});
		
		msg = process(msg, "", false);
		
		msg = process(msg, "?id=1", true);*/		
	}
	
	public static JSONMessage process(JSONMessage msg, String param, boolean put) throws Exception {
		System.out.println(msg==null?"NULL":msg.getHeader());
		SimpleClient client = new SimpleClient("http://localhost:8080/acepricot-sync/" + param);
		DefaultContent c = null;
		if(put) {
			client.setMethod(HttpClientConst.PUT_METHOD);
			c = new DefaultContent(new File("D:\\Dokumenty\\My Documents.rar"), HttpClientConst.APP_H2DB_CONTENT);
		}
		else {
			c = new DefaultContent(new Gson().toJson(msg), HttpClientConst.APP_JSON_CONTENT);
		}
		//c.setAdditionalProperty("Accept-Encoding", "gzip");
		//c.setContentEncoding(HttpClientConst.GZIP_INDEX);
		//InputStreamReader in = new InputStreamReader(new GZIPInputStream(client.execute(c)), "UTF-8");
		InputStream in = client.execute(c);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int i = 0;
		while((i = in.read()) >= 0) {
			out.write(i);
		}
		client.close();
		InputStreamReader bin = new InputStreamReader(new ByteArrayInputStream(out.toByteArray()), "UTF-8");
		try {
			msg = new Gson().fromJson(bin, JSONMessage.class);
		}
		catch(Exception e) {
			msg = new JSONMessage("error", new Object[]{"UNKNOWN"});
			bin = new InputStreamReader(new ByteArrayInputStream(out.toByteArray()), "UTF-8");
		}
		if(msg == null) {
			msg = new JSONMessage("error", new Object[]{"NULL"});
		}
		System.out.println(msg.getHeader());
		//System.out.println(Arrays.toString(msg.getBody()));
		System.out.println(msg.getBody()[0]);
		char[] chr = new char[1024];
		i = 0;
		while((i = bin.read(chr)) >= 0) {
			System.out.println(new String(chr, 0, i));
		}
		return msg;
	}
	
}
