package org.pabk.emanager.parser.fin;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

abstract class BlockImpl extends ArrayList<BlockImpl> implements Block {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String BLOCK_IS_NOT_DEFINED = "Block is not defined";
	private static final String BLOCK_BEGINNING = "\\{";
	private static final int MAX_BLOCK_ID_LENGTH = 3;
	static final char END_OF_BLOCK_ID = ':';
	private static final int MIN_BLOCK_LENGTH = 0;
	protected static final String BLOCK_ID_PATTERN = "[0-9A-Za-z]{1,3}";
	private static final String CHARACTER_NOT_MATCH = "Character '%c' does not match to pattern \"%s\"";
	private static final String SEQUENCE_IS_SHORT = "Sequence of character \"%s\" is shorter than %d";
	private static final String SEQUENCE_IS_WORNG = "Sequence of character \"%s\" does not match pattern \"%s\" or is longer than %d";
	static final String END_OF_STREAM = "End of stream reached";
	private static final String END_AND_PATTERN_NULL = "Pattern and end character cannot be null both";
	private static final String BLOCK_END_INDICATOR = "\\}";
	private static final String BLOCK_ID_ERROR = "Block idenfifier cannot have \"%s\" value";
	private static final String FAILED_BI = "Failed to read block identifier";
	public static String  BASIC_HEADER			= "1";
	public static String  APPLICATION_HEADER	= "2";
	public static String  USER_HEADER			= "3";
	public static String  TEXT					= "4";
	public static String  TRAILERS				= "5";
	
	private String blockIdentifier;
	
	public void setBlockIdentifier(String bi) throws IOException {
		if(bi == null || (!bi.matches(BLOCK_ID_PATTERN))) {
			throw new IOException(String.format(BLOCK_ID_ERROR, bi));
		}
		this.blockIdentifier = bi;
	}
	
	public String getBlockIdentifier() throws IOException {
		if(blockIdentifier == null) {
			throw new IOException(BLOCK_IS_NOT_DEFINED);
		}
		return blockIdentifier;
	}
	public void parse(InputStreamReader in) throws IOException {
		BlockImpl.readCharacter(in, BLOCK_BEGINNING);
		setBlockIdentifier (BlockImpl.readTo(in, END_OF_BLOCK_ID, MIN_BLOCK_LENGTH, MAX_BLOCK_ID_LENGTH, BLOCK_ID_PATTERN));
		if(parseBlockContent(in)) {
			BlockImpl.readCharacter(in, BLOCK_END_INDICATOR);
		}
	}
		
	abstract boolean parseBlockContent(InputStreamReader in) throws IOException;
	
	protected static String readTo(InputStreamReader in, char end, int min, int max, String pattern) throws IOException {
		if(end == 0 && pattern == null) {
			throw new IOException(END_AND_PATTERN_NULL);
		}
		StringBuffer buf = new StringBuffer(max);
		max = max < 0 ?	Integer.MAX_VALUE : max;
		while(buf.length() < max) {
			if(min >= 0 && buf.length() >= min) {
				if(end == 0 && buf.toString().matches(pattern)) {
					break;
				}
			}
			char c = BlockImpl.readCharacter(in, null);
			if(end > 0 && end == c) {
				break;
			}
			buf.append(c);
			if(end == 0 && buf.toString().matches(pattern)) {
				break;
			}
		}
		if(min >=0 && buf.length() < min) {
			throw new IOException(String.format(SEQUENCE_IS_SHORT, buf.toString(), min));
		}
		if(!buf.toString().matches(pattern)) {
			throw new IOException(String.format(SEQUENCE_IS_WORNG, buf.toString(), pattern, max));
		}
		return buf.toString();
	}
	protected static char readCharacter(InputStreamReader in, String pattern) throws IOException {
		int i = in.read();
		if(i < 0) {
			throw new IOException(END_OF_STREAM);
		}
		if(pattern != null && (!(new String(new char[]{(char) i}).matches(pattern)))) {
			throw new IOException(String.format(CHARACTER_NOT_MATCH, (char) i, pattern));
		}
		return (char) i;
	}
	
	static String readBlockId(InputStreamReader in) throws IOException {
		BlockImpl.readCharacter(in, BLOCK_BEGINNING);
		try {
			return BlockImpl.readTo(in, END_OF_BLOCK_ID, MIN_BLOCK_LENGTH, MAX_BLOCK_ID_LENGTH, BLOCK_ID_PATTERN);
		}
		catch(Exception e) {
			throw new IOException(FAILED_BI);
		}
	}
}
