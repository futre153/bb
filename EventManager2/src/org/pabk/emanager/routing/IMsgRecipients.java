package org.pabk.emanager.routing;

import java.io.IOException;
import java.util.Hashtable;

interface IMsgRecipients {
	//XRecipients getRecipients() throws IOException;
	XRecipients getRecipients(Hashtable<String, Object> join) throws IOException;
}
