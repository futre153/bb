package org.pabk.ber;


public class BERInteger extends BER {
	
	public BERInteger(String name) {
		this.ido=IdentifierOctets.INTEGER_ID;
		this.lno=new LengthOctets();
		this.coo=new IntegerContent();
		this.name=name;
	}
	
	public BERInteger(String name, long l) {
		this.ido=IdentifierOctets.INTEGER_ID;
		this.coo=new IntegerContent(l);
		this.lno=new LengthOctets(this.coo.length());
		this.name=name;
	}

	@Override
	public BERInteger clone() throws CloneNotSupportedException {
		Object val=this.coo.getValue();
		if(val==null) {return new BERInteger(this.name);}
		return new BERInteger(this.name, (long)val);
	}


	
	
	



}
