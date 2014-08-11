package com.acepricot.finance.sync;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import java.util.Properties;

import javax.xml.bind.DatatypeConverter;

import org.pabk.util.Base64Coder;
import org.pabk.util.Huffman;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.acepricot.finance.sync.share.AppConst;
import com.acepricot.finance.sync.share.JSONMessage;
import com.acepricot.finance.sync.share.sql.CompPred;
import com.acepricot.finance.sync.share.sql.Identifier;
import com.acepricot.finance.sync.share.sql.Predicate;
import com.acepricot.finance.sync.share.sql.Query;
import com.acepricot.finance.sync.share.sql.TableName;
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

	
	
	
	public class UPLOADED_FILES extends TableSchema {
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
	
	public class DOWNLOADED_FILES extends TableSchema {
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
	
	private static final class REGISTERED_GROUPS {
		//private static final String TABLE_NAME = "REGISTERED_GROUPS";
		//private static final String ID = UNIVERSAL_ID;
		private static final String GROUP_NAME = "GROUP_NAME";
		private static final String PASSWORD = "PASSWORD";
		private static final String EMAIL = "EMAIL";
		private static final String DB_VERSION = "DB_VERSION";
		//private static final String ENABLED = UNIVERSAL_ENABLED;
		//public static final String[] COLS = {ID,GROUP_NANE,PASSWORD,EMAIL,DB_VERSION,ENABLED};
	}
	
	private static final class REGISTERED_DEVICES {
		//private static final String TABLE_NAME = "REGISTERED_GROUPS";
		private static final String ID = UNIVERSAL_ID;
		private static final String GROUP_ID = "GROUP_ID";
		private static final String DEVICE_NAME = "DEVICE_NAME";
		private static final String PRIMARY_DEVICE = "PRIMARY_DEVICE";
		private static final String ENABLED = UNIVERSAL_ENABLED;
		//public static final String[] COLS = {ID,GROUP_ID,DEVICE_NAME,PRIMARY_DEVICE,ENABLED};
	}
	
	
	//private static Connection con;
	private static String dsn;
	private static Properties pro;
	
	private UPLOADED_FILES uploaded_files = new UPLOADED_FILES();
	private DOWNLOADED_FILES downloaded_files = new DOWNLOADED_FILES();
				
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
			} catch (SQLException e) {e.printStackTrace();}
		}
	}
	
	@SuppressWarnings("unused")
	private static final JSONMessage initSync(JSONMessageProcessor mp, Connection con, JSONMessage msg) {
		int grpId, devId;
		String grpName, devName, table;
		ArrayList<HashMap<String, Object>> rows;
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
						break;
					case JSONMessageProcessor.UPLOADED_FILES.STATUS_SYNC_ENABLED:
						long l = SyncEngine.nodeStop(grpId);
						msg = prepareDownload(con, mp.uploaded_files.tmp_file, msg);
						SyncEngine.nodeStart(grpId);
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
			try {
				tableName = new TableName(new Identifier(JSONMessageProcessor.DOWNLOADED_FILES.class.getSimpleName()));
				disable(con, tableName, fileId);
				delete(con, tableName, fileId);
				if(last) {
					return msg;
				}
			}
			catch (SQLException e2) {
				e1 = e2;
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
		SyncEngine.addEngine(getEnginesPropertiesFilename(mp.uploaded_files.group_id));
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
		FileOutputStream out = new FileOutputStream(getEnginesPropertiesFilename(mp.uploaded_files.group_id));
		pro.storeToXML(out, "propeties for engine id " + mp.uploaded_files.group_id, DEFAULT_CHARSET);
		setPassword(pro, H2_PASSWORD);
		String dsn = pro.getProperty(DBConnector.DB_DSN_KEY);
		DBConnector.bind(pro, dsn);
		Connection con2 = DBConnector.lookup(dsn);
		con2.close();
		con2 = null;
		DBConnector.unbind(dsn);
		return msg.returnOK();
	}
	
	private static void setPassword(Properties pro, CharSequence pass) throws IOException {
		try {
			pro.setProperty(DBConnector.DB_PASSWORD_KEY, Huffman.decode(Base64Coder.decodeString(pass.toString()), null));
		} catch (Exception e) {
			throw new IOException(e);
		}
	}
	
	private static final String getEnginesPropertiesFilename(int grpId) {
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
	 * 3 - TableSchema
	 * 4 - WhereClause
	 * 5 - unique
	 */
	
	static final JSONMessage retrieveRows2(Object ... o) {
		try {
			Query select = DBConnector.createSelect();
			select.addFromClause(new Identifier(o[3].getClass().getSimpleName()));
			select.addTableSpec((WhereClause) o[4]);
			Rows rows = DBConnector.select((Connection) o[2], select);
			if(rows.size() == 0) {
				throw new SQLException(AppError.getMessage(AppError.RECORD_NOT_FOUND));
			}
			if((boolean) o[5]) {
				if(rows.size() != 1) {
					throw new SQLException(AppError.getMessage(AppError.MULTIPLE_RECORD_FOUND));
				}
			}
			TableSchema schema = (TableSchema) o[0].getClass().getDeclaredField(o[3].getClass().getSimpleName().toLowerCase()).get(o[0]);
			schema.rows = rows;
			schema.index = -1;
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
}
