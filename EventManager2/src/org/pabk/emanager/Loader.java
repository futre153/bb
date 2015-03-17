package org.pabk.emanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.pabk.emanager.routing.Distribution;
import org.pabk.emanager.util.Const;
import org.pabk.emanager.util.Sys;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.util.StatusPrinter;

public class Loader {
	
	private static Properties props;
	private static Logger log;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Properties dp = new Properties();
		props = new Properties(dp);
		try {
			if(args.length > 0 && args[0] != null) {
				try {
					props.loadFromXML(new FileInputStream(args[0]));
				}
				catch (Exception e) {
					try {
						props.loadFromXML(Loader.class.getResourceAsStream(args[0]));
					} catch (Exception e1) {}
				}
			}
			Loader.log = initLogger(props);
			File distriburionList = (File) Sys.getProperty(Loader.class, Const.DISTRIBUTION_LIST_KEY, Const.DISTRIBUTION_LIST_VALUE, true, File.class, null);
			File codeRepositoryList = (File) Sys.getProperty(Loader.props, Const.CODE_REPOSITORY_LIST_KEY, Const.CODE_REPOSITORY_LIST_VALUE, true, File.class, null);
			Distribution.init(distriburionList, codeRepositoryList);
			log.info("Distribution was successfully loaded");
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	
	
	static Logger initLogger (Object obj) throws IOException {
		String name = obj == null || obj instanceof Properties ? Loader.class.getName() : obj.getClass().getName();
		String logDirName = (obj == null|| obj instanceof Properties ? Loader.class.getSimpleName() : obj.getClass().getSimpleName()).toLowerCase();
		name = (String) Sys.getProperty(obj, Const.LOG_NAME, name, true, String.class, null);
		String pattern = (String) Sys.getProperty(obj, Const.LOG_PATTERN, Const.LOG_PATTERN_DEFAULT_VALUE, true, String.class, null);
		String directory = (String) Sys.getProperty(obj, Const.LOG_DIRECTORY, null, false, String.class, null);
		directory = directory == null ? Const.LOG_DIRECTORY_DEFAULT_VALUE + Const.FS + logDirName : directory;
		String extension = (String) Sys.getProperty(obj, Const.LOG_EXTENSION, Const.LOG_EXTENSION_DEFAULT_VALUE, true, String.class, null);
		String dateFormat = (String) Sys.getProperty(obj, Const.LOG_DATE_FORMAT_IN_ARCHIVE_FILENAME, Const.LOG_DATE_FORMAT_IN_ARCHIVE_FILENAME_DEFAULT_VALUE, true, String.class, null);
		String archiveExtension = (String) Sys.getProperty(obj, Const.LOG_ARCHIVE_FILENAME_EXTENSION, Const.LOG_ARCHIVE_FILENAME_EXTENSION_DEFAULT_VALUE, true, String.class, null);
		int history = (int) Sys.getProperty(obj, Const.LOG_HISTORY, Const.LOG_HISTORY_DEFAULT_VALUE, true, int.class, null);
		//System.setProperty("logback.debug", "true");
		
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		
		PatternLayoutEncoder ple = new PatternLayoutEncoder();
		ple.setPattern(pattern);
		ple.setContext(lc);
		ple.start();
		
		RollingFileAppender<ILoggingEvent> fileApp = new RollingFileAppender<ILoggingEvent> ();
		//File f = new File(props.getProperty(Transporter.LOG_DIRECTORY) + System.getProperty("file.separator") + name + props.getProperty(Transporter.LOG_EXTENSION));
		fileApp.setFile(directory + Const.FS + logDirName + extension);
		//fileApp.setFile(f.getAbsolutePath());
		fileApp.setContext(lc);
				
		TimeBasedRollingPolicy<ILoggingEvent> tbrp = new TimeBasedRollingPolicy<ILoggingEvent>();
		tbrp.setParent(fileApp);
		//f = new File(props.getProperty(Transporter.LOG_DIRECTORY) + System.getProperty("file.separator") + name + ".%d{yyyy-MM-dd}" + props.getProperty(Transporter.LOG_EXTENSION) + ".zip");
		tbrp.setFileNamePattern(directory + Const.FS + logDirName + dateFormat + extension + archiveExtension);
		tbrp.setCleanHistoryOnStart(false);
		//tbrp.setFileNamePattern(f.getAbsolutePath());
		tbrp.setContext(lc);
		tbrp.setMaxHistory(history);
		tbrp.start();
		
		fileApp.setRollingPolicy(tbrp);
		fileApp.setEncoder(ple);
		fileApp.start();
		
		Logger log = lc.getLogger(name);
		log.addAppender(fileApp);
		
		StatusPrinter.print(lc);
		
		log.info(String.format(Const.LOGGER_STARTED, name));
		return log;
	}

}
