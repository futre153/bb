package org.acepricot.finance.server.ws;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Hashtable;
import java.util.InvalidPropertiesFormatException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.acepricot.ber.BERIOStream;
import org.acepricot.finance.server.db.DBConnector;
import org.acepricot.finance.server.exc.MsgProcException;
import org.acepricot.finance.web.msgs.AceData;
import org.acepricot.finance.web.msgs.AceDataItemImpl;
import org.acepricot.finance.web.msgs.ErrorResponse;
import org.acepricot.finance.web.msgs.RegistrationRequestResponse;
import org.acepricot.finance.web.msgs.SyncConfRequestResponse;
import org.acepricot.finance.web.util.MyBoolean;
import org.acepricot.finance.web.util.OidRepository;
import org.acepricot.finance.web.util.Sys;
import org.apache.axis2.AxisFault;


public class MessageProcessor {
	
	private static final String DEFAULT_TEXT_ENCODING 		= "hex";
	private static final String REFERENCE_ENCODING_ALGORITHM= "AES/ECB/PKCS5Padding";
	private static final String BASE64_TEXT_ENCODING 		= "base64";
	private static final String NONE_TEXT_ENCODING 			= "none";
	private static final String NULL_MESSAGE_ID 			= "Message action ID shall be mandatory";
	private static final String NULL_ID			 			= "Message ID shall be mandatory";
	private static final String DEFAULT_ENCODING_ALGORITHM 	= "AES/ECB/PKCS5Padding";
	private static final String DEFAULT_DIGEST_ALGORITHM 	= "SHA-256";
	private String id;
	
	private static final long MAX_TIME_DELAY = 1000*60*10;
	private static final String MESSAGE_OUT_OF_TIME = "Message is out of allowed time interval";
	private static final String MESSAGE_DIGEST_MISHMATCH = "Security violation: message digest is not correct";
	
	//server properties
	private static final long MAX_GROUP_ON_THIS_SERVER = 0;
	private static final int MAX_DEVICES_ON_GROUP = 100;

	private static final String REGISTERED_GROUPS = "REGISTERED_SUBJECTS";
	private static final String REGISTERED_GROUPS_ID = "ID";
	private static final String REGISTERED_GROUPS_GROUP_ID = "GROUP_ID";
	private static final String REGISTERED_GROUPS_EMAIL = "EMAIL";
	private static final String REGISTERED_GROUPS_PASSWORD = "PASSWORD";
	private static final String REGISTERED_GROUPS_ENABLED = "ENABLED";
	private static final String REGISTERED_GROUPS_DB_VERSION = "DB_VERSION";
	private static final String[] REGISTERED_GROUPS_COLS = {
		REGISTERED_GROUPS_ID,
		REGISTERED_GROUPS_GROUP_ID,
		REGISTERED_GROUPS_PASSWORD,
		REGISTERED_GROUPS_EMAIL,
		REGISTERED_GROUPS_DB_VERSION,
		REGISTERED_GROUPS_ENABLED
	};
	/*
	private static final String REGISTERED_SUBJECTS = "REGISTERED_SUBJECTS";
	private static final String REGISTERED_SUBJECTS_ID = "ID";
	private static final String REGISTERED_SUBJECTS_EMAIL = "EMAIL";
	private static final String REGISTERED_SUBJECTS_PASSWORD = "PASSWORD";
	private static final String REGISTERED_SUBJECTS_ENABLED = "ENABLED";
	private static final String REGISTERED_SUBJECTS_DB_VERSION = "DB_VERSION";
	private static final String[] REGISTERED_SUBJECTS_COLS = {
		REGISTERED_SUBJECTS_ID,
		REGISTERED_SUBJECTS_EMAIL,
		REGISTERED_SUBJECTS_PASSWORD,
		REGISTERED_SUBJECTS_ENABLED,
		REGISTERED_SUBJECTS_DB_VERSION
	};
	*/
	
	
	private static final String ERROR_MESSAGE_TYPE = "Undefined message type";
	
	
	
	
	

	private static final String REGISTERED_DEVICES = "REGISTERED_DEVICES";
	private static final String REGISTERED_DEVICES_ID = "ID";
	private static final String REGISTERED_DEVICES_GROUP_ID = "GROUP_ID";
	private static final String REGISTERED_DEVICES_DEVICE_ID = "DEVICE_ID"; 
	private static final String REGISTERED_DEVICES_PRIMARY_DEVICE = "PRIMARY_DEVICE";
	private static final String REGISTERED_DEVICES_DEVICE_NAME = "DEVICE_NAME";
	private static final String REGISTERED_DEVICES_ENABLED = "ENABLED";
	
	private static final String[] REGISTERED_DEVICES_COLS = {
		REGISTERED_DEVICES,
		REGISTERED_DEVICES_ID,
		REGISTERED_DEVICES_GROUP_ID,
		REGISTERED_DEVICES_DEVICE_ID,
		REGISTERED_DEVICES_PRIMARY_DEVICE,
		REGISTERED_DEVICES_DEVICE_NAME,
		REGISTERED_DEVICES_ENABLED
	};
	/*
	private static final String CLIENT_NODES_CLID = "CLID";
	private static final String CLIENT_NODES_SVID = "SVID";
	private static final String CLIENT_NODES_ENABLED = "ENABLED";
	private static final String CLIENT_NODES = "CLIENT_NODES";
	
	private static final String[] CLIENT_NODES_COLS = {
		CLIENT_NODES_CLID,
		CLIENT_NODES_SVID,
		CLIENT_NODES_ENABLED
	};
	*/
	@SuppressWarnings("unused")
	private static final String FILENAME_MISHMATCH = "Filename is failed to check";
	private static final String UPLOADED_FILES = "UPLOADED_FILES";
	private static final String UPLOADED_FILES_FILENAME = "FILENAME";
	private static final String UPLOADED_FILES_FILEPART_COUNT = "FILEPART_COUNT";
	private static final String UPLOADED_FILES_DIGEST_ALGORITHM = "DIGEST_ALGORITHM";
	private static final String UPLOADED_FILES_DIGEST = "DIGEST";
	private static final String UPLOADED_FILES_ENABLED = "ENABLED";
	private static final String UPLOADED_FILES_UPLOAD_ENABLED = "UPLOAD_ENABLED";
	
	private static final String[] UPLOADED_FILES_COLS = {
		UPLOADED_FILES_FILENAME,
		UPLOADED_FILES_FILEPART_COUNT,
		UPLOADED_FILES_DIGEST_ALGORITHM,
		UPLOADED_FILES_DIGEST,
		UPLOADED_FILES_ENABLED,
		UPLOADED_FILES_UPLOAD_ENABLED
	};
	
	private static final String UPLOADED_FILEPARTS = "UPLOADED_FILEPARTS";
	private static final String UPLOADED_FILEPARTS_FILENAME = "FILENAME";
	private static final String UPLOADED_FILEPARTS_FIELAPART_ID	= "FILEPART_ID";
	private static final String UPLOADED_FILEPARTS_FILEPART = "FILEPART";
	private static final String[] UPLOADED_FILEPARTS_COLS = {
		UPLOADED_FILEPARTS_FILENAME,
		UPLOADED_FILEPARTS_FIELAPART_ID,
		UPLOADED_FILEPARTS_FILEPART
	};
	
	//Error messages returned by server
	//common
	private static final String FAILED_TO_RETRIEVE_DATA =
			"Failed to retrieve data from '%s' for key '%s'";
	//login
	private static final String GROUP_ID_NOT_REGISTERED = "Group %s is not registered";
	private static final String GROUP_ID_IS_DISABLED = "Group %s is disabled";
	@SuppressWarnings("unused")
	private static final String ID_MISHMATCH = "Registered group id is failed ot check";
	@SuppressWarnings("unused")
	private static final String VERSION_DB_MISHMATCH = "DB version incorrect";
	private static final String WRONG_PASSWORD = "Incorrect password for %s";
	//register
	private static final String DUPLICATE_GROUP_ID = "Group %s is allready registered";
	private static final String SERVER_FULL = "Max groups reached on %s (%d)";
	//register device
	private static final String DEVICE_NAME_DUPLICITY = "Device name %s is allready used in goup %s. Device name must be unique behind group";
	private static final String MAX_CLIENT_NODES_REACHED = "Maximum number of devices (%d) for group %s reached";
	//request for upload file
	private static final String DEVICE_FORMAT = "%02d";
	private static final String FILENAME_FORMAT = "%010d";
	private static final String FILE_ALLREADY_UPLOADED = "File %s is allready uploaded";
	private static final String DEVICE_NOT_REGISTERED = "Device %s is not registered";
	private static final String DEVICE_IS_DISABLED = "Device %s is disabled";
	private static final String DEVICE_IS_NOT_PRIMARY = "Device %s is not primary. Only from primary device can be uploaded database file";
	//filepart upload
	private static final String FILE_NOT_AVAILABLE = "File %s not found";
	private static final String FILE_DISABLED = "File %s not available for upload";
	private static final String FILEPART_COUNT_OUT_OF_BOUNDS = "File %s: Filepart number (%d) is out of bounds (1 - %d)";
	
	
	private static final long FILEPART_LENGTH = 1024*16;
	
	private static final String FILEPART_ADDED = "Filepart added";
	private static final String FILEPART_UPDATED = "Filepart updated";
	@SuppressWarnings("unused")
	private static final String FILEPART_ID_OUT_OF_RANGE = "Filepar ID is out of range";
	private static final String FILE_NOT_CREATED = "Failed to create file %s";
	private static final String FILE_NOT_DELETED = "Delete file %s failed" ;
	@SuppressWarnings("unused")
	private static final String NO_ACTIVE_CLIENT_NODE = "No active client node for this server node";
	
	//private static final String CLIENT_DB_PATH = "D:\\Temp\\clientdb";
	private static final String CLIENT_DB_PATH = "/opt/h2/db";
	
	private static final String H2_DB_EXTENSION = ".h2.db";
	private static final String FILE_DIGEST_CHECK_FAILED = "Digest of DB file %s is failed to check";
	private static final String FILE_CREATE_ERROR = "I/O error while file %s creation";
	@SuppressWarnings("unused")
	private static final String NO_SERVER_NODE = "No server node for synchronization";
	
	private static final String H2_DB_DRIVER = "org.h2.Driver";
	
	//private static final String SERVER_DB_URL = "jdbc:h2:tcp://%s/%s;AUTO_SERVER=TRUE;LOCK_TIMEOUT=60000;CIPHER=AES";
	private static final String SERVER_DB_URL = "jdbc:h2://%s/%s;AUTO_RECONNECT=TRUE;AUTO_SERVER=TRUE;LOCK_TIMEOUT=60000;CIPHER=AES";
	
	private static final String UNKNOWN_INPUT_VALUE = "%s";
	private static final int ROUTING_JOB_INTERVAL = 2500;
	private static final int PUSH_JOB_INTERVAL = 5000;
	private static final int PULL_JOB_INTERVAL = 5000;
	private static final int SERVER_ROUTING_JOB_INTERVAL = 1000;
	private static final int SERVER_PUSH_JOB_INTERVAL = 2000;
	private static final int SERVER_PULL_JOB_INTERVAL = 2000;
	
	//private static final String SYMMETRIC_URL = "http://localhost:%s/sync/%s";
	private static final String SYMMETRIC_URL = "http://23.239.26.122:%s/sync/%s";
	
	private static final String ENGINE_NAME = "engine.name=%s";
	private static final String DB_DRIVER = "db.driver=%s";
	private static final String DB_URL = "db.url=%s";
	private static final String DB_USER = "db.user=%s";
	private static final String DB_PASSWORD = "db.password=%s";
	private static final String GROUP_ID = "group.id=%s";
	private static final String EXTERNAL_ID = "external.id=%s";
	private static final String REGISTRATION_URL = "registration.url=%s";
	private static final String ROUTING_PERIOD = "job.routing.period.time.ms=%d";
	private static final String PUSH_PERIOD = "job.push.period.time.ms=%d";
	private static final String PULL_PERIOD = "job.pull.period.time.ms=%d";
	private static final String ENGINE_FILENAME = "%s.properties";
	private static final String TEXT_ENCODING_ERROR = "Failed to text encode response data";
	private static final String CLIENT_USER = "";
	private static final String CLIENT_PASSWORD = "cnuewf092no ptraajtn39ln";
	
	private static final String SYMMETRIC_PORT = "31415";
	
	private static final String PURGE_PERIOD = "job.purge.period.time.ms=%d";
	private static final int PURGE_PERIOD_INTERVAL = 7200000;
	
	//private static final String SYMMETRIC_HOME = "D:\\Programy\\BB\\corp\\symmetric-3.5.13";
	private static final String SYMMETRIC_HOME = "/opt/symmetric-ds";
	
