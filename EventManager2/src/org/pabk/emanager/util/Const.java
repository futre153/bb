package org.pabk.emanager.util;


public class Const {
	//published system properties
	public static final String FS = System.getProperty("file.separator");
	//main properties keys value
	public static final String DISTRIBUTION_LIST_KEY = "org.pabk.emanager.Loader.distributionList";
	public static final String CODE_REPOSITORY_LIST_KEY = "org.pabk.emanager.Loader.codeRepositoryList";
	//main properties values
	public static final String DISTRIBUTION_LIST_VALUE = "conf/distribution_list.xml";
	public static final String CODE_REPOSITORY_LIST_VALUE = "conf/code_repository.xml";
	//log properties keys value
	public static final String LOG_NAME = "logName";
	public static final String LOG_PATTERN = "logPattern";
	public static final String LOG_DIRECTORY = "logDirectory";
	public static final String LOG_EXTENSION = "logExtension";
	public static final String LOG_DATE_FORMAT_IN_ARCHIVE_FILENAME = "logDateFormatInArchiveFilename";
	public static final String LOG_ARCHIVE_FILENAME_EXTENSION = "logArchiveFileNameExtension";
	public static final String LOG_HISTORY = "logHistory";
	//log properties values
	public static final String LOG_PATTERN_DEFAULT_VALUE = "%date %level [%thread] %logger{10} [%file:%line] %msg%n";
	public static final String LOG_DIRECTORY_DEFAULT_VALUE = "logs";
	public static final String LOG_EXTENSION_DEFAULT_VALUE = ".log";
	public static final String LOG_DATE_FORMAT_IN_ARCHIVE_FILENAME_DEFAULT_VALUE = ".%d{yyyy-MM-dd}";
	public static final String LOG_ARCHIVE_FILENAME_EXTENSION_DEFAULT_VALUE = ".zip";
	public static final String LOG_HISTORY_DEFAULT_VALUE = "60";
	//database connector properties keys
	public static final String DB_DRIVER_CLASS_KEY = "org.pabk.emanager.db.dbconnector.driver";
	public static final String DB_URL_KEY = "org.pabk.emanager.db.dbconnector.url";
	public static final String DB_USERNAME_KEY = "org.pabk.emanager.db.dbconnector.user";
	public static final String DB_PASSWORD_KEY = "org.pabk.emanager.db.dbconnector.pass";
	public static final String DB_POOL_INIT_SIZE_KEY = "org.pabk.emanager.db.dbconnector.pool.initSize";
	public static final String DB_POOL_MAX_TOTAL_KEY = "org.pabk.emanager.db.dbconnector.pool.maxTotal";
	public static final String DB_POOL_MIN_IDLE_KEY = "org.pabk.emanager.db.dbconnector.pool.minIdle";
	public static final String DB_POOL_MAX_IDLE_KEY = "org.pabk.emanager.db.dbconnector.pool.maxIdle";
	public static final String DB_POOL_MAX_WAIT_MILLIS_KEY = "org.pabk.emanager.db.dbconnector.pool.maxWaitMillis";;
	//database connector properties values
	public static final String DB_POOL_INIT_SIZE_VALUE = "0";
	public static final String DB_POOL_MAX_TOTAL_VALUE = "8";
	public static final String DB_POOL_MAX_IDLE_VALUE = "8";
	public static final String DB_POOL_MIN_IDLE_VALUE = "0";
	public static final String DB_POOL_MAX_WAIT_MILLIS_VALUE = "-1";
	//error messages
	public static final String NULL_PROPERTIES_ERROR = "Properties are null. %s will be interrupted";
	public static final String KEY_PROPERTY_ERROR = "Key of property cannot be null";
	public static final String PROPERTY_NULL_ERROR  = "Property %s cannot have null value";
	public static final String PROPERTY_CAST_ERROR = "Failed to create an instance of %s for property %s from value %s in handler %s";
	public static final String PROPERTY_PARSE_INTEGER_ERROR = "Failed to parse integer for property %s from value %s in handler %s";
	public static final String PROPERTY_PARSE_LONG_ERROR = "Failed to parse long integer for property %s from value %s in handler %s";
	public static final String PROPERTY_PARSE_BYTE_ERROR = "Failed to parse byte for property %s from value %s in handler %s";
	//log info messages
	public static final String DEFAULT_PROPERTY_SET = "Set default value %s for property %s";
	public static final String PROPERTY_SET = "Loaded value %s for property %s from properties.";
	public static final String LOGGER_STARTED = "Logger for %s was successfully started";
	//miscellaneous
	public static final String PROPERTIES_FIELD_NAME = "props";
	public static final String LOGGER_FIELD_NAME = "log";
	public static final String DEFAULT_PROPERTY_SEPARATOR = ",";
	static final String TMP_PATH = "tmp";
	static final String TMP_FILENAME = "_tempfile_";
	public static final String NOT_EXECUTED = "0";
	public static final String EXECUTED = "1";
	public static final String MARKED_FOR_DELETION = "2";		
	//start instance leads to end JVM
	private Const(){System.exit(1);}
}
