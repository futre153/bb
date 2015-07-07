package org.pabk.emanager.routing;

import java.util.ArrayList;

public class XRoutingCondition extends ArrayList<XRoutingPoint> implements IRoutingCondition {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int priority;
	private String name;

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public int getPriority() {
		return this.priority;
	}

	public void setName(String name) {
		this.name = name;		
	}

	public void setPriority(int priority) {
		this.priority = priority;		
	}

}