	private static final String SYMMETRIC_ENGINE_PATH = "engines";
	private static final String ENGINE_EXISTS = "Engine exists";
	
	//private static final String CLIENT_DB_URL = "localhost/D:/Temp/clientdb";
	private static final String CLIENT_DB_URL = "/opt/h2/db";
	
	private static final String SYMMENTRIC_BIN = "bin";
	
	//private static final String SYMMETRIC_ADMIN = "symadmin.bat";
	private static final String SYMMETRIC_ADMIN = "symadmin";
	
	private static final String ACCOUNTS = "ACCOUNTS";
	private static final String CATEGORIES = "CATEGORIES";
	private static final String CATEGORY_BUDGETS = "CATEGORY_BUDGETS";
	private static final String CONFIG = "CONFIG";
	private static final String EXCHANGE_RATES = "EXCHANGE_RATES";
	private static final String PAYEES = "PAYEES";
	private static final String RECURRENT_TRANSACTIONS = "RECURRENT_TRANSACTIONS";
	private static final String RECURRENT_TRANSACTIONS_EXCEPTIONS = "RECURRENT_TRANSACTIONS_EXCEPTIONS";
	private static final String SUBCATEGORIES = "SUBCATEGORIES";
	private static final String SUBCATEGORY_BUDGETS = "SUBCATEGORY_BUDGETS";
	private static final String TRANSACTIONS = "TRANSACTIONS";
	
	@SuppressWarnings("unused")
	private static final String[] DB_COLS = {
		ACCOUNTS,
		CATEGORIES,
		CATEGORY_BUDGETS,
		CONFIG,
		EXCHANGE_RATES,
		PAYEES,
		RECURRENT_TRANSACTIONS,
		RECURRENT_TRANSACTIONS_EXCEPTIONS,
		SUBCATEGORIES,
		SUBCATEGORY_BUDGETS,
		TRANSACTIONS
	};
	
	private static final String DB_SCHEMA = "USER";
	private static final String TRIGGER_PREFIX = "T_";
	private static final String DEAD_EXT = "_DEAD";
	
	//private static Process PROCS = null;
	//private static SymmetricWebServer server = null; 
	private static boolean started = true;
	
	
 	private static final String[] SERVER_NODES = {
 		"--",
		"-- Nodes",
		"--",
		"delete from sym_node_group_link;",
		"delete from sym_node_group;",
		"delete from sym_node_identity;",
		"delete from sym_node_security;",
		"delete from sym_node;",
		"",
		"insert into sym_node_group (node_group_id, description)", 
		"values ('%1$s', 'server node');",
		"insert into sym_node_group (node_group_id, description)", 
		"values ('%4$s', 'End user interfaces');",
		"",
		"insert into sym_node_group_link (source_node_group_id, target_node_group_id, data_event_action)",
		"values ('%4$s', '%1$s', 'P');",
		"insert into sym_node_group_link (source_node_group_id, target_node_group_id, data_event_action)",
		"values ('%1$s', '%4$s', 'W');",

		"",
		"insert into sym_node (node_id, node_group_id, external_id, sync_enabled)",
		"values ('%2$s', '%1$s', '%3$s', 1);",
		"insert into sym_node_security (node_id,node_password,registration_enabled,registration_time,initial_load_enabled,initial_load_time,initial_load_id,initial_load_create_by,rev_initial_load_enabled,rev_initial_load_time,rev_initial_load_id,rev_initial_load_create_by,created_at_node_id)", 
		"values ('%2$s','changeme',0,current_timestamp,0,current_timestamp,null,null,0,null,null,null,'%2$s');",
		"insert into sym_node_identity values ('%2$s');",
		"",
		"--",
		"-- Channels",
		"--",
		"-- %4$s",
		"insert into sym_channel", 
		"(channel_id, processing_order, max_batch_size, enabled, description)",
		"values('CH0001', 1, 100000, 1, 'Default channel');",
		"",
		"--",
		"-- Routers",
		"--",
		"insert into sym_router", 
		"(router_id,source_node_group_id,target_node_group_id,router_type,create_time,last_update_time)",
		"values('RT%1$s2%4$s', '%1$s', '%4$s', 'default',current_timestamp, current_timestamp);",
		"",
		"insert into sym_router", 
		"(router_id,source_node_group_id,target_node_group_id,router_type,create_time,last_update_time)",
		"values('RT%4$s2%1$s', '%4$s', '%1$s', 'default',current_timestamp, current_timestamp);",
		"--",
		"-- Triggers",
		"--",
		"%5$s",
		"--",
		"-- Trigger Router Links",
		"--",
		"%6$s",
		"--",
		"-- Conflicts --",
		"--",
		"%7$s"
 	};
	
 	@SuppressWarnings("unused")
	private static final String[] TRIGGER = {
 		"%s",
 		"%s"
 	};
 	
 	private static final String[] TRIGGER_DEFAULT = {
 		"",
 		"insert into sym_trigger", 
		"(trigger_id,source_schema_name,source_table_name,channel_id,last_update_time,create_time,sync_on_incoming_batch)",
		"values('%s','%s','%s','%s',current_timestamp,current_timestamp,%d);"
 	};		
	
 	private static final String[] TRIGGER_DEAD = {
 		"",
 		"insert into sym_trigger", 
		"(trigger_id,source_schema_name,source_table_name,channel_id, sync_on_insert, sync_on_update, sync_on_delete, last_update_time,create_time)",
		"values('%s','%s','%s','%s',0,0,0,current_timestamp,current_timestamp);"
 	};
	
 	private static final String[] TRIGGER_LINK = {
 		"",
 		"insert into sym_trigger_router",
 		"(trigger_id,router_id,initial_load_order,ping_back_enabled,last_update_time,create_time)",
 		"values('%s','%s', %d, %d, current_timestamp, current_timestamp);"
 	};
 	
 	private static final String[] CONFLICT = {
 		"",
 		"insert into conflict",
 		"(conflict_id,source_node_group_id,target_node_group_id,target_channel_id,target_schema_name,target_table_name,detect_type,resolve_type,ping_back,last_update_time,create_time)",
 		"values('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', %d, current_timestamp, current_timestamp);"
 	};
 	
	private static final String SYMMETRIC_SAMPLES_PATH = "samples";
	private static final String SQL_FILENAME = "batch.sql";
	//private static final String SYMMETRIC_DBIMPORT = "dbimport.bat";
	private static final String SYMMETRIC_DBIMPORT = "dbimport";
	private static final String WINDOWS_SERVICE_COMMAND = "net";
	private static final String SUDO_COMMAND = "sudo";
	private static final String LINUX_SERVICE_COMMAND = "service";
	private static final String SYMMENTRIC_SERVICE_NAME = "symmetricds";
	private static final String LINUX_SERVICE_NAME = "sym_service";
	private static final String SYMMETRIC_START = "start";
	private static final String SYMMETRIC_STOP = "stop";
	private static final String WINDOWS_OS = "WINDOWS";
	private static final String LINUX_OS = "LINUX";
	private static final String UNKNOWN_OS = "Unknown OS: %s";
	@SuppressWarnings("unused")
	private static final String CLIENT_ID_NOT_EXISTS = "Client id does not exists";
	private static final String FATAL_ERROR = "Fatal error: %s";
	@SuppressWarnings("unused")
	private static final String DUPLICATE_CLIENT_ID = "Duplicate client id";
	private static final String SERVER_ENGINE_DELETION_FAILS = "Server engine deletion fails";
	private static final Object DB_FILE_DELETION_FAILS = "DB file deletion fails";
	
	@SuppressWarnings("unused")
	private static final String SERVER_ID_NOT_DEFINED = "Server id is not defined";
	private static final String ACTIVE_NODES = "Cannot delete group %s - active node/s found";
	private static final String REMOVE_NODE_LINE1 = "delete from sym_node_security where node_id='%s';";
	private static final String REMOVE_NODE_LINE2 = "delete from sym_node_host where node_id='%s';";
	private static final String REMOVE_NODE_LINE3 = "delete from sym_node_host_job_stats where node_id='%s';";
	private static final String REMOVE_NODE_LINE4 = "delete from sym_node_host_stats where node_id='%s';";
	private static final String REMOVE_NODE_LINE5 = "delete from sym_node_host_channel_stats where node_id='%s';";
	private static final String REMOVE_NODE_LINE6 = "delete from sym_node_communication where node_id='%s';";
	private static final String REMOVE_NODE_LINE7 = "delete from sym_node_channel_ctl where node_id='%s';";
	private static final String REMOVE_NODE_LINE8 = "delete from sym_node where node_id='%s';";
	private static final String REMOVE_NODE_LINE9 = "delete from sym_extract_request where node_id='%s';";
	private static final String REMOVE_NODE_LINE10 = "delete from sym_file_incoming where node_id='%s';";
	private static final String REMOVE_NODE_LINE11 = "delete from sym_incoming_batch where node_id='%s';";
	private static final String REMOVE_NODE_LINE12 = "delete from sym_incoming_error where node_id='%s';";
	private static final String REMOVE_NODE_LINE13 = "delete from sym_incoming_batch where node_id='%s';";
	
	private static final String DEFAULT_CHANNEL = "CH0001";
	private static final String CONFLICT_PREFIX = "CT_";
	private static final String DEFAULT_DETECT_TYPE = "use_pk_data";
	private static final String DEFAULT_RESOLVE_TYPE = "manual";
	private static final String TO_CLIENT = "C_";
	private static final String TO_SERVER = "S_";
	private static final String CONFIG_FILENAME = "config.filename";
	private static final String CONFIG_FILE = "config.file";
	private static final String DELETE_BATCH = "delete.batch";
	private static final String DELETE_MESSAGE = "delete.message";
	private static final String LAST_NODE = "Last node";
	private static final String DELETE_NEW_PRIMARY = "delete.newPrimary";
	private static final String PRIMARY_MOVE = "Primary device change";
	private static final String LAST_NODE_MESSAGE = "Your last device was removed from sync. If you wish to delete your group for sync please hit button 'Stop sync'";
	private static final String NOT_DEVELOPED = "Service %s not developed";
		
	private final BERIOStream enc = new BERIOStream();
	
	private static String createClientEngine(String svid, String clid) {
		String[] clientEngine = { 		
			String.format(ENGINE_NAME, clid),
			String.format(DB_DRIVER, H2_DB_DRIVER),
			String.format(DB_URL, UNKNOWN_INPUT_VALUE),
			String.format(DB_USER, UNKNOWN_INPUT_VALUE),
			String.format(DB_PASSWORD, UNKNOWN_INPUT_VALUE),
			String.format(GROUP_ID, clid.substring(0, 2) + "00" + clid.substring(4) + "-01"),
			String.format(EXTERNAL_ID, clid.substring(2)),
			String.format(REGISTRATION_URL, String.format(SYMMETRIC_URL, SYMMETRIC_PORT, svid)),
			String.format(ROUTING_PERIOD, ROUTING_JOB_INTERVAL),
			String.format(PUSH_PERIOD, PUSH_JOB_INTERVAL),
			String.format(PULL_PERIOD, PULL_JOB_INTERVAL),
		};
		return Sys.concatenate(clientEngine, System.getProperty("line.separator"));
	}
	
	
	private static String createServerEngine (String svid) {
		String[] serverEngine = {
			String.format(ENGINE_NAME, svid),
			String.format(DB_DRIVER, H2_DB_DRIVER),
			String.format(DB_URL, String.format(SERVER_DB_URL, CLIENT_DB_URL, svid)),
			String.format(DB_USER, CLIENT_USER),
			String.format(DB_PASSWORD, CLIENT_PASSWORD),
			String.format(GROUP_ID, svid + "-01"),
			String.format(EXTERNAL_ID, svid.substring(4)),
			String.format(REGISTRATION_URL, String.format(SYMMETRIC_URL, SYMMETRIC_PORT, svid)),
			String.format(PURGE_PERIOD, PURGE_PERIOD_INTERVAL),
			String.format(ROUTING_PERIOD, SERVER_ROUTING_JOB_INTERVAL),
			String.format(PUSH_PERIOD, SERVER_PUSH_JOB_INTERVAL),
			String.format(PULL_PERIOD, SERVER_PULL_JOB_INTERVAL),
		};
		return Sys.concatenate(serverEngine, System.getProperty("line.separator"));
	}
	
