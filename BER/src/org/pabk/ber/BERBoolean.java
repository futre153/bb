package org.pabk.ber;

class BERBoolean extends BER {
	public BERBoolean(String name) {
		this.ido=IdentifierOctets.BOOLEAN_ID;
		this.lno=LengthOctets.ONE;
		this.coo=new BooleanContent();
		this.name=name;
	}
	public BERBoolean(String name, boolean b) {
		this.ido=IdentifierOctets.BOOLEAN_ID;
		this.lno=LengthOctets.ONE;
		this.coo=new BooleanContent(b);
		this.name=name;
	}
	@Override
	public BERBoolean clone() throws CloneNotSupportedException {
		BERBoolean ber=new BERBoolean(this.name);
		try {this.coo=new BooleanContent((boolean) this.coo.getValue());}
		catch(UnsupportedOperationException e){}
		return ber;
	}
}
