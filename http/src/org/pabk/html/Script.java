package org.pabk.html;

import java.io.IOException;
import java.io.PrintWriter;

public class Script extends HtmlTag {
	
	private static final String SOURCE_FILE = "src";
	private static final String TYPE = "type";
	private static final String XHTML_CODE_PREFIX = "//>![CDATA[";
	private static final String XHTML_CODE_FOOTER = "//]]>";
		
	private Script(String type, Object src) {
		if(type != null) {
			atts.put(TYPE, type);
		}
		if(src instanceof String) {
			atts.put(SOURCE_FILE, (String) src);
		}
		else if(src instanceof String[]) {
			for(int i=0;i<((String[])src).length;i++) {
				appendChild(TextTag.getInstance(((String[])src)[i]));
				appendChild(TextTag.CRLF);
			}
		}
	}
	
	private Script(String type, Object src, String charset) {
		this(type,src);
		this.setAttribute("charset", charset);
	}
	
	protected static Script getInstance(String type, Object obj) {return new Script(type,obj);}
	
	protected static Script getInstance(String type, Object obj, String charset) {return new Script(type,obj,charset);}
	
	public void doFinal(PrintWriter out, int c) throws IOException {
		TextTag prefix = null,footer = null;
		if(this.isXHtml()) {
			prefix=TextTag.getInstance(XHTML_CODE_PREFIX);
			footer=TextTag.getInstance(XHTML_CODE_FOOTER);
			appendChildBefore(prefix,prefix.getClass().getSimpleName());
			appendChild(footer);
		}
		super.doFinal(out,c);
		if(this.isXHtml()) {
			removeChild(prefix);
			removeChild(footer);
		}
	}
	
}
