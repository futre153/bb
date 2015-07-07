package org.pabk.emanager.routing;

public class XAttachment implements IAttachment {

	private String text;
	
	protected XAttachment(String text) {
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