	private static String createRemoveNodeBatch(String clid) {
		clid = clid.substring(2);
		String [] remNode = {
			String.format(REMOVE_NODE_LINE1, clid),
			String.format(REMOVE_NODE_LINE2, clid),
			String.format(REMOVE_NODE_LINE3, clid),
			String.format(REMOVE_NODE_LINE4, clid),
			String.format(REMOVE_NODE_LINE5, clid),
			String.format(REMOVE_NODE_LINE6, clid),
			String.format(REMOVE_NODE_LINE7, clid),
			String.format(REMOVE_NODE_LINE8, clid),
			String.format(REMOVE_NODE_LINE9, clid),
			String.format(REMOVE_NODE_LINE10, clid),
			String.format(REMOVE_NODE_LINE11, clid),
			String.format(REMOVE_NODE_LINE12, clid),
			String.format(REMOVE_NODE_LINE13, clid)
		};
		return Sys.concatenate(remNode, System.getProperty("line.separator"));
	}
	
	void process(Message msg) throws AxisFault {
		AceData resp = null;
		try {
			/*if(PROCS == null) {
				run();
			}*/
			AceData data = getMessage(msg);
			if(data.getMessageType().equals(AceData.REGISTRATION_REQUEST)) {
				resp = register(data);
			}
			else if(data.getMessageType().equals(AceData.SYNCHRONOZATION_REQUEST)) {
				resp = joinDevice(data);
			}
			else if(data.getMessageType().equals(AceData.FILE_UPLOAD)) {
				resp = upload(data);
			}
			else if(data.getMessageType().equals(AceData.FILEPART_UPLOAD)) {
				resp = uploadFilepart(data);
			}
			else if(data.getMessageType().equals(AceData.SYNCHRONIZATION_CONF_REQUEST)) {
				resp = configureSync(data);
			}
			else if(data.getMessageType().equals(AceData.DEREGISTRATION_REQUEST)) {
				resp = deregister(data);
			}
			else if(data.getMessageType().equals(AceData.REMOVE_NODE)) {
				resp = removeNode(data);
			}
			else if(data.getMessageType().equals(AceData.SQL_OPERATION)) {
				resp = SQLOperation(data);
			}
			else {
				resp = new ErrorResponse(ERROR_MESSAGE_TYPE, AceData.ERROR_RESPONSE);
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			enc.setOutputStream(out);
			resp.encode(enc);
			setMessage(msg, getId(), null, null, null, resp.getMessageType(), out.toByteArray());
		}
		catch (Exception e) {
			try {
				resp = new ErrorResponse(e.getMessage(), AceData.ERROR_RESPONSE);
			}
			catch (IOException e1) {
				throw new AxisFault(e1.getMessage());
			};
		}
	}
	
	
	private static synchronized AceData SQLOperation(AceData data) throws MsgProcException {
		throw new MsgProcException(NOT_DEVELOPED, data.getMessageType());
	}
	/*
	private AceData SQLOperation(AceData data, String clid) throws SQLException, NullPointerException, ClassNotFoundException, IOException {
		DBConnector db = DBConnector.getDb();
		db.select(
				CLIENT_NODES,
				CLIENT_NODES_COLS,
				"where " + CLIENT_NODES_CLID + " = '" + clid + "' AND " + CLIENT_NODES_ENABLED + " = '" + MyBoolean.TRUE + "'");
		Hashtable<String, Object> ret;
		db.next();
		try {
			ret = db.getRowAsHashtable(CLIENT_NODES_COLS);
			db.close();
		}
		catch(SQLException e) {
			return new ErrorResponse(e.getMessage(), AceData.ERROR_RESPONSE);
		}
		String svid = (String) ret.get(CLIENT_NODES_SVID);
		if(svid == null) {
			return new ErrorResponse(SERVER_ID_NOT_DEFINED, AceData.ERROR_RESPONSE);
		}
		AceDataItemImpl item = data.get(AceData.SQL_OPERATION_HEADER);
		String operation = new String(item.getItemValue());
		item = data.get(AceData.SQL_DATA_HEADER);
		ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(item.getItemValue()));
		Hashtable<?, ?> table = (Hashtable<?, ?>) in.readObject();
		try {
			DBConnector.multiConnector(svid, operation, table);
		}
		catch (Exception e) {
			return new ErrorResponse(e.getMessage(), AceData.ERROR_RESPONSE);
		}
		return new RegistrationRequestResponse(clid, AceData.OK_RESPONSE);
	}
	*/
	private static Hashtable<String, Object> deleteNode(String grpName, String hash, String devName) throws MsgProcException {
		int grpId = (int) login(grpName, hash).get(REGISTERED_GROUPS_ID);
		Hashtable<String, Object> row = getRow (
			REGISTERED_DEVICES,
			REGISTERED_DEVICES_COLS,
			"WHERE (" + REGISTERED_DEVICES_GROUP_ID + " = '" + grpId + "') AND (" + REGISTERED_DEVICES_DEVICE_NAME + " = '" + devName + "')");
		if(row == null) {
			throw new MsgProcException(MessageProcessor.DEVICE_NOT_REGISTERED, devName);
		}
		int devId = (int) row.get(REGISTERED_DEVICES_DEVICE_ID);
		boolean primary = (boolean) row.get(REGISTERED_DEVICES_PRIMARY_DEVICE);
		delete(
			REGISTERED_DEVICES,
			"WHERE (" + REGISTERED_DEVICES_GROUP_ID + " = '" + grpId + "') AND (" + REGISTERED_DEVICES_DEVICE_ID + " = '" + devId + "')");
		String message = LAST_NODE;
		String newPrimary = LAST_NODE_MESSAGE;
		if(primary) {
			row = getRow (
				REGISTERED_DEVICES,
				new String[] {REGISTERED_DEVICES_DEVICE_ID},
				"WHERE " + REGISTERED_DEVICES_GROUP_ID + " = '" + grpId + "'");
			if(row == null) {
				row = new Hashtable<String, Object>();
			}
			else {
				devId = (int) row.get(REGISTERED_DEVICES_DEVICE_ID);
				update (
					REGISTERED_DEVICES,
					new String[] {REGISTERED_DEVICES_PRIMARY_DEVICE},
					new String[] {MyBoolean.toString(MyBoolean.TRUE)},
					"WHERE (" + REGISTERED_DEVICES_GROUP_ID + " = '" + grpId + "') AND (" + REGISTERED_DEVICES_DEVICE_ID + " = '" + devId + "')");
				message = PRIMARY_MOVE;
				newPrimary = (String) row.get(REGISTERED_DEVICES_DEVICE_NAME);
			}
		}
		row.put(DELETE_MESSAGE, message);
		row.put(DELETE_NEW_PRIMARY, newPrimary);
		try {
			row.put(
				DELETE_BATCH,
				encodeText (
					createRemoveNodeBatch(
						String.format(DEVICE_FORMAT, devId)).getBytes(),
						DEFAULT_TEXT_ENCODING).getBytes());
		}
		catch (NoSuchAlgorithmException e) {
			throw new MsgProcException(TEXT_ENCODING_ERROR);
		}
		return row;	
	}
	

	private static synchronized AceData removeNode(AceData data) throws IOException, NoSuchAlgorithmException, NullPointerException, ClassNotFoundException, SQLException {
		String hash = getItem(data, AceData.PASSWORD_HEADER, true)[ITEM_VALUE_INDEX];
		String devName = getItem(data, AceData.DEVICE_NAME_HEADER, false)[ITEM_VALUE_INDEX];
		String grpName = getItem(data, AceData.GROUP_NAME_HEADER, false)[ITEM_VALUE_INDEX];
		Hashtable<String, Object> row = deleteNode(grpName, hash, devName);
		return new SyncConfRequestResponse (
				AceData.OK_RESPONSE,
				(String) row.get(DELETE_MESSAGE),
				(byte[]) row.get(DELETE_BATCH),
				(String) row.get(DELETE_NEW_PRIMARY));
		
	}
	
	/*
	private static String stopReplication(String svid) throws IOException {
		String [] pgm = null;
		if(started) {
			if(System.getProperty("os.name").toUpperCase().contains((WINDOWS_OS))) {
				pgm = new String[] {WINDOWS_SERVICE_COMMAND, SYMMETRIC_STOP, SYMMENTRIC_SERVICE_NAME};
			}
			else if(System.getProperty("os.name").toUpperCase().contains((LINUX_OS))) {
				pgm = new String[] {SUDO_COMMAND, LINUX_SERVICE_COMMAND, LINUX_SERVICE_NAME, SYMMETRIC_STOP};
			}
			else {
				throw new IOException(UNKNOWN_OS);
			}
			exec(pgm);
			pgm = null;
			started = false;
		}
		Exception e = null;
		String response = AceData.OK_RESPONSE;
		try {
			response = deleteFileRecord(svid);
			response = deleteEngineFile(svid);
			response = deleteDBFile(svid);
			response = deleteUploadedFileparts(svid);
			response = clearNodes(svid);
		}
		catch (Exception err) {e = err;}
		if(!started) {
			if(System.getProperty("os.name").toUpperCase().contains((WINDOWS_OS))) {
				pgm = new String[] {WINDOWS_SERVICE_COMMAND, SYMMETRIC_START, SYMMENTRIC_SERVICE_NAME};
			}
			else if(System.getProperty("os.name").toUpperCase().contains((LINUX_OS))) {
				pgm = new String[] {SUDO_COMMAND, LINUX_SERVICE_COMMAND, LINUX_SERVICE_NAME, SYMMETRIC_START};
			}
			else {
				throw new IOException(UNKNOWN_OS);
			}
			exec(pgm);
			pgm = null;
			started = true;
		}
		if(e != null) {
			throw new IOException(e);
		}
		return response;
	}
	*/
	private static void removeGroup(String grpName, String hash) throws IOException {
		int grpId = (int) login(grpName, hash).get(REGISTERED_GROUPS_ID);
		String [] pgm = null;
		String os = System.getProperty("os.name");
		if(started) {
			if(os.toUpperCase().contains((WINDOWS_OS))) {
				pgm = new String[] {WINDOWS_SERVICE_COMMAND, SYMMETRIC_STOP, SYMMENTRIC_SERVICE_NAME};
			}
			else if(System.getProperty("os.name").toUpperCase().contains((LINUX_OS))) {
				pgm = new String[] {SUDO_COMMAND, LINUX_SERVICE_COMMAND, LINUX_SERVICE_NAME, SYMMETRIC_STOP};
			}
			else {
				throw new MsgProcException(UNKNOWN_OS, os);
			}
			exec(pgm);
			pgm = null;
			started = false;
		}
		long l = getCount(
			REGISTERED_DEVICES,
			REGISTERED_DEVICES_DEVICE_ID,
			"WHERE " + REGISTERED_DEVICES_GROUP_ID + " = '" + grpId + "'");
		if(l > 0) {
			throw new MsgProcException(ACTIVE_NODES, grpName);
		}
		Exception e = null;
		try {
			deleteUploadedFileparts(grpId);
			deleteFileRecord(grpId);
			deleteEngineFile(grpId);
			deleteDBFile(grpId);
			delete (REGISTERED_GROUPS, "WHERE " + REGISTERED_GROUPS_ID + " = '" + grpId + "'");
		}
		catch (Exception err) {e = err;}
		if(!started) {
			if(System.getProperty("os.name").toUpperCase().contains((WINDOWS_OS))) {
				pgm = new String[] {WINDOWS_SERVICE_COMMAND, SYMMETRIC_START, SYMMENTRIC_SERVICE_NAME};
			}
			else if(System.getProperty("os.name").toUpperCase().contains((LINUX_OS))) {
				pgm = new String[] {SUDO_COMMAND, LINUX_SERVICE_COMMAND, LINUX_SERVICE_NAME, SYMMETRIC_START};
			}
			else {
				throw new IOException(UNKNOWN_OS);
			}
			exec(pgm);
			pgm = null;
			started = true;
		}
		if(e != null) {
			throw new IOException(e.getMessage());
		}
	}
	

	private static synchronized AceData deregister(AceData data) throws IOException {
		String hash = getItem(data, AceData.PASSWORD_HEADER, true)[ITEM_VALUE_INDEX];
		String grpName = getItem(data, AceData.GROUP_NAME_HEADER, false)[ITEM_VALUE_INDEX];
		removeGroup(grpName, hash);
		return new RegistrationRequestResponse(grpName, AceData.OK_RESPONSE);
	}

	
	private static Hashtable<String, Object> configure(String grpName, String hash, String devName) throws IOException {
		String grpId = (String) login(grpName, hash).get(REGISTERED_GROUPS_ID);
		Hashtable<String, Object> row = getRow (
			REGISTERED_DEVICES,
			REGISTERED_DEVICES_COLS,
			"WHERE (" + REGISTERED_DEVICES_GROUP_ID + " = '" + grpId + "') AND (" + REGISTERED_DEVICES_DEVICE_NAME + " = '" + devName + "')");
		if(row == null) {
			throw new MsgProcException(MessageProcessor.DEVICE_NOT_REGISTERED, devName);
		}
		if(!(boolean) row.get(REGISTERED_DEVICES_ENABLED)) {
			throw new MsgProcException(MessageProcessor.DEVICE_IS_DISABLED, devName);
		}
		boolean primary = (boolean) row.get(REGISTERED_DEVICES_PRIMARY_DEVICE);
		String devId = String.format(DEVICE_FORMAT, row.get(REGISTERED_DEVICES_DEVICE_ID));
		String filename = String.format(FILENAME_FORMAT, grpId);
		File f = new File(CLIENT_DB_PATH + System.getProperty("file.separator") + filename + H2_DB_EXTENSION);
		if(primary) {
			MessageProcessor.createFile(f, filename);
			initSymServerNode(filename, devId, createServerEngine(filename), f);
		}
		regClientNode(filename, devId);
		if(!primary) {
			loadClientNode(filename, devId);
		}
		row.put(CONFIG_FILENAME, String.format(ENGINE_FILENAME, devId));
		try {
			row.put(
				CONFIG_FILE,
				MessageProcessor.encodeText(
					createClientEngine (filename, devId).getBytes(),
					DEFAULT_TEXT_ENCODING).getBytes());
		}
		catch (NoSuchAlgorithmException e) {
			throw new MsgProcException(TEXT_ENCODING_ERROR);
		}
		return row;
	}
	
	private static synchronized AceData configureSync(AceData data) throws IOException {
		String grpName = MessageProcessor.getItem(data, AceData.GROUP_NAME_HEADER, false)[ITEM_VALUE_INDEX];
		String hash = MessageProcessor.getItem(data, AceData.PASSWORD_HEADER, true)[ITEM_VALUE_INDEX];
		String devName = MessageProcessor.getItem(data, AceData.DEVICE_NAME_HEADER, false)[ITEM_VALUE_INDEX];
		Hashtable<String, Object> row = configure(grpName, hash, devName);
		return new SyncConfRequestResponse (
			AceData.OK_RESPONSE,
			(String) row.get(CONFIG_FILENAME),
			(byte[]) row.get(CONFIG_FILE),
			"");
				
		/*
		DBConnector db = DBConnector.getDb();
		db.select(CLIENT_NODES, CLIENT_NODES_COLS, "WHERE (" + CLIENT_NODES_CLID + " = '" + clid + "') AND (" + CLIENT_NODES_ENABLED + " = '" + MyBoolean.TRUE + "')");
		db.next();
		String svid = null;
		try {
			svid = (String) db.getObject(CLIENT_NODES_SVID);
		}
		catch(Exception e) {
			return new ErrorResponse(NO_SERVER_NODE, AceData.ERROR_RESPONSE);
		}
		db.close();
		long l = MessageProcessor.getCount(CLIENT_NODES, CLIENT_NODES_SVID, "WHERE (" + CLIENT_NODES_SVID + " = '" + svid + "') AND (" + CLIENT_NODES_ENABLED + " = '" + MyBoolean.TRUE + "')");
		if(l == 0) {
			return new ErrorResponse(NO_ACTIVE_CLIENT_NODE, AceData.ERROR_RESPONSE);
		}
		File f = new File(CLIENT_DB_PATH + System.getProperty("file.separator") + svid + H2_DB_EXTENSION);
		if(l == 1) {
			try {
				MessageProcessor.createFile(f, svid);
			}
			catch(Exception e) {
				return new ErrorResponse(e.getMessage(), AceData.ERROR_RESPONSE);
			}
			System.out.println(f.length());
			System.out.println(f.canWrite());
			initSymServerNode(svid, clid, createServerEngine(svid), f);
		}
		regClientNode(svid, clid);
		if(l > 1) {
			loadClientNode(svid, clid);
		}
		try {
			return new SyncConfRequestResponse(
					AceData.OK_RESPONSE,
					String.format(ENGINE_FILENAME, clid),
					MessageProcessor.encodeText(createClientEngine(svid, clid).getBytes(), DEFAULT_TEXT_ENCODING).getBytes(),
					""
					);
		}
		catch (NoSuchAlgorithmException e) {
			return new ErrorResponse(TEXT_ENCODING_ERROR, AceData.ERROR_RESPONSE);
		}
		*/
	}

	private static void loadClientNode(String svid, String clid) throws IOException {
		String proPath = Sys.concatenate(new String[]{SYMMETRIC_HOME, SYMMETRIC_ENGINE_PATH, String.format(ENGINE_FILENAME,  svid)},  System.getProperty("file.separator"));
		String[] pgm = {
			SUDO_COMMAND,
			Sys.concatenate(new String[]{SYMMETRIC_HOME, SYMMENTRIC_BIN, SYMMETRIC_ADMIN}, System.getProperty("file.separator")),
			"--engine",
			svid,
			"--properties",
			proPath,
			"reload-node",
			clid.substring(2)
		};
		exec(pgm);
	}


	private static void regClientNode(String svid, String clid) throws IOException {
		String proPath = Sys.concatenate(new String[]{SYMMETRIC_HOME, SYMMETRIC_ENGINE_PATH, String.format(ENGINE_FILENAME,  svid)},  System.getProperty("file.separator"));
		String[] pgm = {
			SUDO_COMMAND,
			Sys.concatenate(new String[]{SYMMETRIC_HOME, SYMMENTRIC_BIN, SYMMETRIC_ADMIN}, System.getProperty("file.separator")),
			"--engine",
			svid,
			"--properties",
			proPath,
			"open-registration",
			clid.substring(0, 2) + "00" + clid.substring(4) + "-01",
			clid.substring(2)
		};
		exec(pgm);
	}


	private static void initSymServerNode(String svid, String clid, String engine, File f2) throws IOException {
		//System.out.println("1,"+f2.length());
		File f = new File(Sys.concatenate(new String[]{SYMMETRIC_HOME, SYMMETRIC_ENGINE_PATH, String.format(ENGINE_FILENAME,  svid)},  System.getProperty("file.separator")));
		if(f.exists()) {
			throw new IOException(ENGINE_EXISTS);
		}
		if(!f.createNewFile()) {
			throw new IOException(FILE_NOT_CREATED);
		}
		//System.out.println("2,"+f2.length());
		FileOutputStream out = new FileOutputStream(f);
		out.write(engine.getBytes());
		out.flush();
		out.close();
		String proPath = f.getAbsolutePath();
		//System.out.println("3,"+f2.length());
		String[] pgm = {
			SUDO_COMMAND,
			Sys.concatenate(new String[]{SYMMETRIC_HOME, SYMMENTRIC_BIN, SYMMETRIC_ADMIN}, System.getProperty("file.separator")),
			"--engine",
			svid,
			"--properties",
			proPath,
			"create-sym-tables"
		};
		exec(pgm);
		//System.out.println("4,"+f2.length());
		f = new File(Sys.concatenate(new String[]{SYMMETRIC_HOME, SYMMETRIC_SAMPLES_PATH, SQL_FILENAME}, System.getProperty("file.separator")));
		if(f.exists()) {
			if(!f.delete()) {
				throw new IOException(FILE_NOT_DELETED);
			}
		}
		if(!f.createNewFile()) {
			throw new IOException(FILE_NOT_CREATED);
		}
		out = new FileOutputStream(f);
		out.write(createSymSQL(svid, clid));
		out.flush();
		out.close();
		//System.out.println("5,"+f2.length());
		pgm = null;
		pgm = new String[] {
			SUDO_COMMAND,
			Sys.concatenate(new String[]{SYMMETRIC_HOME, SYMMENTRIC_BIN, SYMMETRIC_DBIMPORT}, System.getProperty("file.separator")),
			"--engine",
			svid,
			"--properties",
			proPath,
			f.getAbsolutePath(),
		};
		restartSymService();
	}
	
	private static synchronized void restartSymService() throws IOException {
		String[] pgm;
		String os = System.getProperty("os.name");
		if(started) {
			if(os.toUpperCase().contains((WINDOWS_OS))) {
				pgm = new String[] {WINDOWS_SERVICE_COMMAND, SYMMETRIC_STOP, SYMMENTRIC_SERVICE_NAME};
			}
			else if(System.getProperty("os.name").toUpperCase().contains((LINUX_OS))) {
				pgm = new String[] {SUDO_COMMAND, LINUX_SERVICE_COMMAND, LINUX_SERVICE_NAME, SYMMETRIC_STOP};
			}
			else {
				throw new MsgProcException(UNKNOWN_OS, os);
			}
			exec(pgm);
			pgm = null;
			started = false;
		}
		if(!started) {
			if(os.toUpperCase().contains((WINDOWS_OS))) {
				pgm = new String[] {WINDOWS_SERVICE_COMMAND, SYMMETRIC_START, SYMMENTRIC_SERVICE_NAME};
			}
			else if(System.getProperty("os.name").toUpperCase().contains((LINUX_OS))) {
				pgm = new String[] {SUDO_COMMAND, LINUX_SERVICE_COMMAND, LINUX_SERVICE_NAME, SYMMETRIC_START};
			}
			else {
				throw new MsgProcException(UNKNOWN_OS, os);
			}
			exec(pgm);
			pgm = null;
			started = true;
		}
	}
	
	
	private static byte[] createSymSQL(String svid, String clid) {
		String trigger = Sys.concatenate(TRIGGER_DEFAULT, System.getProperty("line.separator"));
		String trigger_dead = Sys.concatenate(TRIGGER_DEAD, System.getProperty("line.separator"));
		String channel = DEFAULT_CHANNEL;
		String[] triggers = {
			//1 - replikuje sa obojsmerne
			//0 - replikuje sa len smerom server - klient
			//ak chyba prvy retazec nereplikuje sa vobec
			//CATEGORIES
			String.format(trigger, (TRIGGER_PREFIX + CATEGORIES).toLowerCase(), DB_SCHEMA, CATEGORIES, channel, 1),
			String.format(trigger_dead, (TRIGGER_PREFIX + CATEGORIES + DEAD_EXT).toLowerCase(), "USER", CATEGORIES, channel),
			//SUBCATEGORIES
			String.format(trigger, (TRIGGER_PREFIX + SUBCATEGORIES).toLowerCase(), DB_SCHEMA, SUBCATEGORIES, channel, 1),
			String.format(trigger_dead, (TRIGGER_PREFIX + SUBCATEGORIES + DEAD_EXT).toLowerCase(), DB_SCHEMA, SUBCATEGORIES, channel),
			//CATEGORY_BUDGETS
			String.format(trigger, (TRIGGER_PREFIX + CATEGORY_BUDGETS).toLowerCase(), DB_SCHEMA, CATEGORY_BUDGETS, channel, 1),
			String.format(trigger_dead, (TRIGGER_PREFIX + CATEGORY_BUDGETS + DEAD_EXT).toLowerCase(), DB_SCHEMA, CATEGORY_BUDGETS, channel),
			//SUBCATEGORY_BUDGETS
			String.format(trigger, (TRIGGER_PREFIX + SUBCATEGORY_BUDGETS).toLowerCase(), DB_SCHEMA, SUBCATEGORY_BUDGETS, channel, 1),
			String.format(trigger_dead, (TRIGGER_PREFIX + SUBCATEGORY_BUDGETS + DEAD_EXT).toLowerCase(), DB_SCHEMA, SUBCATEGORY_BUDGETS, channel),
			//CONFIG
			String.format(trigger, (TRIGGER_PREFIX + CONFIG).toLowerCase(), DB_SCHEMA, CONFIG, channel, 1),
			String.format(trigger_dead, (TRIGGER_PREFIX + CONFIG + DEAD_EXT).toLowerCase(), DB_SCHEMA, CONFIG, channel),
			//EXCHANGE_RATES
			String.format(trigger, (TRIGGER_PREFIX + EXCHANGE_RATES).toLowerCase(), DB_SCHEMA, EXCHANGE_RATES, channel, 1),
			String.format(trigger_dead, (TRIGGER_PREFIX + EXCHANGE_RATES + DEAD_EXT).toLowerCase(), DB_SCHEMA, EXCHANGE_RATES, channel),
			//PAYEES
			String.format(trigger, (TRIGGER_PREFIX + PAYEES).toLowerCase(), DB_SCHEMA, PAYEES, channel, 1),
			String.format(trigger_dead, (TRIGGER_PREFIX + PAYEES + DEAD_EXT).toLowerCase(), DB_SCHEMA, PAYEES, channel),
			//ACCOUNTS
			String.format(trigger, (TRIGGER_PREFIX + ACCOUNTS).toLowerCase(), DB_SCHEMA, ACCOUNTS, channel, 1),
			String.format(trigger_dead, (TRIGGER_PREFIX + ACCOUNTS + DEAD_EXT).toLowerCase(), DB_SCHEMA, ACCOUNTS, channel),
			//TRANSACTIONS
			String.format(trigger, (TRIGGER_PREFIX + TRANSACTIONS).toLowerCase(), DB_SCHEMA, TRANSACTIONS, channel, 1),
			String.format(trigger_dead, (TRIGGER_PREFIX + TRANSACTIONS + DEAD_EXT).toLowerCase(), DB_SCHEMA, TRANSACTIONS, channel),
			//RECURRENT_TRANSACTIONS 
			String.format(trigger, (TRIGGER_PREFIX + RECURRENT_TRANSACTIONS).toLowerCase(), DB_SCHEMA, RECURRENT_TRANSACTIONS, channel, 1),
			String.format(trigger_dead, (TRIGGER_PREFIX + RECURRENT_TRANSACTIONS + DEAD_EXT).toLowerCase(), DB_SCHEMA, RECURRENT_TRANSACTIONS, channel),
			//RECURRENT_TRANSACTIONS_EXCEPTIONS 
			String.format(trigger, (TRIGGER_PREFIX + RECURRENT_TRANSACTIONS_EXCEPTIONS).toLowerCase(), DB_SCHEMA, RECURRENT_TRANSACTIONS_EXCEPTIONS, channel, 1),
			String.format(trigger_dead, (TRIGGER_PREFIX + RECURRENT_TRANSACTIONS_EXCEPTIONS + DEAD_EXT).toLowerCase(), DB_SCHEMA, RECURRENT_TRANSACTIONS_EXCEPTIONS, channel)
		};
		String r1 = "RT%1$s2%2$s";
		String r2 = "RT%2$s2%1$s";
		String trigger_link = Sys.concatenate(TRIGGER_LINK, System.getProperty("line.separator"));
		String[] trigger_links = {
			//r1 - trigger-link smerom server - klient
			//r2 - trigger-link smerom klient - server
			//dead trigger-link vzdy typu r1
			//ak nechceme replikovat chyba prvy retazec
			//CATEGORIES
			String.format(trigger_link, (TRIGGER_PREFIX + CATEGORIES).toLowerCase(), r2, 100, 1),
			String.format(trigger_link, (TRIGGER_PREFIX + CATEGORIES).toLowerCase(), r1, 102, 0),
			//String.format(trigger_link, (TRIGGER_PREFIX + CATEGORIES + DEAD_EXT).toLowerCase(), r1, 101),
			//SUBCATEGORIES
			String.format(trigger_link, (TRIGGER_PREFIX + SUBCATEGORIES).toLowerCase(), r2, 110, 1),
			String.format(trigger_link, (TRIGGER_PREFIX + SUBCATEGORIES).toLowerCase(), r1, 112, 0),
			//String.format(trigger_link, (TRIGGER_PREFIX + SUBCATEGORIES + DEAD_EXT).toLowerCase(), r1, 111),
			//CATEGORIES_BUDGET
			String.format(trigger_link, (TRIGGER_PREFIX + CATEGORY_BUDGETS).toLowerCase(), r2, 120, 1),
			String.format(trigger_link, (TRIGGER_PREFIX + CATEGORY_BUDGETS).toLowerCase(), r1, 122, 0),
			//String.format(trigger_link, (TRIGGER_PREFIX + CATEGORY_BUDGETS + DEAD_EXT).toLowerCase(), r1, 121),
			//SUBCATEGORIES_BUDGET
			String.format(trigger_link, (TRIGGER_PREFIX + SUBCATEGORY_BUDGETS).toLowerCase(), r2, 130, 1),
			String.format(trigger_link, (TRIGGER_PREFIX + SUBCATEGORY_BUDGETS).toLowerCase(), r1, 132, 0),
			//String.format(trigger_link, (TRIGGER_PREFIX + SUBCATEGORY_BUDGETS + DEAD_EXT).toLowerCase(), r1, 131),
			//CONFIG
			String.format(trigger_link, (TRIGGER_PREFIX + CONFIG).toLowerCase(), r2, 140, 1),
			String.format(trigger_link, (TRIGGER_PREFIX + CONFIG).toLowerCase(), r1, 142, 0),
			//String.format(trigger_link, (TRIGGER_PREFIX + CONFIG + DEAD_EXT).toLowerCase(), r1, 141),
			//EXCHANGE_RATES
			String.format(trigger_link, (TRIGGER_PREFIX + EXCHANGE_RATES).toLowerCase(), r2, 150, 1),
			String.format(trigger_link, (TRIGGER_PREFIX + EXCHANGE_RATES).toLowerCase(), r1, 152, 0),
			//String.format(trigger_link, (TRIGGER_PREFIX + EXCHANGE_RATES + DEAD_EXT).toLowerCase(), r1, 151),
			//PAYEES
			String.format(trigger_link, (TRIGGER_PREFIX + PAYEES).toLowerCase(), r2, 160, 1),
			String.format(trigger_link, (TRIGGER_PREFIX + PAYEES).toLowerCase(), r1, 162, 0),
			//String.format(trigger_link, (TRIGGER_PREFIX + PAYEES + DEAD_EXT).toLowerCase(), r1, 161),
			//ACCOUNT
			String.format(trigger_link, (TRIGGER_PREFIX + ACCOUNTS).toLowerCase(), r2, 170, 1),
			String.format(trigger_link, (TRIGGER_PREFIX + ACCOUNTS).toLowerCase(), r1, 172, 0),
			//String.format(trigger_link, (TRIGGER_PREFIX + ACCOUNTS + DEAD_EXT).toLowerCase(), r1, 171),
			//TRANSACTIONS
			String.format(trigger_link, (TRIGGER_PREFIX + TRANSACTIONS).toLowerCase(), r2, 180, 1),
			String.format(trigger_link, (TRIGGER_PREFIX + TRANSACTIONS).toLowerCase(), r1, 182, 0),
			//String.format(trigger_link, (TRIGGER_PREFIX + TRANSACTIONS + DEAD_EXT).toLowerCase(), r1, 181),
			//RECCURENT_TRANSACTIONS
			String.format(trigger_link, (TRIGGER_PREFIX + RECURRENT_TRANSACTIONS).toLowerCase(), r2, 190, 1),
			String.format(trigger_link, (TRIGGER_PREFIX + RECURRENT_TRANSACTIONS).toLowerCase(), r1, 191, 0),
			//String.format(trigger_link, (TRIGGER_PREFIX + RECURRENT_TRANSACTIONS + DEAD_EXT).toLowerCase().toLowerCase(), r1, 192),
			//RECCURENT_TRANSSACTIONS_EXCEPTIONS
			String.format(trigger_link, (TRIGGER_PREFIX + RECURRENT_TRANSACTIONS_EXCEPTIONS).toLowerCase(), r2, 200, 1),
			String.format(trigger_link, (TRIGGER_PREFIX + RECURRENT_TRANSACTIONS_EXCEPTIONS).toLowerCase(), r1, 202, 0),
			//String.format(trigger_link, (TRIGGER_PREFIX + RECURRENT_TRANSACTIONS_EXCEPTIONS + DEAD_EXT).toLowerCase(), r1, 201),
		};
		
		String conflict = Sys.concatenate(CONFLICT, System.getProperty("line.separator"));
		
		String toClientDetectType = DEFAULT_DETECT_TYPE;
		String toClientResolveType = DEFAULT_RESOLVE_TYPE;
		int toClientPingBack = 0;
		String toServerDetectType = DEFAULT_DETECT_TYPE;
		String toServerResolveType = DEFAULT_RESOLVE_TYPE;
		int toServerPingBack = 0;
		
		String[] conflicts = {
			//CATEGORIES
			String.format(conflict, (CONFLICT_PREFIX + TO_CLIENT + CATEGORIES).toLowerCase(), "%1$s", "%2$s", channel, DB_SCHEMA, CATEGORIES, toClientDetectType, toClientResolveType, toClientPingBack),
			String.format(conflict, (CONFLICT_PREFIX + TO_SERVER + CATEGORIES).toLowerCase(), "%2$s", "%1$s", channel, DB_SCHEMA, CATEGORIES, toServerDetectType, toServerResolveType, toServerPingBack),
			
			//SUBCATEGORIES
			String.format(conflict, (CONFLICT_PREFIX + TO_CLIENT + SUBCATEGORIES).toLowerCase(), "%1$s", "%2$s", channel, DB_SCHEMA, SUBCATEGORIES, toClientDetectType, toClientResolveType, toClientPingBack),
			String.format(conflict, (CONFLICT_PREFIX + TO_SERVER + SUBCATEGORIES).toLowerCase(), "%2$s", "%1$s", channel, DB_SCHEMA, SUBCATEGORIES, toServerDetectType, toServerResolveType, toServerPingBack),
			
			//CATEGORY_BUDGETS
			String.format(conflict, (CONFLICT_PREFIX + TO_CLIENT + CATEGORY_BUDGETS).toLowerCase(), "%1$s", "%2$s", channel, DB_SCHEMA, CATEGORY_BUDGETS, toClientDetectType, toClientResolveType, toClientPingBack),
			String.format(conflict, (CONFLICT_PREFIX + TO_SERVER + CATEGORY_BUDGETS).toLowerCase(), "%2$s", "%1$s", channel, DB_SCHEMA, CATEGORY_BUDGETS, toServerDetectType, toServerResolveType, toServerPingBack),
			
			//SUBCATEGORY_BUDGETS
			String.format(conflict, (CONFLICT_PREFIX + TO_CLIENT + SUBCATEGORY_BUDGETS).toLowerCase(), "%1$s", "%2$s", channel, DB_SCHEMA, SUBCATEGORY_BUDGETS, toClientDetectType, toClientResolveType, toClientPingBack),
			String.format(conflict, (CONFLICT_PREFIX + TO_SERVER + SUBCATEGORY_BUDGETS).toLowerCase(), "%2$s", "%1$s", channel, DB_SCHEMA, SUBCATEGORY_BUDGETS, toServerDetectType, toServerResolveType, toServerPingBack),
			
			//CONFIG
			String.format(conflict, (CONFLICT_PREFIX + TO_CLIENT + CONFIG).toLowerCase(), "%1$s", "%2$s", channel, DB_SCHEMA, CONFIG, toClientDetectType, toClientResolveType, toClientPingBack),
			String.format(conflict, (CONFLICT_PREFIX + TO_SERVER + CONFIG).toLowerCase(), "%2$s", "%1$s", channel, DB_SCHEMA, CONFIG, toServerDetectType, toServerResolveType, toServerPingBack),
			
			//EXCHANGE_RATES
			String.format(conflict, (CONFLICT_PREFIX + TO_CLIENT + EXCHANGE_RATES).toLowerCase(), "%1$s", "%2$s", channel, DB_SCHEMA, EXCHANGE_RATES, toClientDetectType, toClientResolveType, toClientPingBack),
			String.format(conflict, (CONFLICT_PREFIX + TO_SERVER + EXCHANGE_RATES).toLowerCase(), "%2$s", "%1$s", channel, DB_SCHEMA, EXCHANGE_RATES, toServerDetectType, toServerResolveType, toServerPingBack),
			
			//PAYEES
			String.format(conflict, (CONFLICT_PREFIX + TO_CLIENT + PAYEES).toLowerCase(), "%1$s", "%2$s", channel, DB_SCHEMA, PAYEES, toClientDetectType, toClientResolveType, toClientPingBack),
			String.format(conflict, (CONFLICT_PREFIX + TO_SERVER + PAYEES).toLowerCase(), "%2$s", "%1$s", channel, DB_SCHEMA, PAYEES, toServerDetectType, toServerResolveType, toServerPingBack),
			
			//ACCOUNTS
			String.format(conflict, (CONFLICT_PREFIX + TO_CLIENT + ACCOUNTS).toLowerCase(), "%1$s", "%2$s", channel, DB_SCHEMA, ACCOUNTS, toClientDetectType, toClientResolveType, toClientPingBack),
			String.format(conflict, (CONFLICT_PREFIX + TO_SERVER + ACCOUNTS).toLowerCase(), "%2$s", "%1$s", channel, DB_SCHEMA, ACCOUNTS, toServerDetectType, toServerResolveType, toServerPingBack),
			
			//TRANSACTIONS
			String.format(conflict, (CONFLICT_PREFIX + TO_CLIENT + TRANSACTIONS).toLowerCase(), "%1$s", "%2$s", channel, DB_SCHEMA, TRANSACTIONS, toClientDetectType, toClientResolveType, toClientPingBack),
			String.format(conflict, (CONFLICT_PREFIX + TO_SERVER + TRANSACTIONS).toLowerCase(), "%2$s", "%1$s", channel, DB_SCHEMA, TRANSACTIONS, toServerDetectType, toServerResolveType, toServerPingBack),
			
			//RECURRENT_TRANSACTIONS
			String.format(conflict, (CONFLICT_PREFIX + TO_CLIENT + RECURRENT_TRANSACTIONS).toLowerCase(), "%1$s", "%2$s", channel, DB_SCHEMA, RECURRENT_TRANSACTIONS, toClientDetectType, toClientResolveType, toClientPingBack),
			String.format(conflict, (CONFLICT_PREFIX + TO_SERVER + RECURRENT_TRANSACTIONS).toLowerCase(), "%2$s", "%1$s", channel, DB_SCHEMA, RECURRENT_TRANSACTIONS, toServerDetectType, toServerResolveType, toServerPingBack),
			
			//RECURRENT_TRANSACTIONS_EXCEPTIONS
			String.format(conflict, (CONFLICT_PREFIX + TO_CLIENT + RECURRENT_TRANSACTIONS_EXCEPTIONS).toLowerCase(), "%1$s", "%2$s", channel, DB_SCHEMA, RECURRENT_TRANSACTIONS_EXCEPTIONS, toClientDetectType, toClientResolveType, toClientPingBack),
			String.format(conflict, (CONFLICT_PREFIX + TO_SERVER + RECURRENT_TRANSACTIONS_EXCEPTIONS).toLowerCase(), "%2$s", "%1$s", channel, DB_SCHEMA, RECURRENT_TRANSACTIONS_EXCEPTIONS, toServerDetectType, toServerResolveType, toServerPingBack)

		};
		
		
		String server_group_id = svid + "-01";
		String node_id = svid.substring(4);
		String external_id = svid.substring(4);
		String client_group_id = clid.substring(0, 2) + "00" + clid.substring(4) + "-01";
		String sym_triggers = String.format(
				Sys.concatenate(triggers, System.getProperty("line.separator")),
				server_group_id,
				client_group_id);
		String sym_trigger_links = String.format(
				Sys.concatenate(trigger_links, System.getProperty("line.separator")),
				server_group_id,
				client_group_id);
		String sym_conflicts = String.format(
				Sys.concatenate(conflicts, System.getProperty("line.separator")),
				server_group_id,
				client_group_id);
		
		String sql = Sys.concatenate(SERVER_NODES, System.getProperty("line.separator"));
		sql = String.format(
			sql,
			server_group_id,
			node_id,
			external_id,
			client_group_id,
			sym_triggers,
			sym_trigger_links,
			sym_conflicts
			);
		return sql.getBytes();
	}


	private static void exec(String[] pgm) throws IOException {
		Process p = Runtime.getRuntime().exec(pgm);
		int status;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayOutputStream err = new ByteArrayOutputStream();
		Thread tins = new ChannelReader(p.getInputStream(), out);
		tins.setDaemon(true);
		Thread terr = new ChannelReader(p.getErrorStream(), err);
		terr.setDaemon(true);
		tins.start();
		terr.start();
		try {
			status = p.waitFor();
		} catch (InterruptedException e) {
			status = 1;
		}
		p.destroy();
		if(tins.isAlive()) {
			tins.interrupt();
		}
		if(terr.isAlive()) {
			terr.interrupt();
		}
		System.out.println(out.toString());
		System.err.println(err.toString());
		if(status != 0) {
			throw new IOException("Failed to load symmetric tables");
		}
		
	}
	
	
	private static Hashtable<String, Object> uploadFilepart(String filename, int c, String filepart) throws MsgProcException {
		Hashtable<String, Object> row = getRow(
			UPLOADED_FILES,
			UPLOADED_FILES_COLS,
			"WHERE " + UPLOADED_FILES_FILENAME + " = '" + filename + "'");
		if(row == null) {
			throw new MsgProcException(FILE_NOT_AVAILABLE, filename);
		}
		if((boolean) row.get(UPLOADED_FILES_ENABLED)) {
			if((boolean) row.get(UPLOADED_FILES_UPLOAD_ENABLED)) {
				int max = Integer.parseInt((String) row.get(UPLOADED_FILES_FILEPART_COUNT));
				if(c > 0 && c <= max) {
					if(checkDuplicity (
						UPLOADED_FILEPARTS,
						UPLOADED_FILEPARTS_FIELAPART_ID,
						"WHERE (" + UPLOADED_FILEPARTS_FILENAME + " = '" + filename + "') AND (" + UPLOADED_FILEPARTS_FIELAPART_ID + " = '" + c + "')")) {
						updateWithPreparedStatement (
								UPLOADED_FILEPARTS,
								new String[] {UPLOADED_FILEPARTS_FILEPART},
								new String[] {filepart},
								"WHERE (" + UPLOADED_FILEPARTS_FILENAME + " = '" + filename + "') AND (" + UPLOADED_FILEPARTS_FIELAPART_ID + " = " + c + ")");
							row.put(UPLOADED_FILEPARTS_FILEPART, String.format(FILEPART_UPDATED, c, filepart.length() / 2));
					}
					else {
						insertWithPreparedStatement (
							UPLOADED_FILEPARTS,
							UPLOADED_FILEPARTS_COLS,
							new String[]{filename, Integer.toString(c), filepart});
						row.put(UPLOADED_FILEPARTS_FILEPART, String.format(FILEPART_ADDED, c, filepart.length() / 2));
							
					}
						
					
				}
				throw new MsgProcException(FILEPART_COUNT_OUT_OF_BOUNDS, filename, c, max);
			}
			throw new MsgProcException(FILE_ALLREADY_UPLOADED, filename);
		}
		throw new MsgProcException(FILE_DISABLED, filename);
	}

	private static int updateWithPreparedStatement(String table, String[] cNames, String[] values, String where) throws MsgProcException {
		try {
			DBConnector db = DBConnector.getDb();
			return db.updateWithPreparedStatement (table, cNames, values, where);
		}
		catch(SQLException e) {
			throw new MsgProcException(e.getMessage());
		}
		
	}


	private static int insertWithPreparedStatement(String table, String[] cNames, String[] values) throws MsgProcException {
		try {
			DBConnector db = DBConnector.getDb();
			return db.insertWithPreparedStatement (table, cNames, values);
		}
		catch(SQLException e) {
			throw new MsgProcException(e.getMessage());
		}
	}


	private static synchronized AceData uploadFilepart(AceData data) throws IOException {
		String filename = getItem(data, AceData.FILENAME_HEADER, false)[ITEM_VALUE_INDEX];
		int c = Integer.parseInt(getItem(data, AceData.FILEPART_ID_HEADER, false)[ITEM_VALUE_INDEX]);
		String filepart = getItem(data, AceData.FILEPART_HEADER, true)[ITEM_VALUE_INDEX];
		Hashtable<String, Object> row = uploadFilepart(filename, c, filepart);
		return new RegistrationRequestResponse((String) row.get(UPLOADED_FILEPARTS_FILEPART), AceData.OK_RESPONSE);
	}
	
	private static Hashtable<String, Object> getRow(String tableName, String[] cName, String where) throws MsgProcException {
		DBConnector db = null;
		try {
			db = DBConnector.getDb();
			db.select(tableName, cName, where);
		}
		catch(SQLException e) {
			throw new MsgProcException(e.getMessage());
		}
		db.next();
		Hashtable<String, Object> row;
		try {
			row = db.getRowAsHashtable(cName);
		}
		catch (SQLException e) {
			row = null;
		}
		db.close();
		return row;
	}
	
	
	private static Hashtable<String, Object> reqForFileUpload(String grpName, String hash, String devName, String count, String alg, String digest) throws MsgProcException {
		Hashtable<String, Object> row = MessageProcessor.login(grpName, hash);
		int grpId = (int) row.get(MessageProcessor.REGISTERED_GROUPS_ID);
		String filename = String.format(FILENAME_FORMAT, grpId);
		row = getRow (
			MessageProcessor.REGISTERED_DEVICES,
			MessageProcessor.REGISTERED_DEVICES_COLS,
			"WHERE (" + REGISTERED_DEVICES_GROUP_ID + " = '" + grpId + "') AND (" + REGISTERED_DEVICES_DEVICE_NAME + " = '" + devName + "')");
		if(row == null) {
			throw new MsgProcException(MessageProcessor.DEVICE_NOT_REGISTERED, devName);
		}
		if(!(boolean) row.get(REGISTERED_DEVICES_ENABLED)) {
			throw new MsgProcException(MessageProcessor.DEVICE_IS_DISABLED, devName);
		}
		if(!(boolean) row.get(REGISTERED_DEVICES_PRIMARY_DEVICE)) {
			throw new MsgProcException(MessageProcessor.DEVICE_IS_NOT_PRIMARY, devName);
		}
		if(checkDuplicity(UPLOADED_FILES, UPLOADED_FILES_FILENAME, " WHERE " + UPLOADED_FILES_FILENAME + " = '" + filename + "'")) {
			throw new MsgProcException(FILE_ALLREADY_UPLOADED, filename);
		}
		insert (
			UPLOADED_FILES,
			UPLOADED_FILES_COLS,
			new String[]{filename, count, alg, digest, MyBoolean.toString(MyBoolean.TRUE), MyBoolean.toString(MyBoolean.TRUE)});
		row.put(UPLOADED_FILES_FILENAME, filename);
		return row;
	}
	
	
	
	private static synchronized AceData upload(AceData data) throws IOException {
		String grpName = getItem(data, AceData.FILENAME_HEADER, false)[ITEM_VALUE_INDEX];
		String hash = getItem(data, AceData.PASSWORD_HEADER, false)[ITEM_VALUE_INDEX];
		String devName = getItem(data, AceData.DEVICE_NAME_HEADER, false)[ITEM_VALUE_INDEX];
		String count = getItem(data, AceData.FILEPART_COUNT_HEADER, false)[ITEM_VALUE_INDEX];
		String alg = getItem(data, AceData.FILE_DIGEST_HEADER, false)[ITEM_OID_INDEX];
		String digest = getItem(data, AceData.FILE_DIGEST_HEADER, true)[ITEM_VALUE_INDEX];
		Hashtable<String, Object> row = reqForFileUpload(grpName, hash, devName, count, alg, digest);
		return new RegistrationRequestResponse((String) row.get(UPLOADED_FILES_FILENAME), AceData.OK_RESPONSE);
	}

	private static Hashtable<String, Object> registerDevice(String hash, String grpName, String devName) throws MsgProcException {
		int grpId = (int) login(grpName, hash).get(REGISTERED_GROUPS_ID);
		if(checkDuplicity(
			REGISTERED_DEVICES,
			REGISTERED_DEVICES_DEVICE_NAME,
			"WHERE (" + REGISTERED_DEVICES_GROUP_ID + " = '" + grpId + "') AND (" + REGISTERED_DEVICES_DEVICE_NAME + " = '" + devName + "')")) {
			throw new MsgProcException (DEVICE_NAME_DUPLICITY, devName, grpName);
		}
		int devId = getFirstEmptyDevice(grpId);
		if(devId >= MessageProcessor.MAX_DEVICES_ON_GROUP) {
			throw new MsgProcException (MAX_CLIENT_NODES_REACHED, MAX_DEVICES_ON_GROUP, grpName);
		}
		long l = getCount(
			REGISTERED_DEVICES,
			REGISTERED_DEVICES_DEVICE_ID,
			"WHERE " + REGISTERED_DEVICES_GROUP_ID + " = '" + grpId + "'");
		l = l > 0 ? 1 : l;
		insert (
			REGISTERED_DEVICES,
			new String[] {
				REGISTERED_DEVICES_GROUP_ID,
				REGISTERED_DEVICES_DEVICE_ID,
				REGISTERED_DEVICES_DEVICE_NAME,
				REGISTERED_DEVICES_PRIMARY_DEVICE
			},
			new String[]{Integer.toString(grpId), Integer.toString(devId), devName, Long.toString(l)});
		return getRow(
			REGISTERED_DEVICES,
			REGISTERED_DEVICES_COLS,
			"WHERE " + REGISTERED_DEVICES_DEVICE_ID + "= '" + devId + "' AND " + REGISTERED_DEVICES_GROUP_ID + " = '" + grpId + "'");
	}
	
	private static synchronized AceData joinDevice(AceData data) throws IOException {
		String grpName = MessageProcessor.getItem(data, AceData.GROUP_NAME_HEADER, false)[ITEM_VALUE_INDEX];
		String hash = MessageProcessor.getItem(data, AceData.PASSWORD_HEADER, true)[ITEM_VALUE_INDEX];
		String devName = MessageProcessor.getItem(data, AceData.DEVICE_NAME_HEADER, false)[ITEM_VALUE_INDEX];
		Hashtable<String, Object> row = registerDevice(hash, grpName, devName);
		String devId = Integer.toString((int) row.get(REGISTERED_DEVICES_DEVICE_ID)); 
		return new RegistrationRequestResponse(devId, AceData.OK_RESPONSE);
	}
	
	
	@SuppressWarnings("unused")
	private static void clear(String table, String col, String[] col2, String[] value, String where) throws NullPointerException, InvalidPropertiesFormatException, ClassNotFoundException, SQLException {
		DBConnector db = DBConnector.getDb();
		db.select(table, new String[]{col}, where);
		while(true) {
			db.next();
			try {
				db.update(table, col2, value, where);
			} catch (SQLException e) {
				break;
			}
		}
	}
	
	private static Hashtable<String, Object> login(String grpName, String hash) throws MsgProcException {
		Hashtable<String, Object> row = getRow (
			REGISTERED_GROUPS,
			REGISTERED_GROUPS_COLS,
			"where " + REGISTERED_GROUPS_GROUP_ID + " = '" + grpName + "'");
		if(row == null) {
			throw new MsgProcException (GROUP_ID_NOT_REGISTERED, grpName);
		}
		if((boolean) row.get(REGISTERED_GROUPS_ENABLED)) {
			if(hash.equalsIgnoreCase((String) row.get(REGISTERED_GROUPS_PASSWORD))) {
				return row;
			}
			throw new MsgProcException (WRONG_PASSWORD, grpName);
		}
		throw new MsgProcException (GROUP_ID_IS_DISABLED, grpName);
		
		
	}

	public void setMessage(Message msg, String id, String textEncoding, String encodingAlgorithm, String digestAlgorithm, String msgId, byte[] content) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
		/*String xxx = "zatulany maly pes";
		xxx = toHexString(xxx);
		System.out.println(xxx);
		System.out.println(new String(fromHexString(xxx)));
		*/
		if(msgId == null) {
			throw new IOException(NULL_MESSAGE_ID);
		}
		if(id == null) {
			throw new IOException(NULL_ID);
		}
		if(textEncoding == null) {
			textEncoding = DEFAULT_TEXT_ENCODING;
		}
		if(encodingAlgorithm == null) {
			encodingAlgorithm = DEFAULT_ENCODING_ALGORITHM;
		}
		if(digestAlgorithm == null) {
			digestAlgorithm = DEFAULT_DIGEST_ALGORITHM;
		}
		String timestamp = encodeText(Long.toString(new Date().getTime()).getBytes(), DEFAULT_TEXT_ENCODING);
		String pass = Long.toHexString((long) (Math.random()*Long.MAX_VALUE)); 
		MessageDigest messageDigest = MessageDigest.getInstance(digestAlgorithm);
		messageDigest.update(content);
		String digest = encodeText(messageDigest.digest(),DEFAULT_TEXT_ENCODING);
		//must be set
		//String messageId = "1";
		
		id = String.format("%8s", id);
		textEncoding = String.format("%8s", textEncoding);
		encodingAlgorithm = String.format("%32s", encodingAlgorithm);
		digestAlgorithm = String.format("%32s", digestAlgorithm);
		pass = String.format("%16s", pass);
		digest = String.format("%64s", digest);
		timestamp = String.format("%64s", timestamp);
		msgId = String.format("%32s", msgId);
		
		SecretKeySpec key = new SecretKeySpec(pass.getBytes(), 0, 16, encodingAlgorithm.split("\\/")[0].trim());
		Cipher cipher = Cipher.getInstance(encodingAlgorithm);
		cipher.init(Cipher.ENCRYPT_MODE, key);
		msg.setContent(encodeText(cipher.doFinal(content),textEncoding.trim()));
		
		String reference = id + textEncoding + encodingAlgorithm + digestAlgorithm + pass + digest + timestamp + msgId;
		
		key = new SecretKeySpec(decodeText("7a1b6f1a76177900204d2c403919690c7f",DEFAULT_TEXT_ENCODING.trim()), 0, 16, REFERENCE_ENCODING_ALGORITHM.split("\\/")[0].trim());
		cipher = Cipher.getInstance(REFERENCE_ENCODING_ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, key);
		msg.setReference(encodeText(cipher.doFinal(reference.getBytes()),DEFAULT_TEXT_ENCODING));
	}
	
