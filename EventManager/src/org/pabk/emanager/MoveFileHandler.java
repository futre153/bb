package org.pabk.emanager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;

import org.pabk.emanager.util.SimpleFileFilter;

public class MoveFileHandler extends HandlerImpl {
	
	private static final String NULL_PROPERTIES_ERROR = "Properties are null. %s will be interrupted";
	private static final String KEY_PROPERTY_ERROR = "Key of property cannot be null";
	private static final String PROPERTY_NULL_ERROR  = "Property %s cannot have null value";
	private static final String DEFAULT_PROPERTY_SET = "Set default value %s for property %s";
	private static final String PROPERTY_SET = "Loaded value %s for property %s from properties.";
	private static final String PROPERTY_PARSE_INTEGER_ERROR = "Failed to parse integer for property %s from value %s in handler %s";
	private static final String PROPERTY_PARSE_LONG_ERROR = "Failed to parse long integer for property %s from value %s in handler %s";
	private static final String PROPERTY_PARSE_BYTE_ERROR = "Failed to parse byte for property %s from value %s in handler %s";
	private static final String PROPERTY_CAST_ERROR = "Failed to create an instance of %s for property %s from value %s in handler %s";
	private static final String SEPARATOR_KEY = "move.separator";
	private static final String DEFAULT_SEPARATOR = ",";
	private static final String SOURCE_SERVER_KEY = "move.sourceServer";
	private static final String OPERATION_KEY = "move.operation";
	private static final String OPERATION_MOVE = "move";
	private static final String OPERATION_CONVERT_STMT_MOVE = "convertStmtAndMove";
	private static final String DEFAULT_OPERATION = OPERATION_MOVE;
	private static final String Q_OPERATION_KEY = "move.queue.%s.operation";
	private static final String DEFAULT_SOURCE_SERVER = "192.168.210.20";
	private static final String DESTINATION_SERVER_KEY = "move.destinationServer";
	private static final String SOURCE_FILENAME_MASK_KEY = "move.sourceFileMask";
	private static final String RENAME_KEY = "move.renameFile";
	private static final String QUEUES_KEY = "move.queues";
	private static final String Q_SOURCE_SERVER_KEY = "move.queue.%s.sourceServer";
	private static final String Q_DESTINATION_SERVER_KEY = "move.queue.%s.destinationServer";
	private static final String Q_SOURCE_KEY = "move.queue.%s.source";
	private static final String Q_DESTINATION_KEY = "move.queue.%s.destination";
	private static final String Q_SOURCE_FILENAME_MASK_KEY = "move.queue.%s.sourceFileMask";
	private static final String Q_RENAME_FILENAME_KEY = "move.queue.%s.renameFile";
	private static final String PRINTER_POOL_INTERVAL_KEY = "move.poolInterval";
	private static final String DEFAULT_PRINTER_POOL_INTERVAL = "11";
	private static final String STMT_MASK_KEY = "move.stmt.originalSeq";
	private static final String STMT_REPLACEMENT_SEQUENCE_KEY = "move.stmt.replacementSeq";
	private static final String DEFAULT_STMT_MASK = "\\x09\\x09";
	private static final String DEFAULT_STMT_REPLACEMENT_SEQUENCE = "9,12";
	
