package org.acepricot;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import javax.naming.NamingException;

import org.acepricot.finance.client.MessageProcessor;

//import org.acepricot.finance.naming.AceName;

public class Start {

	public static void main(String[] args) throws NamingException, SQLException, ClassNotFoundException, IOException  {
		
		final String krbfile = "D:/Temp/krb5.conf";
	    final String loginfile = "D:/Temp/login.conf";
	    
	    System.setProperty("java.security.krb5.conf", krbfile);
        System.setProperty("java.security.auth.login.config", loginfile);
        
		//ApplicationContext ctx2 = new ClassPathXmlApplicationContext("classpath:/META-INF/spring/app-context.xml");
		//BasicDataSource ds = ctx2.getBean(BasicDataSource.class);
		/*
		System.setProperty("java.io.tmpdir", "D:\\Temp");
		System.setProperty("log4j.configuration", "file:D:\\Programy\\BB\\s002\\symmetric-3.5.13\\conf\\log4j.xml");
		String proPath = "D:\\Programy\\BB\\s002\\symmetric-3.5.13\\conf\\symmetric.properties";
		Properties pro = new Properties();
		FileInputStream in = new FileInputStream(proPath);
		pro.load(in);
		in.close();
		proPath = "D:\\Programy\\BB\\s002\\symmetric-3.5.13\\engines\\CL010000.properties";
		in = new FileInputStream(proPath);
		pro.load(in);
		in.close();
		
		ISymmetricEngine engine = new ClientSymmetricEngine(pro, true);
		//System.out.println(engine.start());
		engine.start();
		try {
			Thread.sleep(180000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		engine.stop();
		System.out.println(System.getProperty("os.name"));
		System.out.println(System.getProperty("os.name").toUpperCase().contains(("WINDOWS")));
		System.exit(0);
		*/
		//Name ace = new AceName();
		//Class.forName("org.acepricot.finance.naming.AceName");
		//System.setProperty(Context.INITIAL_CONTEXT_FACTORY,"org.acepricot.finance.naming.AceName");
		  //      System.setProperty(Context.URL_PKG_PREFIXES,"org.apache.naming"); 
		//Context ctx = new InitialContext();
		//ctx.bind("moje", "je toto");
		//ctx.bind("jdbc","jano");
		//DataSource ds = (DataSource) env.lookup("jdbc/h2");
		//Properties pro = (Properties) ctx.getEnvironment();
		
		//System.out.println(pro);
		//DBConnector3 db = new DBConnector3("MAIN", null);
		
		
		/*System.setProperty("com.sun.management.jmxremote", "true");
		System.setProperty("com.sun.management.jmxremote.port", "31416");
		System.setProperty("com.sun.management.jmxremote.authenticate", "false");
		System.setProperty("com.sun.management.jmxremote.ssl", "false");*/
		//JMXServiceURL url = new JMXServiceURL("rmi", "p3600x006", 31416);
		//JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://p3600x006:31418/jmxrmi");
        //JMXConnector jmxc = JMXConnectorFactory.connect(url);
        //jmxc.close();
        //System.exit(0);
		
		/*String[] pgm = {
				"D:\\Programy\\BB\\corp\\symmetric-3.5.13\\bin\\symadmin.bat",
				"--engine",
				"SERV0003",
				"--properties",
				"D:\\Programy\\BB\\corp\\symmetric-3.5.13\\engines\\SERV0003.properties",
				"create-sym-tables"
			};
			Process process = Runtime.getRuntime().exec(pgm);
			int status = 1;
			 String line;
			 BufferedReader bri = new BufferedReader
			        (new InputStreamReader(process.getInputStream()));
			      BufferedReader bre = new BufferedReader
			        (new InputStreamReader(process.getErrorStream()));
			      while ((line = bri.readLine()) != null) {
			        System.out.println(line);
			      }
			      bri.close();
			      while ((line = bre.readLine()) != null) {
			        System.out.println(line);
			      }
			      bre.close();
			try {
				status = process.waitFor();
			} catch (InterruptedException e) {}
			process.destroy();
			if(status != 0) {
				throw new IOException("Failed to load symmetric tables");
			}
		*/
		
		//REGISTRATION
		MessageProcessor.register("futre5@szm.sk", "password5", "1");
		System.out.println("Status="+MessageProcessor.getStatus());
		System.out.println("Message="+MessageProcessor.getMessage());
		
		String svid = MessageProcessor.getMessage();
		
		
		//String svid = "SERV0000";
		//String clid = "CL000000";
		
		//INIT SYNCHRONIZATION
		MessageProcessor.initSynchronization(svid, "futre5@szm.sk", "password5", null);
		System.out.println("Status="+MessageProcessor.getStatus());
		System.out.println("Message="+MessageProcessor.getMessage());
		String clid = MessageProcessor.getMessage();
		
		//uplad db file
		MessageProcessor.upload(svid, new File("D:\\Programy\\BB\\AceClient.zip"));
		System.out.println("Status="+MessageProcessor.getStatus());
		System.out.println("Message="+MessageProcessor.getMessage());
		
		String msg = MessageProcessor.getMessage();
		if(!msg.equals("NOFILE")) {	
			Date t1 = new Date();
			byte[] b = new byte[16*1024];
			FileInputStream in = new FileInputStream("D:\\Programy\\BB\\AceClient.zip");
			int i;
			int counter = 1;
			while((i = in.read(b)) >=0) {
				byte[] a = new byte[i];
				System.arraycopy(b, 0, a, 0, i);
				//System.out.println(Arrays.toString(a));
				MessageProcessor.uploadFilepart(clid, svid,  counter, a);
				//System.exit(1);
				System.out.println(MessageProcessor.getStatus());
				System.out.println(MessageProcessor.getMessage()+", "+counter);
				counter++;
			}
			in.close();
			System.out.println(((new Date().getTime() - t1.getTime())/1000) + " sec");
		}
		
		//REPLICATION START
		MessageProcessor.requestSyncConfig(clid);
		
		System.out.println("Status="+MessageProcessor.getStatus());
		System.out.println("Message="+MessageProcessor.getMessage());
		System.out.println("Filename="+MessageProcessor.getConfigFilename());
		System.out.println("Config="+MessageProcessor.getConfigFile());
		
		
		/*
		//REMOVE_NODE
		MessageProcessor.removeNode("futre@szm.sk", "password", "SERV0000", "CL000000");
		System.out.println("Status="+MessageProcessor.getStatus());
		System.out.println("Message="+MessageProcessor.getMessage());
		*/
		
		/*
		//DEREGISTER SUBJECT
		MessageProcessor.deregister("futre@szm.sk", "password", "SERV0000");
		System.out.println("Status="+MessageProcessor.getStatus());
		System.out.println("Message="+MessageProcessor.getMessage());
		*/
				
		/*
		//REMOTE SQL OPERATION
		Hashtable<String, Object> data = new Hashtable<String, Object>();
		data.put(AceData.SQL_TABLE_NAME_KEY, "USER.CATEGORIES");
		data.put("CATEGORY_NAME", "mouse");
		//data.put(AceData.SQL_WHERE_KEY, "WHERE CATEGORY_NAME = 'war dog'");
		MessageProcessor.remoteInsert(data, "CL000000");
		System.out.println("Status="+MessageProcessor.getStatus());
		System.out.println("Message="+MessageProcessor.getMessage());
		*/
		
		/*String xxx= "MIICszCCAZsCAQAwbjEfMB0GA1UEAxMWQWxsaWFuY2UgQWNjZXNzIGFjY2VzczEM"+
					"MAoGA1UECxMDRElUMRswGQYDVQQKExJQb3N0b3ZhIGJhbmthIGEucy4xEzARBgNV"+
					"BAcTCkJyYXRpc2xhdmExCzAJBgNVBAYTAlNLMIIBIjANBgkqhkiG9w0BAQEFAAOC"+
					"AQ8AMIIBCgKCAQEAvg5rduZiR+KmMgg6cCBzHBV8bSnSN0op7kd7MjMj6xKFxPvm"+
					"KUtADBdILzcHB0HkczS82WrtLilNlCVSBYUV6O/NSLAWRMHeazdVea/Bziq699sh"+
					"tOaSkhsNSK1gz4lqDTTHldmeQfM7R1qYsAlmdbqDbG40ZAJ1Cs+CuOSDjcqyAKXt"+
					"zdFVfiFIdkO4IVSbVazO5n910YfU4u0VpidXhkLNI+5/xa4zENWeXjtJCczaFp7v"+
					"N4TDwfLwPlu/pTyH3xnxtQnv1sDMiY41qHRzyWrApokpJ2Vuj/I5yKWVqnh5XxfY"+
					"zj8Ixi+rc3vo9w07inZFeHsfpgGRbWoXE4P/wwIDAQABoAAwDQYJKoZIhvcNAQEF"+
					"BQADggEBAGXNjHlduzf4m6kHyoGLIIInoxqkvd+Cq5q7b6oRO9uV3pY9IXWYKp23"+
					"3gEfXROnIEDgQkgo/QzqImmyFtm+cnqgO6LA3GaaZLB3lMYDan3iwlPoI/AueLdU"+
					"7d3gxSUHXgHDQc19IUj2TrV9KaXhvajzZrdOvPibDNfdKk0HJyUoFq0Xrb1kjtxZ"+
					"ywdr+RW685DKm6a9Oj5O65lxmA0cDPsBi9zsWdXB6hCaf4wy+4SGwk7hyoZPgEGR"+
					"6A5IrahRiKF7Q7MZq/swXmPJoVYO131CWhTGLlTLKW35HiJGVMC1A1eR/TaTNGLk"+
					"tRIaUbBqDFizZWUMd54XyUr+RPKqnak=";
*/		//byte[] v = new byte[0x0FFFFFFF];
		/*
		String xxx= "MHMwMAQRUmVnaXN0cmF0aW9uRW1haWwGAAQZYnJhbmlzbGFiLmJyYW5keXNAcGFiay5zazA/BBBEaWdlc3RlZFBhc3N3b3JkBglghkgBZQMEAgEEIMHrPpW+xypR19Ds4eN/zwBM6z8CZemoCZJQHRnGMu1x";
					//System.out.println(Arrays.toString(Base64Coder.decode(xxx)));
		//BERIOStream enc = new BERIOStream();
		//enc.setInputStream(new ByteArrayInputStream(Base64Coder.decode(xxx)));
		//BER ber = new BER();
		AceData data = new AceData();
		
		//data.encode(enc);

		try {
			data.decode(enc);
//			req.loadFromExisting(ber);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(data);
		
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update("Maly strakaty pes".getBytes());
		System.out.println(Arrays.equals(data.get("DigestedPassword").getItemValue(), md.digest()));
		*/
	}
}
