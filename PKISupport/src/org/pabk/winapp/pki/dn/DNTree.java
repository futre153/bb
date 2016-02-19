package org.pabk.winapp.pki.dn;

public interface DNTree {
	DNEntry getInstance();
	void addEntry(DNEntryImpl entry);
}
