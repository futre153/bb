package com.acepricot.finance.sync;

import java.io.IOException;
import java.util.Hashtable;

import com.acepricot.finance.sync.share.JSONMessage;

public class SyncEngine extends Hashtable <String, GroupNode> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final SyncEngine globalEngine = new SyncEngine(); 
	
	
	private SyncEngine() {};
	
	static void startEngine() throws IOException {
		Rows rows = JSONMessageProcessor.retrieveRunnableGroups();
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

	public static JSONMessage processSyncRequest(Row row) {
		String grpName = (String) row.get(JSONMessageProcessor.REGISTERED_GROUPS.GROUP_NAME);
		//JSONMessageProcessor mp = (JSONMessageProcessor) row.get(JSONMessageProcessor.class.getName());
		GroupNode grpNode = SyncEngine.getGroupNode(grpName);
		if(grpNode != null) {
			if(grpNode.getStatus() == GroupNode.ACTIVE) {
				
			}
			return new JSONMessage().sendAppError("Grop node for group name " + grpName + " is not in active state");
		}
		return new JSONMessage().sendAppError("Grop node for group name " + grpName + " does not exists");
	}

	private static GroupNode getGroupNode(String grpName) {
		return globalEngine.get(grpName);
	}
	
}
