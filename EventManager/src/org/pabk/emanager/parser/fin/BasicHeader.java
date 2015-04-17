package org.pabk.emanager.parser.fin;

import java.io.IOException;
import java.io.InputStreamReader;

public class BasicHeader extends BlockImpl {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String BASIC_HEADER_EXPECTED = "Basic header is expected";
	private static final String APPLICATION_ID_PATTERN = ".";
	private static final String SERVICE_ID_PATTERN = "([0][12356])|([1][4])|([2][12356])|([4][23])";
	static final String LOGICAL_TERMINAL_PATTERN = "[A-Z]{6}[0-9A-Z]{2}[A-Z][0-9]{3}";
	static final char BLOCK_END_INDICATOR = '}';
	private static final String SESSION_AND_SEQUENCE_PATTERN = "\\d{10}";
	private static final String SESSION_PATTERN = "\\d{6}";
	private static final String SEQUENCE_PATTERN = "\\d{4}";
	private static final String SEQUENCE_ERROR = "Session and sequence number format error in Basic Header";
	
	private String applicationId;
	private String serviceId;
	private String logicalTerminal;
	private String sesseionNumber;
	private String sequenceNumber;
	
	@Override
	public String getBlockContent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	boolean parseBlockContent(InputStreamReader in) throws IOException {
		if(!this.getBlockIdentifier().equals(BlockImpl.BASIC_HEADER)) {		
			throw new IOException(BASIC_HEADER_EXPECTED);
		}
		applicationId = BlockImpl.readTo(in, (char) 0, 1, 1, APPLICATION_ID_PATTERN);
		serviceId = BlockImpl.readTo(in, (char) 0, 2, 2, SERVICE_ID_PATTERN);
		logicalTerminal = BlockImpl.readTo(in, (char) 0, 12, 12, LOGICAL_TERMINAL_PATTERN);
		String seq = BlockImpl.readTo(in, BLOCK_END_INDICATOR, 0, 10, null);
		if(seq.matches(SESSION_AND_SEQUENCE_PATTERN)) {
			sesseionNumber = seq.substring(0, 6);
			sequenceNumber = seq.substring(6);
		}
		else if(seq.matches(SESSION_PATTERN)) {
			sesseionNumber = seq;
		}
		else if(seq.matches(SEQUENCE_PATTERN)) {
			sequenceNumber = seq;
		}
		else if(seq.length() > 0) {
			throw new IOException(SEQUENCE_ERROR);
		}
		return false;
	}

	protected final String getApplicationId() {
		return applicationId;
	}

	protected final void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	protected final String getServiceId() {
		return serviceId;
	}

	protected final void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	protected final String getLogicalTerminal() {
		return logicalTerminal;
	}

	protected final void setLogicalTerminal(String logicalTerminal) {
		this.logicalTerminal = logicalTerminal;
	}

	protected final String getSesseionNumber() {
		return sesseionNumber;
	}

	protected final void setSesseionNumber(String sesseionNumber) {
		this.sesseionNumber = sesseionNumber;
	}

	protected final String getSequenceNumber() {
		return sequenceNumber;
	}

	protected final void setSequenceNumber(String sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
}