	private static String[] getItem(AceData data, String key, boolean decode) throws MsgProcException {
		try {
			AceDataItemImpl item = data.get(key);
			if(decode) {
				return new String[] {
						item.getName(),
						item.getOid(),
						encodeText(item.getItemValue(), DEFAULT_TEXT_ENCODING)
				};
			}
			return new String[] {item.getName(), item.getOid(), new String(item.getItemValue())};
		}
		catch(IOException | NoSuchAlgorithmException e) {
			throw new MsgProcException(FAILED_TO_RETRIEVE_DATA, data.getMessageType(), key);
		}
	}
	
	@SuppressWarnings("unused")
	private static final int ITEM_NAME_INDEX = 0;
	private static final int ITEM_OID_INDEX = 1;
	private static final int ITEM_VALUE_INDEX = 2;
	
	private static synchronized AceData register(AceData data) throws IOException {
		String email = getItem(data, AceData.EMAIL_HEADER, false)[ITEM_VALUE_INDEX];
		String hash = getItem(data, AceData.PASSWORD_HEADER, true)[ITEM_VALUE_INDEX];
		String version = getItem(data, AceData.VERSION_HEADER, false)[ITEM_VALUE_INDEX];
		String grpName = getItem(data, AceData.GROUP_NAME_HEADER, false)[ITEM_VALUE_INDEX];
		Hashtable<String, Object> row = registerGroup(grpName, hash, email, version);
		String id = Integer.toString((int) row.get(REGISTERED_GROUPS_ID));
		return new RegistrationRequestResponse(id, AceData.OK_RESPONSE);
	}
	
