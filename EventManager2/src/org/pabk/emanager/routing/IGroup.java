package org.pabk.emanager.routing;

interface IGroup {
	String getName();
	XRecipients getAllRecipients();
	XRecipients getEnabledRecipients();
}
