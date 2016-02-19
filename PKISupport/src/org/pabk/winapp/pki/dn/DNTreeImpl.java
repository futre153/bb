package org.pabk.winapp.pki.dn;

public class DNTreeImpl implements DNTree {
	
	private DNEntryImpl model;
	
		
	@Override
	public void addEntry(DNEntryImpl entry) {
		if(this.model == null) {
			this.model = entry;
		}
		else {
			if(this.model.equal(entry)) {
				model.addEntry(entry);
			}
		}
	}


	@Override
	public DNEntry getInstance() {
		return (DNEntry) model.clone();
	}

}
