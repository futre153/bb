package org.pabk.emanager.routing;

import java.util.ArrayList;

public class XRecipients extends ArrayList<XRecipient> implements IRecipients {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String ID_NOT_FOUND = "Recipient with identifier %d was not found";
	private static final String MAIN_NOT_SET = "Main recipient is not set";
	private XRecipient main;

	public void setMainRecipient(XRecipient main) {
		if(main.isEnabled()) {
			this.main = main;
		}
	}
	public XRecipient getMainRecipient() throws IndexOutOfBoundsException {
		if(main == null) {
			throw new IndexOutOfBoundsException (MAIN_NOT_SET);
		}
		return this.main;
	}
	public XRecipient getRecipient(int id) throws IndexOutOfBoundsException {
		for(int i = 0; i < this.size(); i ++) {
			if(this.get(i).getRecipientId() == id) {
				return this.get(i);
			}
		}
		throw new IndexOutOfBoundsException (String.format(ID_NOT_FOUND, id));
	}
}
