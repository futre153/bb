package org.pabk.emanager.parser.fin;

public interface ApplicationHeaderBlock {
	void setIOIdentifier(char io);
	char getIOIdentifier();
	void setMessageType(String mt);
	String getMessageType();
	String getDestinationAddress();
	void setDestinationAddress(String da);
	char getMessagePriority();
	void setMessagePriority(char mp);
	void setDeliveryMonitoring(char dm);
	char getDeliveryMonitoring();
	void setObsolencePeriod(String op);
	String setObsolencePeriod();
	String getGMTTime();
	void setGMTTime(String gmt);
	void setMessageInputReference(String mir);
	String getMessageInputReference();
	String getLocalTime();
	String getLocalDate();
	void setLocalTime(String time);
	void setLocalDate(String date);
	boolean isU2UMessage();
	boolean isGPUMessage();
	boolean isU2SMessage();
	boolean isInput();
	boolean isOutput();
}
