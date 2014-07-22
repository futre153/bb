package com.acepricot.finance.sync;

public abstract class ConstraintImpl implements Constraint {
	
	private String name; 
	
	public ConstraintImpl(String name) {
		this.setName(name);
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	public abstract String getTableName();
}
