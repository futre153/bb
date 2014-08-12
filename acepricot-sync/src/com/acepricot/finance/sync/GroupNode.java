package com.acepricot.finance.sync;

import java.util.Hashtable;

public class GroupNode extends Hashtable <String, DeviceNode> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int ACTIVE = 0x01;
	
	private int status;

	public int getStatus() {
		return this.status;
	}

}
