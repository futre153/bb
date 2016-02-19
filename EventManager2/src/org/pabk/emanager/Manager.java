package org.pabk.emanager;

import java.util.Hashtable;

public class Manager extends AModule {
	
	private static final Hashtable<String, AModule> modules = new Hashtable<String, AModule>(); 
		
	
	@Override
	public void doBusinessLogic() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init() {
		super.init();
	}

	@Override
	public void wakeUp() {
		// TODO Auto-generated method stub

	}

	public static AModule getModule(String moduleName) {
		return Manager.modules.get(moduleName);
	}

	public static void requestToStart(String string) {
		// TODO Auto-generated method stub
		
	}

	public static void loadModule(String string) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void doWork() {
		// TODO Auto-generated method stub
		
	}

	
}