	private static String separator;
	private static long poolInterval;
	private static Object[][] moveQueues;
	private static String sourceServer;
	private static String destinationServer;
	private static String sourceFilenameMask;
	private static int[] rename;
	private String operation;
	private String stmtMask;
	private byte[] stmtReplSeq;
		
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
				for(int i=0 ; i < moveQueues.length;i++) {
					execute(moveQueues[i]);
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
	
	private static final String START_MOVE = "Start moving files on queue %s";
	private static final String SRC_DIR_EXISTS = "Found source directory %s for queue %s";
	private static final String DST_DIR_EXISTS = "Found destination directory %s for queue %s";
	private static final String END_MOVE_OK = "Successfully ends moving files on queue %s";
	private static final String END_MOVE_ERROR = "Moving files on queue %s ends with following error: %s";
	private static final String SRC_DIR_NOT_EXISTS = "Source directory %s does not exist on queue %s";
	private static final String DST_DIR_NOT_EXISTS = "Destination directory %s does not exist on queue %s";
	private static final String MASK_USED = "Used filename pattern %s for input files on queue %s";
	private static final String NO_RENAME = "Output files will not be renamed on queue %s";
	private static final String APPLY_RENAME = "Rename output files %s applies on queue %s";
	private static final String FILES_FOUND = "%d files found on source directory %s that matching filter %s on queue %s";
	private static final String FS = System.getProperty("file.separator");
	private static final String FAILED_TO_RENAME = "Failed to rename filename %s for substring %s, renaming is skipped, original filename is used";
	private static final String FILE_FOUND = "File %s found on queue %s";
	private static final String FILE_ALLREADY_EXISTS = "File %s is allready exists on destination directory for queue %s. Moving of this file is skipped";
	private static final String UNKNOWN_NOVING_ERROR = "Failed to rename file from %s to %s on queue %s. Reason is unknown";
	private static final String FILE_MOVED = "File %s is successfully moved to %s no queue %s";
	private static final String OPERATION_NOT_DEFINED = "Operation %s is not defined for module %s";
	private static final int MAX_READED_BYTES = 4096;
	private static final String FAILED_DELETE = "Failed to delete file %s on queue %s";
	private static final String FAILED_CREATE = "Failed to create file %s on queue %s";
	private static final String STMT_CONVERT = "File %s was successfully stmt converted on queue %s";
	
	private void execute(Object[] objs) {
		String name =  (String) objs[0];
		this.log.info(String.format(START_MOVE, name));
		String error = null;
		File srcDir, dstDir;
		FileFilter filter;
		int[] rename = null;
		String operation = (String) objs[7];
		try {
			if(objs[1] instanceof String) {
				objs[1] = new File(FS + FS + objs[1] + FS + objs[3]);
			}
			srcDir = (File) objs[1];
			if((!srcDir.exists()) || (!srcDir.isDirectory())) {
				throw new IOException(String.format(SRC_DIR_NOT_EXISTS, srcDir.getAbsoluteFile(), name));
			}
			this.log.info(String.format(SRC_DIR_EXISTS, srcDir.getAbsolutePath(), name));
			if(objs[2] instanceof String) {
				objs[2] = new File(FS + FS + objs[2] + FS + objs[4]);
			}
			dstDir = (File) objs[2];
			if((!dstDir.exists()) || (!dstDir.isDirectory())) {
				throw new IOException(String.format(DST_DIR_NOT_EXISTS, dstDir.getAbsoluteFile(), name));
			}
			this.log.info(String.format(DST_DIR_EXISTS, dstDir.getAbsolutePath(), name));
			if(objs[5] instanceof String) {
				objs[5] = new SimpleFileFilter((String) objs[5]);
			}
			filter = (FileFilter) objs[5];
			this.log.info(String.format(MASK_USED, filter.toString(), name));
			if(objs[6] == null) {
				this.log.info(String.format(NO_RENAME, name));
			}
			else {
				rename = (int[]) objs[6];
				this.log.info(String.format(APPLY_RENAME, Arrays.toString(rename), name));
			}
			File[] list = srcDir.listFiles(filter);
			log.info(String.format(FILES_FOUND, list.length, srcDir.getAbsolutePath(), filter.toString(), name));
			for (int i = 0; i < list.length; i ++) {
				log.info(String.format(FILE_FOUND, list[i].getAbsoluteFile(), name));
				String filename = list[i].getName();
				StringBuffer newName = new StringBuffer(list[i].getName().length());
				if(rename != null) {
					try {
						for(int j = 0; j < rename.length; j += 2) {
							newName.append(filename, rename[j], rename[j + 1]);
						}
					}
					catch(Exception e) {
						log.severe(String.format(FAILED_TO_RENAME, filename, Arrays.toString(rename)));
						newName.append(list[i].getName());
					}
				}
				else {
					newName.append(list[i].getName());
				}
				File newFile = new File(dstDir.getAbsolutePath() + FS + newName.toString());
				if(newFile.exists()) {
					log.severe(String.format(FILE_ALLREADY_EXISTS, newFile.getAbsolutePath(), name));
					continue;
				}
				try {
					//main nove operations
					//move
					if(operation.equalsIgnoreCase(OPERATION_MOVE))  {
						moveOperation(list[i], newFile, name);
					}
					//convert statements and move
					else if(operation.equalsIgnoreCase(MoveFileHandler.OPERATION_CONVERT_STMT_MOVE)) {
						try {
							convertStmt(list[i], name);
							moveOperation(list[i], newFile, name);
						}
						catch(Exception e) {
							throw new IOException (e);
						}
					}
					else {
						log.warning(String.format(OPERATION_NOT_DEFINED, operation, this.getClass().getSimpleName()));
					}
				}
				catch(Exception e) {
					log.severe(e.getMessage());
					continue;
				}
				log.info(String.format(FILE_MOVED, list[i].getAbsolutePath(), newFile.getAbsoluteFile(), name));
			}
		}
		catch (Exception e) {
			error = e.getMessage();
		}
		if(error == null) {
			this.log.info(String.format(END_MOVE_OK, objs[0]));
		}
		else {
			this.log.warning(String.format(END_MOVE_ERROR, objs[0], error));
		}
	}
	
	private void convertStmt(File file, String name) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] b = new byte[MAX_READED_BYTES];
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			int i;
			while ((i = in.read(b)) >= 0) {
				out.write(b, 0, i);
			}
		}
		catch(Exception e) {
			throw new IOException(e);
		}
		finally {
			try {in.close();} catch (Exception e) {}
		}
		String _new = new String(out.toByteArray());
		try {
			_new = _new.replaceAll(stmtMask, new String(stmtReplSeq));
		}
		catch(Exception e) {
			throw new IOException(e);
		}
		try {
			if(!file.delete()) {
				throw new IOException(String.format(FAILED_DELETE, file.getAbsolutePath(), name));
			}
		}
		catch (Exception e) {
			throw new IOException(e);
		}
		try {
			if(!file.createNewFile()) {
				throw new IOException(String.format(FAILED_CREATE, file.getAbsolutePath(), name));
			}
		}
		catch (Exception e) {
			throw new IOException(e);
		}
		FileOutputStream fout = new FileOutputStream(file);
		try {
			fout.write(_new.getBytes());
			log.info(String.format(STMT_CONVERT, file.getAbsolutePath(), name));
		}
		catch(Exception e) {
			throw new IOException (e);
		}
		finally {
			try {fout.close();} catch (Exception e) {}
		}
	}

	private void moveOperation(File oldFile, File newFile, String name) throws IOException {
		boolean b = oldFile.renameTo(newFile);
		if(!b) {
			throw new IOException(String.format(UNKNOWN_NOVING_ERROR, oldFile.getAbsolutePath(), newFile.getAbsoluteFile(), name));
		}
	}
	
	
	private void loadSettings() throws IOException {
		separator = (String) getProperty(this, SEPARATOR_KEY, DEFAULT_SEPARATOR, true, String.class, null);
		sourceServer = (String) getProperty(this, SOURCE_SERVER_KEY, DEFAULT_SOURCE_SERVER, true, String.class, separator);
		destinationServer = (String) getProperty(this, DESTINATION_SERVER_KEY, null, false, String.class, separator);
		destinationServer = destinationServer == null ? sourceServer : destinationServer;
		sourceFilenameMask = (String) getProperty(this, SOURCE_FILENAME_MASK_KEY, null, false, String.class, separator);
		rename = (int[]) getProperty(this, RENAME_KEY, null, false, int.class, separator);
		rename = sourceFilenameMask == null ? null : rename;
		String[] tmp = ((String) getProperty(this, QUEUES_KEY, null, true, String.class, separator)).split(separator);
		moveQueues = new Object[tmp.length][8];
		operation = (String) getProperty(this, OPERATION_KEY, DEFAULT_OPERATION, true, String.class, separator);
		stmtMask = (String) getProperty(this, STMT_MASK_KEY, DEFAULT_STMT_MASK, true, String.class, separator);
		stmtReplSeq = (byte[]) getProperty(this, STMT_REPLACEMENT_SEQUENCE_KEY, DEFAULT_STMT_REPLACEMENT_SEQUENCE, true, byte.class, separator);
		for (int i = 0; i < moveQueues.length; i ++) {
			//meno radu
			moveQueues[i][0] = tmp[i];
			//zdrojovy server
			moveQueues[i][1] = getProperty(this, String.format(Q_SOURCE_SERVER_KEY, tmp[i]), sourceServer, true, String.class, separator);
			//cielovy server
			moveQueues[i][2] = getProperty(this, String.format(Q_DESTINATION_SERVER_KEY, tmp[i]), destinationServer, false, String.class, separator);
			//zdrojovy adresar
			moveQueues[i][3] = getProperty(this, String.format(Q_SOURCE_KEY, tmp[i]), null, true, String.class, separator);
			//cielovy adresar
			moveQueues[i][4] = getProperty(this, String.format(Q_DESTINATION_KEY, tmp[i]), null, true, String.class, separator);
			//maska vstupneho suboru na zdrojovom adresari
			moveQueues[i][5] = getProperty(this, String.format(Q_SOURCE_FILENAME_MASK_KEY, tmp[i]), sourceFilenameMask, false, String.class, separator);
			//zmena mena suboru formou substring
			moveQueues[i][6] = getProperty(this, String.format(Q_RENAME_FILENAME_KEY, tmp[i]), null, false, int.class, separator);
			moveQueues[i][6] = moveQueues[i][6] == null ? rename : moveQueues[i][6]; 
			moveQueues[i][6] = moveQueues[i][5] == null ? null : moveQueues[i][6];
			moveQueues[i][7] = getProperty(this, String.format(Q_OPERATION_KEY, tmp[i]), operation, true, String.class, separator);
		}
		poolInterval = ((long) getProperty(this, PRINTER_POOL_INTERVAL_KEY, DEFAULT_PRINTER_POOL_INTERVAL, true, long.class, separator)) * 60 * 1000;
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