	private static final String THIS_SERVER_NAME = "23.239.26.122";
		
	private static int insert(String table, String[] cName, String[] value) throws MsgProcException {
		try {
			DBConnector db = DBConnector.getDb();
			return db.insert(table, cName, value);
		}
		catch(SQLException e) {
			throw new MsgProcException(e.getMessage());
		}
	}
	
	private static Hashtable<String, Object> registerGroup(String grpName, String hash, String email, String version) throws MsgProcException {
		if(checkDuplicity(REGISTERED_GROUPS, REGISTERED_GROUPS_ID, " WHERE (" + REGISTERED_GROUPS_GROUP_ID + " = '" + grpName + "')")) {
			throw new MsgProcException(DUPLICATE_GROUP_ID);
		}
		if(getCount(REGISTERED_GROUPS, REGISTERED_GROUPS_ID, null) >= MAX_GROUP_ON_THIS_SERVER ) {
			throw new MsgProcException(SERVER_FULL, THIS_SERVER_NAME, MAX_GROUP_ON_THIS_SERVER);
		}
		insert(
			REGISTERED_GROUPS,
			new String[] {
				REGISTERED_GROUPS_GROUP_ID,
				REGISTERED_GROUPS_PASSWORD,
				REGISTERED_GROUPS_EMAIL,
				REGISTERED_GROUPS_DB_VERSION},
			new String[] {grpName, hash, email, version});
		Hashtable<String, Object> row = getRow (
			REGISTERED_GROUPS,
			REGISTERED_GROUPS_COLS,
			"where " + REGISTERED_GROUPS_GROUP_ID + " = '" + grpName + "'");
		return row;
	}
	
