package org.pabk.emanager;

import java.io.File;
import java.util.Properties;

import org.pabk.emanager.routing.Distribution;
import org.pabk.emanager.util.Const;
import org.pabk.emanager.util.Sys;

import ch.qos.logback.classic.Logger;

public class Loader {
	
	private static Properties props;
	private static Logger log;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			if(args.length > 0 && args[0] != null) {
				props = Sys.loadProperties(args[0], null);
				/*
				try {
					props.loadFromXML(new FileInputStream(args[0]));
				}
				catch (Exception e) {
					try {
						props.loadFromXML(Loader.class.getResourceAsStream(args[0]));
					} catch (Exception e1) {}
				}
				*/
			}
			Loader.log = Sys.initLogger(Loader.class);
			Sys.setDefaultLogger(log);
			File distriburionList =   (File) Sys.getProperty(Loader.class, Const.DISTRIBUTION_LIST_KEY,    Const.DISTRIBUTION_LIST_VALUE,    true, File.class, null);
			File codeRepositoryList = (File) Sys.getProperty(Loader.class, Const.CODE_REPOSITORY_LIST_KEY, Const.CODE_REPOSITORY_LIST_VALUE, true, File.class, null);
			Distribution.init(distriburionList, codeRepositoryList);
			log.info("Distribution was successfully loaded");
			Manager manager = new Manager();
			manager.init();
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static Properties getProperties () {
		return Loader.props;
	}
	public static Logger getLogger() {
		return Loader.log;
	}
	
	

}
