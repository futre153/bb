package com.acepricot.finance.sync.client;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.xml.bind.DatatypeConverter;

import org.pabk.net.http.DefaultContent;
import org.pabk.net.http.HttpClientConst;
import org.pabk.net.http.SimpleClient;
import org.pabk.util.Base64Coder;
import org.pabk.util.Huffman;

import com.acepricot.finance.sync.DBConnector;
import com.acepricot.finance.sync.DBSchema;
import com.acepricot.finance.sync.Row;
import com.acepricot.finance.sync.share.AppConst;
import com.acepricot.finance.sync.share.JSONMessage;
import com.acepricot.finance.sync.share.sql.ColumnSpec;
import com.acepricot.finance.sync.share.sql.CompPred;
import com.acepricot.finance.sync.share.sql.Identifier;
import com.acepricot.finance.sync.share.sql.Insert;
import com.acepricot.finance.sync.share.sql.Predicate;
import com.acepricot.finance.sync.share.sql.SchemaName;
import com.acepricot.finance.sync.share.sql.TableName;
import com.acepricot.finance.sync.share.sql.WhereClause;
import com.google.gson.Gson;

public class JSONMessageProcessorClient {
	
	private static final String DEFAULT_SYNC_PROPATH = "D:\\TEMP\\sync_properties.xml";
	public static final String GRP_NAME_KEY = "grpName";
	public static final String GRP_PSWD_KEY = "grpPswd";
	public static final String GRP_PSCH_KEY = "grpPsCh";
	public static final String GRP_JOIN_KEY = "grpJoin";
	public static final String DEV_NAME_KEY = "devName";
	public static final String GRP_EMAI_KEY = "grpEmai";
	public static final String DEV_PRIM_KEY = "devPrim";
	private static final String DEFAULT_URL = "http://localhost:8080/acepricot-sync/";
	private static final String DEFAULT_MESSAGE_DIGEST_ALGORITHM = "SHA-256";
	static final String HEARTBEAT_HEADER = "heartbeat";
	private static final String REGISTER_HEADER = "register";
	static final Integer DEFAULT_DB_VERSION = 1;
	//private static final String DATABASE_FILENAME = "D:\\TEMP\\clientsamples\\database1-2.h2.db"; 
	//private static final File DATABASE_FILE = new File(DATABASE_FILENAME);
	private static final String REGISTER_DEVICE_HEADER = "registerDevice";
	public static final String INIT_UPLOAD_HEADER = "initUpload";
	private static final String INIT_SYNC_HEADER = "initSync";
	public static final String DB_USER_KEY = "dbUser";
	private static final String LOCAL_DB_USER = "";
	public static final String DB_PSWD_KEY = "DBPswd";
	private static final String LOCAL_DB_USER_PASSWORD = "E3o0SRTrfUJj7W8S+Ik5b951DG30AA==";
	public static final String DB_NAME_KEY = "DBName";
	public static final String DEFAULT_URL_KEY = "DefUrl";
	public static final String JDBC_DRIVER_KEY = "JDBCDriver";
	private static final String JDBC_DRIVER = "org.h2.Driver";
	public static final String URL_STRING_KEY = "DBUrl";
	private static final String URL_STRING = "jdbc:h2:tcp://localhost/%s;AUTO_SERVER=TRUE;LOCK_TIMEOUT=60000;CIPHER=AES";
	public static final String EXT_REPL_KEY = "ExtRepl";
	private static final String EXT_REPLACEMENT = "\\.h2\\.db";
	public static final String SYNC_REQUEST_HEADER = "syncRequest";
	


	private JSONMessageProcessorClient() {}
	
