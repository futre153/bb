package org.pabk.html;

abstract class TableContentImpl extends HtmlTag implements TableContent {
	
	@Override
	public Tr[] getRows() {
		Tr[] rows = new Tr[this.children.size()];
		return this.getChildren().toArray(rows);
	}

	@Override
	public Tr[] removeAll() {
		Tr[] rows = getRows();
		this.getChildren().removeAll(this.getChildren());
		return rows;
	}

	@Override
	public Tr removeRow(int index) throws IndexOutOfBoundsException {
		if(index > -1 && index < this.getChildren().size()) {
			return (Tr) this.removeChild(this.getChildren().get(index));
		}
		throw new IndexOutOfBoundsException("Failed to remove row (index = " + index + ")");
	}

	@Override
	public void addRow(Object... objs) {
		if(objs != null && objs.length > 0) {
			Tr row = Tr.getInstance();
			for (int i = 0; i < objs.length; i++) {
				row.appendChild (Td.getInstance(objs[i]));
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
					row.appendChild (Td.getInstance(objs[i]));
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
				row.appendChild (Td.getInstance(objs[i]));
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
					row.appendChild (Td.getInstance(objs[i]));
				}
				this.appendChild(index, row);
			}
			throw new IndexOutOfBoundsException("Failed to remove row  at index = " + index);
		}
	}
	
}
