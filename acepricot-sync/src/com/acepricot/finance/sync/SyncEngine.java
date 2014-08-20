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
	
	static void startEngine(int grpId) throws IOException {
		Rows rows = JSONMessageProcessor.retrieveRunnableGroups(grpId);
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
		try {
			JSONMessageProcessor.setPassword(p, JSONMessageProcessor.getDefaultUserPassword());
		} catch (Exception e) {
			throw new IOException(e);
		}
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
		return 0;
	}
	
	public static void nodeContinue(int grpId, Object lock) throws IOException {
		GroupNode grpNode = SyncEngine.getGroupNode(grpId);
		if(grpNode == null) {
			throw new IOException("Group node with id = " + grpId + " does not exists");
		}
		grpNode.action(GroupNode.ACTIVE, lock);
	}
	
	public static void addEngine(JSONMessageProcessor mp) throws IOException {
		String filename = JSONMessageProcessor.getEnginesPropertiesFilename(mp.registered_groups.id);
		Properties p = new Properties();
		p.loadFromXML(new FileInputStream(filename));
		p.setProperty(JSONMessageProcessor.REGISTERED_DEVICES.GROUP_ID, Integer.toString(mp.registered_groups.id));
		p.setProperty(JSONMessageProcessor.REGISTERED_GROUPS.GROUP_NAME, mp.registered_groups.group_name);
		p.setProperty(JSONMessageProcessor.REGISTERED_GROUPS.EMAIL, mp.registered_groups.email);
		JSONMessageProcessor.storeEngineProps(mp.registered_groups.id, p);
	}
	
	public static boolean isStarted(int grpId, int devId) {
		GroupNode grpNode = SyncEngine.getGroupNode(grpId);
		if(grpNode != null) {
			grpNode.getDeviceNode(devId);
		}
		return false;
	}
	

	public static JSONMessage processSyncRequest(Operation op) throws IOException {
		if(op.getGroupNode().getStatus() == GroupNode.ACTIVE) {
			return op.getGroupNode().action(-1, null, op);
		}
		op.setType(JSONMessage.BUSY_RESPONSE);
		return op.constructJSONMessage();
	}

	static GroupNode getGroupNode(String grpName) {
		return globalEngine.get(grpName);
	}
	
	static GroupNode getGroupNode(int grpId) {
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
