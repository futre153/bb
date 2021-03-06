package com.acepricot.finance.sync;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import javax.xml.bind.DatatypeConverter;

import org.pabk.util.Base64Coder;
import org.pabk.util.Huffman;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.acepricot.finance.sync.share.AppConst;
import com.acepricot.finance.sync.share.JSONMessage;
import com.acepricot.finance.sync.share.sql.ColumnSpec;
import com.acepricot.finance.sync.share.sql.CompPred;
import com.acepricot.finance.sync.share.sql.Delete;
import com.acepricot.finance.sync.share.sql.DistinctSpec;
import com.acepricot.finance.sync.share.sql.FromClause;
import com.acepricot.finance.sync.share.sql.Identifier;
import com.acepricot.finance.sync.share.sql.Insert;
import com.acepricot.finance.sync.share.sql.Predicate;
import com.acepricot.finance.sync.share.sql.Query;
import com.acepricot.finance.sync.share.sql.SQLSyntaxImpl;
import com.acepricot.finance.sync.share.sql.SchemaName;
import com.acepricot.finance.sync.share.sql.TableName;
import com.acepricot.finance.sync.share.sql.Update;
import com.acepricot.finance.sync.share.sql.WhereClause;

final class JSONMessageProcessor {
	
	final static Logger logger = LoggerFactory.getLogger(JSONMessageProcessor.class);
	
	static final String UNIVERSAL_ID = "ID";
	private static final String UNIVERSAL_ENABLED = "ENABLED";
	private static final int ID_INDEX = 0;
	private static final int OBJECT_INDEX = 1;
	private static final String DEFAULT_DIGEST_ALGORITHM = "SHA-256";
	private static final String TEMP_DIR = "D:\\TEMP\\acetmpdir";
	private static final String TEMP_DIR_MASK = "D:\\TEMP\\acetmpdir%s%s%s";
	
	private static final int MAX_READED_BYTES = 0x1000;

	private static final String DB_PATH = "D:\\TEMP\\clientdb%s%s%s";
	private static final String DB_EXTENSION = ".h2.db";

	private static final String ACTION_METHOD_NAME = "action";

	private static final String H2_DSN_PREFIX = "H2-";
	private static final String H2_JDBC_DRIVER = "org.h2.Driver";
	//private static final String H2_JDBC_DRIVER = "org.h2.jdbcx.JdbcConnectionPool";
	private static final String H2_USER = "";
	private static final String H2_PASSWORD = "RTNvMFNSVHJmVUpqN1c4UytJazViOTUxREczMEFBPT0=";
	private static final String H2_URL_MASK = "jdbc:h2:tcp://localhost/%s;AUTO_SERVER=TRUE;LOCK_TIMEOUT=60000;CIPHER=AES";

	private static final String ENGINES_PROPERTIES_STORE = "D:\\TEMP\\engines%s%s%s";

	private static final String XML_PROPERTIES_EXTENSIONS = ".properties.xml";

	private static final String DEFAULT_CHARSET = "UTF-8";

	private static final CharSequence DB_PASSWORD = "MlQzeEEyN3ZyY3dIMEE9PQ==";

	private static final String TMP_FILE_EXTENSION = ".tmp";

	private static final int COMMON_NUMBER_OF_PARTS = 100;

	private static final int MAX_BYTES = 0x100000;

	
	private static volatile int syncRequests = 0x00;
	private static final int MAX_SYNC_REQUESTS = 0x40;

	private static final Object ENABLED = BigDecimal.ONE;

	@SuppressWarnings("unused")
	private static final String MAX_FUNCTION = "MAX";

	//static final String LOCAL_LABEL = "***";
	
	public final class UPLOADED_FILES extends TableSchema {
		private static final String GROUP_ID = "GROUP_ID";
		private static final String STATUS = "STATUS";
		private static final String DIGEST = "DIGEST";
		private static final String DIGEST_ALGORITHM = "DIGEST_ALGORITHM";
		private static final String URI = "URI";
		private static final String TMP_FILE = "TMP_FILE";
		private static final String MD_INDEX = "MD_INDEX";
		private static final int STATUS_UPLOADED_NOT_STARTED = 0;
		private static final int STATUS_UPLOAD_IN_PROGRESS = 1;
		private static final int STATUS_FILE_UPLOADED = 2;
		private static final int STATUS_DB_FILE_CREATED = 3;
		private static final int STATUS_SYNC_ENABLED = 5;
		private static final int STATUS_DB_POOL_CREATED = 4;
		int id;
		int group_id;
		BigDecimal status;
		String digest;
		String digest_algorithm;
		int uri;
		BigDecimal enabled;
		String tmp_file;
		int md_index;
	}
	
	public final class DOWNLOADED_FILES extends TableSchema {
		private static final String URI = "URI";
		private static final String TMP_FILE = "TMP_FILE";
		public static final String POINTER = "POINTER";
		int id;
		int uri;
		String tmp_file;
		BigDecimal pointer;
		BigDecimal enabled;
		Timestamp insert_date;
	}
	
	public final class REGISTERED_GROUPS extends TableSchema {
		//private static final String TABLE_NAME = "REGISTERED_GROUPS";
		//private static final String ID = UNIVERSAL_ID;
		public static final String GROUP_NAME = "GROUP_NAME";
		private static final String PASSWORD = "PASSWORD";
		static final String EMAIL = "EMAIL";
		private static final String DB_VERSION = "DB_VERSION";
		//private static final String ENABLED = UNIVERSAL_ENABLED;
		//public static final String[] COLS = {ID,GROUP_NANE,PASSWORD,EMAIL,DB_VERSION,ENABLED};
		int id;
		String group_name;
		String password;
		String email;
		int db_version;
		BigDecimal enabled;
	}
	
	public final class REGISTERED_DEVICES extends TableSchema {
		//private static final String TABLE_NAME = "REGISTERED_GROUPS";
		private static final String ID = UNIVERSAL_ID;
		public static final String GROUP_ID = "GROUP_ID";
		static final String DEVICE_NAME = "DEVICE_NAME";
		private static final String PRIMARY_DEVICE = "PRIMARY_DEVICE";
		private static final String ENABLED = UNIVERSAL_ENABLED;
		//public static final String[] COLS = {ID,GROUP_ID,DEVICE_NAME,PRIMARY_DEVICE,ENABLED};
		private static final String SYNC_ENABLED = "SYNC_ENABLED";
		int id;
		int group_id;
		BigDecimal primary_device;
		BigDecimal enabled;
		BigDecimal sync_enabled;
	}
	
	public final class SYNC_RESPONSES extends TableSchema {
		static final String GROUP_ID = "GROUP_ID";
		static final String DEVICE_ID = "DEVICE_ID";
		static final String STATUS = "STATUS";
		static final String QUERY = "QUERY";
		static final String TYPE = "TYPE";
		static final String TABLE_NAME = "TABLE_NAME";
		static final String SYNC_ID = "SYNC_ID";
		static final int PENDING = 1;
		public static final int WAITING = 0;
		int id;
		int group_id;
		int device_id;
		BigDecimal status;
		String query;
		int type;
		String table_name;
		int sync_id;
		
	}
	
	//private static Connection con;
	private static String dsn;
	private static Properties pro;
	
	private UPLOADED_FILES uploaded_files = new UPLOADED_FILES();
	private DOWNLOADED_FILES downloaded_files = new DOWNLOADED_FILES();
	REGISTERED_GROUPS registered_groups = new REGISTERED_GROUPS();
	SYNC_RESPONSES sync_responses = new SYNC_RESPONSES();
				
	private JSONMessageProcessor() throws IOException{
		if(dsn == null) {
			try {
				pro = new Properties();
				InputStream in = JSONMessageProcessor.class.getResourceAsStream("/com/acepricot/finance/sync/db_aceserver.xml");
				pro.loadFromXML(in);
				//System.out.println(pro);
				in.close();
				dsn = pro.getProperty(DBConnector.DB_DSN_KEY);
				setPassword(pro, DB_PASSWORD);
				DBConnector.bind(pro, dsn);
				pro.remove(DBConnector.DB_PASSWORD_KEY);
			}
			catch(Exception e) {
				e.printStackTrace();
				throw new IOException(e);
			}
			try {
				Connection con = DBConnector.lookup(dsn);
				con.close();
				con = null;
			}
			catch (Exception e) {
				e.printStackTrace();
				throw new IOException(e);
			}
		}
	}
	
	static JSONMessageProcessor getInstance() throws IOException {
		return new JSONMessageProcessor();
	}
	
