package org.pabk.emanager;

import java.io.IOException;
import java.util.Properties;

import org.pabk.emanager.util.Const;
import org.pabk.emanager.util.Sys;

import ch.qos.logback.classic.Logger;

abstract class AModule extends Thread implements IModule {
	
	private static final int WORKING_MASK 			= 0x01;
	private static final int INITIALIZED_MASK 		= 0x02;
	private static final int STARTED_MASK			= 0x04;
	private static final int ON_ERROR_MASK			= 0x08;
	
	private static final int ACTION_SET = 0x01;
	private static final int ACTION_DEL = 0x02;
	private static final int ACTION_XOR = 0x03;
		
	private transient int moduleState;
	private Properties props;
	private Logger log;
	private String[] neededModules = null;
	private String name;
	private int neededModulesCheckMaxLoop = Const.NEEDED_MODULES_MAX_LOOP_DEFAULT_VALUE;
	private long neededModulesWaitInterval = Const.NEEDED_MODULES_WAIT_INTERVAL_DEFAULT_VALUE;
	
	public void init() {
		this.setModuelState(ACTION_SET, WORKING_MASK | INITIALIZED_MASK);
		try {
			this.props = Sys.loadProperties((String) Sys.getProperty(Loader.class, this.getClass().getName() + Const.PROPERTIES_PATH_KEY), null);
			this.log = Sys.initLogger(this);
			this.name = this.getClass().getSimpleName().toLowerCase();
			Sys.log(this.log, null, Const.INFO, Const.SET_MODULE_NAME, this.getModuleName());
			this.neededModulesCheckMaxLoop = (int) Sys.getProperty(Loader.class, Const.NEEDED_MODULES_MAX_LOOP_KEY, this.neededModulesCheckMaxLoop, true, int.class);
			this.neededModulesWaitInterval = (long) Sys.getProperty(Loader.class, Const.NEEDED_MODULES_WAIT_INTERVAL_KEY, this.neededModulesWaitInterval, true, long.class);
			this.neededModules = (String[]) Sys.getProperty(this, this.getClass().getName() + Const.NEEDED_MODULES_KEY);
			if(!checkNeededModules()) {
				String msg = String.format(Const.FAILED_NEEDED_MODULES_CHECK, this.getModuleName());
				Sys.log(log, null, Const.FATAL, msg);
				throw new IOException(msg);
			}
			Sys.log(log, null, Const.INFO, Const.NEEDED_MODULES_CHECK_SUCCESS, this.getModuleName());
			this.setModuelState(ACTION_XOR, WORKING_MASK | INITIALIZED_MASK | STARTED_MASK);
		}
		catch (IOException e) {
			this.setModuelState(ACTION_XOR, WORKING_MASK | INITIALIZED_MASK | ON_ERROR_MASK);
			Sys.log(this.log, null, Const.FATAL, Const.FAILED_INIT_MODULE, this.getModuleName());
		}
	}
	
	@Override
	public Object getProperties() {
		return this.props;
	}

	@Override
	public Object getLogger() {
		return this.log;
	}
		
	public boolean isWorking() {
		return (getModuleState() & WORKING_MASK) > 0;
	}
	
	public boolean isSleeping() {
		return ! isWorking();
	}
	
	public boolean isInitialized() {
		return (getModuleState() & INITIALIZED_MASK) > 0;
	}
	
	public boolean isStarted() {
		return (getModuleState() & STARTED_MASK) > 0;
	}
	
	public boolean isOnError() {
		return (getModuleState() & ON_ERROR_MASK) > 0;
	}
			
	protected boolean checkNeededModules() {
		boolean loop = false;
		if(this.neededModules != null) {
			for (int j = 0; j < this.neededModulesCheckMaxLoop; j ++) {
				if(j > 0) {
					try {
						wait (this.neededModulesWaitInterval);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				loop = false;
				for (int i = 0; i < this.neededModules.length; i ++) {
					AModule module = Manager.getModule(this.neededModules[i]);
					if(module != null) {
						if (module.isStarted()) {
							continue;
						}
						loop = true;
						if(!module.isAlive()) {
							Manager.requestToStart(this.neededModules[i]);
							Sys.log(log, null, Const.INFO, Const.NEEDED_MODULE_REQUEST_TO_START, this.neededModules[i], this.getModuleName());
						}
						else if (module.isOnError()) {
							Sys.log(log, null, Const.FATAL, Const.FAILED_NEEDED_CHECK_MODULE_ON_ERROR, this.neededModules[i], this.getModuleName());
						}
					}
					else {
						loop = true;
						Manager.loadModule(this.neededModules[i]);
						Sys.log(log, null, Const.INFO, Const.NEEDED_MODULE_REQUEST_TO_LOAD, this.neededModules[i], this.getModuleName());
					}
				}
				if(!loop) {
					break;
				}
			}
		}
		return !loop;
	}
	
	protected synchronized void setModuelState(int action, int mask) {
		switch (action) {
		case ACTION_SET:
			setModuleState(getModuleState() | mask);
			break;
		case ACTION_DEL:
			setModuleState((getModuleState() | mask) ^ mask);
			break;
		case ACTION_XOR:
			setModuleState(getModuleState() ^ mask);
			break;
		default:
		}
	}
	
	public synchronized void setModuleState(int i) {
		this.moduleState = i;
	}
	
	public synchronized int getModuleState() {
		return moduleState;
	}

	public final String getModuleName() {
		return name;
	}
}
