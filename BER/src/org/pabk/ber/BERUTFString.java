package org.pabk.ber;

public class BERUTFString extends BEROctetString {

	public BERUTFString(String name) {
		super(name);
		this.ido=IdentifierOctets.UTFSTRING_PRIMITIVE_ID;
	}
	
	public BERUTFString(String name,boolean c) {
		super(name,c);
		this.ido=IdentifierOctets.UTFSTRING_PRIMITIVE_ID;
		if(c){this.ido=IdentifierOctets.UTFSTRING_CONSTRUCTED_ID;}
	}
	
	public BERUTFString(String name,byte[][] b) {
		super(name,b);
		this.ido=IdentifierOctets.UTFSTRING_PRIMITIVE_ID;
		if(b.length>0) {this.ido=IdentifierOctets.UTFSTRING_CONSTRUCTED_ID;}	
	}

}
