package org.pabk.emanager;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import org.pabk.emanager.parser.SAAEventParser2;
import org.pabk.emanager.parser.fin.Field;
import org.pabk.emanager.parser.fin.RJEFinMessage;
import org.pabk.emanager.parser.fin.SAARJEParser;
import org.pabk.emanager.util.SimpleFileFilter;

public class TrapCollector extends HandlerImpl {
	
	private static final String SEPARATOR_KEY = "trapCollector.separator";
	private static final String DEFAULT_SEPARATOR = ",";
	private static final String SOURCE_SERVER_KEY = "trapCollector.sourceServer";
	private static final String DEFAULT_SOURCE_SERVER = "192.168.210.20";
	private static final String SOURCE_FILENAME_MASK_KEY = "trapCollector.sourceFileMask";
	private static final String QUEUES_KEY = "trapCollector.queus";
	private static final String Q_INSTANCE_KEY = "trapCollector.queue.%s.instance";
	private static final String Q_SOURCE_SERVER_KEY = "trapCollector.queue.%s.sourceServer";
	private static final String Q_SOURCE_KEY = "trapCollector.queue.%s.source";
	private static final String Q_SOURCE_FILENAME_MASK_KEY = "trapCollector.queue.%s.sourceFileMask";
	private static final String MOVE_POOL_INTERVAL_KEY = "trapCollector.poolInterval";
	private static final String DEFAULT_MOVE_POOL_INTERVAL = "5";
	private static final String NULL_PROPERTIES_ERROR = "Properties are null. %s will be interrupted";
	private static final String KEY_PROPERTY_ERROR = "Key of property cannot be null";
	private static final String PROPERTY_NULL_ERROR  = "Property %s cannot have null value";
	private static final String DEFAULT_PROPERTY_SET = "Set default value %s for property %s";
	private static final String PROPERTY_SET = "Loaded value %s for property %s from properties.";
	private static final String PROPERTY_PARSE_INTEGER_ERROR = "Failed to parse integer for property %s from value %s in handler %s";
	private static final String PROPERTY_PARSE_LONG_ERROR = "Failed to parse long integer for property %s from value %s in handler %s";
	private static final String PROPERTY_PARSE_BYTE_ERROR = "Failed to parse byte for property %s from value %s in handler %s";
	private static final String PROPERTY_CAST_ERROR = "Failed to create an instance of %s for property %s from value %s in handler %s";

	private static String separator;
	private static long poolInterval;
	private static Object[][] trapQueues;
	private static String sourceServer;
	private static String sourceFilenameMask;
	private static SAARJEParser parser = new SAARJEParser();
		
	@Override
	public void businessLogic() {
		try {
			loadSettings();
			
			sleep=new Sleeper();
			while(true) {
				log.info(this.getClass().getSimpleName() + " is working");
				if(shutdown) {
					break;
				}
				for(int i=0 ; i < trapQueues.length;i++) {
					execute(trapQueues[i]);
				}
				log.info(this.getClass().getSimpleName() + " is SLEEPING");
				sleep.sleep (poolInterval);
				if(shutdown) {
					break;
				}
			}
			
			
		} catch (Exception e) {
			if (! (e instanceof IOException)) {
				e.printStackTrace();
			}
		}

	}
	
	private static final String START_TRAP_COLLECTION = "Start collection of traps on queue %s";
	private static final String SRC_DIR_EXISTS = "Found source directory %s for queue %s";
	private static final String END_OK = "Successfully ends trap capturing on queue %s";
	private static final String END_ERROR = "Failed to capture traps on queue %s. Error: %s";
	private static final String SRC_DIR_NOT_EXISTS = "Source directory %s does not exist on queue %s";
	private static final String MASK_USED = "Used filename pattern %s for input files on queue %s";
	private static final String FILES_FOUND = "%d files found on source directory %s that matching filter %s on queue %s";
	private static final String FS = System.getProperty("file.separator");
	private static final String FILE_FOUND = "File %s found on queue %s";
	private static final String FAILED_TO_PARSE = "Failed to parse file %s on queue %s. Reason is %s";
	private static final String ALLOWED_APP_ID = "01";
	private static final String ALLOWED_MT = "999";
	private static final String TEXT_CONTENT_FIELD = "79";
	private static final String SAA_TRAP_TABLE_NAME = "SAA_TRAPS";
	private static final String TRAP_FOUND = "Trap found and saved to database";
	private static final String TRAP_FAILED = "Failed to add trap due to %s";
	
