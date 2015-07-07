package org.pabk.emanager.routing;

import java.io.IOException;
import java.util.ArrayList;

public class XRouting extends ArrayList<XRoutingCondition> implements IRouting {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String RC_NOT_FOUND = "Routing condition %s was not found";

	@Override
	public XRoutingCondition getRountingCondition(String name) throws IOException {
		for(int i = 0; i < this.size(); i ++) {
			if(this.get(i).getName().equals(name)) {
				return this.get(i);
			}
		}
		throw new IOException(String.format(RC_NOT_FOUND, name));
	}
	
}