	private static final String FIND_EMPTY_DEVICE_ERROR = "DATABASE: unknown error while device name creation";
	
	private static int getFirstEmptyDevice (int grpId) throws MsgProcException {
		DBConnector db;
		try {
			db= DBConnector.getDb();
			db.select (
				REGISTERED_DEVICES,
				new String[]{REGISTERED_DEVICES_DEVICE_ID},
				" WHERE " + REGISTERED_DEVICES_GROUP_ID + " = '" + grpId + "' ORDER BY " + REGISTERED_DEVICES_DEVICE_ID);
		}
		catch(SQLException e) {
			throw new MsgProcException(FIND_EMPTY_DEVICE_ERROR);
		}
		int i = 0;
		for(; i < MAX_DEVICES_ON_GROUP; i++) {
			db.next();
			try {
				if(i < (int)db.getObject(REGISTERED_DEVICES_GROUP_ID)) {
					break;
				}
			}
			catch (SQLException e) {
				break;
			}
		}
		return i;
	}
	
	/*
	private static String getFirstDisabled(String table, String id, String where) throws SQLException, NullPointerException, InvalidPropertiesFormatException, ClassNotFoundException {
		DBConnector db = DBConnector.getDb();
		db.select(table, new String[]{id}, where);
		db.next();
		try {
			String ret = (String) db.getObject(id);
			db.close();
			return 	ret;
		}
		catch (SQLException e) {
			return null;
		}
	
	}*/
	
