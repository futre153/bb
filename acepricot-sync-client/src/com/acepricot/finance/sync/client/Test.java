package com.acepricot.finance.sync.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

import javax.xml.bind.DatatypeConverter;

import org.pabk.net.http.DefaultContent;
import org.pabk.net.http.HttpClientConst;
import org.pabk.net.http.SimpleClient;
import org.pabk.util.Base64Coder;
import org.pabk.util.Huffman;

import com.acepricot.finance.sync.DBConnector;
import com.acepricot.finance.sync.share.JSONMessage;
import com.acepricot.finance.sync.share.sql.CompPred;
import com.acepricot.finance.sync.share.sql.Identifier;
import com.acepricot.finance.sync.share.sql.Insert;
import com.acepricot.finance.sync.share.sql.Predicate;
import com.acepricot.finance.sync.share.sql.Query;
import com.acepricot.finance.sync.share.sql.TableName;
import com.acepricot.finance.sync.share.sql.WhereClause;
import com.google.gson.Gson;

public class Test {
	
	public static String url = "http://localhost:8080/acepricot-sync/";
	//public static File f = new File("D:\\TEMP\\sampleclient\\database1-1.h2.db");
	public static void main(final String[] a) throws Exception {
		//System.out.println(Huffman.encode("ptraajtn39ln", null));
		//System.exit(1);
		/*
		Object com1 = new CompPred(new Object[]{new Identifier("A"),new Identifier("B")}, new Object[]{1, 2}, Predicate.EQUAL);
		Object com2 = new CompPred(new Object[]{new Identifier("C")}, new Object[]{3},Predicate.EQUAL);
		Object where = new WhereClause(com1, WhereClause.AND, com2);
		
		System.out.println(where);
		Class.forName("com.sap.dbtech.jdbc.DriverSapDB");
		Connection con = DriverManager.getConnection("jdbc:sapdb://p3600x006/aceserve", "ACESVRADM", "nahradnik06");
		
		TableName tableName = new TableName(new Identifier("SYNC_RESPONSES"));
		Object[] values = new Object[]{61, 10};
		String[] cols = new String[]{"GROUP_ID","DEVICE_ID"};
		Insert sql = DBConnector.createInsert(tableName, (Object)values, cols);
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bout);
		out.writeObject(sql);
		
		String enc = new String(Base64Coder.encode(Huffman.encode(bout.toByteArray(), null)));
		byte[] b = Huffman.decode(Base64Coder.decode(enc.toCharArray()), null);
		
		ByteArrayInputStream bin = new ByteArrayInputStream(b);
		ObjectInputStream in = new ObjectInputStream(bin);
		
		Insert insert = (Insert) in.readObject();
		
		

		try {
			DBConnector.insert(con, insert);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		*/
		JSONMessageProcessorClient.syncStart(new String[]{
				"D:\\TEMP\\clientprops\\sync_props1-1.xml",
				"D:\\TEMP\\clientprops\\sync_props1-2.xml",
				"D:\\TEMP\\clientprops\\sync_props1-3.xml"});

	}
	
	public static JSONMessage process(JSONMessage msg, String param, boolean put) throws Exception {
		SimpleClient.setCookiesAllowed(true);
		System.out.println(msg==null?"NULL":msg.getHeader());
		SimpleClient client = new SimpleClient(url + param);
		DefaultContent c = null;
		if(put) {
		
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
		if(msg.isError()) {
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

	public static Properties getSyncProperties() {
		Properties pro = new Properties();
		pro.put(JSONMessageProcessorClient.GRP_NAME_KEY, "MyGroup1");
		pro.put(JSONMessageProcessorClient.GRP_PSWD_KEY, "MyGroup1");
		pro.put(JSONMessageProcessorClient.GRP_PSCH_KEY, "UTF-8");
		pro.put(JSONMessageProcessorClient.GRP_JOIN_KEY, true);
		pro.put(JSONMessageProcessorClient.DEV_NAME_KEY, "MyDevice1-3");
		pro.put(JSONMessageProcessorClient.GRP_EMAI_KEY, "futre@szm.sk");
		pro.put(JSONMessageProcessorClient.DEV_PRIM_KEY, false);
		pro.put(JSONMessageProcessorClient.DB_NAME_KEY, "D:\\TEMP\\clientsamples\\database1-3.h2.db");
		return pro;
	}
	
}
