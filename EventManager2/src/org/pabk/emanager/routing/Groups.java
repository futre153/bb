package org.pabk.emanager.routing;

import java.io.IOException;
import java.util.ArrayList;

public class Groups extends ArrayList<Group> implements IGroups {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String GRP_NOT_DEFINED = "group %s is not defined";

	@Override
	public XRecipients getGroup(String name) throws IOException {
		for(int i = 0; i < this.size(); i ++) {
			if(this.get(i).getName().equals(name)) {
				return this.get(i);
			}
		}
		throw new IOException (String.format(GRP_NOT_DEFINED, name));
	}

}
