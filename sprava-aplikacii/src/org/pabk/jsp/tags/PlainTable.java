package org.pabk.jsp.tags;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.pabk.html.HtmlTag;
import org.pabk.html.Table;
import org.pabk.html.TextTag;

public class PlainTable  extends BodyTagSupport implements TabularDataHandler{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_COLUMN_SEPARATOR = ",";
	private static final String DEFAULT_HEADER_NAME = "item";
	private static final String DEFAULT_ENCODING = "US-ASCII";
	
	private String columnSeparator = DEFAULT_COLUMN_SEPARATOR;
	private String[] headerCols;
	private BufferedReader in;
	private Hashtable<String, Object> next = new Hashtable<String, Object>();
	private String encoding = DEFAULT_ENCODING;
	private String srcFile;
	private boolean header;
	private boolean replaceSpace;
	private String[] cols = new String[]{};
	private String className;
	
	public void setEncoding(String value) {
		this.encoding = value; 
	}
	
	public void setSourceFile(String value) {
		this.srcFile = value;
	}
	
	public void setColumnSeparator(String value) {
		this.columnSeparator = value;
	}
	
	public void setHasHeader(String value) {
		this.header =  Boolean.parseBoolean(value);
	}
	
	public void setReplaceSpace (String value){
		this.replaceSpace = Boolean.parseBoolean(value);
	}
	
	public void setColumns(String value) {
		this.cols = value.split(",");
	}
	
	public void setClassName(String value) {
		this.className = value;
	}
	
	public int doAfterBody() {
		try {
			setSource(new InputStreamReader(this.pageContext.getServletContext().getResourceAsStream(srcFile), this.encoding));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if(header) {
			try {
				this.headerCols = this.in.readLine().split(columnSeparator);
			}
			catch (Exception e) {
				this.headerCols = null;
			}
		}
		if(this.headerCols == null) {
			this.headerCols = new String[]{};
		}
		Table tab = Table.getInstance(null, 1, 1, cols);
		tab.clearTableBody();
		tab.setClassName(className);
		while(hasNext()) {
			Map<String, Object> row = this.getNext();
			HtmlTag[] tags = new HtmlTag[cols.length];
			for(int i = 0; i < cols.length; i ++) {
				String value = (String) row.get(cols[i]);
				if(value == null) {
					tags[i] = TextTag.getInstance("");
				}
				else {
					tags[i] = replaceSpace ? TextTag.getInstanceNBSP(value) : TextTag.getInstance(value);
				}
			}
			tab.addRow(tags);
		}
		close();
		try {
			tab.doFinal(new PrintWriter(this.getBodyContent().getEnclosingWriter()), 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return BodyTag.SKIP_BODY;
	}
	
	public PlainTable() {
		super();
	}
	
	public void setSource(Object src) {
		if(src instanceof Reader) {
			in = new BufferedReader((Reader) src);
		}
	}
	@Override
	public String[] getHeader() {
		return headerCols.length > 0?headerCols:null;
	}
	@Override
	public Map<String, Object> getNext() {
		if(next.size() == 0) {
			if(!hasNext()) {
				return null;
			}
		}
		Map<String, Object> retValue = next;
		next = new Hashtable<String, Object>();
		return retValue;
	}
	
	
	private boolean next() throws IOException {
		if(next.size() > 0) return true;
		String line = in.readLine();
		if(line == null) return false;
		String[] values = line.split(columnSeparator);
		for(int i = 0; i < values.length; i++) {
			next.put(i < headerCols.length ? headerCols[i] : (DEFAULT_HEADER_NAME + "i"), values[i]);
		}
		return true;
	}
	
	
	
	public boolean hasNext() {
		try {
			return next();
		}
		catch (Exception e) {
			return false;
		}
	}

	@Override
	public void close() {
		try {
			in.close();
		} catch (IOException e) {

		} finally {
			in = null;
		}
		
	}
}