	JSONMessage process(JSONMessage msg) throws IOException {
		logger.info("Incoming JSON message type " + msg.getHeader());
		Connection con = null;
		try {
			con = DBConnector.lookup(dsn);
			con.setAutoCommit(false);
			Method method = JSONMessageProcessor.class.getDeclaredMethod(msg.getHeader(), this.getClass(), Connection.class, JSONMessage.class);
			msg = (JSONMessage) method.invoke(null, this, con, msg);
			logger.info("Outgoing JSON message type " + msg.getHeader());
			return msg;
		}
		catch(Exception e) {
			logger.error("Error while processing incoming JSON message " + msg.getHeader(), e);
			throw new IOException (e);
		}
		finally {
			try {
				con.setAutoCommit(true);
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("unused")
	private static final JSONMessage syncRequest (JSONMessageProcessor mp, Connection con, JSONMessage msg) {
		String grpName, devName;
		int grpId, devId;
		grpName = (String) msg.getBody()[0];
		devName = (String) msg.getBody()[2];
		Row syncRow = new Row();
		if(msg.getBody().length == 5) {
			try {
				syncRow = (Row) load((String) msg.getBody()[4]);
			} catch (Exception e) {
				return msg.sendAppError(e);
			}
		}
		else {
			for(int i = 4; i < msg.getBody().length; i += 2) {
				Object obj = msg.getBody()[i];
				if(!(obj instanceof String)) {
					return msg.sendAppError("String is expected, " + obj + ", " + obj.getClass().getSimpleName() + " was found");
				}
				String key = (String) obj;
				if(i + 1 == msg.getBody().length) {
					return msg.sendAppError("Unexpected end of JSON syncRequest message was found");
				}
				syncRow.put(key, msg.getBody()[i + 1]);
			}
		}	
		try {
			JSONMessageProcessor.setSyncRequests(1);
		} catch (IOException e) {
			return msg.sendAppError(e);
		}
		if(!login (mp, con, msg).isError()) {
			try {
				Operation op = new Operation(mp, syncRow, grpName, devName);
				msg = SyncEngine.processSyncRequest(op);
			}
			catch(IOException | SQLException e) {
				return msg.sendAppError(e);
			}
		}
		try {
			JSONMessageProcessor.setSyncRequests(-1);
		} catch (IOException e) {
			return msg.sendAppError(e);
		}
		return msg;
	}
	
	@SuppressWarnings("unused")
	private static final JSONMessage initSync(JSONMessageProcessor mp, Connection con, JSONMessage msg) {
		int grpId, devId;
		String grpName, devName, table;
		Rows rows;
		grpName = (String) msg.getBody()[0];
		devName = (String) msg.getBody()[2];
		if(!login(mp, con, msg).isError()) {
			try {
				grpId = JSONMessageProcessor.getGroupId(con, grpName);
				devId = JSONMessageProcessor.getDeviceId(con, grpId, devName);
				if(SyncEngine.isStarted(grpId, devId)) {
					return msg.sendAppError("Device " + devName + " is allready active in context of group " + grpName);
				}
				table = JSONMessageProcessor.UPLOADED_FILES.class.getSimpleName();
				//where = w.set(w.equ(JSONMessageProcessor.UPLOADED_FILES.GROUP_ID, grpId));
				Object com1 = new CompPred(new Object[]{new Identifier(JSONMessageProcessor.UPLOADED_FILES.GROUP_ID)}, new Object[]{grpId}, Predicate.EQUAL);
				msg = retrieveRows2(mp, msg, con, mp.uploaded_files, new WhereClause(com1), true);
				if(msg.isError()) {
					return msg;
				}
				if(mp.uploaded_files.hasNext()) {
					mp.uploaded_files.next();
					boolean download = true;
					switch(mp.uploaded_files.status.intValue()) {
					case JSONMessageProcessor.UPLOADED_FILES.STATUS_UPLOADED_NOT_STARTED:
						return msg.sendAppError(AppError.getMessage(AppError.DATABASE_FILE_UPLOAD_NOT_STARTED, grpId));
					case JSONMessageProcessor.UPLOADED_FILES.STATUS_UPLOAD_IN_PROGRESS:
						return msg.sendAppError(AppError.getMessage(AppError.DATABASE_FILE_UPLOAD_IN_PROGRESS, grpId));
					case JSONMessageProcessor.UPLOADED_FILES.STATUS_FILE_UPLOADED:
						msg = createAction(mp, con, msg); //create db file
						if(msg.isError()) {
							return msg;
						}
					case JSONMessageProcessor.UPLOADED_FILES.STATUS_DB_FILE_CREATED:
						msg = createAction(mp, con, msg); //create connection pool
						if(msg.isError()) {
							return msg;
						}
					case JSONMessageProcessor.UPLOADED_FILES.STATUS_DB_POOL_CREATED:
						msg = createAction(mp, con, msg); //add node
						if(msg.isError()) {
							return msg;
						}
						TableName tableName = new TableName(new Identifier(JSONMessageProcessor.REGISTERED_DEVICES.class.getSimpleName()));
						String[] cols = {JSONMessageProcessor.REGISTERED_DEVICES.SYNC_ENABLED};
						Object[] values = {BigDecimal.ONE};
						com1 = new CompPred(new Object[]{new Identifier(JSONMessageProcessor.UNIVERSAL_ID)}, new Object[]{devId}, Predicate.EQUAL);
						try {
							DBConnector.update(con, DBConnector.createUpdate(tableName, cols, values, new WhereClause(com1)), false);
						} catch (SQLException e) {
							con.rollback();
							throw (e);
						} finally {
							con.commit();
						}
						download = false;
					case JSONMessageProcessor.UPLOADED_FILES.STATUS_SYNC_ENABLED:
						tableName = new TableName(new Identifier(JSONMessageProcessor.REGISTERED_DEVICES.class.getSimpleName()));
						cols = new String[] {JSONMessageProcessor.REGISTERED_DEVICES.SYNC_ENABLED};
						values = new Object[]{BigDecimal.ONE};
						com1 = new CompPred(new Object[]{new Identifier(JSONMessageProcessor.UNIVERSAL_ID)}, new Object[]{devId}, Predicate.EQUAL);
						try {
							DBConnector.update(con, DBConnector.createUpdate(tableName, cols, values, new WhereClause(com1)), false);
						} catch (SQLException e) {
							con.rollback();
							throw (e);
						} finally {
							con.commit();
						}
						Object lock = new Object();
						long l = SyncEngine.nodePause(grpId, lock);
						if(download) {
							msg = prepareDownload(con, mp.uploaded_files.tmp_file, msg);
						}
						else {
							msg = msg.returnOK();
						}
						SyncEngine.startEngine(grpId);
						SyncEngine.nodeContinue(grpId, lock);
						if(msg.isError()) {
							return msg;
						}
						msg.appendBody(l);
						return msg;
					default:
						throw new IOException("Action " + mp.uploaded_files.status.intValue() + " is not defined");
					}
				}
				else {
					return msg.sendAppError(AppError.getMessage(AppError.FILE_INSERT_NOT_FOUND, grpId));
				}
			}
			catch(SQLException | IOException | NoSuchAlgorithmException e) {
				return msg.sendAppError(e);
			}
		}
		return msg;
	}
	
	@SuppressWarnings("unused")
	private static final JSONMessage download(JSONMessageProcessor mp, Connection con, JSONMessage msg) {
		Where w = new Where();
		//String table = JSONMessageProcessor.DOWNLOADED_FILES.class.getSimpleName();
		TableName tableName = null;
		String col = JSONMessageProcessor.UNIVERSAL_ID;
		String col2 = JSONMessageProcessor.DOWNLOADED_FILES.URI;
		int fileId = Integer.parseInt((String) msg.getBody()[0], 2);
		int uri = Integer.parseInt((String) msg.getBody()[1], 2);
		//Object[] where = w.set(w.and(w.equ(col, fileId), w.equ(col2, uri)));
		boolean last = false;
		try {
			tableName = new TableName(new Identifier(JSONMessageProcessor.DOWNLOADED_FILES.class.getSimpleName()));
			Object com1 = new CompPred(new Object[]{new Identifier(col), new Identifier(col2)}, new Object[]{fileId, uri}, Predicate.EQUAL);
			msg = retrieveRows2(mp, msg, con, mp.downloaded_files, new WhereClause(com1), true);
			if(msg.isError()) {
				throw new SQLException((String) msg.getBody()[0]);
			}
			if(!mp.downloaded_files.hasNext()) {
				throw new SQLException(AppError.getMessage(AppError.DOWNLOAD_NOT_FOUND, "[UNKNOWN_FILE]"));
			}
			mp.downloaded_files.next();
			File f = new File(mp.downloaded_files.tmp_file);
			if(!f.exists()) {
				throw new IOException(AppError.getMessage(AppError.DOWNLOAD_NOT_FOUND, mp.downloaded_files.tmp_file));
			}
			long l = f.length();
			int i;
			long pointer = mp.downloaded_files.pointer.longValue();
			int max = l > (COMMON_NUMBER_OF_PARTS * MAX_BYTES) ? (int) (l / MAX_BYTES) + 1 : COMMON_NUMBER_OF_PARTS;
				int size = (int) (l/max);
				int last_size = (int) (l - size * (max - 1));
				int bufferSize = size > last_size ? size : last_size;
				last = (l - pointer) <= bufferSize;
				byte[] b = new byte[bufferSize];
				FileInputStream in = new FileInputStream(f);
				try {
					long skipped = in.skip(pointer);
					if(skipped != pointer) {
						throw new IOException("Operation skip failed on downloaded file " + mp.downloaded_files.tmp_file);
					}
					i = in.read(b);
					pointer += i;
				}
				catch(IOException e) {
					throw (e);
				}
				finally {
					in.close();
				}
				int _uri = (int) (Math.random() * Integer.MAX_VALUE);
				String[] cols = {col2, JSONMessageProcessor.DOWNLOADED_FILES.POINTER};
				Object[] values = {Integer.toString(_uri), Long.toString(pointer)};
				DBConnector.update(con, DBConnector.createUpdate(tableName, cols, values, new WhereClause(com1)));
				msg = msg.returnOK(b, i, Integer.toBinaryString(fileId), (last ? AppConst.LAST_URI_SIGN : "") + Integer.toBinaryString(_uri));
				msg.setHeader(null);
				if(last) {
					throw new IOException("End of download");
				}
				return msg;
		}
		catch(IOException | SQLException e) {
			Exception e1 = e;
			tableName = new TableName(new Identifier(JSONMessageProcessor.DOWNLOADED_FILES.class.getSimpleName()));
			disable(con, tableName, fileId);
			delete(con, tableName, fileId);
			if(last) {
				return msg;
			}
			return msg.sendAppError(e1);
		}
	}
	
	private static boolean delete (Connection con, TableName tableName, int id) {
		try {
			Object com1 = new CompPred(new Object[]{new Identifier(UNIVERSAL_ID)}, new Object[]{id}, Predicate.EQUAL);
			return DBConnector.delete(con, DBConnector.createDelete(tableName, null, new WhereClause(com1))) == 1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private static boolean setEnabled(Connection con, TableName tableName, int id, boolean enabled) {
		String[] cols = {UNIVERSAL_ENABLED};
		Object[] values = {enabled ? "1" : "0"};
		try {
			Object com1 = new CompPred(new Object[]{new Identifier(UNIVERSAL_ID)}, new Object[]{id}, Predicate.EQUAL);
			return DBConnector.update(con, DBConnector.createUpdate(tableName, cols, values, new WhereClause(com1 ))) == 1;
		} catch (SQLException e) {}
		return false;
	}
	
	private static boolean disable(Connection con, TableName table, int id) {
		return setEnabled(con, table, id, false);
	}

	private static JSONMessage prepareDownload(Connection con, String src_file, JSONMessage msg) throws IOException, SQLException, NoSuchAlgorithmException {
		File srcFile = new File(src_file);
		String dstPath = String.format(TEMP_DIR_MASK, System.getProperty("file.separator"), srcFile.getName(), TMP_FILE_EXTENSION);
		File dstFile = new File(dstPath);
		copy(srcFile, dstFile);
		int uri = (int) (Math.random() * Integer.MAX_VALUE);
		TableName tableName = new TableName(new Identifier(JSONMessageProcessor.DOWNLOADED_FILES.class.getSimpleName()));
		String col = JSONMessageProcessor.DOWNLOADED_FILES.TMP_FILE;
		String col2 = JSONMessageProcessor.DOWNLOADED_FILES.URI;
		Object[] values = {Integer.toString(uri), dstPath};
		DBConnector.insert(con, DBConnector.createInsert(tableName, values, col2, col));
		int[] indexes = {AppError.DOWNLOAD_NOT_FOUND, AppError.DOWNLOAD_MULTIPLE, AppError.DOWNLOAD_NOT_ENABLED};
		CompPred com1 = new CompPred(new Object[]{new Identifier(col), new Identifier(col2)}, new Object[]{dstPath, uri}, Predicate.EQUAL);		
		int id = (Integer) JSONMessageProcessor.getObject(con, tableName, indexes, col, dstPath, new WhereClause(com1), true)[ID_INDEX];
		FileInputStream in = new FileInputStream(dstFile);
		MessageDigestSerializer mds = MessageDigestSerializer.getMDS(MessageDigestSerializer.getInstance(DEFAULT_DIGEST_ALGORITHM));
		byte[] b = new byte[MAX_BYTES];
		int i;
		while((i = in.read(b)) >= 0) {
			mds.update(b, 0, i);
		}
		in.close();
		b = mds.digest();
		mds.reset();
		long l = dstFile.length();
		return msg.returnOK(uri, id, JSONMessageProcessor.DEFAULT_DIGEST_ALGORITHM, b, l > (COMMON_NUMBER_OF_PARTS * MAX_BYTES) ? (int) (l / MAX_BYTES) + 1 : COMMON_NUMBER_OF_PARTS);
	}
	
	private static void copy(File srcFile, File dstFile) throws IOException {
		if(!srcFile.exists()) {
			throw new IOException("Source file " + srcFile.getAbsolutePath() + " does not exixts");
		}
		if(dstFile.exists()) {
			if(!dstFile.delete()) {
				throw new IOException("Failed to remove old destination file " + dstFile.getAbsolutePath());
			}
		}
		if(!dstFile.createNewFile()) {
			throw new IOException("Failed to create new destination file " + dstFile.getAbsolutePath());
		}
		FileInputStream in = new FileInputStream(srcFile);
		FileOutputStream out = new FileOutputStream(dstFile);
		byte[] b = new byte[MAX_READED_BYTES];
		int i = -1;
		while((i = in.read(b)) >= 0) {
			out.write(b, 0, i);
		}
		in.close();
		out.close();
	}
	
	
	private static JSONMessage createAction(JSONMessageProcessor mp, Connection con, JSONMessage msg) throws SQLException, IOException {		
		TableName tableName = new TableName(new Identifier(JSONMessageProcessor.UPLOADED_FILES.class.getSimpleName()));
		String[] cols = {JSONMessageProcessor.UPLOADED_FILES.STATUS};
		Object[] values = {Integer.toString(mp.uploaded_files.status.intValue() + 1)};
		Object com1 = new CompPred(new Object[]{new Identifier(JSONMessageProcessor.UPLOADED_FILES.GROUP_ID)}, new Object[]{mp.uploaded_files.group_id}, Predicate.EQUAL);
		try {
			DBConnector.update(con, DBConnector.createUpdate(tableName, cols, values, new WhereClause(com1)), false);
			String methodName = ACTION_METHOD_NAME + mp.uploaded_files.status.intValue();
			Method method = JSONMessageProcessor.class.getDeclaredMethod(methodName, mp.getClass(), Connection.class, msg.getClass());
			msg = (JSONMessage) method.invoke(null, mp, con, msg);
		} catch (SQLException e) {
			con.rollback();
			throw (e);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			con.rollback();
			throw new IOException (e);
		} finally {
			mp.uploaded_files.status = mp.uploaded_files.status.add(BigDecimal.ONE);
			con.commit();
		}
		return msg;
	}
	
	@SuppressWarnings("unused")
	private static JSONMessage action4(JSONMessageProcessor mp, Connection con, JSONMessage msg) throws IOException, SQLException {
		TableName tableName = new TableName(new Identifier(JSONMessageProcessor.REGISTERED_GROUPS.class.getSimpleName()));
		WhereClause where = new WhereClause(new CompPred(new Object[]{new Identifier(JSONMessageProcessor.UNIVERSAL_ID)}, new Object[]{mp.uploaded_files.group_id}, Predicate.EQUAL));
		JSONMessageProcessor.retrieveRows2(mp, msg, con, mp.registered_groups, where, true);
		mp.registered_groups.next();
		SyncEngine.addEngine(mp);
		SyncEngine.nodeStart(mp.uploaded_files.group_id);
		return msg.returnOK(0x00L);
	}
	
	@SuppressWarnings("unused")
	private static JSONMessage action3(JSONMessageProcessor mp, Connection con, JSONMessage msg) throws IOException, SQLException {
		Properties pro = new Properties(JSONMessageProcessor.pro);
		pro.setProperty(DBConnector.DB_DSN_KEY, H2_DSN_PREFIX + Integer.toString(mp.uploaded_files.id));
		pro.setProperty(DBConnector.DB_DRIVER_CLASS_KEY, H2_JDBC_DRIVER);
		pro.setProperty(DBConnector.DB_URL_KEY, String.format(H2_URL_MASK, mp.uploaded_files.tmp_file.replaceAll("\\\\", "/").replaceAll("\\.h2\\.db", "")));
		pro.setProperty(DBConnector.DB_USERNAME_KEY, H2_USER);
		storeEngineProps(mp.uploaded_files.group_id, pro);
		setPassword(pro, H2_PASSWORD);
		String dsn = pro.getProperty(DBConnector.DB_DSN_KEY);
		DBConnector.bind(pro, dsn);
		Connection con2 = DBConnector.lookup(dsn);
		con2.close();
		con2 = null;
		DBConnector.unbind(dsn);
		return msg.returnOK();
	}
	
	static void storeEngineProps(int grpId, Properties pro) throws IOException {
		FileOutputStream out = new FileOutputStream(getEnginesPropertiesFilename(grpId));
		pro.storeToXML(out, "propeties for engine id " + grpId, DEFAULT_CHARSET);
	}
	
	static void setPassword(Properties pro, CharSequence pass) throws IOException {
		try {
			pro.setProperty(DBConnector.DB_PASSWORD_KEY, Huffman.decode(Base64Coder.decodeString(pass.toString()), null));
		} catch (Exception e) {
			throw new IOException(e);
		}
	}
	
	static final String getEnginesPropertiesFilename(int grpId) {
		return String.format(ENGINES_PROPERTIES_STORE, System.getProperty("file.separator"), Integer.toBinaryString(grpId), XML_PROPERTIES_EXTENSIONS);
	}
	
	@SuppressWarnings("unused")
	private static JSONMessage action2 (JSONMessageProcessor mp, Connection con, JSONMessage msg) throws IOException, SQLException {
		String dbFilename = String.format(DB_PATH, System.getProperty("file.separator"), Integer.toBinaryString(mp.uploaded_files.group_id), DB_EXTENSION);
		TableName tableName = new TableName(new Identifier(JSONMessageProcessor.UPLOADED_FILES.class.getSimpleName()));
		String[] cols = {JSONMessageProcessor.UPLOADED_FILES.TMP_FILE};
		Object[] values = {dbFilename};
		Where w = new Where();
		Object[] where = w.set(w.equ(JSONMessageProcessor.UPLOADED_FILES.GROUP_ID, mp.uploaded_files.group_id));
		Object com1 = new CompPred(new Object[]{new Identifier(JSONMessageProcessor.UPLOADED_FILES.GROUP_ID)}, new Object[]{mp.uploaded_files.group_id}, Predicate.EQUAL);
		//DBConnector.update(con, table, cols, values, where, false);
		DBConnector.update(con, DBConnector.createUpdate(tableName, cols, values, new WhereClause(com1)), false);
		File dbFile = new File(dbFilename);
		if(dbFile.exists()) {
			if(!dbFile.delete()) {
				throw new IOException("Failed to remove previous DB file");
			}
		}
		if(!(new File(mp.uploaded_files.tmp_file).renameTo(dbFile))) {
			throw new IOException("Failed to move DB file");
		}
		mp.uploaded_files.tmp_file = dbFilename;
		return msg.returnOK();
	}
	
	/*			mp, msg, con, mp.downloaded_files, (char) 0, where, true
	 * 0 - JSONMessageProcessor
	 * 1 - JSOnMessage
	 * 2 - SQLConnection
	 * 3 - FromClause
	 * 4 - WhereClause
	 * 5 - unique
	 */
	
	
	static final int retrieveRows (Object ...o) throws SQLException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		Rows rows = DBConnector.select((Connection) o[1], DBConnector.createSelect().addFromClause(new TableName(new Identifier(o[2].getClass().getSimpleName()))).addTableSpec((WhereClause) o[3]));
		TableSchema schema = (TableSchema) o[0].getClass().getDeclaredField(o[2].getClass().getSimpleName().toLowerCase()).get(o[0]);
		schema.rows = rows;
		schema.index = -1;
		return rows.size();
	}
	
	static final JSONMessage retrieveRows2(Object ... o) {
		try {
			
			/*
			Rows rows = DBConnector.select((Connection) o[2], DBConnector.createSelect().addFromClause(new TableName(new Identifier(o[3].getClass().getSimpleName()))).addTableSpec((WhereClause) o[4]));
			TableSchema schema = (TableSchema) o[0].getClass().getDeclaredField(o[3].getClass().getSimpleName().toLowerCase()).get(o[0]);
			schema.rows = rows;
			schema.index = -1;
			int l  = rows.size();
			
			*/
			int l = retrieveRows(o[0], o[2], o[3], o[4]); 
			if(l == 0) {
				throw new SQLException(AppError.getMessage(AppError.RECORD_NOT_FOUND));
			}
			if((boolean) o[5]) {
				if(l != 1) {
					throw new SQLException(AppError.getMessage(AppError.MULTIPLE_RECORD_FOUND));
				}
			}
			return ((JSONMessage) o[1]).returnOK();
		} catch (SQLException | IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			return ((JSONMessage) o[1]).sendAppError(e);
		}
	}
	
	@SuppressWarnings("unused")
	private static final JSONMessage upload(JSONMessageProcessor mp, Connection con, JSONMessage msg) {
		String reqId, reqUri, digest, digestAlgorithm, tmpPath;
		int id, status, uri, grpId, enabled, mdIndex = -1;
		Rows rows;
		File tmpFile;
		MessageDigestSerializer mds;
		String[] cols;
		Object[] values;
		boolean last;
		InputStream in;
		reqId = (String) msg.getBody()[0];
		reqUri = (String) msg.getBody()[1];
		TableName tableName;
		Object com1;
		try {
			in = (InputStream) msg.getBody()[2];
			last = (boolean) msg.getBody()[3];
			
			tableName = new TableName(new Identifier(JSONMessageProcessor.UPLOADED_FILES.class.getSimpleName()));
			com1 = new CompPred(new Object[]{new Identifier(UNIVERSAL_ID), new Identifier(JSONMessageProcessor.UPLOADED_FILES.URI)},
					new Object[]{Integer.parseInt(reqId, 2), Integer.parseInt(reqUri, 2)},
					Predicate.EQUAL);
			Query select = DBConnector.createSelect();
			select.addFromClause(tableName);
			com1 = new CompPred(
					new Object[]{new Identifier(UNIVERSAL_ID), new Identifier(JSONMessageProcessor.UPLOADED_FILES.URI)},
					new Object[]{Integer.parseInt(reqId, 2), Integer.parseInt(reqUri, 2)},
					Predicate.EQUAL);
			select.addTableSpec(new WhereClause(com1 ));
			rows = DBConnector.select(con, select);
			if(rows.size() == 0) {
				throw new SQLException(AppError.getMessage(AppError.FILE_INSERT_NOT_FOUND));
			}
			if(rows.size() > 1) {
				throw new SQLException(AppError.getMessage(AppError.MULTIPLE_FILE_INSERT));
			}
			id = DBConnector.intValue(rows.get(0).get(UNIVERSAL_ID));
			grpId = DBConnector.intValue(rows.get(0).get(JSONMessageProcessor.UPLOADED_FILES.GROUP_ID));
			status = DBConnector.bigDecimalValue(rows.get(0).get(JSONMessageProcessor.UPLOADED_FILES.STATUS)).intValue();
			digest = DBConnector.toString(rows.get(0).get(JSONMessageProcessor.UPLOADED_FILES.DIGEST));
			digestAlgorithm = DBConnector.toString (rows.get(0).get(JSONMessageProcessor.UPLOADED_FILES.DIGEST_ALGORITHM));
			enabled = DBConnector.bigDecimalValue(rows.get(0).get(UNIVERSAL_ENABLED)).intValue();
			tmpPath = DBConnector.toString (rows.get(0).get(JSONMessageProcessor.UPLOADED_FILES.TMP_FILE));
			mdIndex = DBConnector.intValue(rows.get(0).get(JSONMessageProcessor.UPLOADED_FILES.MD_INDEX));
			if(enabled != 1) {
				throw new IOException("File is not enabled for upload");
			}
			switch(status) {
			case 0:
				if(tmpPath == null) {
					tmpPath = TEMP_DIR + System.getProperty("file.separator") + reqId + new Date().getTime();
				}
				tmpFile = new File(tmpPath);
				if(tmpFile.exists()) {
					tmpFile.delete();
				}
				if(!tmpFile.createNewFile()) {
					throw new IOException("Failed to create file " + tmpFile.getName());
				}
				mdIndex = MessageDigestSerializer.getInstance(digestAlgorithm);
				cols = new String[] {JSONMessageProcessor.UPLOADED_FILES.STATUS, JSONMessageProcessor.UPLOADED_FILES.TMP_FILE, JSONMessageProcessor.UPLOADED_FILES.MD_INDEX};
				values = new Object[] {Integer.toString(status + 1), tmpPath, Integer.toString(mdIndex)};
				DBConnector.update(con, DBConnector.createUpdate(tableName, cols, values, new WhereClause(com1)));
				status ++;
			case 1:
				tmpFile = new File(tmpPath);
				if(!tmpFile.exists()) {
					throw new IOException("File " + tmpFile.getName() + "does not exixts");
				}
				mds = MessageDigestSerializer.getMDS(mdIndex);
				byte[] b = new byte[MAX_READED_BYTES];
				int i = -1;
				FileOutputStream out = new FileOutputStream(tmpFile, true);
				while((i = in.read(b)) >= 0) {
					out.write(b, 0, i);
					mds.update (b, 0, i);
				}
				out.close();
				if(last) {
					byte[] digest2 = mds.digest();
					mds.reset();
					if(!MessageDigest.isEqual(digest2, DatatypeConverter.parseHexBinary(digest))) {
						throw new IOException("Message digest of dowloaded file is failed to check");
					}
					cols = new String[] {JSONMessageProcessor.UPLOADED_FILES.STATUS, JSONMessageProcessor.UPLOADED_FILES.MD_INDEX};
					values = new Object[] {Integer.toString(status + 1), "-1"};
					DBConnector.update(con, DBConnector.createUpdate(tableName, cols, values, new WhereClause(com1)));
					status ++;
					return msg.returnOK();
				}
				else {
					uri = (int) (Math.random() * Integer.MAX_VALUE);
					cols = new String[] {JSONMessageProcessor.UPLOADED_FILES.URI};
					values = new Object[] {Integer.toString(uri)};
					DBConnector.update(con, DBConnector.createUpdate(tableName, cols, values, new WhereClause(com1)));
					return msg.returnOK(id, uri);
				}
			default:
				return msg.sendAppError("File is allready uploaded");
			}
		}
		catch(Exception e) {
			try {	
				tableName = new TableName(new Identifier(JSONMessageProcessor.UPLOADED_FILES.class.getSimpleName()));
				com1 = new CompPred(new Object[]{new Identifier(UNIVERSAL_ID), new Identifier(JSONMessageProcessor.UPLOADED_FILES.URI)},
						new Object[]{Integer.parseInt(reqId, 2), Integer.parseInt(reqUri, 2)}, Predicate.EQUAL);
				cols = new String[] {JSONMessageProcessor.UPLOADED_FILES.STATUS, JSONMessageProcessor.UPLOADED_FILES.MD_INDEX};
				values = new Object[] {"0", "-1"};
				if(mdIndex >= 0) {
					MessageDigestSerializer.reset(mdIndex);
				}
				DBConnector.update(con, DBConnector.createUpdate(tableName, cols, values, new WhereClause(com1)));
			}
			catch(Exception e1) {
				e1.printStackTrace();
			}
			return msg.sendAppError(e);
		}
	}
	
	@SuppressWarnings("unused")
	private static final JSONMessage initUpload(JSONMessageProcessor mp, Connection con, JSONMessage msg) {
		String grpName = (String) msg.getBody()[0];
		String fileDigest = JSONMessageProcessor.constructHexHash((ArrayList<?>) msg.getBody()[2]);
		String digestAlgorithm = DEFAULT_DIGEST_ALGORITHM;
		if(msg.getBody().length > 3) {
			digestAlgorithm = (String) msg.getBody()[3];
		}
		msg = JSONMessageProcessor.login(mp, con, msg);
		if(msg.getBody()[0].equals(AppConst.OK_RESPONSE)) {
			int grpId;
			try {
				grpId = getGroupId(con, grpName);
			} catch (SQLException | IOException e) {
				return msg.sendAppError(e);
			}
			TableName tableName;
			String col = JSONMessageProcessor.UPLOADED_FILES.GROUP_ID;
			String[] cols = {
				JSONMessageProcessor.UPLOADED_FILES.GROUP_ID,
				JSONMessageProcessor.UPLOADED_FILES.DIGEST,
				JSONMessageProcessor.UPLOADED_FILES.DIGEST_ALGORITHM,
				JSONMessageProcessor.UPLOADED_FILES.URI
			};
			int[] indexes = {0, AppError.DUPLICATE_FILE_INSERT, AppError.MULTIPLE_FILE_INSERT, AppError.FILE_UNEXPECTED_RETURN_VALUE};
			String[] values = {Integer.toString(grpId), fileDigest, digestAlgorithm, Integer.toString((int) (Math.random() * Integer.MAX_VALUE))};
			Where w = new Where();
			Object[] where = w.set(w.equ(col, grpId));
			Object com1;
			try {
				tableName = new TableName(new Identifier(JSONMessageProcessor.UPLOADED_FILES.class.getSimpleName()));
				com1 = new CompPred(new Object[]{new Identifier(col)}, new Object[]{grpId}, Predicate.EQUAL);
				JSONMessageProcessor.insertRow(con, tableName, indexes, cols, values, new WhereClause(com1), true);
			} catch (SQLException | IOException e) {
				return msg.sendAppError(e);
			}
			col = JSONMessageProcessor.UPLOADED_FILES.URI;
			indexes = null;
			indexes = new int[]{AppError.DUPLICATE_FILE_INSERT, AppError.MULTIPLE_FILE_INSERT, AppError.FILE_INSERT_NOT_ENABLED};
			Object[] uri = null;
			try {
				tableName = new TableName(new Identifier(JSONMessageProcessor.UPLOADED_FILES.class.getSimpleName()));
				uri = JSONMessageProcessor.getObject(con, tableName, indexes, col, grpName, new WhereClause(com1), true);
			} catch (SQLException | IOException e) {
				return msg.sendAppError(e);
			}
			return msg.returnOK(uri[0], uri[1]);
		}
		return msg;
	}
	
	private static final int getGroupId(Connection con, String grpName) throws SQLException, IOException {
		TableName tableName = new TableName(new Identifier(JSONMessageProcessor.REGISTERED_GROUPS.class.getSimpleName()));
		String col = JSONMessageProcessor.REGISTERED_GROUPS.GROUP_NAME;
		int[] indexes = {AppError.GROUP_NOT_REGISTERED, AppError.GROUP_MULTIPLE_REGISTRATION, AppError.GROUP_NOT_ENABLED};
		Object com1 = new CompPred(new Object[]{new Identifier(col)}, new Object[]{grpName}, Predicate.EQUAL);
		return (Integer) JSONMessageProcessor.getObject(con, tableName, indexes, col, grpName, new WhereClause(com1), true)[ID_INDEX];
	}
	
	private static final int getDeviceId(Connection con, int grpId, String devName) throws SQLException, IOException {
		TableName tableName = new TableName(new Identifier(JSONMessageProcessor.REGISTERED_DEVICES.class.getSimpleName()));
		String col = JSONMessageProcessor.REGISTERED_DEVICES.DEVICE_NAME;
		String col2 = JSONMessageProcessor.REGISTERED_DEVICES.GROUP_ID;
		int[] indexes = {AppError.DEVICE_NOT_REGISTERED, AppError.DUPLICATE_DEVICE_NAME, AppError.DEVICE_NOT_ENABLED};
		Object com1 = new CompPred(new Object[]{new Identifier(col), new Identifier(col2)}, new Object[]{devName, grpId}, Predicate.EQUAL);
		return (Integer) JSONMessageProcessor.getObject(con, tableName, indexes, col, devName, new WhereClause(com1), true)[ID_INDEX];
	}
	
	@SuppressWarnings("unused")
	private static final JSONMessage registerDevice(JSONMessageProcessor mp, Connection con, JSONMessage msg) {
		String grpName = (String) msg.getBody()[0];
		//String hash = JSONMessageProcessor.constructHexHash((ArrayList<?>) msg.getBody()[1]);
		String devName = (String) msg.getBody()[2];
		double primary = ((double) msg.getBody()[3]);
		//double dbVersion = ((double) msg.getBody()[4]);
		String grpIdCol = JSONMessageProcessor.REGISTERED_DEVICES.GROUP_ID;
		String devNameCol = JSONMessageProcessor.REGISTERED_DEVICES.DEVICE_NAME;
		msg = JSONMessageProcessor.login(mp, con, msg);
		if(msg.getBody()[0].equals(AppConst.OK_RESPONSE)) {
			TableName tableName;
			String col = JSONMessageProcessor.REGISTERED_GROUPS.GROUP_NAME;
			Where w = new Where();
			Object[] where = w.set(w.equ(col, grpName));
			int[] indexes = {AppError.GROUP_NOT_REGISTERED, AppError.GROUP_MULTIPLE_REGISTRATION, AppError.GROUP_NOT_ENABLED};
			int grpId;
			try {
				tableName = new TableName(new Identifier(JSONMessageProcessor.REGISTERED_GROUPS.class.getSimpleName()));
				Object com1 = new CompPred(new Object[]{new Identifier(col)}, new Object[]{grpName}, Predicate.EQUAL);
				grpId = (Integer) JSONMessageProcessor.getObject(con, tableName, indexes, col, grpName, new WhereClause(com1), true)[ID_INDEX];
			} catch (SQLException | IOException e) {
				return msg.sendAppError(e);
			}
			w.clear();
			where = w.set(w.and(w.equ(grpIdCol, grpId), w.equ(devNameCol, devName)));
			//Object [] where2 = new Object[]{"(" + grpIdCol + " = ?) AND (" + devNameCol + " = ?)", grpId, devName};
			indexes = null;
			indexes = new int[] {2, AppError.DUPLICATE_DEVICE_NAME, AppError.MULTIPLE_DEVICE_NAME, AppError.DEVICE_UNEXPECTED_RETURN_VALUE};
			String[] cols = {
					JSONMessageProcessor.REGISTERED_DEVICES.GROUP_ID,
					JSONMessageProcessor.REGISTERED_DEVICES.PRIMARY_DEVICE,
					JSONMessageProcessor.REGISTERED_DEVICES.DEVICE_NAME,
			};
			String[] values = {Integer.toString(grpId), Double.toString(primary), devName};
			try {
				tableName = new TableName(new Identifier(REGISTERED_DEVICES.class.getSimpleName()));
				Object com2 = new CompPred(new Object[]{new Identifier(grpIdCol), new Identifier(devNameCol)}, new Object[]{grpId, devName}, Predicate.EQUAL);
				JSONMessageProcessor.insertRow(con, tableName, indexes, cols, values, new WhereClause(com2), true);
			} catch (SQLException | IOException e) {
				return msg.sendAppError(e);
			}
			try {
				JSONMessageProcessor.setPrimaryDevice(con, grpId, devName);
			} catch (SQLException | IOException e) {
				return msg.sendAppError(e);
			}
			return msg.returnOK();
		}
		return msg;
	}
	
	private static void setPrimaryDevice(Connection con, int grpId, String devName) throws SQLException, IOException {
		TableName tableName = new TableName(new Identifier(REGISTERED_DEVICES.class.getSimpleName()));
		String grpIdCol = JSONMessageProcessor.REGISTERED_DEVICES.GROUP_ID;
		String enabledCol = JSONMessageProcessor.REGISTERED_DEVICES.ENABLED;
		String primDevCol = JSONMessageProcessor.REGISTERED_DEVICES.PRIMARY_DEVICE;
		String idCol = JSONMessageProcessor.REGISTERED_DEVICES.ID;
		String devNameCol = JSONMessageProcessor.REGISTERED_DEVICES.DEVICE_NAME;
		Where w = new Where();
		
		CompPred com1 = new CompPred(
				new Object[]{new Identifier(grpIdCol), new Identifier(enabledCol), new Identifier(primDevCol)},
				new Object[]{grpId, 1, 1},
				Predicate.EQUAL); 
		String[] cols = {JSONMessageProcessor.REGISTERED_DEVICES.PRIMARY_DEVICE};
		Object[] values;
		int count = DBConnector.count(con, tableName, new WhereClause(com1));
		switch(count) {
		case 0:
			w.clear();
			if(devName == null) {
				int[] indexes = {AppError.NO_ENABLED_DEVICES, 0, 0};
				String col = JSONMessageProcessor.REGISTERED_DEVICES.DEVICE_NAME;
				com1 = new CompPred(new Object[]{new Identifier(grpIdCol), new Identifier(enabledCol)}, new Object[]{grpId, 1}, Predicate.EQUAL);
				int devId = (int) JSONMessageProcessor.getObject(con, tableName, indexes, col, devName, new WhereClause(com1), false)[ID_INDEX];
				com1 = new CompPred(new Object[]{new Identifier(idCol)}, new Object[]{devId}, Predicate.EQUAL);
			}
			else {
				com1 = new CompPred(new Object[]{new Identifier(grpIdCol), new Identifier(devNameCol)}, new Object[]{grpId, devName}, Predicate.EQUAL);
			}
			values = new String[]{"1"};
			int status = DBConnector.update(con, DBConnector.createUpdate(tableName, cols, values, new WhereClause(com1)));
			if(status != 1) {
				throw new IOException (AppError.getMessage(AppError.PRIMARY_KEY_UNEXPECTED_ERROR, status));
			}
			break;
		case 1:
			break;
		default:
			values = new Object[]{0};
			com1 = new CompPred(new Object[]{new Identifier(grpIdCol)}, new Object[]{grpId}, Predicate.EQUAL);
			DBConnector.update(con, DBConnector.createUpdate(tableName, cols, values, new WhereClause(com1)));
			JSONMessageProcessor.setPrimaryDevice(con, grpId, devName);
		}
	}
	
	
	@SuppressWarnings("unused")
	private static final JSONMessage register(JSONMessageProcessor mp, Connection con, JSONMessage msg) {
		String grpName = (String) msg.getBody()[0];
		String hash = JSONMessageProcessor.constructHexHash((ArrayList<?>) msg.getBody()[OBJECT_INDEX]);
		String email = (String) msg.getBody()[2];
		double dbVersion = ((double) msg.getBody()[3]);
		TableName tableName; 
		Where w = new Where();
		Object[] where = w.set(w.equ(JSONMessageProcessor.REGISTERED_GROUPS.GROUP_NAME, grpName));
		int[] indexes = {0, AppError.ALLREADY_REGISTERED, AppError.GROUP_MULTIPLE_REGISTRATION, AppError.UNEXPECTED_RETURN_VALUE};
		String[] cols = {
				JSONMessageProcessor.REGISTERED_GROUPS.GROUP_NAME,
				JSONMessageProcessor.REGISTERED_GROUPS.PASSWORD,
				JSONMessageProcessor.REGISTERED_GROUPS.EMAIL,
				JSONMessageProcessor.REGISTERED_GROUPS.DB_VERSION
		};
		String[] values = {grpName, hash, email, Double.toString(dbVersion)};
		try {
			tableName = new TableName(new Identifier(JSONMessageProcessor.REGISTERED_GROUPS.class.getSimpleName()));
			Object com1 = new CompPred(new Object[]{new Identifier(JSONMessageProcessor.REGISTERED_GROUPS.GROUP_NAME)}, new Object[]{grpName}, Predicate.EQUAL);
			JSONMessageProcessor.insertRow(con, tableName, indexes, cols, values, new WhereClause(com1), true);
		} catch (SQLException | IOException e) {
			return msg.sendAppError(e);
		}
		return msg.returnOK();
	}
		
	private static final JSONMessage login(JSONMessageProcessor mp, Connection con, JSONMessage msg) {
		String grpName = (String) msg.getBody()[0];
		String hash = JSONMessageProcessor.constructHexHash((ArrayList<?>) msg.getBody()[1]);
		TableName tableName = null;
		int[] indexes = {AppError.GROUP_NOT_REGISTERED, AppError.GROUP_MULTIPLE_REGISTRATION, AppError.GROUP_NOT_ENABLED}; 
		String col = JSONMessageProcessor.REGISTERED_GROUPS.PASSWORD;
		String hash2;
		try {
			tableName = new TableName(new Identifier(JSONMessageProcessor.REGISTERED_GROUPS.class.getSimpleName()));
			Object com1 = new CompPred(new Object[]{new Identifier(JSONMessageProcessor.REGISTERED_GROUPS.GROUP_NAME)}, new Object[]{grpName}, Predicate.EQUAL);
			hash2 = (String) JSONMessageProcessor.getObject(con, tableName, indexes, col, col, new WhereClause(com1), true)[1];
		} catch (SQLException | IOException e) {
			return msg.sendAppError(e);
		}
		if(!hash.equals(hash2)) {
			return msg.sendAppError(AppError.getMessage(AppError.LOGIN_FAILED, grpName));
		}
		return msg.returnOK();
	}
	
	private static void insertRow(Connection con, TableName tableName, int[] index, String[] cols, String[] values, WhereClause where, boolean unique) throws SQLException, IOException {
		if(unique) {
			int count = 0;
			count = DBConnector.count(con, tableName, where);
			if(count == 1) {
				throw new IOException (AppError.getMessage(index[1], values[index[0]]));
			}
			if(count > 1) {
				throw new IOException (AppError.getMessage(index[2], values[index[0]]));
			}
		}
		int status = DBConnector.insert(con, DBConnector.createInsert(tableName, values, cols));
		if(status != 1) {
			throw new IOException (AppError.getMessage(index[3], values[index[0]]));
		}
	}
	
	private static Object[] getObject (Connection con, TableName table, int[] index, String col, String value, WhereClause where, boolean unique) throws SQLException, IOException {
		int count = 0;
		count = DBConnector.count(con, table, where);
		if(count == 0) {
			throw new IOException(AppError.getMessage(index[1], value));
		}
		if(count > 1 && unique) {
			throw new IOException(AppError.getMessage(index[2], value));
		}
		Query select = DBConnector.createSelect();
		select.addFromClause(table);
		select.addTableSpec(where);
		select.addColumns(UNIVERSAL_ID, col, UNIVERSAL_ENABLED);
		HashMap<String, Object> row = DBConnector.getFirstRowOf(con, select);
		boolean enabled = ((BigDecimal)row.get(UNIVERSAL_ENABLED)).intValue() == 1;
		if(!enabled) {
			throw new IOException(AppError.getMessage(index[2], value));
		}
		return new Object[]{row.get(UNIVERSAL_ID), row.get(col)};
	}
	
	private static String constructHexHash(ArrayList<?> arrayList) {
		StringBuffer sb = new StringBuffer(arrayList.size() * 2);
		for(int i = 0; i < arrayList.size(); i ++) {
			Double d = (Double) arrayList.get(i);
			sb.append(String.format("%2s", Integer.toHexString(d.intValue() & 0xFF)).replaceAll(" ", "0"));
			//System.out.println(Integer.toHexString(d.intValue() & 0xFF));
		}
		//"System.out.println(sb + ", " + sb.length());
		return sb.toString();
	}
	

	@SuppressWarnings("unused")
	private static final JSONMessage heartbeat(JSONMessageProcessor mp, Connection con, JSONMessage msg) {
		Object[] objs = msg.getBody();
		Object[] newObjs = new Object[objs.length + 1];
		System.arraycopy(objs, 0, newObjs, 0, objs.length);
		objs = null;
		newObjs[newObjs.length - 1] = new Date().getTime();
		logger.info("Proccess JSON message type " + msg.getHeader() + ", added outgoing date " + new Date((long) newObjs[newObjs.length - 1]));
		msg.setBody(newObjs);
		return msg;
	}

	private static int getSyncRequests() {
		return JSONMessageProcessor.syncRequests;
	}

	private static void setSyncRequests(int add) throws IOException {
		switch(add > 0 ? 1 : (add < 0 ? -1 : 0)) {
		case 1:
			if(JSONMessageProcessor.getSyncRequests() == JSONMessageProcessor.MAX_SYNC_REQUESTS) {
				throw new IOException("Max sync requests (" + JSONMessageProcessor.MAX_SYNC_REQUESTS + ") reached");
			}
			JSONMessageProcessor.syncRequests ++;
			break;
		case -1:
			if(JSONMessageProcessor.getSyncRequests() > 0) {
				JSONMessageProcessor.syncRequests --;
			}
			break;
		default:
			throw new IOException("Unknown status of sync requests counter ");
		}
		
			
	}

	public static Rows retrieveRunnableGroups(int grpId) {
		Rows rows = null;
		Connection con = null;
		try {
			Query select = DBConnector.createSelect();
			TableName regGroups = new TableName(new Identifier(JSONMessageProcessor.REGISTERED_GROUPS.class.getSimpleName()));
			TableName regDevs = new TableName(new Identifier(JSONMessageProcessor.REGISTERED_DEVICES.class.getSimpleName()));
			TableName uFiles = new TableName(new Identifier(JSONMessageProcessor.UPLOADED_FILES.class.getSimpleName()));
			Identifier rg = new Identifier("RG");
			Identifier rd = new Identifier("RD");
			Identifier uf = new Identifier("UF");
			select.addFromClause(new FromClause(regGroups, rg), new FromClause(regDevs, rd), new FromClause(uFiles, uf));
			ColumnSpec rgId = new ColumnSpec(rg, new Identifier(JSONMessageProcessor.UNIVERSAL_ID));
			select.addColumns(
					rgId,
					new ColumnSpec(rg, new Identifier(JSONMessageProcessor.REGISTERED_GROUPS.GROUP_NAME)),
					new ColumnSpec(rg, new Identifier(JSONMessageProcessor.REGISTERED_GROUPS.EMAIL)),
					new ColumnSpec(rd, new Identifier(JSONMessageProcessor.UNIVERSAL_ID)),
					new ColumnSpec(rd, new Identifier(JSONMessageProcessor.REGISTERED_DEVICES.DEVICE_NAME)),
					new ColumnSpec(uf, new Identifier(JSONMessageProcessor.UPLOADED_FILES.TMP_FILE)));
			select.addDerived(
					JSONMessageProcessor.REGISTERED_DEVICES.GROUP_ID,
					JSONMessageProcessor.REGISTERED_GROUPS.GROUP_NAME,
					JSONMessageProcessor.REGISTERED_GROUPS.EMAIL,
					JSONMessageProcessor.UNIVERSAL_ID,
					JSONMessageProcessor.REGISTERED_DEVICES.DEVICE_NAME,
					JSONMessageProcessor.UPLOADED_FILES.TMP_FILE);
			Object com1 = new CompPred(
					new Object[]{
							new ColumnSpec(rg, new Identifier(JSONMessageProcessor.UNIVERSAL_ENABLED)),
							new ColumnSpec(rd, new Identifier(JSONMessageProcessor.REGISTERED_DEVICES.GROUP_ID)),
							new ColumnSpec(rd, new Identifier(JSONMessageProcessor.UNIVERSAL_ENABLED)),
							new ColumnSpec(rd, new Identifier(JSONMessageProcessor.REGISTERED_DEVICES.SYNC_ENABLED)),
							new ColumnSpec(uf, new Identifier(JSONMessageProcessor.UNIVERSAL_ENABLED)),
							new ColumnSpec(uf, new Identifier(JSONMessageProcessor.UPLOADED_FILES.GROUP_ID)),
							new ColumnSpec(uf, new Identifier(JSONMessageProcessor.UPLOADED_FILES.STATUS))},
					new Object[]{
							JSONMessageProcessor.ENABLED,
							rgId,
							JSONMessageProcessor.ENABLED,
							JSONMessageProcessor.ENABLED,
							JSONMessageProcessor.ENABLED,
							rgId,
							JSONMessageProcessor.UPLOADED_FILES.STATUS_SYNC_ENABLED},
					Predicate.EQUAL);
			if(grpId < 0) {
				select.addTableSpec(new WhereClause(com1));
			}
			else {
				select.addTableSpec(new WhereClause(com1, WhereClause.AND, new CompPred(new Object[]{rgId}, new Object[]{grpId}, Predicate.EQUAL)));
			}
			select.addQuerySpec(DistinctSpec.Distinct);
			//System.out.println(select.toSQLString());
			con = DBConnector.lookup(dsn);
			rows = DBConnector.select(con, select);
		} catch (SQLException e) {
			rows = null;
			e.printStackTrace();
		}
		finally {
			try {con.close();} catch (SQLException e) {}
		}
		return rows;
	}

	static CharSequence getDefaultUserPassword() {
		return JSONMessageProcessor.H2_PASSWORD;
	}
/*
	public static long getLastOperationId(int grpId) {
		long l = -1;
		Connection con = null;
		try {
			Query select = DBConnector.createSelect();
			TableName tableName = new TableName (new Identifier(JSONMessageProcessor.SYNC_RESPONSES.class.getSimpleName()));
			select.addFromClause(tableName);
			Object com1 = new CompPred(new Object[] {new Identifier(JSONMessageProcessor.SYNC_RESPONSES.GROUP_ID)}, new Object[] {grpId}, Predicate.EQUAL);
			select.addColumns(JSONMessageProcessor.UNIVERSAL_ID);
			select.addTableSpec(new WhereClause(com1));
			select.addDerived(MAX_FUNCTION);
			select.addSelectColumnFunction(MAX_FUNCTION);
			con = DBConnector.lookup(dsn);
			Rows rows = DBConnector.select(con, select);
			if(rows.size() > 0) {
				Object obj = rows.get(0).get(JSONMessageProcessor.MAX_FUNCTION);
				if(obj != null) {
					l = (long) ((int) obj); 
				}
			}
		} catch (SQLException e) {
		}
		finally {
			try{con.close();} catch (SQLException e) {}
		}
		return l;
	}
	
	public static int getOperation(Operation op, int status) throws SQLException {
		int grpId = op.getGroupNode().getGroupId();
		int devId = op.getDeviceNode().getDeviceId();
		Connection con = null;
		try {
			TableName tableName = new TableName (new Identifier(JSONMessageProcessor.SYNC_RESPONSES.class.getSimpleName()));
			Object com1 = new CompPred(
				new Object[] {
					new Identifier(JSONMessageProcessor.SYNC_RESPONSES.GROUP_ID),
					new Identifier(JSONMessageProcessor.SYNC_RESPONSES.DEVICE_ID),
					new Identifier(JSONMessageProcessor.SYNC_RESPONSES.STATUS)},
				new Object[] {grpId, devId, status}, Predicate.EQUAL);
			con = DBConnector.lookup(dsn);
			Rows rows = DBConnector.select(con, DBConnector.createSelect().addFromClause(tableName).addColumns(JSONMessageProcessor.UNIVERSAL_ID).addTableSpec(new WhereClause(com1)));
			if(rows.size() > 0) {
				return (int) rows.get(0).get(JSONMessageProcessor.UNIVERSAL_ID);
			}
			try {
				return JSONMessageProcessor.retrieveRows(op.getMessageProcessor(), con, op.getMessageProcessor().sync_responses, new WhereClause(com1));
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				throw new SQLException(e);
			}
		}
		catch (SQLException e) { 
			throw e;
		}
		finally {
			try{con.close();} catch (SQLException e) {}
		}
	}
	
	
	
*/	
	public static int getOperation(Operation op, int status) throws SQLException {
		int grpId = op.getGroupNode().getGroupId();
		int devId = op.getDeviceNode().getDeviceId();
		Connection con = null;
		try {
			Object com1 = new CompPred(
				new Object[] {
					new Identifier(JSONMessageProcessor.SYNC_RESPONSES.GROUP_ID),
					new Identifier(JSONMessageProcessor.SYNC_RESPONSES.DEVICE_ID),
					new Identifier(JSONMessageProcessor.SYNC_RESPONSES.STATUS)},
				new Object[] {grpId, devId, status}, Predicate.EQUAL);
			con = DBConnector.lookup(dsn);
			try {
				return JSONMessageProcessor.retrieveRows(op.getMessageProcessor(), con, op.getMessageProcessor().sync_responses, new WhereClause(com1));
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				throw new SQLException(e);
			}
		}
		catch (SQLException e) { 
			throw e;
		}
		finally {
			try{con.close();} catch (SQLException e) {}
		}
	}
	
	public static int getWaitingOperation(Operation op) throws SQLException {
		return JSONMessageProcessor.getOperation(op, JSONMessageProcessor.SYNC_RESPONSES.WAITING);
	}
	public static int getPendingOperation(Operation op) throws SQLException {
		return JSONMessageProcessor.getOperation(op, JSONMessageProcessor.SYNC_RESPONSES.PENDING);
	}

	public static void deleteOperation(int l) {
		Connection con = null;
		try {
			con = DBConnector.lookup(dsn);
			TableName tableName = new TableName(new Identifier(JSONMessageProcessor.SYNC_RESPONSES.class.getSimpleName()));
			JSONMessageProcessor.delete(con, tableName, l);
		} catch (SQLException e) {
			/*
			 * TODO chyba pri mazani zaznamu operacie
			 */
		}		
		finally {
			try{con.close();} catch (SQLException e) {}
		}
	}
	/*
	public static boolean[] checkPartial(String dsn, Operation op) throws SQLException {
		Connection con = null;
		boolean[] retValue = new boolean[op.getPrimaryKeys().length + 1];
		try {
			con = DBConnector.lookup(dsn);
			for(int i = 0; i < op.getPrimaryKeys().length; i ++) {
				retValue[i] = check (con, op.getSchemaName(), op.getTableName(), new String[]{op.getPrimaryKeys()[i]}, new Object[]{op.getPrimaryKeysValues()[i]});
				retValue[retValue.length - 1] |= retValue[i];
			}
			return retValue;
		}
		catch(SQLException e) {
			throw e;
		}
		finally {
			try{con.close();} catch (SQLException e) {}
		}
	}
	*/
	private static boolean check(Connection con, String schema, String table, String[] columns, Object[] values) throws SQLException {
		SchemaName schemaName = new SchemaName(new Identifier(schema));
		TableName tableName = new TableName(schemaName, new Identifier(table));
		ColumnSpec[] columnSpecs = JSONMessageProcessor.getColumnSpec(tableName, columns);
		Object com1 = new CompPred(columnSpecs, values, Predicate.EQUAL);
		return DBConnector.select(con, DBConnector.createSelect().addFromClause(tableName).addTableSpec(new WhereClause(com1))).size() == 1;
	}

	
	public static boolean checkAll(String dsn, Operation op) throws SQLException {
		Connection con = null;
		try {
			con = DBConnector.lookup(dsn);
			return check(con, op.getSchemaName(), op.getTableName(), op.getColumns(), op.getNewValues());
		}
		catch(SQLException e) {
			throw e;
		}
		finally {
			try{con.close();} catch (SQLException e) {}
		}
	}

	private static ColumnSpec[] getColumnSpec(SQLSyntaxImpl ref, String[] colNames) {
		ColumnSpec[] cols = new ColumnSpec[colNames.length];
		for(int i = 0; i < cols.length; i ++) {
			if(ref == null) {
				cols[i] = new ColumnSpec(new Identifier(colNames[i])); 
			}
			else {
				cols[i] = new ColumnSpec(ref, new Identifier(colNames[i]));
			}
		}
		return cols;
	}

	static Object[] getSyncColumnValues(String[] colNames, Row row) {
		Object[] values = new Object[colNames.length];
		for(int i = 0; i < colNames.length; i ++) {
			values[i] = row.get(colNames[i]);
		}
		return values;
	}

	static String[] getSyncColumnNames(Row row) {
		Iterator<String> i = row.keySet().iterator();
		ArrayList<String> a = new ArrayList<String>();
		while(i.hasNext()) {
			String key = i.next();
			if(!(key.startsWith(DBSchema.SYNC_LABEL))) {
				a.add(key);
			}
		}
		String[] s = new String[a.size()];
		return a.toArray(s);
	}

/*	

	private static Object[] getValuesForColumns(Object[] vals, String[] cols, String[] cols2) {
		Object[] v = new Object[cols2.length];
		for(int i = 0; i < v.length; i ++) {
			int index = getIndexOfArray(cols, cols[i]);
			if(index < 0) {
				continue;
			}
			v[i] = vals[index];
		}
		return v;
	}
*/
	static int getIndexOfArray(String[] cols, String s) {
		for(int i = 0; i < cols.length; i ++) {
			if(cols[i].equals(s)) {
				return i;
			}
		}
		return -1;
	}

	public static Connection getConnection() throws SQLException {
		return DBConnector.lookup(dsn);
	}
	
	
	private static Object getUpdateData(Operation op) throws SQLException {
		String[] cols = op.getGroupNode().getDBSchema().getColumns(op.getTableName());
		ArrayList<Integer> changes = new ArrayList<Integer>();
		Object[] c1 = new Object[cols.length];
		Object[] c2 = new Object[op.getOldValues().length];
		Object[] oldVals = op.getOldValues();
		Object[] newVals = op.getNewValues();
		int i;
		for(i = 0; i < cols.length; i ++) {
			if((oldVals[i] == null && newVals[i] != null) || (oldVals[i] != null && newVals[i] == null) || (oldVals[i] != null && newVals[i] != null && !oldVals[i].equals(newVals[i]))) {
				changes.add(i);
			}
		}
		String[] nCols = new String[changes.size()];
		Object[] nVals = new Object[changes.size()];
		for(i = 0; i < nCols.length; i ++) {
			int index = changes.get(i);
			nCols[i] = cols[index];
			nVals[i] = op.getNewValues()[index];
		}
		for(i = 0; i < cols.length; i++) {
			c1[i] = new Identifier(cols[i]);
			c2[i] = op.getOldValues()[i];
		}
		if(c1.length == 0) {
			throw new SQLException("No where clause upon update operation");
		}
		return new Object[]{nCols, nVals, new CompPred(c1, c2, Predicate.EQUAL)};
	}
	
	static void deleteOperation(String dsn2, Operation op) throws SQLException {
		Connection con = DBConnector.lookup(dsn);
		Connection con2 =  DBConnector.lookup(dsn2);
		try {
			con.setAutoCommit(false);
			con.setAutoCommit(false);
			Object obj[] = (Object[]) getUpdateData(op);
			SchemaName schemaName = new SchemaName(new Identifier(op.getSchemaName()));
			TableName tableName = new TableName(schemaName, new Identifier(op.getTableName()));
			op.setQuery(save(new Object[]{op.getOldValues(), op.getNewValues()}));
			Delete delete = DBConnector.createDelete(tableName , null, new WhereClause(obj[2]));
			DBConnector.delete(con2, delete, false);
			
			tableName = new TableName(new Identifier(JSONMessageProcessor.SYNC_RESPONSES.class.getSimpleName()));
			String[] colNames = {
					JSONMessageProcessor.SYNC_RESPONSES.GROUP_ID,
					JSONMessageProcessor.SYNC_RESPONSES.DEVICE_ID,
					JSONMessageProcessor.SYNC_RESPONSES.QUERY,
					JSONMessageProcessor.SYNC_RESPONSES.TYPE,
					JSONMessageProcessor.SYNC_RESPONSES.TABLE_NAME,
					JSONMessageProcessor.SYNC_RESPONSES.SYNC_ID
			};
			Object vals = new Object[] {
					op.getGroupNode().getGroupId(), null, op.getQuery(), JSONMessage.DELETE_OPERATION, op.getTableName(), op.getId()
			};
			DeviceNode[] devNodes = JSONMessageProcessor.getOtherDevNodes(op);
			for(int i = 0; i < devNodes.length; i ++) {
				((Object[]) vals)[1] = devNodes[i].getDeviceId();
				DBConnector.insert(con, DBConnector.createInsert(tableName, vals, colNames), false);
			}
			colNames = new String[] {
					JSONMessageProcessor.SYNC_RESPONSES.GROUP_ID,
					JSONMessageProcessor.SYNC_RESPONSES.DEVICE_ID,
					JSONMessageProcessor.SYNC_RESPONSES.STATUS,
					JSONMessageProcessor.SYNC_RESPONSES.TYPE,
					JSONMessageProcessor.SYNC_RESPONSES.TABLE_NAME,
					JSONMessageProcessor.SYNC_RESPONSES.SYNC_ID
			};
			vals = new Object[] {
					op.getGroupNode().getGroupId(), op.getDeviceNode().getDeviceId(), JSONMessageProcessor.SYNC_RESPONSES.PENDING, JSONMessage.DELETE_NO_ACTION, op.getTableName(), op.getId()
			};
			DBConnector.insert(con, DBConnector.createInsert(tableName, vals, colNames), false);
			
			
		}
		catch (Exception e) {
			con2.rollback();
			con.rollback();
			throw new SQLException(e);
		}
		finally {
			try {
				con.commit();
				con2.commit();
				con.setAutoCommit(true);
				con.setAutoCommit(true);
			}
			catch (SQLException e) {
				throw e;
			}
			finally {
				try {
					con.close();
					con2.close();
				}
				catch (SQLException e) {}
			}
		}
	}
	
	static void updateOperation(String dsn2, Operation op) throws SQLException {
		Connection con = DBConnector.lookup(dsn);
		Connection con2 =  DBConnector.lookup(dsn2);
		try {
			//Object[] values = op.getValues();
			con.setAutoCommit(false);
			con.setAutoCommit(false);
			Object obj[] = (Object[]) getUpdateData(op);
			SchemaName schemaName = new SchemaName(new Identifier(op.getSchemaName()));
			TableName tableName = new TableName(schemaName, new Identifier(op.getTableName()));
			Update update = DBConnector.createUpdate(tableName , (String[]) obj[0], (Object[]) obj[1], new WhereClause(obj[2]));
			op.setQuery(save(new Object[]{op.getOldValues(), op.getNewValues()}));
			DBConnector.update(con2, update, false);
			tableName = new TableName(new Identifier(JSONMessageProcessor.SYNC_RESPONSES.class.getSimpleName()));
			String[] colNames = {
					JSONMessageProcessor.SYNC_RESPONSES.GROUP_ID,
					JSONMessageProcessor.SYNC_RESPONSES.DEVICE_ID,
					JSONMessageProcessor.SYNC_RESPONSES.QUERY,
					JSONMessageProcessor.SYNC_RESPONSES.TYPE,
					JSONMessageProcessor.SYNC_RESPONSES.TABLE_NAME,
					JSONMessageProcessor.SYNC_RESPONSES.SYNC_ID
			};
			Object vals = new Object[] {
					op.getGroupNode().getGroupId(), null, op.getQuery(), JSONMessage.UPDATE_OPERATION, op.getTableName(), op.getId()
			};
			DeviceNode[] devNodes = JSONMessageProcessor.getOtherDevNodes(op);
			for(int i = 0; i < devNodes.length; i ++) {
				((Object[]) vals)[1] = devNodes[i].getDeviceId();
				DBConnector.insert(con, DBConnector.createInsert(tableName, vals, colNames), false);
			}
			colNames = new String[] {
					JSONMessageProcessor.SYNC_RESPONSES.GROUP_ID,
					JSONMessageProcessor.SYNC_RESPONSES.DEVICE_ID,
					JSONMessageProcessor.SYNC_RESPONSES.STATUS,
					JSONMessageProcessor.SYNC_RESPONSES.TYPE,
					JSONMessageProcessor.SYNC_RESPONSES.TABLE_NAME,
					JSONMessageProcessor.SYNC_RESPONSES.SYNC_ID
			};
			vals = new Object[] {
					op.getGroupNode().getGroupId(), op.getDeviceNode().getDeviceId(), JSONMessageProcessor.SYNC_RESPONSES.PENDING, JSONMessage.UPDATE_NO_ACTION, op.getTableName(), op.getId()
			};
			DBConnector.insert(con, DBConnector.createInsert(tableName, vals, colNames), false);
		}
		catch (Exception e) {
			con2.rollback();
			con.rollback();
			throw new SQLException(e);
		}
		finally {
			try {
				con.commit();
				con2.commit();
				con.setAutoCommit(true);
				con.setAutoCommit(true);
			}
			catch (SQLException e) {
				throw e;
			}
			finally {
				try {
					con.close();
					con2.close();
				}
				catch (SQLException e) {}
			}
		}
	}
	
	static void insertOperation(String dsn2, Operation op) throws SQLException {
		Connection con = DBConnector.lookup(dsn);
		Connection con2 =  DBConnector.lookup(dsn2);
		try {
			con.setAutoCommit(false);
			con2.setAutoCommit(false);
			Object[] values = op.getNewValues();
			/*if(op.getNewPrimaryKeysValues() != null) {
				for(int i = 0; i < op.getPrimaryKeys().length; i ++) {
					values[JSONMessageProcessor.getIndexOfArray(op.getColumns(), op.getPrimaryKeys()[i])] = op.getNewPrimaryKeysValues()[i];
				}
			}*/
			Insert insert = DBConnector.createInsert (new TableName(new SchemaName(new Identifier(op.getSchemaName())), new Identifier(op.getTableName())), (Object)values, op.getColumns());
			op.setQuery(save(insert));
			DBConnector.insert(con2, insert, false);
			TableName tableName = new TableName(new Identifier(JSONMessageProcessor.SYNC_RESPONSES.class.getSimpleName()));
			String[] colNames = {
					JSONMessageProcessor.SYNC_RESPONSES.GROUP_ID,
					JSONMessageProcessor.SYNC_RESPONSES.DEVICE_ID,
					JSONMessageProcessor.SYNC_RESPONSES.QUERY,
					JSONMessageProcessor.SYNC_RESPONSES.TYPE,
					JSONMessageProcessor.SYNC_RESPONSES.TABLE_NAME,
					JSONMessageProcessor.SYNC_RESPONSES.SYNC_ID
			};
			Object vals = new Object[] {
					op.getGroupNode().getGroupId(), null, op.getQuery(), JSONMessage.INSERT_OPERATION, op.getTableName(), op.getId()
			};
			DeviceNode[] devNodes = JSONMessageProcessor.getOtherDevNodes(op);
			for(int i = 0; i < devNodes.length; i ++) {
				((Object[]) vals)[1] = devNodes[i].getDeviceId();
				DBConnector.insert(con, DBConnector.createInsert(tableName, vals, colNames), false);
			}
			/*
			if(op.getNewPrimaryKeysValues() != null) {
				ArrayList<Object> pkVal = new ArrayList<Object>();
				for(int i = 0; i < op.getPrimaryKeys().length; i ++) {
					if((op.getPrimaryKeys()[i] != null) && (op.getNewPrimaryKeysValues()[i] != null) && (op.getPrimaryKeysValues()[i] != null)) {
						pkVal.add(op.getPrimaryKeys()[i]);
						pkVal.add(op.getPrimaryKeysValues()[i]);
						pkVal.add(op.getNewPrimaryKeysValues()[i]);
					}
				}
				op.setQuery(save(pkVal.toArray()));
				colNames = new String[] {
						JSONMessageProcessor.SYNC_RESPONSES.GROUP_ID,
						JSONMessageProcessor.SYNC_RESPONSES.DEVICE_ID,
						JSONMessageProcessor.SYNC_RESPONSES.STATUS,
						JSONMessageProcessor.SYNC_RESPONSES.QUERY,
						JSONMessageProcessor.SYNC_RESPONSES.TYPE,
						JSONMessageProcessor.SYNC_RESPONSES.TABLE_NAME,
						JSONMessageProcessor.SYNC_RESPONSES.SYNC_ID
				};
				vals = new Object[] {
						op.getGroupNode().getGroupId(), op.getDeviceNode().getDeviceId(), JSONMessageProcessor.SYNC_RESPONSES.PENDING, op.getQuery(), JSONMessage.INSERT_UPDATE_PK, op.getTableName(), op.getId()
				};
				DBConnector.insert(con, DBConnector.createInsert(tableName, vals, colNames));
			}
			else {*/
			colNames = new String[] {
					JSONMessageProcessor.SYNC_RESPONSES.GROUP_ID,
					JSONMessageProcessor.SYNC_RESPONSES.DEVICE_ID,
					JSONMessageProcessor.SYNC_RESPONSES.STATUS,
					JSONMessageProcessor.SYNC_RESPONSES.TYPE,
					JSONMessageProcessor.SYNC_RESPONSES.TABLE_NAME,
					JSONMessageProcessor.SYNC_RESPONSES.SYNC_ID
			};
			vals = new Object[] {
					op.getGroupNode().getGroupId(), op.getDeviceNode().getDeviceId(), JSONMessageProcessor.SYNC_RESPONSES.PENDING, JSONMessage.INSERT_NO_ACTION, op.getTableName(), op.getId()
			};
			DBConnector.insert(con, DBConnector.createInsert(tableName, vals, colNames), false);
			//}
		}
		catch (Exception e) {
			con2.rollback();
			con.rollback();
			throw new SQLException(e);
		}
		finally {
			try {
				con.commit();
				con2.commit();
				con.setAutoCommit(true);
				con.setAutoCommit(true);
			}
			catch (SQLException e) {
				throw e;
			}
			finally {
				try {
					con.close();
					con2.close();
				}
				catch (SQLException e) {}
			}
		}
	}


	private static DeviceNode[] getOtherDevNodes(Operation op) {
		DeviceNode[] devNodes = new DeviceNode[op.getGroupNode().size() -1];
		Iterator<String> i = op.getGroupNode().keySet().iterator();
		int j = 0;
		while (i.hasNext()) {
			DeviceNode node = op.getGroupNode().get(i.next());
			if(node.getDeviceId() != op.getDeviceNode().getDeviceId()) {
				devNodes[j] = node;
				j ++;
			}
		}
		return devNodes;
	}
	
	static Object load (String s) throws Exception {
		byte[] b = Huffman.decode(Base64Coder.decode(s.toCharArray()), null);
		ByteArrayInputStream bin = new ByteArrayInputStream(b);
		ObjectInputStream in = new ObjectInputStream(bin);
		return in.readObject();
	}

	private static String save(Object obj) throws Exception {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bout);
		out.writeObject(obj);
		return new String(Base64Coder.encode(Huffman.encode(bout.toByteArray(), null)));
	}
/*
	public static Object getNewPKValue(String dsn, Operation op, int index) throws SQLException {
		Connection con = null;
		try {
			con = DBConnector.lookup(dsn);
			SchemaName schemaName = new SchemaName(new Identifier(op.getSchemaName()));
			TableName tableName = new TableName(schemaName, new Identifier(op.getTableName()));
			ColumnSpec[] specs= new ColumnSpec[]{new ColumnSpec(tableName, new Identifier(op.getPrimaryKeys()[index]))};
			Rows rows = DBConnector.select(con, DBConnector.createSelect().addFromClause(tableName).addColumns(specs).addDerived(JSONMessageProcessor.MAX_FUNCTION).addSelectColumnFunction(JSONMessageProcessor.MAX_FUNCTION));
			Object obj = rows.get(0).get(JSONMessageProcessor.MAX_FUNCTION);
			if(obj == null) {
				return 0;
			}
			else {
				if(obj instanceof Integer) {
					return ((int) obj) + 1;
				}
				if(obj instanceof BigDecimal) {
					return ((BigDecimal) obj).add(BigDecimal.ONE);
				}
				if(obj instanceof Timestamp) {
					new Timestamp(((Timestamp) obj).getTime() + 1);
				}
				if(obj instanceof String) {
					return new String (new char[]{(char) (((String) obj).charAt(0) + 1)}) + ((String) obj).substring(1);
				}
				throw new SQLException(obj.getClass() + " is not supported as primary key");
			}
		} catch (SQLException e) {
			throw e;
		}
		finally {
			try {con.close();}catch(SQLException e){}
		}
	}
*/
	public static int findAffectedOperation(Operation op) {
		JSONMessageProcessor mp = op.getMessageProcessor();
		while(mp.sync_responses.hasNext()) {
			mp.sync_responses.next();
			if(mp.sync_responses.table_name.equals(op.getTableName()) && (mp.sync_responses.sync_id == op.getId())) {
				return mp.sync_responses.id;
			}
		}
		return -1;
	}

	public static void updateWaitingToPending(Operation op) throws SQLException {
		Connection con = null;
		try {
			con = DBConnector.lookup(dsn);
			TableName tableName = new TableName(new Identifier(JSONMessageProcessor.SYNC_RESPONSES.class.getSimpleName()));
			String[] cols = {JSONMessageProcessor.SYNC_RESPONSES.STATUS};
			Object[] values = {JSONMessageProcessor.SYNC_RESPONSES.PENDING};
			Object com1 = new CompPred (
					new Object[]{new Identifier(JSONMessageProcessor.UNIVERSAL_ID)},
					new Object[]{op.getMessageProcessor().sync_responses.id},
					Predicate.EQUAL);
			DBConnector.update(con, DBConnector.createUpdate(tableName, cols, values, new WhereClause(com1)));
		}
		catch(SQLException e) {
			throw e;
		}
		finally {
			con.close();
		}
	}
	
}
