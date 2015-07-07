package org.pabk.emanager.routing;

import java.io.IOException;
import java.util.ArrayList;

public class XMessages extends ArrayList<XMessageTemplate> implements IMessages {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String MSG_TEMPLATE_NOT_FOUND = "Message template %s was not found";
	private String encoding;

	public XMessages(String encoding) {
		super();
		this.setEncoding(encoding);
	}

	private void setEncoding(String encoding) {
		this.encoding = encoding;		
	}

	@Override
	public String getEncoding() {
		return this.encoding;
	}

	@Override
	public XMessageTemplate getMessageTemplate(String className) throws IOException {
		for(int i = 0; i < this.size(); i ++) {
			if(this.get(i).get_Class().equals(className)) {
				return this.get(i);
			}
		}
		throw new IOException (String.format(MSG_TEMPLATE_NOT_FOUND, className));
	}

}
