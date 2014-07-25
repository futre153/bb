package com.acepricot.finance.sync;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.acepricot.finance.sync.share.JSONMessage;

final class JSONMessageProcessor {
	
	final static Logger logger = LoggerFactory.getLogger(JSONMessageProcessor.class);
	
	private static final String UNIVERSAL_ID = "ID";
	private static final String UNIVERSAL_ENABLED = "ENABLED";
	private static final int ID_INDEX = 0;
	private static final int OBJECT_INDEX = 1;
	private static final String DEFAULT_DIGEST_ALGORITHM = "SHA-256";
	private static final String TEMP_DIR = "D:\\TEMP\\acetmpdir";
	private static final int MAX_READED_BYTES = 0x1000;

	private static final String DB_PATH = "D:\\TEMP\\clientdb%s%s%s";
	private static final String DB_EXTENSION = ".h2.db";
	
	
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
		public static final int STATUS_DB_FILE_CREATED = 3;
		private static final int STATUS_SYNC_ENABLED = 100;
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
	
	private UPLOADED_FILES uploaded_files = new UPLOADED_FILES();
				
	private JSONMessageProcessor() throws IOException{
		if(dsn == null) {
			try {
				Properties pro = new Properties();
				InputStream in = JSONMessageProcessor.class.getResourceAsStream("/com/acepricot/finance/sync/db_aceserver.xml");
				pro.loadFromXML(in);
				//System.out.println(pro);
				in.close();
				dsn = pro.getProperty(DBConnector.DB_DSN_KEY);
				DBConnector.bind(pro, dsn);
			}
			catch(Exception e) {
				e.printStackTrace();
				throw new IOException(e);
			}
			try {
				Connection con = DBConnector.lookup(dsn);
				con.setAutoCommit(false);
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
				con.close();
			} catch (SQLException e) {}
		}
	}
	
