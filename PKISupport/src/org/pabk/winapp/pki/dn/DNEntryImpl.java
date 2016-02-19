package org.pabk.winapp.pki.dn;

import java.util.ArrayList;

import org.ietf.jgss.Oid;

class DNEntryImpl extends ArrayList<DNEntry> implements DNEntry {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private String name;
	private Oid oid;
	private ArrayList<String> value = new ArrayList<String>();
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public Oid getOID() {
		return oid;
	}

	@Override
	public String[] getDNString() {
		String[] tmp = new String[value.size() * 2];
		for(int i = 0; i < value.size(); i ++) {
			tmp[i * 2] = this.getName();
			tmp[i * 2 + 1] = value.get(i);
		}
		return tmp;
	}

	@Override
	public void addValue(String value) {
		if(value != null && value.length() > 0) {
			this.value.add(value);
		}
	}
	
	public void addEntry(String dn, String value) {
		if(this.getName().equalsIgnoreCase(dn)) {
			this.value.add(value);
		}
		else {
			if(this.size() == 1) {
				this.get(0).addEntry(dn, value);
			}
		}
	}
	
	public DNEntryImpl clone() {
		DNEntryImpl dn = new DNEntryImpl();
		dn.oid = oid;
		dn.name = name;
		if(this.size() > 0) {
			dn.add(((DNEntryImpl) this.get(0)).clone());
		}
		return dn;
	}
	
	public boolean equal(DNEntryImpl entry) {
		return this.getName().equalsIgnoreCase(entry.getName()) ? (this.size() > 0 ? (entry.size() > 0 ? this.get(0).equals(entry.get(0)) : false) : entry.size() == 0) : false;
	}

	public void addEntry(DNEntry entry) {
		for(int i = 0; i < ((DNEntryImpl) entry).size(); i ++) {
			int j = 0;
			for(; j < this.size(); j ++) {
				if(this.get(j).equals(((DNEntryImpl) entry).get(i))) {
					break;
				}
			}
			if(j >= this.size()) {
				DNEntryImpl e = new DNEntryImpl();
				e.name = this.get(0).getName();
				e.oid = this.get(0).getOID();
				e.value = ((DNEntryImpl) entry).get(i).getValue();
				this.add(e);
				e.addEntry(((DNEntryImpl) entry).get(i));
			}
			else {
				this.get(j).addEntry(((DNEntryImpl) entry).get(i));
			}
		}
	}
	@Override
	public ArrayList<String> getValue() {
		return value;
	}
}
