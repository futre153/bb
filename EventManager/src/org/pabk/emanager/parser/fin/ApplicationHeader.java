package org.pabk.emanager.parser.fin;

import java.io.IOException;
import java.io.InputStreamReader;

public class ApplicationHeader extends BlockImpl implements ApplicationHeaderBlock {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String IOID_PATTERN = "[OI]";

	private static final String MESSAGE_PRIORITY_PATTERN = "[\\}SUN]";
	private static final char USER_TO_USER_NORMAL_MSG = 'N';
	private static final char USER_TO_SYSTEM_MSG = 'S';
	private static final char USER_TO_USER_URGENT_MSG = 'U';

	private static final String DELIVERY_MONITORING_PATTERN = "[\\}123]";
	
	private static final char NON_DELIVERY_WARNING = '1';
	private static final char DELIVERY_NOTIF = '2';
	private static final char NON_DELIVERY_WARNING_AND_DEIVERY_NOTIF = '3';

	private static final String OBSOLENCE_PERIOD_PATTERN = "\\d{3}";
	private static final String OBSOLENCE_PERIOD_FALSE = "Obsolence period cannot have value \"%s\"";

	private static final String TIME_PATTERN = "[0-2][0-9][0-5][0-9]";

	private static final String MIR_PATTERN = "[0-9]{2}[01][0-9][0-3][0-9][A-Z]{6}[0-9A-Z]{2}[A-Z][0-9]{3}[0-9]{10}";

	private static final String DATE_PATTERN = "[0-9]{2}[01][0-9][0-3][0-9]";

	private static final String MT_PATTERN = "\\d{3}";

	private static final String OUTPUT_MESSAGE_PRIORITY_PATTERN = "[\\}S]";

	private String GMTTime = null;
	private char IOIdentifier;
	private String messageType;
	private String destinationAddress;
	private char messagePriority;
	private char deliveryMonitoring;
	private String obsolencePeriod;
	private String messageInputReference;
	private String localDate;
	private String localTime;
	
	@Override
	public Object getBlockContent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	boolean parseBlockContent(InputStreamReader in) throws IOException {
		setIOIdentifier(BlockImpl.readCharacter(in, IOID_PATTERN));
		setMessageType(BlockImpl.readTo(in, (char) 0, 3, 3, MT_PATTERN));
		if(this.isInput()) {
			parseInputAppHeader(in);
		}
		else {
			parseOutputAppHeader(in);
		}
		return false;
	}

	private void parseOutputAppHeader(InputStreamReader in) throws IOException {
		// TODO Auto-generated method stub
		setGMTTime(BlockImpl.readTo(in, (char) 0, 4, 4, TIME_PATTERN));
		setMessageInputReference(BlockImpl.readTo(in, (char) 0, 28, 28, MIR_PATTERN));
		setLocalDate(BlockImpl.readTo(in, (char) 0, 6, 6, DATE_PATTERN));
		setLocalTime(BlockImpl.readTo(in, (char) 0, 4, 4, TIME_PATTERN));
		char mp = BlockImpl.readCharacter(in, OUTPUT_MESSAGE_PRIORITY_PATTERN);
		if(mp != BasicHeader.BLOCK_END_INDICATOR) {
			setMessagePriority(mp);
		}
	}

	private void parseInputAppHeader(InputStreamReader in) throws IOException {
		setDestinationAddress(BlockImpl.readTo(in, (char) 0, 12, 12, BasicHeader.LOGICAL_TERMINAL_PATTERN));
		char mp = BlockImpl.readCharacter(in, MESSAGE_PRIORITY_PATTERN);
		if(mp != BasicHeader.BLOCK_END_INDICATOR) {
			setMessagePriority(mp);
			parseInputU2XAppHeader(in);
		}
	}

	private void parseInputU2XAppHeader(InputStreamReader in) throws IOException {
		char dm = BlockImpl.readCharacter(in, DELIVERY_MONITORING_PATTERN);
		char mp = getMessagePriority();
		if(dm != BasicHeader.BLOCK_END_INDICATOR) {
			if((mp == USER_TO_USER_NORMAL_MSG && dm == DELIVERY_NOTIF) || ((mp == USER_TO_USER_URGENT_MSG) && (dm == NON_DELIVERY_WARNING || dm == NON_DELIVERY_WARNING_AND_DEIVERY_NOTIF)))
			setDeliveryMonitoring(dm);
			String op = BlockImpl.readTo(in, BasicHeader.BLOCK_END_INDICATOR, 0, 3, null);
			if(op.length() == 3 && op.matches(OBSOLENCE_PERIOD_PATTERN)) {
				setObsolencePeriod(op);
			}
			else if (op.length() != 0) {
				throw new IOException (String.format(OBSOLENCE_PERIOD_FALSE, op));
			}
		}
	}

	@Override
	public void setIOIdentifier(char io) {
		this.IOIdentifier = io;		
	}

	@Override
	public char getIOIdentifier() {
		return IOIdentifier;
	}

	@Override
	public boolean isInput() {
		return getIOIdentifier() == 'I';
	}

	@Override
	public boolean isOutput() {
		return getIOIdentifier() == 'O';
	}

	@Override
	public void setMessageType(String mt) {
		this.messageType = mt;
	}

	@Override
	public String getMessageType() {
		return messageType;
	}

	@Override
	public String getDestinationAddress() {
		return destinationAddress;
	}

	@Override
	public void setDestinationAddress(String da) {
		destinationAddress = da;
	}

	public char getMessagePriority() {
		return messagePriority;
	}

	public void setMessagePriority(char messagePriority) {
		this.messagePriority = messagePriority;
	}

	@Override
	public void setDeliveryMonitoring(char dm) {
		this.deliveryMonitoring = dm;		
	}

	@Override
	public char getDeliveryMonitoring() {
		return this.deliveryMonitoring;
	}

	@Override
	public boolean isU2UMessage() {
		return (this.getMessagePriority() == ApplicationHeader.USER_TO_USER_NORMAL_MSG) || (this.getMessagePriority() == ApplicationHeader.USER_TO_USER_URGENT_MSG);
	}

	@Override
	public boolean isGPUMessage() {
		return this.getMessagePriority() == 0;
	}

	@Override
	public boolean isU2SMessage() {
		return this.getMessagePriority() == ApplicationHeader.USER_TO_SYSTEM_MSG;
	}

	@Override
	public void setObsolencePeriod(String op) {
		this.obsolencePeriod = op;		
	}

	@Override
	public String setObsolencePeriod() {
		return this.obsolencePeriod;
	}

	@Override
	public String getGMTTime() {
		return this.GMTTime;
	}

	@Override
	public void setGMTTime(String gmt) {
		this.GMTTime = gmt;		
	}

	@Override
	public void setMessageInputReference(String mir) {
		this.messageInputReference = mir;
	}

	@Override
	public String getMessageInputReference() {
		return this.messageInputReference;
	}

	public String getLocalDate() {
		return localDate;
	}

	public void setLocalDate(String localDate) {
		this.localDate = localDate;
	}

	public String getLocalTime() {
		return localTime;
	}

	public void setLocalTime(String localTime) {
		this.localTime = localTime;
	}

}