	@SuppressWarnings("unused")
	private static final JSONMessage initSync(JSONMessageProcessor mp, Connection con, JSONMessage msg) {
		int grpId;
		String grpName, table;
		Object[] where;
		Where w = new Where();
		ArrayList<HashMap<String, Object>> rows;
		grpName = (String) msg.getBody()[0];
		if(!login(mp, con, msg).isError()) {
			try {
				grpId = JSONMessageProcessor.getGroupId(con, grpName);
				table = JSONMessageProcessor.UPLOADED_FILES.class.getSimpleName();
				where = w.set(w.equ(JSONMessageProcessor.UPLOADED_FILES.GROUP_ID, grpId));
				msg = retrieveRows(mp, msg, con, mp.uploaded_files, (char) 0, where, true);
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
						createDBFile(mp, con);
					case JSONMessageProcessor.UPLOADED_FILES.STATUS_DB_FILE_CREATED:
						
						
					default:
						
					}
				}
				else {
					return msg.sendAppError(AppError.getMessage(AppError.FILE_INSERT_NOT_FOUND, grpId));
				}
			}
			catch(SQLException | IOException e) {
				return msg.sendAppError(e);
			}
		}
		return msg;
	}
	
	private static void createDBFile(JSONMessageProcessor mp, Connection con) throws IOException, SQLException {
		String dbFilename = String.format(DB_PATH, System.getProperty("file.separator"), Integer.toBinaryString(mp.uploaded_files.group_id), DB_EXTENSION);
		String table = JSONMessageProcessor.UPLOADED_FILES.class.getSimpleName();
		String[] cols = {JSONMessageProcessor.UPLOADED_FILES.TMP_FILE, JSONMessageProcessor.UPLOADED_FILES.STATUS};
		String[] values = {dbFilename, Integer.toString(JSONMessageProcessor.UPLOADED_FILES.STATUS_DB_FILE_CREATED)};
		Where w = new Where();
		Object[] where = w.set(w.equ(JSONMessageProcessor.UPLOADED_FILES.GROUP_ID, mp.uploaded_files.group_id));
		try {
			DBConnector.update(con, table, cols, values, where, false);
			File dbFile = new File(dbFilename);
			if(dbFile.exists()) {
				if(!dbFile.delete()) {
					throw new IOException("Failed to remove previous DB file");
				}
			}
			if(!(new File(mp.uploaded_files.tmp_file).renameTo(dbFile))) {
				throw new IOException("Failed to move DB file");
			}
			
		} catch (SQLException | IOException e) {
			con.rollback();
			throw (e);
		} finally {
			con.commit();
		}
		
	}

	private static final JSONMessage retrieveRows(Object ... o) {
		try {
			Class<?> _class = o[3].getClass();
			String table = _class.getSimpleName();
			Rows rows = DBConnector.select((Connection) o[2], table, null, (char) o[4], (Object[]) o[5]);
			if(rows.size() == 0) {
				throw new SQLException(AppError.getMessage(AppError.RECORD_NOT_FOUND));
			}
			if((boolean) o[6]) {
				if(rows.size() != 1) {
					throw new SQLException(AppError.getMessage(AppError.MULTIPLE_RECORD_FOUND));
				}
			}
			TableSchema schema = (TableSchema) o[0].getClass().getDeclaredField(_class.getSimpleName().toLowerCase()).get(o[0]);
			schema.rows = rows;
			schema.index = -1;
			return ((JSONMessage) o[1]).returnOK();
		} catch (SQLException | IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			return ((JSONMessage) o[1]).sendAppError(e);
		}
	}
	
	
	@SuppressWarnings("unused")
	private static final JSONMessage upload(JSONMessageProcessor mp, Connection con, JSONMessage msg) {
		String reqId, reqUri, table, digest, digestAlgorithm, tmpPath;
		int id, status, uri, grpId, enabled, mdIndex = -1;
		Where w;
		Object[] where;
		Rows rows;
		File tmpFile;
		MessageDigestSerializer mds;
		String[] cols, values;
		boolean last;
		InputStream in;
		reqId = (String) msg.getBody()[0];
		reqUri = (String) msg.getBody()[1];
		try {
			in = (InputStream) msg.getBody()[2];
			last = (boolean) msg.getBody()[3];
			
			table = JSONMessageProcessor.UPLOADED_FILES.class.getSimpleName();
			w = new Where();
			//System.out.println(Integer.parseInt(reqId, 2) + "," + Integer.parseInt(reqUri, 2));
			where = w.set(w.and(w.equ(UNIVERSAL_ID, Integer.parseInt(reqId, 2)), w.equ(JSONMessageProcessor.UPLOADED_FILES.URI, Integer.parseInt(reqUri, 2))));
			rows = DBConnector.select(con, table, null, '"', where);
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
				values = new String[] {Integer.toString(status + 1), tmpPath, Integer.toString(mdIndex)};
				DBConnector.update(con, table, cols, values, where);
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
					values = new String[] {Integer.toString(status + 1), "-1"};
					DBConnector.update(con, table, cols, values, where);
					status ++;
					return msg.returnOK();
				}
				else {
					uri = (int) (Math.random() * Integer.MAX_VALUE);
					cols = new String[] {JSONMessageProcessor.UPLOADED_FILES.URI};
					values = new String[] {Integer.toString(uri)};
					DBConnector.update(con, table, cols, values, where);
					return msg.returnOK(id, uri);
				}
			default:
				return msg.sendAppError("File is allready uploaded");
			}
		}
		catch(Exception e) {
			try {	
				table = JSONMessageProcessor.UPLOADED_FILES.class.getSimpleName();
				w = new Where();
				where = w.set(w.and(w.equ(UNIVERSAL_ID, Integer.parseInt(reqId, 2)), w.equ(JSONMessageProcessor.UPLOADED_FILES.URI, Integer.parseInt(reqUri, 2))));
				cols = new String[] {JSONMessageProcessor.UPLOADED_FILES.STATUS, JSONMessageProcessor.UPLOADED_FILES.MD_INDEX};
				values = new String[] {"0", "-1"};
				if(mdIndex >= 0) {
					MessageDigestSerializer.reset(mdIndex);
				}
				DBConnector.update(con, table, cols, values, where);
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
			String table = JSONMessageProcessor.UPLOADED_FILES.class.getSimpleName();
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
			try {
				JSONMessageProcessor.insertRow(con, table, indexes, cols, values, where, true);
			} catch (SQLException | IOException e) {
				return msg.sendAppError(e);
			}
			col = JSONMessageProcessor.UPLOADED_FILES.URI;
			indexes = null;
			indexes = new int[]{AppError.DUPLICATE_FILE_INSERT, AppError.MULTIPLE_FILE_INSERT, AppError.FILE_INSERT_NOT_ENABLED};
			Object[] uri = null;
			try {
				uri = JSONMessageProcessor.getObject(con, table, indexes, col, grpName, where, true);
			} catch (SQLException | IOException e) {
				return msg.sendAppError(e);
			}
			return msg.returnOK(uri[0], uri[1]);
		}
		return msg;
	}
	
	private static final int getGroupId(Connection con, String grpName) throws SQLException, IOException {
		String table = JSONMessageProcessor.REGISTERED_GROUPS.class.getSimpleName();
		String col = JSONMessageProcessor.REGISTERED_GROUPS.GROUP_NAME;
		Where w = new Where();
		Object[] where = w.set(w.equ(col, grpName));
		int[] indexes = {AppError.GROUP_NOT_REGISTERED, AppError.GROUP_MULTIPLE_REGISTRATION, AppError.GROUP_NOT_ENABLED};
		return (Integer) JSONMessageProcessor.getObject(con, table, indexes, col, grpName, where, true)[ID_INDEX];
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
			String table = JSONMessageProcessor.REGISTERED_GROUPS.class.getSimpleName();
			String col = JSONMessageProcessor.REGISTERED_GROUPS.GROUP_NAME;
			Where w = new Where();
			Object[] where = w.set(w.equ(col, grpName));
			int[] indexes = {AppError.GROUP_NOT_REGISTERED, AppError.GROUP_MULTIPLE_REGISTRATION, AppError.GROUP_NOT_ENABLED};
			int grpId;
			try {
				grpId = (Integer) JSONMessageProcessor.getObject(con, table, indexes, col, grpName, where, true)[ID_INDEX];
			} catch (SQLException | IOException e) {
				return msg.sendAppError(e);
			}
			table = REGISTERED_DEVICES.class.getSimpleName();
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
				JSONMessageProcessor.insertRow(con, table, indexes, cols, values, where, true);
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
		String table = REGISTERED_DEVICES.class.getSimpleName();
		String grpIdCol = JSONMessageProcessor.REGISTERED_DEVICES.GROUP_ID;
		String enabledCol = JSONMessageProcessor.REGISTERED_DEVICES.ENABLED;
		String primDevCol = JSONMessageProcessor.REGISTERED_DEVICES.PRIMARY_DEVICE;
		String idCol = JSONMessageProcessor.REGISTERED_DEVICES.ID;
		String devNameCol = JSONMessageProcessor.REGISTERED_DEVICES.DEVICE_NAME;
		Where w = new Where();
		Object[] where = w.set(w.and(w.equ(grpIdCol, grpId), w.equ(enabledCol, 1), w.equ(primDevCol, 1))); 
		String[] cols = {JSONMessageProcessor.REGISTERED_DEVICES.PRIMARY_DEVICE}, values;
		int count = DBConnector.count(con, table, where);
		switch(count) {
		case 0:
			w.clear();
			if(devName == null) {
				int[] indexes = {AppError.NO_ENABLED_DEVICES, 0, 0};
				String col = JSONMessageProcessor.REGISTERED_DEVICES.DEVICE_NAME;
				where = w.set(w.and(w.equ(grpIdCol, grpId), w.equ(enabledCol, 1)));
				int devId = (int) JSONMessageProcessor.getObject(con, table, indexes, col, devName, where, false)[ID_INDEX];
				w.clear();
				where = w.set(w.equ(idCol, devId));
			}
			else {
				//where = new Object[]{"(" + grpIdCol + " = ?) AND (" + devNameCol + " = ?)", grpId, devName};
				where = w.set(w.and(w.equ(grpIdCol, grpId), w.equ(devNameCol, devName)));
			}
			values = new String[]{"1"};
			int status = DBConnector.update(con, table, cols, values, where);
			if(status != 1) {
				throw new IOException (AppError.getMessage(AppError.PRIMARY_KEY_UNEXPECTED_ERROR, status));
			}
			break;
		case 1:
			break;
		default:
			w.clear();
			where = w.set(w.equ(grpIdCol, grpId));
			values = new String[]{"0"};
			DBConnector.update(con, table, cols, values, where);
			JSONMessageProcessor.setPrimaryDevice(con, grpId, devName);
		}
	}
	
	
	@SuppressWarnings("unused")
	private static final JSONMessage register(JSONMessageProcessor mp, Connection con, JSONMessage msg) {
		String grpName = (String) msg.getBody()[0];
		String hash = JSONMessageProcessor.constructHexHash((ArrayList<?>) msg.getBody()[OBJECT_INDEX]);
		String email = (String) msg.getBody()[2];
		double dbVersion = ((double) msg.getBody()[3]);
		String table = JSONMessageProcessor.REGISTERED_GROUPS.class.getSimpleName();
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
			JSONMessageProcessor.insertRow(con, table, indexes, cols, values, where, true);
		} catch (SQLException | IOException e) {
			return msg.sendAppError(e);
		}
		return msg.returnOK();
	}
		
	private static final JSONMessage login(JSONMessageProcessor mp, Connection con, JSONMessage msg) {
		String grpName = (String) msg.getBody()[0];
		String hash = JSONMessageProcessor.constructHexHash((ArrayList<?>) msg.getBody()[1]);
		String table = JSONMessageProcessor.REGISTERED_GROUPS.class.getSimpleName();
		Where w = new Where();
		Object[] where = w.set(w.equ(JSONMessageProcessor.REGISTERED_GROUPS.GROUP_NAME, grpName));
		//where.add(where.equ(JSONMessageProcessor.REGISTERED_GROUPS.GROUP_NANE, grpName));
		int[] indexes = {AppError.GROUP_NOT_REGISTERED, AppError.GROUP_MULTIPLE_REGISTRATION, AppError.GROUP_NOT_ENABLED}; 
		String col = JSONMessageProcessor.REGISTERED_GROUPS.PASSWORD;
		String hash2;
		try {
			hash2 = (String) JSONMessageProcessor.getObject(con, table, indexes, col, col, where, true)[1];
		} catch (SQLException | IOException e) {
			return msg.sendAppError(e);
		}
		if(!hash.equals(hash2)) {
			return msg.sendAppError(AppError.getMessage(AppError.LOGIN_FAILED, grpName));
		}
		return msg.returnOK();
	}
	
	private static void insertRow(Connection con, String table, int[] index, String[] cols, String[] values, Object[] where, boolean unique) throws SQLException, IOException {
		if(unique) {
			int count = 0;
			count = DBConnector.count(con, table, where);
			if(count == 1) {
				throw new IOException (AppError.getMessage(index[1], values[index[0]]));
			}
			if(count > 1) {
				throw new IOException (AppError.getMessage(index[2], values[index[0]]));
			}
		}
		int status = DBConnector.insert(con, table, cols, values);
		if(status != 1) {
			throw new IOException (AppError.getMessage(index[3], values[index[0]]));
		}
	}
	
	private static Object[] getObject (Connection con, String table, int[] index, String col, String value, Object[] where, boolean unique) throws SQLException, IOException {
		int count = 0;
		count = DBConnector.count(con, table, where);
		if(count == 0) {
			throw new IOException(AppError.getMessage(index[1], value));
		}
		if(count > 1 && unique) {
			throw new IOException(AppError.getMessage(index[2], value));
		}
		HashMap<String, Object> row = DBConnector.getFirstRowOf(con, table, '"', new String[]{UNIVERSAL_ID, col, UNIVERSAL_ENABLED}, where);
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
