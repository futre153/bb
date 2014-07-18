package org.pabk.ber;




public class BEROid extends BER {
	 
	 public BEROid(String name) {
			this.ido=IdentifierOctets.OID_ID;
			this.lno=new LengthOctets();
			this.coo=new OidContent();
			this.name=name;
		}
		public BEROid(String name,int[] i) {
			this.ido=IdentifierOctets.OID_ID;
			this.coo=new OidContent(i);
			this.lno=new LengthOctets(this.coo.length());
			this.name=name;
		}
		@Override
		public BEROid clone() throws CloneNotSupportedException {
			try {
				int[] tmp=new int[((int[])this.coo.getValue()).length];
				for(int i=0;i<tmp.length;i++) {
					tmp[i]=((int[])this.coo.getValue())[i];
				}
				return new BEROid(this.name,tmp);}
			catch(UnsupportedOperationException e) {return new BEROid(this.name);}
		}
		public void setValue(Object obj){this.coo=new OidContent((int[]) obj);}
	 
	 /*

	@Override
	public void decode(Encoder encoder, long l) throws IOException {
		byte[] b=new byte[_len.getValue().intValue()];
		encoder.read(b);
		int l=1;
		for(int i=0;i<b.length;i++) {if(b[i]<0)l++;}
		_val=new int[l];
		l=0;
		int v=0;
		for(int i=0;i<b.length;i++) {
			v<<=0x07;
			v|=(b[i]&0x07);
			if(b[i]<0) {
				((int[])_val)[l]=v;
				v=0;
				l++;
			}
		}
	}
*/
	

}