	static final void syncStart(String[] propath) {
		/*
		 * TODO database lock
		 */
		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run(){
				SyncClientEngine.setShutdown();
			};
		});
		File f = null;
		if(propath == null) {
			propath = new String[] {DEFAULT_SYNC_PROPATH};
		}
		for(int i = 0; i < propath.length; i ++) {
			Exception error = null;
			Properties p = new Properties();
			try {
				
				f = new File(propath[i]);
				if(!f.exists()) {
					if(f.createNewFile()) {
						/*
						 * odkaz na obrzovku, kde sa ziskaju nasledovne vlastnosti>
						 * "grpName" - meno group			String
						 * "grpPswd" - heslo pre group		CharSequence
						 * "grpPsCh" - znakova sada pre helso String
						 * "grpJoin" - boolean, typ operacie, ak sa bude vytvarat novy uzol potom bude false, ak sa bude pripajat k existujucemu uzlu, tak true
						 * "devName" - meno pre device		String
						 * "grpEmai" - registrovany e-mail	String
						 * "devPrim" - primarne royhranie	boolean
						 * "dbName" - umiestnenie databazoveho suboru String
						 * "dbUser" - lokalny pouzivatel db
						 * "dbPswd" - heslo lokalneho pouzivatela
						 */
						p = Test.getSyncProperties();
						p.setProperty(EXT_REPL_KEY, EXT_REPLACEMENT);
						p.setProperty(URL_STRING_KEY, URL_STRING);
						p.setProperty(JDBC_DRIVER_KEY, JDBC_DRIVER);
						p.setProperty(DEFAULT_URL_KEY, DEFAULT_URL);
						p.setProperty(DB_USER_KEY, LOCAL_DB_USER);
						p.setProperty(DB_PSWD_KEY, LOCAL_DB_USER_PASSWORD);
						p.put(GRP_PSWD_KEY, Huffman.encode((String) p.get(GRP_PSWD_KEY), p.getProperty(GRP_PSCH_KEY), null));
						Object join = p.remove(GRP_JOIN_KEY);
						if(join == null) {
							throw new IOException("Property grpJoin is missing");
						}
						if((boolean) join || (i > 0)) {
							try {
								joinToGroup(p);
							}
							catch(Exception e) {
								//TODO akcia pri chybe pri pridani device
								throw e;
							}
						}
						else {
							try {
								createGroup(p);
							}
							catch(Exception e) {
								//TODO akcia pri chybe pri vztvoreni group
								throw e;
							}
						}
						p.storeToXML(new FileOutputStream (f), "Properties for synchronization", p.getProperty(GRP_PSCH_KEY));					
					}
					else {
						throw new IOException("Failed to create sync properties file " + propath + ". Please set correct path to sync properties");
					}
				}
			}
			catch(Exception e) {
				e.printStackTrace();
				//zobraz chybu konfiguracie
				/*
				 * TODO synchronizacia sa nenakonfigurovala
				 */
				try {
					f.delete();
				}
				catch(Exception e1) {
					//TODO zobraz chybu
					//TODO akcia pri chybe
					e1.printStackTrace();
				}
				error = e;
			}
			finally {
				/*
				 * TODO database unlock
				 */
				
				
			}
			if(error == null) {
				try {
					if(f.isFile()) {
						p.loadFromXML(new FileInputStream(f));
					}
					else {
						throw new IOException(propath + " is directory, please set correct path to sync properties");
					}
					SyncClientEngine.start(p);
				} catch (IOException | SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				finally {
					/*
					 * TODO synchronizacia pre node i koniec
					 */
				}
			}
			else {
				/*
				 * spracuj chybu 2
				 */
			}
		}
		try {
			SyncClientEngine.joinTo();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		 * TODO synchroniyacia koniec
		 */
	}
	
	public static JSONMessage process(JSONMessage msg, String url, String param) throws Exception {
		SimpleClient client;
		DefaultContent c;
		if(msg == null) {
			return new  JSONMessage().sendAppError("JSONMessage cannot be null");
		}
		if(url == null) {
			return new  JSONMessage().sendAppError("URL cannot be null");
		}
		if(param == null) {
			client = new SimpleClient(url);
		}
		else {
			client = new SimpleClient(url + param);
		}
		c = new DefaultContent(new Gson().toJson(msg), HttpClientConst.APP_JSON_CONTENT);
		
		//c.setAdditionalProperty("Accept-Encoding", "gzip");
		//c.setContentEncoding(HttpClientConst.GZIP_INDEX);
		//InputStreamReader in = new InputStreamReader(new GZIPInputStream(client.execute(c)), "UTF-8");
		try {
			InputStream in = client.execute(c);
			return responseProcessor(in);
		}
		catch (Exception e) {
			return new JSONMessage().sendAppError(e);
		}
		/*ByteArrayOutputStream out = new ByteArrayOutputStream();
		int i = 0;
		while((i = in.read()) >= 0) {
			out.write(i);
		}
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
		return msg;*/
	}
	
	
	
	private static void createGroup(Properties p) throws Exception {
		String url = p.getProperty(DEFAULT_URL_KEY, DEFAULT_URL);
		JSONMessage msg = new JSONMessage(HEARTBEAT_HEADER, new Object[]{new Date().getTime()});
		msg = process(msg, url, null);
		if(msg.isError()) {
			//TODO zobrazit chybu
			throw new IOException((String) msg.getBody()[0]);
		}
		else {
			//TODO zobrazit spojenie OK
		}
		MessageDigest md = MessageDigest.getInstance(DEFAULT_MESSAGE_DIGEST_ALGORITHM);
		byte[] digest = md.digest(Huffman.decode(p.getProperty(GRP_PSWD_KEY), p.getProperty(GRP_PSCH_KEY), null).getBytes(p.getProperty(GRP_PSCH_KEY)));
		msg = new JSONMessage(REGISTER_HEADER, new Object[] {p.getProperty(GRP_NAME_KEY), digest, p.getProperty(GRP_EMAI_KEY), DEFAULT_DB_VERSION});
		msg = process(msg, url, null);
		if(msg.isError()) {
			//TODO zobrazit chybu
			throw new IOException((String) msg.getBody()[0]);
		}
		else {
			//TODO zobrazit registracia OK
		}
		msg = new JSONMessage(REGISTER_DEVICE_HEADER, new Object[] {p.getProperty(GRP_NAME_KEY), digest, p.getProperty(DEV_NAME_KEY), (boolean) p.get(DEV_PRIM_KEY) ? 1 : 0, DEFAULT_DB_VERSION});
		msg = process(msg, url, null);
		if(msg.isError()) {
			//TODO zobrazit chybu
			throw new IOException((String) msg.getBody()[0]);
		}
		else {
			//TODO zobrazit registracia zariadenia OK
		}
		UploadFile t = new UploadFile (
				p.getProperty(GRP_NAME_KEY),
				Huffman.decode(p.getProperty(GRP_PSWD_KEY), p.getProperty(GRP_PSCH_KEY), null),
				new File(p.getProperty(DB_NAME_KEY)),
				url,
				DEFAULT_MESSAGE_DIGEST_ALGORITHM,
				p.getProperty(GRP_PSCH_KEY));
		t.setDaemon(true);
		t.start();
		Sleeper s = new Sleeper();
		while(t.isAlive()) {
			s.sleep(100);
			// TODO zobraz napredovanie uploadu
		}
		msg = t.getMessage();
		if(msg.isError()) {
			//TODO zobrazit chybu
			throw new IOException((String) msg.getBody()[0]);
		}
		else {
			//TODO zobrazit upload OK
		}
		msg = new JSONMessage(INIT_SYNC_HEADER, new Object[]{p.getProperty(GRP_NAME_KEY), digest, p.getProperty(DEV_NAME_KEY)});
		msg = process(msg, url, null);
		if(msg.isError()) {
			//TODO zobrazit chybu
			throw new IOException((String) msg.getBody()[0]);
		}
		else {
			//TODO zobrazit inicializacia synchronizacie OK
		}
		p.setProperty(GRP_PSWD_KEY, DatatypeConverter.printHexBinary(digest));
	}

	private static void joinToGroup(Properties p) throws Exception {
		String url = p.getProperty(DEFAULT_URL_KEY, DEFAULT_URL);
		JSONMessage msg = new JSONMessage(HEARTBEAT_HEADER, new Object[]{new Date().getTime()});
		msg = process(msg, url, null);
		if(msg.isError()) {
			//TODO zobrazit chybu
			throw new IOException((String) msg.getBody()[0]);
		}
		else {
			//TODO zobrazit spojenie OK
		}
		MessageDigest md = MessageDigest.getInstance(DEFAULT_MESSAGE_DIGEST_ALGORITHM);
		byte[] digest = md.digest(Huffman.decode(p.getProperty(GRP_PSWD_KEY), p.getProperty(GRP_PSCH_KEY), null).getBytes(p.getProperty(GRP_PSCH_KEY)));
		msg = new JSONMessage(REGISTER_DEVICE_HEADER, new Object[] {p.getProperty(GRP_NAME_KEY), digest, p.getProperty(DEV_NAME_KEY), (boolean) p.get(DEV_PRIM_KEY) ? 1 : 0, DEFAULT_DB_VERSION});
		msg = process(msg, url, null);
		if(msg.isError()) {
			//TODO zobrazit chybu
			throw new IOException((String) msg.getBody()[0]);
		}
		else {
			//TODO zobrazit registracia yariadenia OK
		}
		msg = new JSONMessage(INIT_SYNC_HEADER, new Object[]{p.getProperty(GRP_NAME_KEY), digest, p.getProperty(DEV_NAME_KEY)});
		msg = process(msg, url, null);
		if(msg.isError()) {
			//TODO zobrazit chybu
			throw new IOException((String) msg.getBody()[0]);
		}
		else {
			//TODO zobrazit inicialiyacia synchroniyacie OK
		}
		try {
			/*
			 * TODO odpojenie databazoveho spojenia
			 */
			
			DownloadFile t2 = new DownloadFile(
				p.getProperty(DB_NAME_KEY),
				url,
				((Double) msg.getBody()[1]).intValue(),
				((Double) msg.getBody()[2]).intValue(),
				(String) msg.getBody()[3],
				DatatypeConverter.parseHexBinary(JSONMessageProcessorClient.constructHexHash((ArrayList<?>) msg.getBody()[4])),
				((Double) msg.getBody()[5]).intValue());
			t2.start();
			Sleeper s = new Sleeper();
			while(t2.isAlive()) {
				s.sleep(100);
				// TODO zobraz priebeh stahovania
			}
			if(msg.isError()) {
				//TODO zobrazit chybu
				throw new IOException((String) msg.getBody()[0]);
			}
			else {
				//TODO zobrazit stahovanie OK
			}
			msg = t2.getMessage();
			p.setProperty(GRP_PSWD_KEY, DatatypeConverter.printHexBinary(digest));
		}
		catch (Exception e) {
			throw e;
		}
		finally {
		/*
		 * TODO obnovenie databazoveho spojenia
		 */
		}
	}

	static final JSONMessage responseProcessor(InputStream in) throws IOException {
		JSONMessage msg = new JSONMessage();
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
		//System.out.println(msg.getHeader());
		//System.out.println(Arrays.toString(msg.getBody()));
		//System.out.println(msg.getBody()[0]);
		char[] chr = new char[1024];
		i = 0;
		StringBuffer sb = new StringBuffer();
		while((i = bin.read(chr)) >= 0) {
			sb.append(chr, 0, i);
		}
		msg.appendBody(sb.toString());
		return msg;
		
	}
	
	static final JSONMessage responseError(InputStream in, String encoding) throws IOException {
		JSONMessage msg = new JSONMessage(AppConst.JSON_ERROR_MSG);
		InputStreamReader bin = new InputStreamReader(in, encoding);
		BufferedReader reader = new BufferedReader(bin);
		String line;
		while ((line = reader.readLine()) != null) {
			msg.appendBody(line);
		}
		return msg;
	}
	
	
	static String constructHexHash(ArrayList<?> arrayList) {
		StringBuffer sb = new StringBuffer(arrayList.size() * 2);
		for(int i = 0; i < arrayList.size(); i ++) {
			Double d = (Double) arrayList.get(i);
			sb.append(String.format("%2s", Integer.toHexString(d.intValue() & 0xFF)).replaceAll(" ", "0"));
		}
		return sb.toString();
	}

	public static JSONMessage processIncomming(Connection con, Properties props, JSONMessage msg, JSONMessage inMsg) throws IOException, SQLException {
		JSONMessage _new = null;
		if(!msg.isError()) {
			Object[] body = msg.getBody();
			if(body.length > 1) {
				if(body[1] instanceof Double) {
					int type = ((Double) body[1]).intValue();
					switch(type) {
					case JSONMessage.NO_ACTION:
						break;
					case JSONMessage.REQUEST_PENDING_RESPONSE:
						_new = processIncommingPendingRequest(con, props, msg);
						break;
					case JSONMessage.INSERT_NO_ACTION:
						_new = processInsertNoAction(con, props, msg);
						break;
					case JSONMessage.INSERT_OPERATION:
						_new = processInsertAction(con, props, msg);
					default:
						throw new IOException("Message type " + type + " is not defined");
					}
					if(inMsg != null) {
						Row row = getRow(inMsg.getBody());
						type = ((Number) row.get(DBSchemas.SYNC_TYPE)).intValue();
						switch(type) {
						case JSONMessage.RESPONSE_FOR_PENDING_RESULT_OK:
							processResponseForPending(con, props, row);
						default:
						}
					}
					return _new;
				}
			}
			/*
			 * TODO spracuj chybu
			 */
		}
		return null;
	}

	private static JSONMessage processInsertAction(Connection con, Properties props, JSONMessage msg) throws SQLException {
		Object com1 = null;
		TableName tableName = null;
		Object[] body = msg.getBody();
		try {
			String schema = DBSchemas.getSyncSchemaName();
			SchemaName schemaName = new SchemaName(new Identifier(schema));
			tableName = new TableName(schemaName, new Identifier(DBSchemas.SYNC_INCOMMING_REQUESTS));
			String[] cols = {DBSchemas.SYNC_ID, DBSchemas.SYNC_TYPE, DBSchemas.SYNC_INSERT, DBSchemas.SYNC_SCHEMA, DBSchemas.SYNC_TABLE, DBSchemas.SYNC_STATUS, DBSchemas.SYNC_CHANGES};
			//ColumnSpec[] colspecs = ColumnSpec.getColSpecArray(tableName, cols);
			Object[] vals = {body[2], body[1], new Date().getTime(), schema, body[3], SyncRequest.STATUS_NEW, body[4]};
			DBConnector.insert(con, DBConnector.createInsert(tableName, vals, cols).setIgnoreTrigger(true));
			con.setAutoCommit(false);
			com1 = new CompPred(new Object[]{new Identifier(DBSchemas.SYNC_ID), new Identifier(DBSchemas.SYNC_TABLE)}, new Object[]{body[2], body[3]}, Predicate.EQUAL);
			int id = (int) DBConnector.select(con, DBConnector.createSelect().addFromClause(tableName).addColumns(DBSchemas.SYNC_INCOMMING_REQUESTS_ID).addTableSpec(new WhereClause(com1))).get(0).get(DBSchemas.SYNC_INCOMMING_REQUESTS_ID);
			byte[] b = Huffman.decode(Base64Coder.decode(((String) body[4]).toCharArray()), null);
			ByteArrayInputStream bin = new ByteArrayInputStream(b);
			ObjectInputStream in = new ObjectInputStream(bin);
			Insert insert = (Insert) in.readObject();
			DBConnector.insert(con, insert);
			com1 = new CompPred(new Object[]{new Identifier(DBSchemas.SYNC_INCOMMING_REQUESTS_ID)}, new Object[]{id}, Predicate.EQUAL);
			DBConnector.update(con, DBConnector.createUpdate(tableName, new String[]{DBSchemas.SYNC_STATUS}, new Object[]{SyncRequest.STATUS_PENDING_OK}, new WhereClause(com1)), false);
			return SyncRequest.getInstance(props).appendBody(DBSchemas.SYNC_TYPE, JSONMessage.RESPONSE_FOR_PENDING_RESULT_OK, DBSchemas.SYNC_ID, body[2], DBSchemas.SYNC_TABLE, body[3]);
		}
		catch(Exception e) {
			con.rollback();
			DBConnector.update(con, DBConnector.createUpdate(tableName, new String[]{DBSchemas.SYNC_STATUS}, new Object[]{SyncRequest.STATUS_PENDING_FAILED}, new WhereClause(com1)), false);
			throw new SQLException(e);
			//return SyncRequest.getInstance(props).appendBody(DBSchemas.SYNC_TYPE, JSONMessage.RESPONSE_FOR_PENDING_RESULT_FAILED, DBSchemas.SYNC_ID, body[2], DBSchemas.SYNC_TABLE, body[3]);
		}
		finally {
			con.commit();
			con.setAutoCommit(true);
		}
	}

	private static void processResponseForPending(Connection con, Properties props, Row row) throws SQLException {
		update(con, DBSchema.getSyncSchemaName(), (String) row.get(DBSchemas.SYNC_TABLE), DBSchemas.SYNC_STATUS, SyncRequest.STATUS_ARCHIVED_OK, DBSchemas.SYNC_ID, row.get(DBSchemas.SYNC_ID));		
	}

	private static JSONMessage processInsertNoAction(Connection con, Properties props, JSONMessage msg) throws SQLException {
		Object[] body = msg.getBody();
		try {
			con.setAutoCommit(false);
			update(con, DBSchema.getSyncSchemaName(), (String) body[3], DBSchemas.SYNC_STATUS, SyncRequest.STATUS_PENDING_OK, DBSchemas.SYNC_ID, body[2]);
			return SyncRequest.getInstance(props).appendBody(DBSchemas.SYNC_TYPE, JSONMessage.RESPONSE_FOR_PENDING_RESULT_OK, DBSchemas.SYNC_ID, body[2], DBSchemas.SYNC_TABLE, body[3]);
		} catch (SQLException e) {
			con.rollback();
			throw e;
		}
		finally {
			con.commit();
			con.setAutoCommit(true);
		}
	}
	
	private static void update(Connection con, String schema, String table, String targerCol, Object targetVal, String col, Object val) throws SQLException {
		TableName tableName = new TableName(new SchemaName(new Identifier(schema)), new Identifier(table));
		Object com1 = new CompPred(new Object[]{new ColumnSpec(tableName, new Identifier(col))}, new Object[]{val}, Predicate.EQUAL);
		DBConnector.update(con, DBConnector.createUpdate(tableName, new String[]{targerCol}, new Object[]{targetVal}, new WhereClause(com1)), false);
	}
	/*
	private static void delete(Connection con, String schema, String table, String col, Object val) throws SQLException {
		TableName tableName = new TableName(new SchemaName(new Identifier(schema)), new Identifier(table));
		Object com1 = new CompPred(new Object[]{new ColumnSpec(tableName, new Identifier(col))}, new Object[]{val}, Predicate.EQUAL);
		DBConnector.delete(con, DBConnector.createDelete(tableName, null, new WhereClause(com1)), false);
	}
	*/
	private static JSONMessage processIncommingPendingRequest(Connection con, Properties props, JSONMessage msg) throws SQLException {
		Object[] body = msg.getBody();
		return SyncRequest.getInstance(props).appendBody(SyncRequest.checkForPending(con, SyncRequest.ALL, ((Double)body[2]).intValue(), (String) body[3]));
	}
	
	private static Row getRow(Object body[]) throws IOException {
		Row syncRow = new Row();
		for(int i = 4; i < body.length; i += 2) {
			Object obj = body[i];
			if(!(obj instanceof String)) {
				throw new IOException("String is expected, " + obj + ", " + obj.getClass().getSimpleName() + " was found");
			}
			String key = (String) obj;
			if(i + 1 == body.length) {
				throw new IOException("Unexpected end of JSON syncRequest message was found");
			}
			syncRow.put(key, body[i + 1]);
		}
		return syncRow;
	}
	
}
