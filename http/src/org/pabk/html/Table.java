package org.pabk.html;

import java.util.ArrayList;

public class Table extends HtmlTag implements TableTag {
	
	private Caption caption;
	private Thead thead;
	private Tfoot tfoot;
	private Tbody tbody;
	private ArrayList<Tag> rows;
	
	private Table(String title, int x, int y, String[] header) {
		tbody=Tbody.getInstance();
		this.appendChild(tbody);
		if(title!=null) setCaption(title); 
		if(header!=null) setHeader(header);
		if(x>=0&&y>=0)this.setEmptyTableBody(x, y);
	}
	
	public void addRow(Tag[] tags) {
		Tr row = Tr.getInstance();
		for(int i = 0; i < tags.length; i ++) {
			Td cell = Td.getInstance(null);
			cell.getChildren().removeAll(cell.getChildren());
			cell.appendChild(tags[i]);
			row.appendChild(cell);
		}
		this.getTableBody().appendChild(row);
	}
	
	public void setHeader(String[] header) {
		this.removeHeader();
		thead=Thead.getInstance(header);
		if(caption==null)this.appendChild(0,thead);
		else this.appendChild(1,thead);
	}
	public Thead removeHeader() {return (Thead) this.remove(thead);}

	public void setCaption(String title) {
		this.removeCaption();
		caption=Caption.getInstance(title);
		this.appendChild(0,caption);
	}
	public Caption removeCaption() {return (Caption) this.remove(caption);}
	
	private Tag remove(Tag tag) {
		if(tag!=null) return this.removeChild(tag);
		return null;
	}

	public Caption getCaption() {return caption;}

	public Thead getHeader() {return thead;}

	public void setFooter(String[] footerValue) {
		this.removeFooter();
		thead=Thead.getInstance(footerValue);
		if(caption==null)this.appendChild(0,thead);
		else this.appendChild(1,thead);	
	}

	public Tfoot removeFooter() {return (Tfoot) this.removeChild(tfoot);}

	public Tfoot getFooter() {return tfoot;}

	public void setEmptyTableBody(int width, int height) {
		for(int y=0;y<height;y++) {
			Tr row=Tr.getInstance();
			for(int x=0;x<width;x++) {
				Td cell=Td.getInstance(null);
				row.appendChild(cell);
			}
			tbody.appendChild(row);
			rows=tbody.getChildren();
		}
	}

	public void clearTableBody() {rows.removeAll(rows);}

	public void setTableBody(Tbody tb) {
		this.clearTableBody();
		rows=tb.getChildren();
		tbody=tb;
	}

	public Tbody getTableBody() {return tbody;}

	public Tr[] getRows() {
		Tr[] tr=new Tr[rows.size()];
		rows.toArray(tr);
		return tr;
	}

	public static Table getInstance(String caption, int x, int y, String[] header) {return new Table(caption, x,y,header);}

	@Override
	public boolean setContent(Tag tag, int x, int y) {
		//System.out.println(rows);
		Td cell=(Td) rows.get(y).getChildren().get(x);
		cell.getChildren().removeAll(cell.getChildren());
		try {cell.appendChild(tag);}catch(Exception e) {e.printStackTrace();return false;}
		//System.out.println(rows);
		return true;
	}

	public boolean appendContent(Tag tag, int x, int y) {
		Td cell=(Td) rows.get(y).getChildren().get(x);
		try {cell.appendChild(tag);}catch(Exception e) {e.printStackTrace();return false;}
		//System.out.println(rows);
		return true;
	}

	@Override
	public void setColgroup(String... classes) {
		this.appendChildAtStart(Colgroup.getInstance(classes));		
	}


}
