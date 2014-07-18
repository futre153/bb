package org.pabk.ber;

import java.io.IOException;

public class BERSetOf extends BER {
	
	private Object[] set;
	
	public BERSetOf(String name, Object[] seq) {
		this.set=seq;
		this.ido=IdentifierOctets.SET_ID;
		this.lno=new LengthOctets();
		this.coo=new SequenceOfContent();
		this.name=name;
	}
	
	public void decode(Encoder en, long l) throws IOException {
		this.level=en.getLevel();
		this.ido.decode(en, -1);
		this.lno.decode(en, -1);
		long len=(long) this.lno.getValue();
		l=len;
		en.setLevel(en.getLevel()+1);
		while(true) {
			if(len<0) {
				boolean ok=true;
				try {EOCOctets.EOC.ido.decode(en, 0);}
				catch (UnsupportedOperationException e) {ok=false;}
				if(ok) {
					this.eoc=EOCOctets.EOC;
					this.eoc.decode(en, 0);
					break;
				}
			}
			else {
				if(l==0) break;
			}
			for(int i=0;i<set.length;i++) {
				BER ber=null;
				try {ber=((BER) set[i]).clone();}
				catch (CloneNotSupportedException e) {
					en.setLevel(en.getLevel()-1);
					throw new IOException(e.getMessage());
				}
				ber.decode(en, -1);
				if(len>=0) {
					l-=ber.length();
					if(l<0) {
						en.setLevel(en.getLevel()-1);
						throw new IOException ("Length mismatch while SetOf decoding");
					}
				}
				this.coo.add(ber);
			}
		}
		en.setLevel(en.getLevel()-1);
	}

	@Override
	public BERSetOf clone() throws CloneNotSupportedException {
		BER[] tmp=new BER[set.length];
		for(int i=0;i<tmp.length;i++) {
			tmp[i]=((BER) set[i]).clone();
		}
		BERSetOf ber=new BERSetOf(this.name,tmp);
		ber.lno=this.lno==null?null:this.lno.clone();
		BER[] obj=(BER[])this.coo.getValue();
		for(int i=0;i<obj.length;i++) {
			ber.coo.add(obj[i].clone());
		}
		ber.eoc=this.eoc;
		return ber;
	}
	
	
	
}