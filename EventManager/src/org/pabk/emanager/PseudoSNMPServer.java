package org.pabk.emanager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.pabk.emanager.util.SimpleFileFilter;

public class PseudoSNMPServer extends HandlerImpl {
	private static final String NULL_PROPERTIES_ERROR = "Properties are null. %s will be interrupted";
	private static final String KEY_PROPERTY_ERROR = "Key of property cannot be null";
	private static final String PROPERTY_NULL_ERROR  = "Property %s cannot have null value";
	private static final String DEFAULT_PROPERTY_SET = "Set default value %s for property %s";
	private static final String PROPERTY_SET = "Loaded value %s for property %s from properties.";
	private static final String PROPERTY_PARSE_INTEGER_ERROR = "Failed to parse integer for property %s from value %s in handler %s";
	private static final String PROPERTY_PARSE_LONG_ERROR = "Failed to parse long integer for property %s from value %s in handler %s";
	private static final String PROPERTY_PARSE_BYTE_ERROR = "Failed to parse byte for property %s from value %s in handler %s";
	private static final String PROPERTY_CAST_ERROR = "Failed to create an instance of %s for property %s from value %s in handler %s";
	private static final String SEPARATOR_KEY = "pseudo.separator";
	private static final String DEFAULT_SEPARATOR = ",";
	private static final String SOURCE_SERVER_KEY = "pseudo.sourceServer";
	private static final String SOURCE_FILENAME_MASK_KEY = "pseudo.sourceFileMask";
	private static final String SOURCE_DIRECTORY_KEY = "pseudo.srcDirectory";
	private static final String PSEUDO_SNMP_PORT_KEY = "pseudo.snmp.port";
	private static final String PSEUDO_POOL_INTERVAL_KEY = "pseudo.poolInterval";
	private static final String DEFAULT_PSEUDO_SNMP_PORT = "8162";
	private static final String DEFAULT_PSEUDO_POOL_INTERVAL = "6";
	private static final String FS = System.getProperty("file.separator");
	private static final String END_PSEUDO_OK = "Successfully ends pseudo server loop";
	private static final String END_PSEUDO_ERROR = "Pseudo server loop ends with following error: %s";
	
	private int port = 0;
	private String sourceDir;
	private static String sourceFilenameMask;
	private static String separator;
	private static long poolInterval;
	private static String sourceServer;
	
	
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
				execute();
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
	
	private static final String SOURCE_DIR_NOT_EXISTS = "Source directory %s not exists";
	private static final String SOURCE_DIR_NOT_DIR = "Source directory %s is not a director";
	private static final String DIRECTORY_FOUND = "Source direcory %s found";
	private static final String MASK_USED = "File filter %s will be used";
	private static final String FILES_FOUND = "Found %d files on source directory %s for mask %s";
	private static final String ERROR_PROCESING = "Following error was found when file %s was processed: %s";
	private static final String PROCESSING_OK = "File %s was successfully processed on pseudo server";
	private static final String CLOSE_ERROR = "Failed to close datagram socket";
	private static final int MAX_READED_BYTES = 4096;
	private static final String FAILED_TO_CLOSE_FILE = "Failed to close input stream for file %s. File will not be deleted";
	private static final String FILE_DELETED = "File %s was deleted";
	private static final String FAILED_TO_DELETE_FILE = "Failed to delete file %s";
	private static final String FAILED_TO_DELETE_FILE_ERROR = "Failed to delete file %s for error: %s";
	private static final long DATAGRAM_INTERVAL = 1000;
	
