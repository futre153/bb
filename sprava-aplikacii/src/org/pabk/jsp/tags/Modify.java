package org.pabk.jsp.tags;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class Modify extends BodyTagSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String srcFile;
	
	
	public void setSourceFile(String value) {
		this.srcFile = value;
	}
	
	public int doStartTag() {
		File f = new File(this.pageContext.getServletContext().getRealPath(srcFile));
		try {
			this.pageContext.getOut().append(String.format(Locale.forLanguageTag("SK"), "%1$te.&nbsp;%1tB&nbsp;%1$tY", f.lastModified()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return BodyTag.SKIP_BODY;
	}
	
}