	private void execute(Object[] objs) {
		String name =  (String) objs[0];
		String server = (String) objs[1];
		String instance = (String) objs[4];
		this.log.info(String.format(START_TRAP_COLLECTION, name));
		String error = null;
		File srcDir;
		FileFilter filter;
		try {
			if(objs[1] instanceof String) {
				objs[1] = new File(FS + FS + objs[1] + FS + objs[2]);
			}
			srcDir = (File) objs[1];
			if((!srcDir.exists()) || (!srcDir.isDirectory())) {
				throw new IOException(String.format(SRC_DIR_NOT_EXISTS, srcDir.getAbsoluteFile(), name));
			}
			this.log.info(String.format(SRC_DIR_EXISTS, srcDir.getAbsolutePath(), name));
			if(objs[3] instanceof String) {
				objs[3] = new SimpleFileFilter((String) objs[3]);
			}
			filter = (FileFilter) objs[3];
			this.log.info(String.format(MASK_USED, filter.toString(), name));
			File[] list = srcDir.listFiles(filter);
			log.info(String.format(FILES_FOUND, list.length, srcDir.getAbsolutePath(), filter.toString(), name));
			for (int i = 0; i < list.length; i ++) {
				log.info(String.format(FILE_FOUND, list[i].getAbsoluteFile(), name));
				FileInputStream in = null;
				try {
					in = new FileInputStream(list[i]);
					parser.parse(in, null);
				}
				catch(Exception e) {
					log.severe(String.format(FAILED_TO_PARSE, list[i].getAbsolutePath(), name, e.getMessage()));
				}
				finally {
					in.close();
					list[i].delete();
				}
			}
			while (parser.size() > 0) {
				try {
					RJEFinMessage msg = parser.remove(0);
					System.out.println(msg.getBasicHeader().getServiceId());
					System.out.println(msg.getAppHeader().getMessageType());
					System.out.println();
					if (msg.getBasicHeader().getServiceId().equals(ALLOWED_APP_ID) && msg.getAppHeader().getMessageType().equals(ALLOWED_MT)) {
						execute(msg.getText().getBlockContent(), name, server, instance);
					}
				}
				catch(Exception e) {
					log.severe(String.format(TRAP_FAILED, name, e.getMessage()));
				}
			}
			parser.clear();
		}
		catch (Exception e) {
			error = e.getMessage();
		}
		if(error == null) {
			this.log.info(String.format(END_OK, objs[0]));
		}
		else {
			this.log.warning(String.format(END_ERROR, objs[0], error));
		}
	}

	private void execute(Object content, String name, String server, String instance) throws IOException {
		try {
			ArrayList<?> fields = (ArrayList<?>) content;
			Field f = null;
			for(int i = 0; i < fields.size(); i ++) {
				Object obj = fields.get(i);
				if(obj instanceof Field) {
					if(((Field) obj).getBlockIdentifier().equals(TEXT_CONTENT_FIELD)) {
						f = (Field) obj;
						break;
					}
				}
			}
			if(f != null) {
				try {
					String[] lines = new String[f.size()];
					String[][] value=SAAEventParser2.parse(server, instance, f.toArray(lines));
					DBConnector.getDb(false, null).insert(SAA_TRAP_TABLE_NAME, value[0], value[1]);
					log.info(TRAP_FOUND);
				}
				catch (Exception e) {
					throw e;
				}
			}
		}
		catch (Exception e) {
			throw new IOException (e);
		}
	}

