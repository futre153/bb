package org.pabk.emanager.routing;

public class XRoutingPoint extends Groups implements IRoutingPoint {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String regExp;

	@Override
	public Groups getGroups() {
		return this;
	}

	@Override
	public String getRegExp() {
		return this.regExp;
	}

	public void setRegExp(String regExp) {
		this.regExp = regExp;
	}

}
