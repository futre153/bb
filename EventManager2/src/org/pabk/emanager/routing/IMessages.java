package org.pabk.emanager.routing;

import java.io.IOException;

interface IMessages {
	String getEncoding();
	XMessageTemplate getMessageTemplate(String className) throws IOException;
}
