package com.acepricot.finance.sync;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;

import com.acepricot.finance.sync.share.JSONMessage;

public class SyncEngine extends Hashtable <String, GroupNode> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final SyncEngine globalEngine = new SyncEngine(); 
	
	
	private SyncEngine() {};
	
	static void startEngine() throws IOException {
		Rows rows = JSONMessageProcessor.retrieveRunnableGroups(-1);
		for(int i = 0; i < rows.size(); i ++) {
			Row row = rows.get(i);
			String grpName = (String) row.get(JSONMessageProcessor.REGISTERED_GROUPS.GROUP_NAME);
			GroupNode grpNode = SyncEngine.getGroupNode(grpName);
			if(grpNode == null) {
				try {
					grpNode = SyncEngine.nodeStart((int) row.get(JSONMessageProcessor.REGISTERED_DEVICES.GROUP_ID));
				}
				catch(IOException | SQLException e) {
					continue;
				}
			}
			String devName = (String) row.get(JSONMessageProcessor.REGISTERED_DEVICES.DEVICE_NAME);
			DeviceNode devNode = grpNode.getDeviceNode(devName);
			if(devNode == null) {
				grpNode.action(GroupNode.DEVICE_NODE_START, null, row);
			}
			else {
				continue;
			}
		}
	}
	
	
	static GroupNode nodeStart(int grpId) throws IOException, SQLException {
		GroupNode node = null;	
		Properties p = new Properties();
		p.loadFromXML(new FileInputStream(new File(JSONMessageProcessor.getEnginesPropertiesFilename(grpId))));
		JSONMessageProcessor.setPassword(p, JSONMessageProcessor.getDefaultUserPassword());
		node = new GroupNode(p);
		SyncEngine.globalEngine.put(p.getProperty(JSONMessageProcessor.REGISTERED_GROUPS.GROUP_NAME), node);
		return node;
	}
	
	public static long nodePause(int grpId, Object lock) throws IOException {
		GroupNode grpNode = SyncEngine.getGroupNode(grpId);
		if(grpNode == null) {
			throw new IOException("Group node with id = " + grpId + " does not exists");
		}
		grpNode.action(GroupNode.PAUSED, lock);
		return JSONMessageProcessor.getLastOperationId(grpId);
	}
	
	public static void nodeContinue(int grpId, Object lock) throws IOException {
		GroupNode grpNode = SyncEngine.getGroupNode(grpId);
		if(grpNode == null) {
			throw new IOException("Group node with id = " + grpId + " does not exists");
		}
		grpNode.action(GroupNode.ACTIVE, lock);
	}
	
	public static void addEngine(String enginesPropertiesFilename) {
		// TODO Auto-generated method stub
		
	}
	
	public static boolean isStarted(int grpId, int devId) {
		GroupNode grpNode = SyncEngine.getGroupNode(grpId);
		if(grpNode != null) {
			grpNode.getDeviceNode(devId);
		}
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
	
	private static GroupNode getGroupNode(int grpId) {
		Iterator<String> i = SyncEngine.globalEngine.keySet().iterator();
		while(i.hasNext()) {
			GroupNode grpNode = SyncEngine.globalEngine.get(i.next());
			if(grpNode.getGroupId() == grpId) {
				return grpNode;
			}
		}
		return null;
	}
}
