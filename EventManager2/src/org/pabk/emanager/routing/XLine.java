package org.pabk.emanager.routing;

public class XLine implements ILine {

	static final XLine EMPTY_LINE 			= new XLine(null, "");
	static final XLine NO_RECIPIENT_WARNING = new XLine(null, "Táto správa bola odoslaná len Vám, prosím pošlite ju správnemu prijemcovi.");
	
	private String condition;
	private String text;
	
	protected XLine(String condition, String text) {
		super();
		this.setCondition((condition != null && condition.length() > 0) ? condition : null);
		this.setText(text);
	}
	
	@Override
	public String getCondition() {
		return this.condition;
	}

	@Override
	public String getText() {
		return this.text;
	}

	protected final void setCondition(String condition) {
		this.condition = condition;
	}

	protected final void setText(String text) {
		this.text = text;
	}

}
