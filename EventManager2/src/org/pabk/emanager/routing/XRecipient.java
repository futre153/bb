package org.pabk.emanager.routing;

public final class XRecipient extends ARecipient {
	
	public XRecipient(int id, String name, String email, boolean enabled) {
		super();
		this.setRecipientId(id);
		this.setName(name);
		this.setEmailAddress(email);
		if(enabled) {
			this.enable();
		}
		else {
			this.disable();
		}
	}
	
	public String toString() {
		return "[" + this.getRecipientId() + "] " + this.getName() + " (" + this.getEmailAddress() + ") " + (this.isEnabled() ? "E" : "N");
	}

}
