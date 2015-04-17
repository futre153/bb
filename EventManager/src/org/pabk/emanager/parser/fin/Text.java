package org.pabk.emanager.parser.fin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Text extends CommonBlock implements TextBlock {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String ERROR_U2U_TEXT = "Text block od U2U message must start with empty line";
	private static final char END_U2U_TEXT_BLOC_INDICATOR = '-';
	private static final String FIELD_IS_EXPECTED = "Field is expected";
	
	private boolean u2u = false;
	@SuppressWarnings("unused")
	private String messageType;
		
	protected Text(String id) throws IOException {
		super(id);
	}

	public Text(String bi, ApplicationHeader ah) throws IOException {
		this(bi);
		this.u2u = ah == null ? false : ah.isU2UMessage();
		this.messageType = ah == null ? null : ah.getMessageType();
	}
	
	public boolean parseBlockContent(InputStreamReader in) throws IOException {
		if(u2u) {
			parseU2UTextBlockContent(in);
		}
		else {
			super.parseBlockContent(in);
		}
		return false;
	}

	private void parseU2UTextBlockContent(InputStreamReader in) throws IOException {
		BufferedReader reader = new BufferedReader(in);
		String line = reader.readLine();
		if(line.length() != 0) {
			throw new IOException(ERROR_U2U_TEXT);
		}
		char c;
		Field f = null;
		while ((c = BlockImpl.readCharacter(in, null)) != END_U2U_TEXT_BLOC_INDICATOR) {
			if(f == null) {
				if(c == Field.FIELD_ID_INDICATOR) {
					f = new Field(BlockImpl.readTo(in, Field.FIELD_ID_INDICATOR, Field.MIN_ID_LENGTH, Field.MAX_ID_LENGTH, Field.FIELD_ID_PATTERN));
					c = 0;
				}
				else {
					throw new IOException(FIELD_IS_EXPECTED);
				}
			}
			else {
				if(c == Field.FIELD_ID_INDICATOR) {
					Field n = null;
					try {
						n = new Field(BlockImpl.readTo(in, Field.FIELD_ID_INDICATOR, Field.MIN_ID_LENGTH, Field.MAX_ID_LENGTH, Field.FIELD_ID_PATTERN));
					}
					catch (Exception e) {
						n = null;
					}
					if(n != null) {
						f = n;
						c = 0;
					}
				}
			}
			line = (c == 0 ? "" : new String(new char[] {c})) + reader.readLine();
			f.add(line);
		}
	}
	
}
