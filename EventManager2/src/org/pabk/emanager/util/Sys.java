package org.pabk.emanager.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Properties;

import org.pabk.emanager.Sleeper;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.util.StatusPrinter;

public class Sys {
	
	private static final Marker FATAL_MARKER = MarkerFactory.getMarker(Const.FATAL_TEXT);
	private static Logger logger;
	
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
					throw new IOException(String.format(Const.PROPERTY_PARSE_INTEGER_ERROR, key, value, handlerName));
				}
			}
			else if(_class.equals(byte.class)) {
				try {
					return (byte) Integer.parseInt(value);
				}
				catch(Exception e) {
					throw new IOException(String.format(Const.PROPERTY_PARSE_BYTE_ERROR, key, value, handlerName));
				}
			}
			else if(_class.equals(long.class)) {
				try {
					return Long.parseLong(value);
				}
				catch(Exception e) {
					throw new IOException(String.format(Const.PROPERTY_PARSE_LONG_ERROR, key, value, handlerName));
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
	
	/*
	 * 
	 */
	
	private static final Object getProperty2 (Object obj, Logger log, Properties props, String key, String def, boolean notNull, Class<?> clazz, String separator) throws IOException {
		Object val = (props != null && key != null) ? props.getProperty(key, def) : def;
		if(val == null) {
			if(notNull) {
				Sys.log(log, null, Const.FATAL, Const.PROPERTY_NULL_ERROR, key, obj.getClass().getSimpleName());
				throw new IOException (String.format(Const.PROPERTY_NULL_ERROR, key, obj.getClass().getSimpleName()));
			}
		}
		else {
			try {
				val = parse(clazz, (String) val, key, obj != null ? obj.getClass().getSimpleName() : null);
			}
			catch (Exception e) {
				try {
					val = clazz.getDeclaredConstructor(String.class).newInstance(val);
				} catch (Exception e1) {
					try {
						String[] array = ((String) val).split(separator);
						obj = Array.newInstance(clazz, array.length);
						for(int i = 0; i < array.length; i ++) {
							Array.set(obj, i, parse(clazz, array[i], key, obj != null ? obj.getClass().getSimpleName() : null));
						}
						Sys.log(log, null, Const.INFO, Const.PROPERTY_PARSED_TO_ARRAY, key, obj);
						val = obj;
					}
					catch (Exception e2) {
						throw new IOException(String.format(Const.PROPERTY_CAST_ERROR, clazz.getSimpleName(), key, val, obj.getClass().getSimpleName()));
					}
				}
			}
			
		}
		Sys.log(log, null, Const.INFO, Const.PROPERTY_VALUE, val, key, obj.getClass().getSimpleName());
		return val;
		
		
	}
	
	
	
	/*
	 * 0 - object
	 * 1 - key
	 * 2 - default value
	 * 3 - not null
	 * 4 - 
	 */
	public static final Object getProperty (Object ... objs) throws IOException {
		Properties props = getProperties(objs[0]);
		return getProperty2 (
				objs[0],
				Sys.getLogger(props, objs[0]),
				props,
				(String) (objs != null && objs.length > 1 && (objs[1] instanceof String) ? objs[1] : null),
				(String) (objs != null && objs.length > 2 ? objs[2].toString() : null),
				(boolean) (objs != null && objs.length > 3  && (objs[3] instanceof Boolean) ? objs[3] : false),
				(Class<?>) (objs != null && objs.length > 4  && (objs[4] instanceof Class<?>) ? objs[4] : String.class),
				(String) (objs != null && objs.length > 5  && (objs[5] instanceof String) ? objs[5] : Const.DEFAULT_PROPERTY_SEPARATOR));
	}
	
	public static void log(Logger log, PrintStream ps, int severity, String message, Object ... args) {
		String fMsg = String.format(message, args);
		if(log != null) {
			switch(severity) {
			case Const.ALL:
			case Const.TRACE:
				log.trace(fMsg);
				break;
			case Const.DEBUG:
				log.debug(fMsg);
				break;
			case Const.INFO:
				log.info(fMsg);
				break;
			case Const.WARN:
				log.warn(fMsg);
				break;
			case Const.ERROR:
				log.error(fMsg);
				break;
			case Const.FATAL:
				log.error(FATAL_MARKER, fMsg);
			case Const.OFF:
			default:
				break;
			}
		}
		else {
			(ps == null ? System.out : ps).println(fMsg);
		}
	}
	
	private static Properties getProperties(Object obj) {
		try {
			return (Properties) ((obj != null && (obj instanceof Properties)) ? obj : Sys.invoke(obj, Const.GET_FUNCTION_NAME + Properties.class.getSimpleName(), null, null)); 
		}
		catch (Exception e) {
			return null;
		}
	}
	
	
	
	private static Logger getLogger(Properties prop, Object obj) {
		Logger log = null;
		if(prop != null) {
			LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
			String loggerName = prop.getProperty(Const.LOG_NAME);
			log = loggerName != null ? log = lc.exists(loggerName) : null;
		}
		if(log == null) {
			try {
				log = (Logger) Sys.invoke(obj, Const.GET_FUNCTION_NAME + Logger.class.getSimpleName(), null, null);
				log = (log == null) ? Sys.logger : log;
			}
			catch (Exception e) {
				log = Sys.logger;
			}
		}
		return log;
	}

	private static Object invoke(Object object, String methodName, Class<?>[] args, Object[] params) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return ((object instanceof Class<?>) ? (Class<?>) object : object.getClass()).getDeclaredMethod(methodName, args).invoke((object instanceof Class) ? null : object, params);
	}
	
	public static Logger initLogger (Object obj) throws IOException {
		
		obj = obj == null ? Const.MAIN_CLASS : obj;
		String name 			= obj instanceof Class<?> ? ((Class<?>) obj).getName() : obj.getClass().getName();
		String pattern 			= Const.LOG_PATTERN_DEFAULT_VALUE;
		String filename 		= (obj instanceof Class<?> ? ((Class<?>) obj).getSimpleName() : obj.getClass().getSimpleName()).toLowerCase();
		String directory 		= Const.LOG_DIRECTORY_DEFAULT_VALUE + Const.FS + filename;
		String extension 		= Const.LOG_EXTENSION_DEFAULT_VALUE;
		String dateFormat 		= Const.LOG_DATE_FORMAT_IN_ARCHIVE_FILENAME_DEFAULT_VALUE;
		String archiveExtension = Const.LOG_ARCHIVE_FILENAME_EXTENSION_DEFAULT_VALUE;
		String historyDef 		= Const.LOG_HISTORY_DEFAULT_VALUE;
		
		name 				= (String) Sys.getProperty(obj, Const.LOG_NAME, 							name,				true, String.class, null);
		pattern 			= (String) Sys.getProperty(obj, Const.LOG_PATTERN,							pattern,			true, String.class, null);
		directory 			= (String) Sys.getProperty(obj, Const.LOG_DIRECTORY,						directory,			true, String.class, null);
		extension 			= (String) Sys.getProperty(obj, Const.LOG_EXTENSION,						extension,			true, String.class, null);
		dateFormat 			= (String) Sys.getProperty(obj, Const.LOG_DATE_FORMAT_IN_ARCHIVE_FILENAME,	dateFormat,			true, String.class, null);
		archiveExtension 	= (String) Sys.getProperty(obj, Const.LOG_ARCHIVE_FILENAME_EXTENSION,		archiveExtension,	true, String.class, null);
		int history 		= (int)    Sys.getProperty(obj, Const.LOG_HISTORY,							historyDef,			true, int.class,	null);
		
		Sys.createDirectory(directory);
				
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		Logger log = lc.exists(name);
		if(log == null) {
		
			PatternLayoutEncoder ple = new PatternLayoutEncoder();
			ple.setPattern(pattern);
			ple.setContext(lc);
			ple.start();
			
			RollingFileAppender<ILoggingEvent> fileApp = new RollingFileAppender<ILoggingEvent> ();
			fileApp.setFile(directory + Const.FS + filename + extension);
			fileApp.setContext(lc);
					
			TimeBasedRollingPolicy<ILoggingEvent> tbrp = new TimeBasedRollingPolicy<ILoggingEvent>();
			tbrp.setParent(fileApp);
			tbrp.setFileNamePattern(directory + Const.FS + filename + dateFormat + extension + archiveExtension);
			tbrp.setCleanHistoryOnStart(false);
			tbrp.setContext(lc);
			tbrp.setMaxHistory(history);
			tbrp.start();
			
			fileApp.setRollingPolicy(tbrp);
			fileApp.setEncoder(ple);
			fileApp.start();
			
			log = lc.getLogger(name);
			log.addAppender(fileApp);
			
			StatusPrinter.print(lc);
			
			log.info(String.format(Const.LOGGER_STARTED, name));
		}
		else {
			log.info(String.format(Const.LOGGER_ASING, name));
		}
		return log;
	}
	
	
	public static final String concatenate(Object[] array, Character delimiter) {
		if(array==null)return null;
		StringBuffer buf=new StringBuffer();
		for(int i=0;i<array.length;i++) {
			if(i>0 && delimiter!=null) {
				buf.append(delimiter);
			}
			buf.append(array[i]);
		}
		return buf.toString();
	}
	public static String createTmpFile(byte[] parseExpression) throws IOException {
		String path=Const.TMP_PATH+System.getProperty("file.separator");
		String name=null;
		File f;
		while(true) {
			name=Const.TMP_FILENAME+Calendar.getInstance().getTimeInMillis();
			f=new File(path+name);
			if(!f.exists()) break;
			new Sleeper().sleep(10);
		}
		f.createNewFile();
		FileOutputStream out=new FileOutputStream(f);
		out.write(parseExpression);
		out.close();
		return f.getName();
	}

	public static void createDirectory(String directory) throws IOException {
		File dir = new File(directory);
		if (dir.exists()) {
			if(!dir.isDirectory()) {
				throw new IOException (String.format(Const.FAILED_CREATE_DIRECTORY, directory, dir.getAbsolutePath()));
			}
		}
		else {
			try {
				dir.mkdirs();
			}
			catch (Exception e) {
				throw new IOException (e);
			}
		}
	}

	public static void setDefaultLogger(Logger log) {
		Sys.logger = log;		
	}

	public static Properties loadProperties(String path, Properties def) {
		if(path == null) {
			return null;
		}
		File f = new File (path);
		Properties props;
		if(def == null) {
			props = new Properties();
		}
		else {
			props = new Properties(def);
		}
		InputStream in = null;
		if(f.exists() && f.isFile()) {
			try {
				in = new FileInputStream (f);
				props.loadFromXML(in);
				in.close();
			}
			catch (Exception e) {
				props = null;
			}
		}
		else {
			try {
				in = Sys.class.getResourceAsStream(path);
				props.loadFromXML(in);
				in.close();
			}
			catch (Exception e) {
				props = null;
			}
		}
		return props;
	}
	
}
