package org.pabk.basen.asn1.chrs;

import java.nio.charset.Charset;
import java.nio.charset.spi.CharsetProvider;
import java.util.Hashtable;
import java.util.Iterator;

public class BasenCharsetProvider extends CharsetProvider {
	
	private static final Hashtable<String, Charset> css = new Hashtable<String, Charset>();
	
	public BasenCharsetProvider () {
		css.put("numeric-string", new NumStrCharset());
		css.put("printable-string", new PrnStrCharset());
		css.put("teletex-string", new T61StrCharset());
	}
	
	@Override
	public Charset charsetForName(String cs) {
		return css.get(cs);
	}

	@Override
	public Iterator<Charset> charsets() {
		return css.values().iterator();
	}

}
