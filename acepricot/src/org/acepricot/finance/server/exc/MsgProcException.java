package org.acepricot.finance.server.exc;

import java.io.IOException;


public class MsgProcException extends IOException {

	public MsgProcException(String message, Object ... obj) {
		super(String.format(message, obj));
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
