 package org.pabk.emanager.routing;

abstract class ARecipient implements IRecipient {

	private int id = -1;
	private String name;
	private boolean enabled;
	private String email;

	@Override
	public int getRecipientId() {
		return id;
	}

	@Override
	public void setRecipientId(int id) {
		this.id = id;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String fullName) {
		this.name = fullName;
	}

	@Override
	public boolean isEnabled() {
		return this.enabled;
	}

	@Override
	public void enable() {
		this.enabled = true;
	}

	@Override
	public void disable() {
		this.enabled = false;
	}

	@Override
	public String getEmailAddress() {
		return this.email;
	}

	@Override
	public void setEmailAddress(String eMailAddress) {
		this.email = eMailAddress;
	}
	
	public boolean equals (ARecipient rec) {
		return this.getRecipientId() == rec.getRecipientId() && rec.isEnabled() && this.isEnabled();
	}
}
