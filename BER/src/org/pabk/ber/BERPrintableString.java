package org.pabk.ber;

public class BERPrintableString extends BEROctetString {
	
	public BERPrintableString(String name) {
		super(name);
		this.ido=IdentifierOctets.PRINTABLESTRING_PRIMITIVE_ID;
	}
	
	public BERPrintableString(String name,boolean c) {
		super(name,c);
		this.ido=IdentifierOctets.PRINTABLESTRING_PRIMITIVE_ID;
		if(c){this.ido=IdentifierOctets.PRINTABLESTRING_CONSTRUCTED_ID;}
	}
	
	public BERPrintableString(String name,byte[][] b) {
		super(name,b);
		this.ido=IdentifierOctets.PRINTABLESTRING_PRIMITIVE_ID;
		if(b.length>0) {this.ido=IdentifierOctets.PRINTABLESTRING_CONSTRUCTED_ID;}	
	}
}
