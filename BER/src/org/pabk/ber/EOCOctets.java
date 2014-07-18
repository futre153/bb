package org.pabk.ber;


public class EOCOctets extends BER {
	
	public static final EOCOctets EOC=new EOCOctets();
	
	private EOCOctets() {
		this.ido=IdentifierOctets.EOC_ID;
		this.lno=LengthOctets.ZERO;
		this.coo=new NullContent();
	}

	@Override
	public BER clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException(this.getClass().getName()+": Clone not supported");
	}

	@Override
	public void clearContent() {}
	
	
}
