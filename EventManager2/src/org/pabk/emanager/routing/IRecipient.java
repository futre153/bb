package org.pabk.emanager.routing;

interface IRecipient {
	int getRecipientId();
	void setRecipientId(int id);
	String getName();
	void setName(String fullName);
	boolean isEnabled();
	void enable();
	void disable();
	String getEmailAddress();
	void setEmailAddress(String eMailAddress);
}
