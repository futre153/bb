package org.pabk.ber;

public class BERNull extends BER {
	
	public static final BERNull NULL=new BERNull("empty");
	
	public BERNull(String name) {
		this.ido=IdentifierOctets.NULL_ID;
		this.lno=LengthOctets.ZERO;
		this.coo=null;
		this.eoc=null;
		this.name=name;
	}
	@Override
	public BER clone() throws CloneNotSupportedException {return new BERNull(this.name);}
}
