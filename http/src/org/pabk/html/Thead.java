package org.pabk.html;

public class Thead extends TableContentImpl {
	
	static Thead getInstance(Object... headerValue) {
		Thead thead = new Thead();
		thead.addRow(headerValue);
		return thead;
	}
	
	static Thead getInstance(String... headerValue) {
		Thead thead = new Thead();
		thead.addRow(headerValue);
		return thead;
	}
	
	@Override
	public void addRow(Object... objs) {
		if(objs != null && objs.length > 0) {
			Tr row = Tr.getInstance();
			for (int i = 0; i < objs.length; i++) {
				row.appendChild (Th.getInstance(objs[i]));
			}
			this.appendChild(row);
		}
	}

	@Override
	public void insertRow(int index, Object... objs) throws IndexOutOfBoundsException {
		if(objs != null && objs.length > 0) {
			if(index > -1 && index < this.getChildren().size()) {
				Tr row = Tr.getInstance();
				for (int i = 0; i < objs.length; i++) {
					row.appendChild (Th.getInstance(objs[i]));
				}
				this.appendChild(index, row);
			}
			throw new IndexOutOfBoundsException("Failed to insert row  at index = " + index);
		}
	}
	
	@Override
	public void addRow(String... objs) {
		if(objs != null && objs.length > 0) {
			Tr row = Tr.getInstance();
			for (int i = 0; i < objs.length; i++) {
				row.appendChild (Th.getInstance(objs[i]));
			}
			this.appendChild(row);
		}
	}

	@Override
	public void insertRow(int index, String... objs) throws IndexOutOfBoundsException {
		if(objs != null && objs.length > 0) {
			if(index > -1 && index < this.getChildren().size()) {
				Tr row = Tr.getInstance();
				for (int i = 0; i < objs.length; i++) {
					row.appendChild (Th.getInstance(objs[i]));
				}
				this.appendChild(index, row);
			}
			throw new IndexOutOfBoundsException("Failed to remove row  at index = " + index);
		}
	}
	
}
