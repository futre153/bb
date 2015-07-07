package org.pabk.emanager.routing;

public class XSubject implements ISubject {
	
	private String text;
	protected XSubject(String text) {
		super();
		this.setText(text);
	}
	@Override
	public String getText() {
		return this.text;
	}
	protected final void setText(String text) {
		this.text = text;
	}

}
