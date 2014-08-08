package com.acepricot.finance.sync.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import javax.xml.bind.DatatypeConverter;

import org.pabk.net.http.DefaultContent;
import org.pabk.net.http.HttpClientConst;
import org.pabk.net.http.SimpleClient;

import com.acepricot.finance.sync.DBConnector;
import com.acepricot.finance.sync.share.JSONMessage;
import com.acepricot.finance.sync.share.sql.CompPred;
import com.acepricot.finance.sync.share.sql.Identifier;
import com.acepricot.finance.sync.share.sql.Predicate;
import com.acepricot.finance.sync.share.sql.Query;
import com.acepricot.finance.sync.share.sql.WhereClause;
import com.google.gson.Gson;

public class Test {
	
	public static String url = "http://localhost:8080/acepricot-sync/";
	public static File f = new File("C:\\Users\\brandys\\Desktop\\database.h2.db");
	public static void main(final String[] a) throws Exception {
		//System.out.println(Base64Coder.encodeString(Huffman.encode(pass, null)));
		//System.out.println("D:\\TEMP\\clientdb\\11110.h2.db".replaceAll("\\\\", "/").replaceAll("\\.h2\\.db", ""));
		//byte[] bt = DatatypeConverter.parseHexBinary("22f659366bab54ff041fb6c544d9aa9a798aaeee59b55a7c9bde792c15f400".toUpperCase());
		
		/*File f2 = new File("D:\\TEMp\\acetmpdir\\10000001406187367702");
		
		FileInputStream fin1 = new FileInputStream(f);
		FileInputStream fin2 = new FileInputStream(f2);
		
		long c1 = 0;
		long c2 = 0;
		int i1 = 0, i2 = 0;
		while(true) {
			i1 = i1 < 0 ? i1 : fin1.read();
			i2 = i2 < 0 ? i2 : fin2.read();
			if((i1 == -1) && (i2 == -1)) {
				break;
			}
			if((i1 >= 0) && (i2 >= 0)) {
				if(i1 != i2) {
					System.out.println(c1 + ": " + i1 + ", " + i2);
					new Sleeper().sleep(100);
				}
			}
			if(i1 >= 0) {
				c1 ++;
			}
			if(i2 >= 0) {
				c2 ++;
			}
		}
		
		fin1.close();
		fin2.close();*/
		/*
		Class.forName("com.sap.dbtech.jdbc.DriverSapDB");
		Connection con = DriverManager.getConnection("jdbc:sapdb://p3600x006/aceserve", "ACESVRADM", "nahradnik06");
		
		CompPred com1 = new CompPred(
				new Object[]{new Identifier("GROUP_NAME")},
				new Object[]{"MyGroup"},
				Predicate.EQUAL);
		int count = DBConnector.count(con, "REGISTERED_GROUPS", new WhereClause(com1));
		System.out.println(count);
		//com1.getPreparedBuffer().close();
		Query select = DBConnector.createSelect();
		select.addFromClause(new Identifier("REGISTERED_GROUPS"));
		select.addColumns("ID", "PASSWORD", "ENABLED");
		select.addTableSpec(new WhereClause(com1));
		System.out.println(DBConnector.select(con, select));
		
		con.close();
		*/
		/*
		SQLSyntaxImpl.setPrepared(false);
		
		CompPred p = new CompPred(new Object[]{new Identifier("GROUP_NAME")}, new String[]{"MyGroup"}, Predicate.EQUAL);
		LikePred l = new LikePred(true, new Identifier("ID"), 1);
		BetweenPred b = new BetweenPred(false, new Identifier("ID"), 0, 1000);
		
		WhereClause where = new WhereClause(WhereClause.LEFT_BRACKET, WhereClause.NOT, WhereClause.LEFT_BRACKET, p, WhereClause.AND, l, WhereClause.RIGHT_BRACKET, WhereClause.OR, b, WhereClause.RIGHT_BRACKET);
				
		FromClause tableName = new FromClause(new Identifier("REGISTERED_GROUPS"));
		
		TableExp texp = new TableExp(where, null, null, tableName, new FromClause(new Identifier("Regina")));
		SelectColumn cols = new SelectColumn(null);
		QuerySpec qspec = new QuerySpec(texp, cols);
		
		
		Query q = DBConnector.createSelect();
		q.addFromClause(new Identifier("REGISTERED_GROUPS"));
		q.addSelectColumnFunction("COUNT");
		q.addColumns("ID", "GROUP_NAME");
		q.addDerived("COUNT_ID");
		System.out.println(q.toSQLString());
		*/
		//System.exit(1);
		/*
		Class.forName("org.h2.Driver");
		Connection con = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/DATABASE01;AUTO_SERVER=TRUE;LOCK_TIMEOUT=60000;CIPHER=AES", "", "cnuewf092no ptraajtn39ln");
		DBSchemas.setTrigger(false);
		//DBSchemas.dropSyncSchema(con);
		DBSchemas.loadSchemas(con);
		DBSchemas.setTrigger(true);
		con.close();
		System.exit(0);
		*/
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
		
		UploadFile t = new UploadFile("MyGroup", "", f, url, null, null);
		t.setDaemon(true);
		t.start();
		Sleeper s = new Sleeper();
		while(t.isAlive()) {
			s.sleep(100);
			//System.out.println(t.getActionInProgress());
			System.out.print(">");
		}
		System.out.println();
		msg = t.getMessage();
		System.out.println(msg.getHeader());
		//System.out.println(Arrays.toString(msg.getBody()));
		System.out.println(msg.getBody()[0]);
		
		msg = new JSONMessage("heartbeat", new Object[]{new Date().getTime()});
		msg = process(msg, "", false);
		
		msg = new JSONMessage("initSync",new Object[]{"MyGroup", digest, "MyDevice"});
		msg = process(msg, "", false);
		
		String dst = "D:\\TEMP\\jano.txt";
		if(!msg.isError()) {
			DownloadFile t2 = new DownloadFile(
					dst,
					url,
					((Double) msg.getBody()[1]).intValue(),
					((Double) msg.getBody()[2]).intValue(),
					(String) msg.getBody()[3],
					DatatypeConverter.parseHexBinary(JSONMessageProcessorClient.constructHexHash((ArrayList<?>) msg.getBody()[4])),
					((Double) msg.getBody()[5]).intValue());
			t2.start();
			while(t2.isAlive()) {
				s.sleep(1);
				//System.out.println(t2.getActionInProgress());
				System.out.print(">");
			}
			//msg = t2.download();
			msg = t2.getMessage();
			System.out.println(msg.getHeader());
			System.out.println(Arrays.toString(msg.getBody()));
			System.out.println(msg.getBody()[0]);
			
		}
	}
	
	public static JSONMessage process(JSONMessage msg, String param, boolean put) throws Exception {
		SimpleClient.setCookiesAllowed(true);
		System.out.println(msg==null?"NULL":msg.getHeader());
		SimpleClient client = new SimpleClient(url + param);
		DefaultContent c = null;
		if(put) {
			client.setMethod(HttpClientConst.PUT_METHOD);
			c = new DefaultContent(f, HttpClientConst.APP_H2DB_CONTENT);
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
		//client.close();
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
		if(!msg.isError()) {
			System.out.println(Arrays.toString(msg.getBody()));
		}
		System.out.println(msg.getBody()[0]);
		char[] chr = new char[1024];
		i = 0;
		while((i = bin.read(chr)) >= 0) {
			System.out.println(new String(chr, 0, i));
		}
		return msg;
	}
	
}
