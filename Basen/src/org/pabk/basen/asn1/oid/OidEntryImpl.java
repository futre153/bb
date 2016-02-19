package org.pabk.basen.asn1.oid;


public class OidEntryImpl implements OidEntry {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String name;
	String description;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;		
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String desc) {
		this.description = desc;		
	}
	
}
