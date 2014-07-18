package org.pabk.ber;

import java.io.IOException;
import java.util.Arrays;

class OidContent extends ContentOctets {

	private static final long serialVersionUID = 1L;

	private static final int OID_MASK = 0x7F;
	
	private int[] value=null; 
	
	public OidContent(int[] i) {value=i;}
	public OidContent() {value=null;}

	@Override
	public Object getValue() throws UnsupportedOperationException {
		if(value==null)throw new UnsupportedOperationException("Value not set");
		return value;
	}

	@Override
	public void decode(Encoder en, long l) throws IOException {
		byte[] b=new byte[(int) l];
		en.read(b);
		int x=2;
		for(int i=1;i<b.length;i++) {if(b[i]>=0)x++;}
		int[] ret=new int[x];
		x=b[0]&OID_MASK+(b[0]>0?(OID_MASK+1):0);
		if(x>=80) {
			ret[0]=2;ret[1]=x-80;
		}
		else {
			ret[0]=x/40;ret[1]=x%40;
		}
		int j=2;
		x=0x00;
		for(int i=1;i<b.length;i++) {
			x<<=Integer.bitCount(OID_MASK);
			x|=(b[i]&OID_MASK);
			if(b[i]>=0) {
				ret[j]=x;
				x=0x00;
				j++;
			}
		}
		if(value==null)value=ret;
		if(!Arrays.equals(value, ret))throw new IOException("Oid value is not equal with predefined value");
	}

	@Override
	public void encode(Encoder en) throws IOException {en.write(getBytes());}

	@Override
	public long length() throws UnsupportedOperationException {
		if(value==null)throw new UnsupportedOperationException("Value not set");
		int l=1;
		for(int i=2;i<value.length;i++) {
			l+=((Integer.numberOfTrailingZeros(Integer.highestOneBit(value[i]))/Integer.bitCount(OID_MASK))+1);
		}
		return l;
	}

	@Override
	public byte[] getBytes() throws UnsupportedOperationException {
		byte[] b=new byte[(int) this.length()];
		int l=1;
		b[0]=(byte) (value[0]*40+value[1]);
		for(int i=2;i<value.length;i++) {
			int k=Integer.numberOfTrailingZeros(Integer.highestOneBit(value[i]))/Integer.bitCount(OID_MASK)+1;
			int x=value[i];
			for(int j=0;j<k;j++) {
				b[l+k-j-1]=(byte) ((x&OID_MASK)+(j>0?(OID_MASK+1):0));
				x>>=(Byte.SIZE-1);
			}
			l+=k;
		}
		return b;
	}
	public String toString() {
		StringBuffer tmp=new StringBuffer();
		for(int i=0;i<value.length;i++) {
			if(i>0)tmp.append(".");
			tmp.append(value[i]);
		}
		String[] desc=OIDRepository.getObject(value);
		if(desc!=null)tmp.append(" "+desc[0]+" description: "+desc[1]);
		return tmp.toString();
	}
	public void clearContent() {this.value=null;}
}
