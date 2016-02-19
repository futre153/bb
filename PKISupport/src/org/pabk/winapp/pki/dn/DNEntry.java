package org.pabk.winapp.pki.dn;

import java.util.ArrayList;

import org.ietf.jgss.Oid;

interface DNEntry {
	String getName();
	Oid getOID();
	void addValue(String value);
	String[] getDNString();
	void addEntry(String dn, String value);
	void addEntry(DNEntry entry);
	ArrayList<String> getValue();
}
