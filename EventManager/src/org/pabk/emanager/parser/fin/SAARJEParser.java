package org.pabk.emanager.parser.fin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SAARJEParser extends ArrayList<RJEFinMessage> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static final char BLOCK_ID_START_INDICATOR = '{';
	static final int MIN_BLOCK_ID_LENGTH = 1;
	static final int MAX_BLOCK_ID_LENGTH = 3;
	static final String FAILED_ID = "Failed to read Block Identifier";
	static final char RJE_MESSAGE_SEPARATOR = '$';
	private static boolean started = false;
		
	public synchronized void parse (InputStream is, String encoding) {
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
					RJEFinMessage message = null;
					try {
						message = new RJEFinMessage();
						message.parse(in);
					}
					catch(Exception e) {
						/*System.out.println(e.getMessage());
						System.out.println(e.getMessage().equals(BlockImpl.END_OF_STREAM));
						System.out.println(e.getClass());
						System.out.println((e instanceof IOException) && e.getMessage().equals(BlockImpl.END_OF_STREAM));*/
						if ((e instanceof IOException) && e.getMessage().equals(BlockImpl.END_OF_STREAM)) {
							//System.out.println(message.getBasicHeader().getSequenceNumber());
							this.add(message);
							break;
						}
						throw new IOException(e);
					}
					//System.out.println(message.getBasicHeader().getSequenceNumber());
					this.add(message);
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
}