	private static final String CHECK_DUPLICITY_ERROR = "DATABASE: Unknow error while retrieving count record";
	private static final String COUNT_STRING = "COUNT(%s)";
	
	public static long getCount(String table, String col, String where) throws MsgProcException {
		col = String.format(COUNT_STRING, col);
		Hashtable<String, Object> row = getRow(table, new String[]{col}, where);
		if(row == null) {
			throw new MsgProcException(CHECK_DUPLICITY_ERROR);
		}
		return (long)row.get(col);
	}
	
	private static boolean checkDuplicity(String table, String col, String where) throws MsgProcException {
		return getCount(table, col, where) != 0;
	}

	private AceData getMessage(Message msg) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
		byte[] r = MessageProcessor.decodeText(msg.getReference(), MessageProcessor.DEFAULT_TEXT_ENCODING);
		SecretKeySpec key = new SecretKeySpec(decodeText("7a1b6f1a76177900204d2c403919690c7f",DEFAULT_TEXT_ENCODING.trim()), 0, 16, REFERENCE_ENCODING_ALGORITHM.split("\\/")[0].trim());
		Cipher cipher = Cipher.getInstance(REFERENCE_ENCODING_ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, key);
		r = cipher.doFinal(r);
		setId(new String(r, 0, 8).trim());
		String te = new String(r, 8, 8).trim();
		String ea = new String(r, 16, 32).trim();
		String da = new String(r, 48, 32).trim();
		String pa = new String(r, 80, 16);
		String di = new String(r, 96, 64);
		String ti = new String(r, 160, 64).trim();
		String mi = new String(r, 224, 32).trim();
		Date d = new Date(Long.parseLong(new String(MessageProcessor.decodeText(ti, DEFAULT_TEXT_ENCODING))));
		Date t1 = new Date();
		Date t2 = new Date(t1.getTime() + MAX_TIME_DELAY);
		if(d.before(t1) && d.after(t2)) {
			throw new IOException(MESSAGE_OUT_OF_TIME);
		}
		byte[] c = MessageProcessor.decodeText(msg.getContent(), te);
		key = new SecretKeySpec(pa.getBytes(), 0, 16, ea.split("\\/")[0].trim());
		cipher = Cipher.getInstance(ea);
		cipher.init(Cipher.DECRYPT_MODE, key);
		c = cipher.doFinal(c);
		MessageDigest messageDigest = MessageDigest.getInstance(da);
		messageDigest.update(c);
		String digest = encodeText(messageDigest.digest(),DEFAULT_TEXT_ENCODING);
		if(!digest.equalsIgnoreCase(di)) {
			throw new IOException(MESSAGE_DIGEST_MISHMATCH);
		}
		AceData data = new AceData();
		ByteArrayInputStream out = new ByteArrayInputStream(c);
		enc.setInputStream(out);
		data.decode(enc);
		data.setMessageType(mi);
		return data;
	}
	
	private static String encodeText(byte[] b, String textEncoding) throws NoSuchAlgorithmException {
		if(textEncoding.equals(DEFAULT_TEXT_ENCODING)) {
			return toHexString(b);
		}
		else if(textEncoding.equals(BASE64_TEXT_ENCODING)) {
			return new String(b);
		}
		else if(textEncoding.equals(NONE_TEXT_ENCODING)) {
			return new String(b);
		}
		else {
			throw new NoSuchAlgorithmException("Unknown text encoding algorithm");
		}
	}
	
	private static byte[] decodeText(String string, String textEncoding) throws NoSuchAlgorithmException {
		if(textEncoding.equals(DEFAULT_TEXT_ENCODING)) {
			return fromHexString(string);
		}
		else if(textEncoding.equals(BASE64_TEXT_ENCODING)) {
			return string.getBytes();
		}
		else if(textEncoding.equals(NONE_TEXT_ENCODING)) {
			return string.getBytes();
		}
		else {
			throw new NoSuchAlgorithmException("Unknown text encoding algorithm");
		}
	}
	private static byte[] fromHexString(String string) {
		byte[] p = string.toLowerCase().getBytes(); 
		byte[] r = new byte[p.length/2];
		for(int i = 0; i < r.length; i++) {
			int h = p[i*2]&0xFF;
			int l = p[i*2 + 1]&0xFF;
			int bh = h>0x39?h-0x57:h-0x30;
			int bl = l>0x39?l-0x57:l-0x30;
			r[i] = (byte) (bh<<0x04|bl);
		}
		for(int i = r.length - 1; i > 0; i--) {
			r[i] ^= r[i-1];
		}
		return r;
	}
	public static String toHexString(byte[] b) {
		for(int i = 1; i < b.length; i++) {
			b[i] ^= b[i-1];
		}
		byte[] r = new byte[b.length * 2];
		for(int i = 0; i < b.length; i++) {
			int h = b[i]&0xF0; 
			int l = b[i]&0x0F;
			h >>= 4;
			h = h<0x0A?h+0x30:h+0x57;
			l = l<0x0A?l+0x30:l+0x57;
			r[2*i] = (byte) h;
			r[2*i + 1] = (byte) l;
		}
		return new String(r);
	}

	private String getId() {
		return id;
	}

	private void setId(String id) {
		this.id = id;
	}
	/*
	public void setMessage(Message msg, String id, String textEncoding, String encodingAlgorithm, String digestAlgorithm, String msgId, byte[] content) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
		String xxx = "zatulany maly pes";
		xxx = toHexString(xxx);
		System.out.println(xxx);
		System.out.println(new String(fromHexString(xxx)));
		
		if(msgId == null) {
			throw new IOException(NULL_MESSAGE_ID);
		}
		if(id == null) {
			throw new IOException(NULL_ID);
		}
		if(textEncoding == null) {
			textEncoding = DEFAULT_TEXT_ENCODING;
		}
		if(encodingAlgorithm == null) {
			encodingAlgorithm = DEFAULT_ENCODING_ALGORITHM;
		}
		if(digestAlgorithm == null) {
			digestAlgorithm = DEFAULT_DIGEST_ALGORITHM;
		}
		String timestamp = encodeText(Long.toString(new Date().getTime()).getBytes(), DEFAULT_TEXT_ENCODING);
		String pass = Long.toHexString((long) (Math.random()*Long.MAX_VALUE)); 
		MessageDigest messageDigest = MessageDigest.getInstance(digestAlgorithm);
		messageDigest.update(content);
		String digest = encodeText(messageDigest.digest(),DEFAULT_TEXT_ENCODING);
		//must be set
		//String messageId = "1";
		
		id = String.format("%8s", id);
		textEncoding = String.format("%8s", textEncoding);
		encodingAlgorithm = String.format("%32s", encodingAlgorithm);
		digestAlgorithm = String.format("%32s", digestAlgorithm);
		pass = String.format("%16s", pass);
		digest = String.format("%64s", digest);
		timestamp = String.format("%64s", timestamp);
		msgId = String.format("%32s", msgId);
		
		SecretKeySpec key = new SecretKeySpec(pass.getBytes(), 0, 16, encodingAlgorithm.split("\\/")[0].trim());
		Cipher cipher = Cipher.getInstance(encodingAlgorithm);
		cipher.init(Cipher.ENCRYPT_MODE, key);
		msg.setContent(encodeText(cipher.doFinal(content),textEncoding.trim()));
		
		String reference = id + textEncoding + encodingAlgorithm + digestAlgorithm + pass + digest + timestamp + msgId;
		
		key = new SecretKeySpec(decodeText("7a1b6f1a76177900204d2c403919690c7f",DEFAULT_TEXT_ENCODING.trim()), 0, 16, REFERENCE_ENCODING_ALGORITHM.split("\\/")[0].trim());
		cipher = Cipher.getInstance(REFERENCE_ENCODING_ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, key);
		msg.setReference(encodeText(cipher.doFinal(reference.getBytes()),DEFAULT_TEXT_ENCODING));
	}
*/

	private static void createFile(File f, String filename) throws IOException {
		if(f.exists()) {
			if(!f.delete()) {
				throw new MsgProcException(FILE_NOT_DELETED, filename);
			}
		}
		try {
			f.createNewFile();
		}
		catch (IOException e) {
			throw new MsgProcException(FILE_NOT_CREATED, filename);
		}
		Hashtable<String, Object> row = getRow (
			UPLOADED_FILES,
			UPLOADED_FILES_COLS,
			"WHERE " + UPLOADED_FILES_FILENAME + " = '" + filename + "'");
		int max = (int) row.get(UPLOADED_FILES_FILEPART_COUNT);
		String oid = (String) row.get(UPLOADED_FILES_DIGEST_ALGORITHM);
		String fileDigest = (String) row.get(UPLOADED_FILES_DIGEST);
		FileOutputStream out = new FileOutputStream(f);
		try {
			for(int i = 0; i < max; i++) {
				row = getRow (
					UPLOADED_FILEPARTS,
					new String[] {UPLOADED_FILEPARTS_FILEPART},
					"WHERE (" + UPLOADED_FILEPARTS_FILENAME + " = '" + filename + "') AND (" + UPLOADED_FILEPARTS_FIELAPART_ID + " = " + (i + 1) + ")");
				byte[] b = MessageProcessor.decodeText((String) row.get(UPLOADED_FILEPARTS_FILEPART), DEFAULT_TEXT_ENCODING);
				out.write(b);
			}
		}
		catch(Exception e) {
			out.close();
			f.delete();
			throw new MsgProcException(FILE_CREATE_ERROR, filename);
		}
		out.flush();
		out.close();
		String digest = null;
		try {
			digest = MessageProcessor.encodeText(MessageProcessor.fileDigest(f, OidRepository.getDesc(oid)), DEFAULT_TEXT_ENCODING);
		}
		catch (NoSuchAlgorithmException e) {
			f.delete();
			throw new MsgProcException(FILE_DIGEST_CHECK_FAILED, filename);
		}
		if(!digest.equalsIgnoreCase(fileDigest)) {
			f.delete();
			throw new MsgProcException(FILE_DIGEST_CHECK_FAILED, filename);
		}
		delete(UPLOADED_FILEPARTS, "WHERE " + UPLOADED_FILEPARTS_FILENAME + " = '" + filename + "'");
		update(	UPLOADED_FILES,
					new String[]{UPLOADED_FILES_ENABLED, UPLOADED_FILES_UPLOAD_ENABLED},
					new String[] {MyBoolean.toString(MyBoolean.TRUE), MyBoolean.toString(MyBoolean.FALSE)},
					"WHERE " + UPLOADED_FILES_FILENAME + " = '" + filename + "'");
	}
	
	private static void update(String table, String[] cNames, String[] values, String where) throws MsgProcException {
		try {
			DBConnector db = DBConnector.getDb();
			db.update(table, cNames, values, where);
		}
		catch(SQLException e) {
			throw new MsgProcException(e.getMessage());
		}
	}


	private static void delete(String table, String where) throws MsgProcException {
		try {
			DBConnector db = DBConnector.getDb();
			db.delete(table, where);
		}
		catch(SQLException e) {
			throw new MsgProcException(e.getMessage());
		}
	}


	private static byte[] fileDigest(File file, String alg) throws IOException {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance(alg);
		}
		catch (NoSuchAlgorithmException e) {
			throw new IOException(e.getMessage());
		}
		FileInputStream in = new FileInputStream(file);
		int i = 0;
		byte[] b = new byte[(int) FILEPART_LENGTH];
		while((i = in.read(b)) >= 0) {
			digest.update(b, 0, i);
		}
		in.close();
		return digest.digest();
	}
	/*
	private static String deleteNode2(String svid, String clid) throws NullPointerException, ClassNotFoundException, SQLException, IOException {
		long i = MessageProcessor.getCount(
				CLIENT_NODES,
				CLIENT_NODES_CLID,
				"WHERE " + CLIENT_NODES_CLID + " = '" + clid + "' AND " + CLIENT_NODES_SVID + " = '" + svid + "' AND " + CLIENT_NODES_ENABLED + " = '" + MyBoolean.TRUE + "'");
		if(i == 0) {
			return CLIENT_ID_NOT_EXISTS;
		}
		if(i != 1) {
			throw new IOException(String.format(FATAL_ERROR, DUPLICATE_CLIENT_ID));
		}
		DBConnector db = DBConnector.getDb();
		db.update(
				CLIENT_NODES,
				new String[]{CLIENT_NODES_ENABLED},
				new String[]{MyBoolean.toString(MyBoolean.FALSE)},
				"WHERE " + CLIENT_NODES_CLID + " = '" + clid + "' AND " + CLIENT_NODES_SVID + " = '" + svid + "'");
		db.close();
		deleteSymNode(svid, clid);
		i = MessageProcessor.getCount(
				CLIENT_NODES,
				CLIENT_NODES_SVID,
				"WHERE " + CLIENT_NODES_SVID + " = '" + svid + "' AND " + CLIENT_NODES_ENABLED + " = '" + MyBoolean.TRUE + "'");
		if(i == 0) {
			return stopReplication(svid);
		}
		return AceData.OK_RESPONSE;
	}
	*/

	/*
	private static void deleteSymNode(String svid, String clid) throws IOException {
		File f = new File(Sys.concatenate(new String[]{SYMMETRIC_HOME, SYMMETRIC_ENGINE_PATH, String.format(ENGINE_FILENAME,  svid)},  System.getProperty("file.separator")));
		String proPath = f.getAbsolutePath();
		f = new File(Sys.concatenate(new String[]{SYMMETRIC_HOME, SYMMETRIC_SAMPLES_PATH, SQL_FILENAME}, System.getProperty("file.separator")));
		if(f.exists()) {
			if(!f.delete()) {
				throw new IOException(FILE_NOT_DELETED);
			}
		}
		if(!f.createNewFile()) {
			throw new IOException(FILE_NOT_CREATED);
		}
		FileOutputStream out = new FileOutputStream(f);
		out.write(createRemoveNodeBatch(clid).getBytes());
		out.flush();
		out.close();
		String[] pgm = null;
		pgm = new String[] {
			SUDO_COMMAND,
			Sys.concatenate(new String[]{SYMMETRIC_HOME, SYMMENTRIC_BIN, SYMMETRIC_DBIMPORT}, System.getProperty("file.separator")),
			"--engine",
			svid,
			"--properties",
			proPath,
			f.getAbsolutePath(),
		};
		exec(pgm);
		pgm = null;
		
	}
*/
	

