package org.pabk.emanager.routing;

import java.io.IOException;

interface IRouting {
	XRoutingCondition getRountingCondition(String name) throws IOException;
}
