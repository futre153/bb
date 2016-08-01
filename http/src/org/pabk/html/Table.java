package org.pabk.html;

public class Table extends HtmlTag implements TableTag {
	
	private Caption caption;
	private Colgroup cols;
	private Thead thead;
	private Tfoot tfoot;
	private Tbody tbody;
	//private ArrayList<Tag> rows;
	
	private Table(String title, int x, int y, String[] header) {
		tbody = Tbody.getInstance();
		reload();
		if (title != null) {
			setCaption(title); 
		}
		if(header != null) {
			setHeader(header);
		}
		if (x >= 0 && y >= 0) {
			this.setEmptyTableBody(x, y);
		}
		//System.out.println(this);
	}
	
	public void addRow(Tag[] tags) {
		Object[] objs = new Object[tags.length];
		for (int i = 0; i < objs.length; i ++) {
			objs[i] = tags[i];
		}
		this.getBody().addRow(objs);
	}
	
	

	public void setCaption(String title) {
		caption = Caption.getInstance(title);
		reload();
	}
	public Caption removeCaption() {return (Caption) this.remove(caption);}
	
	private Tag remove(Tag tag) {
		if(tag!=null) return this.removeChild(tag);
		return null;
	}

	public Caption getCaption() {return caption;}

	public void setHeader(Object... headerValue) {
		thead = Thead.getInstance(headerValue);
		reload();	
	}
	public void setHeader(String... headerValue) {
		thead = Thead.getInstance(headerValue);
		reload();
	}
	public Thead getHeader() {
		return thead;
	}
	public Thead removeHeader() {
		Thead head = null;
		if(thead != null) {
			head = (Thead) this.removeChild(thead);
		}
		thead = null;
		return head;
	}
	
	public void setBody(Object... bodyValue) {
		tbody = Tbody.getInstance(bodyValue);
		reload();	
	}
	public void setBody(String... bodyValue) {
		tbody = Tbody.getInstance(bodyValue);
		reload();
	}
	public Tbody getBody() {
		return tbody;
	}
	public Tbody removeBody() {
		Tbody body = null;
		if(tbody != null) {
			body = (Tbody) this.removeChild(tbody);
		}
		tbody = null;
		return body;
	}
	
	public void setFooter(Object... footerValue) {
		tfoot = Tfoot.getInstance(footerValue);
		reload();	
	}
	public void setFooter(String... footerValue) {
		tfoot = Tfoot.getInstance(footerValue);
		reload();
	}
	public Tfoot getFooter() {
		return tfoot;
	}
	public Tfoot removeFooter() {
		Tfoot foot = null;
		if(tfoot != null) {
			foot = (Tfoot) this.removeChild(tfoot);
		}
		foot = null;
		return foot;
	}
	
	private void reload () {
		this.children.removeAll(this.children);
		if(caption != null) {
			this.appendChild(caption);
		}
		if(cols != null) {
			this.appendChild(cols);
		}
		if(thead != null) {
			this.appendChild(thead);
		}
		if(tbody != null) {
			this.appendChild(tbody);
		}
		if(tfoot != null) {
			this.appendChild(tfoot);
		}
	}
	
	

	public void setEmptyTableBody(int width, int height) {
		for (int y = 0 ; y < height; y ++) {
			Tr row = Tr.getInstance();
			for (int x = 0; x < width ;x ++) {
				Td cell = Td.getInstance(null);
				row.appendChild(cell);
			}
			tbody.appendChild(row);
		}
	}

	public void clearTableBody() {tbody.removeAll();}

	public void setTableBody(Tbody tb) {
		tbody.removeAll();
		tbody = tb;
		reload();
	}

	public Tbody getTableBody() {return getBody();}

	public Tr[] getRows() {
		return tbody.getRows();
	}

	public static Table getInstance(String caption, int x, int y, String[] header) {return new Table(caption, x,y,header);}

	@Override
	public boolean setContent(Tag tag, int x, int y) {
		Tag cell= tbody.getChildren().get(y).getChildren().get(x);
		cell.getChildren().removeAll(cell.getChildren());
		try {
			cell.appendChild(tag);
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean appendContent(Tag tag, int x, int y) {
		Tag cell= tbody.getChildren().get(y).getChildren().get(x);
		try {
			cell.appendChild(tag);
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public void setColgroup(String... classes) {
		cols = Colgroup.getInstance(classes);
		reload();		
	}

	@Override
	public Colgroup getColgroup() {
		return cols;
	}

	@Override
	public Colgroup removeColgroup() {
		cols = null;
		return (Colgroup) this.removeChild(cols);
	}


}