	private void loadSettings() throws IOException {
		separator = (String) getProperty(this, SEPARATOR_KEY, DEFAULT_SEPARATOR, true, String.class, null);
		sourceServer = (String) getProperty(this, SOURCE_SERVER_KEY, DEFAULT_SOURCE_SERVER, true, String.class, separator);
		sourceFilenameMask = (String) getProperty(this, SOURCE_FILENAME_MASK_KEY, null, false, String.class, separator);
		String[] tmp = ((String) getProperty(this, QUEUES_KEY, null, true, String.class, separator)).split(separator);
		trapQueues = new Object[tmp.length][5];
		for (int i = 0; i < trapQueues.length; i ++) {
			//meno radu
			trapQueues[i][0] = tmp[i];
			//zdrojovy server
			trapQueues[i][1] = getProperty(this, String.format(Q_SOURCE_SERVER_KEY, tmp[i]), sourceServer, true, String.class, separator);
			//zdrojový adresár
			trapQueues[i][2] = getProperty(this, String.format(Q_SOURCE_KEY, tmp[i]), null, true, String.class, separator);
			//maska vstupneho suboru na zdrojovom adresari
			trapQueues[i][3] = getProperty(this, String.format(Q_SOURCE_FILENAME_MASK_KEY, tmp[i]), sourceFilenameMask, false, String.class, separator);
			//inštancia servera
			trapQueues[i][4] = getProperty(this, String.format(Q_INSTANCE_KEY, tmp[i]), null, true, String.class, separator);
		}
		poolInterval = ((long) getProperty(this, MOVE_POOL_INTERVAL_KEY, DEFAULT_MOVE_POOL_INTERVAL, true, long.class, separator)) * 60 * 1000;
	}
	
	
	private static final Object parse(Class<?> _class, String value, String key, String handlerName) throws IOException {
		try {
			if(_class.equals(String.class)) {
				return value;
			}
			else if(_class.equals(int.class)) {
				try {
					return Integer.parseInt(value);
				}
				catch(Exception e) {
					throw new IOException(String.format(PROPERTY_PARSE_INTEGER_ERROR, key, value, handlerName));
				}
			}
			else if(_class.equals(byte.class)) {
				try {
					return (byte) Integer.parseInt(value);
				}
				catch(Exception e) {
					throw new IOException(String.format(PROPERTY_PARSE_BYTE_ERROR, key, value, handlerName));
				}
			}
			else if(_class.equals(long.class)) {
				try {
					return Long.parseLong(value);
				}
				catch(Exception e) {
					throw new IOException(String.format(PROPERTY_PARSE_LONG_ERROR, key, value, handlerName));
				}
			}
			else {
				throw new IOException("");
			}
		}
		catch(Exception e) {
			throw new IOException (e);
		}
	}
	
	
	private static final Object getProperty(HandlerImpl handler, String key, String _default, boolean notNull, Class<?> _class, String separator) throws IOException {
		try {
			if(handler.pro == null) {
				throw new IOException(String.format(NULL_PROPERTIES_ERROR, handler.getClass().getSimpleName()));
			}
			if(key == null) {
				throw new IOException(String.format(KEY_PROPERTY_ERROR));
			}
			String value = handler.pro.getProperty(key);
			if(value == null) {
				if(_default == null && notNull) {
					throw new IOException (String.format(PROPERTY_NULL_ERROR, key));
				}
				value = _default;
				handler.log.info(String.format(DEFAULT_PROPERTY_SET, value, key));
				if(value == null) {
					return value;
				}
			}
			else {
				handler.log.info(String.format(PROPERTY_SET, value, key));
			}
			try {
				return parse(_class, value, key, handler.getClass().getSimpleName());
			}
			catch (IOException e) {
				try {
					return _class.getDeclaredConstructor(String.class).newInstance(value);
				} catch (Exception e1) {
					try {
						String[] array = value.split(separator);
						if(array.length == 1 && array[0].length() == 0) {
							return Array.newInstance(_class, 0);
						}
						Object obj = Array.newInstance(_class, array.length);
						for(int i = 0; i < array.length; i ++) {
							Array.set(obj, i, parse(_class, array[i], key, handler.getClass().getSimpleName()));
						}
						return obj;
					}
					catch (Exception e2) {
						throw new IOException(String.format(PROPERTY_CAST_ERROR, _class.getSimpleName(), key, value, handler.getClass().getSimpleName()));
					}
				}
			}
		}
		catch (Exception e) {
			handler.log.severe(e.getMessage());
			throw new IOException(e);
		}
	}

}