/*
	private static String clearNodes(String svid) throws NullPointerException, InvalidPropertiesFormatException, ClassNotFoundException, SQLException {
		DBConnector db = DBConnector.getDb();
		db.update(
				CLIENT_NODES,
				new String[]{CLIENT_NODES_ENABLED},
				new String[]{MyBoolean.toString(MyBoolean.FALSE)},
				"WHERE " + CLIENT_NODES_SVID + "= '" + svid +"'");
		db.close();
		return AceData.OK_RESPONSE;
	}

*/
	private static void deleteUploadedFileparts(int grpId) throws MsgProcException {
		String filename = String.format(FILENAME_FORMAT, grpId);
		delete(
			UPLOADED_FILEPARTS,
			"WHERE " + UPLOADED_FILEPARTS_FILENAME + "= '" + filename + "'");
	}


	private static void deleteDBFile(int grpId) throws MsgProcException {
		String filename = String.format(FILENAME_FORMAT, grpId);
		File f = new File(CLIENT_DB_PATH + System.getProperty("file.separator") + filename + H2_DB_EXTENSION);
		if(f.exists()) {
			if(!f.delete()) {
				throw new MsgProcException(FATAL_ERROR, DB_FILE_DELETION_FAILS);
			}
		}
	}
	
	private static void deleteEngineFile(int grpId) throws MsgProcException {
		String filename = String.format(FILENAME_FORMAT, grpId);
		File f = new File(Sys.concatenate(new String[]{SYMMETRIC_HOME, SYMMETRIC_ENGINE_PATH, String.format(ENGINE_FILENAME,  filename)},  System.getProperty("file.separator")));
		if(f.exists()) {
			if(!f.delete()) {
				throw new MsgProcException(FATAL_ERROR, SERVER_ENGINE_DELETION_FAILS);
			}
		}
	}


	private static void deleteFileRecord(int grpId) throws MsgProcException {
		String filename = String.format(FILENAME_FORMAT, grpId);
		delete(
			UPLOADED_FILES,
			"WHERE " + UPLOADED_FILES_FILENAME + "= '" + filename + "'");
	}


	public static String getUrl(String svid) {
		return String.format(SERVER_DB_URL, CLIENT_DB_URL, svid);
	}
	
	
}
