package org.pabk.emanager.parser.fin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SAARJEParser {
	
	private static final String BLOCK_NOT_EXPECTED = "Block %s is not expected";
	private static final String RJE_MESSAGE_START_PATTERN = "[\\$\\{]";
	private static final char BLOCK_ID_START_INDICATOR = '{';
	private static final int MIN_BLOCK_ID_LENGTH = 1;
	private static final int MAX_BLOCK_ID_LENGTH = 3;
	private static final String FAILED_ID = "Failed to read Block Identifier";
	private static final char RJE_MESSAGE_SEPARATOR = '$';
	private static boolean started = false;
	private static ArrayList<RJEFinMessage> messages = new ArrayList<RJEFinMessage>();
	
	public static synchronized void parse (InputStream is, String encoding) {
		if(!started) {
			started = true;
			InputStreamReader in = null;
			try {
				if(encoding == null) {
					in = new InputStreamReader(is);
				}
				else {
					in = new InputStreamReader(is, encoding);
				}
				while (true) {
					String id = null;
					char c;
					try {
						c = BlockImpl.readCharacter(in, RJE_MESSAGE_START_PATTERN);
					}
					catch(Exception e) {
						if (!(e instanceof IOException && e.getMessage().equals(BlockImpl.END_OF_STREAM))) {
							throw new IOException(e);
						}
						break;
					}
					if(c == BLOCK_ID_START_INDICATOR) {
						id = BlockImpl.readTo(in, BlockImpl.END_OF_BLOCK_ID, MIN_BLOCK_ID_LENGTH, MAX_BLOCK_ID_LENGTH, BlockImpl.BLOCK_ID_PATTERN);
					}
					else if (c == RJE_MESSAGE_SEPARATOR) {
						id = BlockImpl.readBlockId(in);
					}
					else {
						throw new IOException(FAILED_ID);
					}
					if(id.equals(BlockImpl.BASIC_HEADER)) {
						messages.add(parse(in));
					}
					else {
						throw new IOException(String.format(BLOCK_NOT_EXPECTED, id));
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				started = false;
			}
		}
	}

	private static RJEFinMessage parse(InputStreamReader in) throws IOException {
		RJEFinMessage message = null;
		try {
			message = new RJEFinMessage();
			message.parse(in);;
			return message;
		}
		catch(Exception e) {
			throw new IOException(e);
		}
	}
}
