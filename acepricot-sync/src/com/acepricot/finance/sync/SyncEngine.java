package com.acepricot.finance.sync;

import java.io.IOException;
import java.util.Hashtable;

public class SyncEngine extends Hashtable <String, GroupNode> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final SyncEngine globalEngine = new SyncEngine(); 
	
	
	private SyncEngine() {
		
	};
	
	private static void startEngine() throws IOException {
		//int[] grpIds = JSONMessageProcessor.retrieveActiveGroups;
	}
	
	
	public static long nodeStop(int grpId) {
		return grpId;
		// TODO Auto-generated method stub
		
	}

	public static void nodeStart(int grpId) {
		// TODO Auto-generated method stub
		
		
	}

	public static void addEngine(String enginesPropertiesFilename) {
		// TODO Auto-generated method stub
		
	}

	public static boolean isStarted(int grpId, int devId) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
