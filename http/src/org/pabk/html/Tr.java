package org.pabk.html;

public class Tr extends HtmlTag implements TableRow {
	private Tr(){}
	protected static Tr getInstance() {return new Tr();}
	@Override
	public Td[] getCells() {
		Td[] tc=new Td[this.getChildren().size()];
		this.getChildren().toArray(tc);
		return tc;
	}
}
