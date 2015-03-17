package org.pabk.emanager;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

abstract class HandlerImpl extends Thread implements EventHandler {

	protected Logger log;
	protected Properties pro;
	protected String[] needed;//=new String[]{};
	protected int needLoop=0;
	protected boolean shutdown=false;
	protected Sleeper sleep;
	
	public void init(Object args[]) {
		String name=this.getClass().getSimpleName().toLowerCase();
		String proPath=Loader.getProperty(Const.MAIN_CONF_PATH_KEY)+
				System.getProperty(Const.FILE_SEPARATOR)+
				name+
				Const.PROPS_FILE_EXT;
		try {
			pro=Loader.loadProperties(Loader.getDefaultProperties(), proPath);
		}
		catch (IOException e) {
			e.printStackTrace();
			Loader.getMainLog().severe("Application "+name+" failed to load due to properties file "+proPath+" is missing or corrupt!");
			return;
		}
		log=Loader.initLogger(name,pro);
		log.info(name+": logger "+log.getName()+" is successfully initailized!");
		needed=HandlerImpl.loadNeededModules(pro);
	}
	
	private static String[] loadNeededModules(Properties p) {
		String n=p.getProperty(Const.NEEDED_MODULES_KEY);
		if(n!=null) {
			return n.split(Loader.getProperty(Const.MODULES_SEPARATOR_KEY)); 
		}
		return new String[]{};
	}

	public void run() {
		
		log.info("Module "+this.getClass().getName()+" is started!");
		do {
			if(checkNeededModules()) {
				needLoop=0;
				this.businessLogic();
			}
			else {
				needLoop++;
				new Sleeper().sleep(Const.NEED_LOOPS_WAIT_INTERVAL);
			}
			//System.out.println("Handler round "+this.getClass().getSimpleName()+":"+shutdown);
			if(shutdown){break;}
		}
		while(needLoop<this.maxNeedLoops());
		log.info("Module "+this.getClass().getName()+" was stopped!");
	}
	
	public boolean checkNeededModules() {
		for(int i=0;i<needed.length;i++) {
			String mod=needed[i].trim();
			if(EventManager.getEventManager().isAlive(mod)) {continue;}
			log.warning("Module "+this.getClass().getName()+" needs module "+mod+" to working!");
			return false;
		}
		return true;
	}
	
	public int maxNeedLoops() {
		return Const.DEFAULT_MAX_NEED_LOOPS;
	}
	
}
