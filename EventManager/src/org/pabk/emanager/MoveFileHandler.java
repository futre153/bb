package org.pabk.emanager;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.logging.Logger;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.pabk.emanager.util.SimpleFileFilter;
import org.pabk.emanager.util.Sys;

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
	private static final String OPERATION_COPY = "copy";
	private static final String OPERATION_DELETE = "delete";
	private static final String OPERATION_UNZIP = "unzip";
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
	private static final String Q_OPERATION_DELAY = "move.queue.%s.operationDelay";
	private static final String Q_FORMAT_KEY = "move.queue.%s.format";
	private static final String DEFAULT_OPERATION_DELAY = "0";
	private static final String MOVE_POOL_INTERVAL_KEY = "move.poolInterval";
	private static final String DEFAULT_MOVE_POOL_INTERVAL = "11";
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
	private String zipCharset;
		
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
	
	private static final String START_MOVE = "Start %s files on queue %s";
	private static final String SRC_DIR_EXISTS = "Found source directory %s for queue %s";
	private static final String DST_DIR_EXISTS = "Found destination directory %s for queue %s";
	private static final String END_OPERATION_OK = "Successfully ends %s files on queue %s";
	private static final String END_OPERATION_ERROR = "%s files on queue %s ends with following error: %s";
	private static final String SRC_DIR_NOT_EXISTS = "Source directory %s does not exist on queue %s";
	private static final String DST_DIR_NOT_EXISTS = "Destination directory %s does not exist on queue %s";
	private static final String MASK_USED = "Used filename pattern %s for input files on queue %s";
	private static final String NO_RENAME = "Output files will not be renamed on queue %s";
	private static final String NO_FORMAT = "The name of output files will not be formated on queue %s";
	private static final String APPLY_RENAME = "Rename output files %s applies on queue %s";
	private static final String APPLY_FORMAT = "Format the filename of output files [%s] will be applied on queue %s";
	private static final String FILES_FOUND = "%d files found on source directory %s that matching filter %s on queue %s";
	private static final String FS = System.getProperty("file.separator");
	private static final String FAILED_TO_RENAME = "Failed to rename filename %s for substring %s, renaming is skipped, original filename is used";
	private static final String FAILED_TO_FORMAT = "Failed to format filename %s for expression %s, renaming is skipped, original filename is used";
	private static final String FILE_FOUND = "File %s found on queue %s";
	private static final String FILE_ALLREADY_EXISTS = "File %s is allready exists on destination directory for queue %s. The %s is skipped";
	private static final String UNKNOWN_NOVING_ERROR = "Failed to rename file from %s to %s on queue %s. Reason is unknown";
	private static final String UNKNOWN_DELETE_ERROR = "Failed to delete file from %s on queue %s. Reason is unknown";
	private static final String OPERATION_PROCESSED = "File %s is successfully %s no queue %s";
	private static final String NO_DELETE_OPERATION = "%s to %s";
	private static final String OPERATION_NOT_DEFINED = "Operation %s is not defined for module %s";
	private static final int MAX_READED_BYTES = 4096;
	private static final String FAILED_DELETE = "Failed to delete file %s on queue %s";
	private static final String FAILED_CREATE = "Failed to create file %s on queue %s";
	private static final String STMT_CONVERT = "File %s was successfully stmt converted on queue %s";
	private static final String PREPARE_TMP_FILE = "Temporary file %s was successfully prepared for copying of file %s on queue %s";
	private static final String FAILED_TMP_FILE = "Failed to prepare temporary file for copying of file %s on queue %s. Operation was aborted";
	private static final String FILE_DELETED = "File %s was successfully deleted";
	private static final String FILE_NOT_DELETED = "Failed to delete file %s. An IOException will be thrown";
	private static final String FILE_RENAMED = "File %s was successfully renamed to %s";
	private static final String FILE_NOT_RENAMED = "Failed to rename file %s to %s. An IOException will be thrown";
	private static final String FILE_NOT_DELETED_MISSING = "File %s not exists and there was not deleted";
	private static final String UNZIPPED_ALLREADEY_EXISTS = "Unzipped file %s allready exists in directory %s";
	private static final String FAILED_ZIP_ENTRY = "Failed to create zip entry %s";
	private static final String ENTRY_UNZIPPED = "Zip entry %s was successfully unzipped as temporary file %s";
	private static final String ZIP_CHARSET_KEY = "move.zipCharset";
	private static final String DEFAULT_ZIP_CHARSET = null;
	
	private void execute(Object[] objs) {
		String operation = (String) objs[7];
		String name =  (String) objs[0];
		this.log.info(String.format(START_MOVE, operation, name));
		String error = null;
		File srcDir, dstDir;
		FileFilter filter;
		int[] rename = null;
		String[] format = null;
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
				objs[5] = new SimpleFileFilter((String) objs[5], (int) objs[8]);
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
			if(objs[9] == null) {
				this.log.info(String.format(NO_FORMAT, name));
			}
			else {
				format = (String[]) objs[9];
				this.log.info(String.format(APPLY_FORMAT, Arrays.toString(format), name));
			}
			File[] list = srcDir.listFiles(filter);
			log.info(String.format(FILES_FOUND, list.length, srcDir.getAbsolutePath(), filter.toString(), name));
			for (int i = 0; i < list.length; i ++) {
				log.info(String.format(FILE_FOUND, list[i].getAbsoluteFile(), name));
				String filename = list[i].getName();
				StringBuffer newName = new StringBuffer(list[i].getName().length());
				if(rename != null && rename.length > 0) {
					try {
						for(int j = 0; j < rename.length; j += 2) {
							newName.append(filename, rename[j], rename[j + 1]);
						}
					}
					catch(Exception e) {
						log.severe(String.format(FAILED_TO_RENAME, filename, Arrays.toString(rename)));
						newName = new StringBuffer(list[i].getName().length());
						newName.append(list[i].getName());
					}
				}
				else {
					newName.append(list[i].getName());
				}
				if(format != null && format.length > 0) {
					newName = MoveFileHandler.format(newName, format, log);
				}
				File newFile = new File(dstDir.getAbsolutePath() + FS + newName.toString());
				if(newFile.exists()) {
					log.severe(String.format(FILE_ALLREADY_EXISTS, newFile.getAbsolutePath(), name, operation));
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
					//copy
					else if (operation.equalsIgnoreCase(MoveFileHandler.OPERATION_COPY)) {
						try {
							File tmp = prepareCopy(list[i], name);
							moveOperation(tmp, newFile, name);
						}
						catch(Exception e) {
							throw new IOException (e);
						}
					}
					//delete
					else if (operation.equalsIgnoreCase(MoveFileHandler.OPERATION_DELETE)) {
						deleteOperation(list[i], name);
					}
					//unzip
					else if (operation.equalsIgnoreCase(MoveFileHandler.OPERATION_UNZIP)) {
						unzip (list[i], zipCharset, name, log);
					}
					else {
						log.warning(String.format(OPERATION_NOT_DEFINED, operation, this.getClass().getSimpleName()));
					}
				}
				catch(Exception e) {
					log.severe(e.getMessage());
					continue;
				}
				log.info(String.format(OPERATION_PROCESSED, list[i].getAbsolutePath(), operation.equalsIgnoreCase(OPERATION_DELETE) ? operation : String.format(NO_DELETE_OPERATION, operation, newFile.getAbsoluteFile()), name));
			}
		}
		catch (Exception e) {
			error = e.getMessage();
		}
		if(error == null) {
			this.log.info(String.format(END_OPERATION_OK, operation, objs[0]));
		}
		else {
			this.log.warning(String.format(END_OPERATION_ERROR, operation, objs[0], error));
		}
	}
	
	private static StringBuffer format(StringBuffer newName, String[] format, Logger log) {
		String tmp = newName.toString();
		try {
			for(int i = 0; i < format.length; i ++) {
				tmp = String.format(format[i], tmp);
			}
			StringBuffer _new = new StringBuffer(tmp.length());
			_new.append(tmp);
			newName = _new;
		}
		catch (Exception e) {
			log.severe(String.format(FAILED_TO_FORMAT, newName.toString(), Arrays.toString(rename)));
		}
		return newName;
	}

	private void deleteOperation(File file, String name) throws IOException {
		if(!file.delete()) {
			throw new IOException(String.format(UNKNOWN_DELETE_ERROR, file.getAbsolutePath(), name));
		}
	}

	private File prepareCopy(File file, String name) throws IOException {
		FileInputStream in = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] b = new byte[MAX_READED_BYTES];
		try {
			in = new FileInputStream(file);
			int i;
			while ((i = in.read(b)) >= 0) {
				out.write(b, 0, i);
			}
			File f = new File(Sys.getPathOfTmpFile(Sys.createTmpFile(out.toByteArray())));
			log.warning(String.format(PREPARE_TMP_FILE, f.getAbsolutePath(), file.getAbsolutePath(), name));
			return f;
		}
		catch(Exception e) {
			log.warning(String.format(FAILED_TMP_FILE, file.getAbsolutePath(), name));
			throw new IOException(e);
		}
		finally {
			try {in.close();} catch (Exception e) {}
		}
	}
	
	
	private static void unzip (File f, String charset, String name, Logger log) throws IOException {
		File f2 = new File (Sys.getPathOfTmpFile(f.getName()));
		String path = f.getAbsolutePath();
		String fs = System.getProperty("file.separator");
		int i = path.lastIndexOf(fs);
		String dir = path.substring(0, i) + fs;
		try {
			deleteFile (f2, log);
			renameToFile(f, f2, log);
			Hashtable<String, File> files = new Hashtable<String, File>();
			ArrayList<String> dirs = new ArrayList<String>();
			try {
				unzip (dirs, files, f2, charset, dir, log);
				Iterator <String> iterator = files.keySet().iterator();
				while (iterator.hasNext()) {
					String output = iterator.next();
					File out = new File(output);
					File tmp = files.get(output);
					renameToFile (tmp, out, log);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				Iterator <String> iterator = files.keySet().iterator();
				while (iterator.hasNext()) {
					String output = iterator.next();
					File out = new File(output);
					File tmp = files.get(output);
					try {deleteFile(out, log);}	catch (Exception e2) {e2.printStackTrace();}
					try {deleteFile(tmp, log);}	catch (Exception e2) {e2.printStackTrace();}
				}
				renameToFile(f2, f, log);
				throw new IOException (e);
			}
			finally {
				deleteFile(f2, log);
				for(i = 0; i < dirs.size(); i ++) {
					new File(Sys.getPathOfTmpFile(dirs.get(i))).delete();
				}
			}
		}
		catch (Exception e) {
			throw new IOException (e);
		}
	}
	
	private static void unzip (ArrayList<String> dirs, Hashtable<String, File> files, File zip, String charset, String dir, Logger log) throws IOException {
		ZipFile zFile = null;
		//ZipInputStream zin = null;
		try {
			zFile = charset == null ? new ZipFile(zip) : new ZipFile (zip, charset);
			//zin = charset == null ? new ZipInputStream(new BufferedInputStream(new FileInputStream(zip))) : new ZipInputStream(new BufferedInputStream(new FileInputStream(zip)), charset);
			Enumeration<ZipArchiveEntry> entries = zFile.getEntries();
			ZipArchiveEntry entry = null;
			byte[] b = new byte[MAX_READED_BYTES];
			while (entries.hasMoreElements()) {
				entry = entries.nextElement();
				System.out.println(entry.getName());
				File output = new File (dir + entry.getName());
				if(entry.isDirectory()) {
					//System.out.println(entry.getName());
					output.mkdirs();
					new File(Sys.getPathOfTmpFile(entry.getName())).mkdirs();
					dirs.add(0, entry.getName());
					continue;
				}
				File tmp = new File(Sys.getPathOfTmpFile(entry.getName()) + ".tmp");
				if(tmp.exists()) {
					deleteFile(tmp, log);
				}
				if(output.exists()) {
					throw new IOException(String.format(UNZIPPED_ALLREADEY_EXISTS, output.getAbsolutePath(), dir));
				}
				BufferedOutputStream bout = null;
				InputStream in = zFile.getInputStream(entry);
				try {
					bout = new BufferedOutputStream(new FileOutputStream (tmp), MAX_READED_BYTES);
					int i = -1;
					while ((i = in.read(b)) >= 0) {
						bout.write(b, 0, i);
					}
					bout.flush();
				}
				catch (Exception e) {
					if(tmp.exists()) {
						tmp.delete();
					}
					throw new IOException (String.format(FAILED_ZIP_ENTRY, tmp.getAbsolutePath()));
				}
				finally {
					try {bout.close();} catch (IOException e) {e.printStackTrace();}
					try {in.close();} catch (IOException e) {e.printStackTrace();}
				}
				files.put(output.getAbsolutePath(), tmp);
				log.info(String.format(ENTRY_UNZIPPED, output.getAbsolutePath(), tmp.getAbsolutePath()));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new IOException (e);
		}
		finally {
			//try {zin.close();} catch (IOException e) {e.printStackTrace();}
			try {zFile.close();} catch (IOException e) {e.printStackTrace();}
		}
	}
	
	
	private static void deleteFile(File file, Logger log) throws IOException {
		try {
			String path = file.getAbsolutePath();
			if(file.exists()) {
				if(file.delete()) {
					log.info(String.format(FILE_DELETED, path));
				}
				else {
					throw new IOException (String.format(FILE_NOT_DELETED, path));
				}
			}
			else {
				log.info(String.format(FILE_NOT_DELETED_MISSING, path));
			}
		}
		catch (Exception e) {
			throw new IOException (e);
		}
	}
	
	private static void renameToFile(File src, File dst, Logger log) throws IOException {
		try {
			String srcPath = src.getAbsolutePath();
			String dstPath = dst.getAbsolutePath();
			if(src.renameTo(dst)) {
				log.info(String.format(FILE_RENAMED, srcPath, dstPath));
			}
			else {
				throw new IOException (String.format(FILE_NOT_RENAMED, srcPath, dstPath));
			}
		}
		catch (Exception e) {
			throw new IOException (e);
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
		if(!oldFile.renameTo(newFile)) {
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
		operation = (String) getProperty(this, OPERATION_KEY, DEFAULT_OPERATION, true, String.class, separator);
		stmtMask = (String) getProperty(this, STMT_MASK_KEY, DEFAULT_STMT_MASK, true, String.class, separator);
		stmtReplSeq = (byte[]) getProperty(this, STMT_REPLACEMENT_SEQUENCE_KEY, DEFAULT_STMT_REPLACEMENT_SEQUENCE, true, byte.class, separator);
		moveQueues = new Object[tmp.length][10];
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
			//typ operacie
			moveQueues[i][7] = getProperty(this, String.format(Q_OPERATION_KEY, tmp[i]), operation, true, String.class, separator);
			//pozdrzanie operacie
			moveQueues[i][8] = getProperty(this, String.format(Q_OPERATION_DELAY, tmp[i]), DEFAULT_OPERATION_DELAY, false, int.class, separator);
			//zmena mena súboru pomocou format
			moveQueues[i][9] = MoveFileHandler.getProperty(this, String.format(Q_FORMAT_KEY, tmp[i]), null, false, String.class, separator);
			if(moveQueues[i][9] instanceof String) {
				moveQueues[i][9] = new String[]{(String) moveQueues[i][9]};
			}
		}
		poolInterval = ((long) getProperty(this, MOVE_POOL_INTERVAL_KEY, DEFAULT_MOVE_POOL_INTERVAL, true, long.class, separator)) * 60 * 1000;
		String zipCharset = (String) getProperty(this, ZIP_CHARSET_KEY, DEFAULT_ZIP_CHARSET, false, String.class, separator);
		try {
			Charset.forName(zipCharset);
		}
		catch (Exception e) {
			zipCharset = null;
		}
	}
	
	
	private static final Object parse(Class<?> _class, String value, String key, String handlerName) throws IOException {
		try {
			if(_class.equals(String.class)) {
				return value;
			}
			else if(_class.equals(int.class)) {
				try {
					return Integer.parseInt(value.trim());
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

