package org.pabk.emanager.routing;

public class Group extends XRecipients implements IGroup {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	@Override
	public String getName() {
		return this.name;
	}
	@Override
	public XRecipients getAllRecipients() {
		return this;
	}
	@Override
	public XRecipients getEnabledRecipients() {
		XRecipients active = new XRecipients();
		active.setMainRecipient(this.getMainRecipient());
		for(int i = 0; i < this.size(); i ++) {
			if(this.get(i).isEnabled()) {
				active.add(this.getMainRecipient());
			}
		}
		return active;
	}
	public void setName(String name) {
		this.name = name;		
	}
	
	
}
