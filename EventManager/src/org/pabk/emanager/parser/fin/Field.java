package org.pabk.emanager.parser.fin;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Field extends ArrayList<String> implements Block {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String UNSUPPORTED = "Unsupported operation";

	public static final char FIELD_ID_INDICATOR = ':';

	public static final int MIN_ID_LENGTH = 2;

	public static final int MAX_ID_LENGTH = 3;

	public static final String FIELD_ID_PATTERN = "\\d{2}[A-Z]?";
	
	private String blockIdentifier;
	
	public Field(String bi) throws IOException {
		this.setBlockIdentifier(bi);
	}

	@Override
	public String getBlockIdentifier() throws IOException {
		return blockIdentifier;
	}

	@Override
	public void setBlockIdentifier(String bi) throws IOException {
		this.blockIdentifier = bi;		
	}

	@Override
	public void parse(InputStreamReader in) throws IOException {
		throw new IOException(UNSUPPORTED);		
	}

	@Override
	public Object getBlockContent() {
		// TODO Auto-generated method stub
		return null;
	}


}
