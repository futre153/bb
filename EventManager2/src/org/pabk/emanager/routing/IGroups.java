package org.pabk.emanager.routing;

import java.io.IOException;

interface IGroups {
	XRecipients getGroup(String name) throws IOException;
}
