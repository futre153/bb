package org.pabk.emanager.parser.fin;

import java.io.IOException;
import java.io.InputStreamReader;

public class CommonBlock extends BlockImpl {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final char BLOCK_END_INDICATOR = '}';
	private static final char BLOCK_START_INDICATOR = '{';
	
	private String blockContent;
	
	protected CommonBlock(String id) throws IOException {
		setBlockIdentifier(id);
	}

	@Override
	public Object getBlockContent() {
		return blockContent;
	}
	
	@Override
	boolean parseBlockContent(InputStreamReader in) throws IOException {
		if(this.getBlockIdentifier().equals("XXXX")) {
			return true;
		}
		else {
			parseCommonContent(in);
			return false;
		}
	}

	private void parseCommonContent(InputStreamReader in) throws IOException {
		char c;
		StringBuffer sb = new StringBuffer();
		while ((c = BlockImpl.readCharacter(in, null)) != BLOCK_END_INDICATOR) {
			if (c == BLOCK_START_INDICATOR) {
				String ib = BlockImpl.readTo(in, BlockImpl.END_OF_BLOCK_ID, 1, 3, BlockImpl.BLOCK_ID_PATTERN);
				CommonBlock cb = new CommonBlock(ib);
				cb.parseBlockContent(in);
				this.add(cb);
			}
			else {
				sb.append(c);
			}
		}
		blockContent = sb.toString();
	}
	
}
