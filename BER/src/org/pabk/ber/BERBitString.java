package org.pabk.ber;

public class BERBitString extends BER {
	
	
	
	public BERBitString(String name) {
		this.ido=IdentifierOctets.BITSTRING_PRIMITIVE_ID;
		this.lno=new LengthOctets();
		this.coo=new OctetStringContent(false);
		this.name=name;
	}
	
	public BERBitString(String name,boolean c) {
		this.ido=IdentifierOctets.BITSTRING_PRIMITIVE_ID;
		if(c){this.ido=IdentifierOctets.BITSTRING_CONSTRUCTED_ID;}
		this.lno=new LengthOctets();
		this.coo=new OctetStringContent(c);
		this.name=name;
	}
	
	public BERBitString(String name,byte[][] b) {
		this.ido=IdentifierOctets.BITSTRING_PRIMITIVE_ID;
		if(b.length>0) {this.ido=IdentifierOctets.BITSTRING_CONSTRUCTED_ID;}	
		this.lno=new LengthOctets();
		this.coo=new OctetStringContent(b);
		this.name=name;
	}
	
	
	@Override
	public BERBitString clone() throws CloneNotSupportedException {
		BERBitString ber=new BERBitString(this.name,this.ido.isConstructed());
		ber.lno=this.lno==null?null:this.lno.clone();
		Object val=null;
		try {val=this.coo.getValue();}
		catch(UnsupportedOperationException e){}
		if(val!=null) {
			if(val instanceof byte[]) {
				byte[] tmp=new byte[((byte[]) val).length];
				for(int i=0;i<tmp.length;i++) {
					tmp[i]=((byte[])val)[i];
				}
				ber.coo=new OctetStringContent(new byte[][]{tmp});
			}
			else if(val instanceof BER[]) {
				for(int i=0;i<((BER[])val).length;i++) {
					ber.coo.add(((BER[])val)[i].clone());
				}
			}
			//throw new CloneNotSupportedException("failed to clone the octetstring value");
		}
		ber.eoc=this.eoc;
		return ber;
	}
}
