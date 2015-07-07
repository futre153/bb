package org.pabk.emanager.routing;

public class XMsgRouting extends XRoutingCondition {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String condition;
	private Group group;
	
	protected XMsgRouting (String condition, XRoutingCondition rc, Group group) {
		super();
		this.setCondition(condition);
		if(rc != null) {
			this.setName(rc.getName());
			this.setPriority(rc.getPriority());
			this.addAll(rc);
		}
		if(group != null) {
			this.setGroup(group);
		}
	}
	
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}
}
