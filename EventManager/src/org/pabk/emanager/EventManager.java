package org.pabk.emanager;

import java.util.ArrayList;

public class EventManager extends HandlerImpl {
	
	private static final EventManager em =new EventManager();
	private static final ArrayList<HandlerImpl> mods=new ArrayList<HandlerImpl>();
	
	
	
	
	
	private EventManager(){}
	
	
	
	public void init(Object[] args) {
		super.init(args);
		if(pro!=null) {
			String mods=pro.getProperty(Const.MODULES_KEY);
			if(mods!=null) {
				startApplications(mods.split(Loader.getProperty(Const.MODULES_SEPARATOR_KEY)));
			}
			else {
				log.severe("No modules are defined!");
			}
		}
		this.start();
	}

	private boolean startApplications(String[] mod) {
		for(int i=0; i<mod.length; i++) {
			try {
				HandlerImpl obj=(HandlerImpl) Class.forName(mod[i].trim()).newInstance();
				obj.setDaemon(true);
				obj.init(null);
				getMods().add(obj);
				obj.start();
				
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				e.printStackTrace();
				log.severe("Module "+mod[i].trim()+" failed to load!");
			}
		}
		return false;
	}
	
	public void businessLogic() {
		
		while(getMods().size()>0) {
			//System.out.println(getMods().size());
			try {getMods().get(0).join();}
			catch (InterruptedException e) {e.printStackTrace();}
			if(!getMods().get(0).isAlive()) {getMods().remove(0);}
		}
	}

	public static EventManager getEventManager() {return em;}



	public boolean isAlive(String name) {
		for(int i=0;i<getMods().size();i++) {
			HandlerImpl mod=getMods().get(i);
			if(mod.getClass().getName().equals(name) || mod.getClass().getSimpleName().toLowerCase().equals(name.toLowerCase())) {
				return mod.isAlive();
			}
		}
		return false;
	}



	public ArrayList<HandlerImpl> getMods() {
		return mods;
	}
	
}
