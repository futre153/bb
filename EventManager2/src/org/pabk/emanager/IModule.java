package org.pabk.emanager;

interface IModule {
	boolean shutdown();
	String getModuleName();
	int getModuleState();
	void doBusinessLogic();
	Object getProperties();
	Object getLogger();
	void init();
	void wakeUp();
}