	private void execute() {
		String error = null;
		File srcDir = null;
		FileFilter filter = null;
		Sleeper s = new Sleeper();
		try {
			srcDir = new File(FS + FS + sourceServer + FS + sourceDir);
			if(!srcDir.exists()) {
				throw new IOException(String.format(SOURCE_DIR_NOT_EXISTS, srcDir.getAbsolutePath()));
			}
			if(!srcDir.isDirectory()) {
				throw new IOException(String.format(SOURCE_DIR_NOT_DIR, srcDir.getAbsolutePath()));
			}
			filter = new SimpleFileFilter(sourceFilenameMask);
			log.info(String.format(DIRECTORY_FOUND, srcDir.getAbsolutePath()));
			log.info(String.format(MASK_USED, filter.toString()));
			File[] list = srcDir.listFiles(filter);
			log.info(String.format(FILES_FOUND, list.length, srcDir.getAbsolutePath(), filter.toString()));
			PseudoSNMPServer.orderFiles(list);
			DatagramSocket ds = null;
			DatagramPacket dp = null;
			FileInputStream in = null;
			byte[] b = new byte[MAX_READED_BYTES];
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			for(int i = 0; i < list.length; i ++) {
				try {
					out.reset();
					in = new FileInputStream(list[i]);
					int j = 0;
					while((j = in.read(b)) >= 0) {
						out.write(b, 0, j);
					}
					ds = new DatagramSocket();
					ds.connect(InetAddress.getLocalHost(), port);
					dp = new DatagramPacket(out.toByteArray(), out.size(), ds.getInetAddress(), ds.getPort());
					ds.send(dp);
					log.info(String.format(PROCESSING_OK, list[i].getAbsolutePath()));
					try {
						in.close();
						try {
							if(list[i].delete()) {
								log.info(String.format(FILE_DELETED, list[i].getAbsolutePath()));
							}
							else {
								log.warning(String.format(FAILED_TO_DELETE_FILE, list[i].getAbsolutePath()));
							}
						}
						catch (Exception e) {
							log.warning(String.format(FAILED_TO_DELETE_FILE_ERROR, list[i].getAbsolutePath(), e.getMessage()));
						}
					}
					catch(IOException e) {
						log.severe(String.format(FAILED_TO_CLOSE_FILE, list[i].getAbsolutePath()));
					}
					finally {
						in = null;
					}
				}
				catch(Exception e) {
					log.warning(String.format(ERROR_PROCESING, list[i].getAbsoluteFile(), e.getMessage()));
				}
				finally {
					dp = null;
					try {
						ds.close();
					}
					catch(Exception e) {
						log.warning(String.format(CLOSE_ERROR));
					}
					ds = null;
					s.sleep(DATAGRAM_INTERVAL);
				}
			}
		}
		catch (Exception e) {
			error = e.getMessage();
		}
		if(error == null) {
			this.log.info(String.format(END_PSEUDO_OK));
		}
		else {
			this.log.warning(String.format(END_PSEUDO_ERROR, error));
		}

	}

	private static void orderFiles(File[] list) {
		File x;
		for(int i = 0; i < (list.length - 1); i ++) {
			for(int j = i + 1; j < list.length; j ++) {
				if(list[i].lastModified() > list[j].lastModified()) {
					x = list[i];
					list[i] = list[j];
					list[j] = x;
				}
			}
		}
	}

	private void loadSettings() throws IOException {
		separator = (String) getProperty(this, SEPARATOR_KEY, DEFAULT_SEPARATOR, true, String.class, null);
		sourceServer = (String) getProperty(this, SOURCE_SERVER_KEY, null, true, String.class, separator);
		sourceDir = (String) getProperty(this, SOURCE_DIRECTORY_KEY, null, true, String.class, separator);
		sourceFilenameMask = (String) getProperty(this, SOURCE_FILENAME_MASK_KEY, null, false, String.class, separator);
		port = (int) getProperty(this, PSEUDO_SNMP_PORT_KEY, DEFAULT_PSEUDO_SNMP_PORT, true, int.class, separator);
		poolInterval = ((long) getProperty(this, PSEUDO_POOL_INTERVAL_KEY, DEFAULT_PSEUDO_POOL_INTERVAL, true, long.class, separator)) * 60 * 1000;
		
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
