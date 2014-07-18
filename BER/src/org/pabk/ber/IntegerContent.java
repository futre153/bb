package org.pabk.ber;

import java.io.IOException;

public class IntegerContent extends ContentOctets {

	private Long value=null;
	
	private static final long serialVersionUID = 1L;

	public IntegerContent(long l) {this.value=l;}
	public IntegerContent() {this.value=null;}

	

	@Override
	public void decode(Encoder en, long l) throws IOException {
		if(l<0 || l>(Long.SIZE/Byte.SIZE))throw new IOException ("Unsupported length of integer");
		int b=en.read();l--;
		long x=b>0x7F?-1:0;
		x<<=Byte.SIZE;
		x|=b;
		for(int i=0;i<l;i++) {
			x<<=Byte.SIZE;
			x|=en.read();
		}
		if(value==null)value=x;
		if(x!=value)throw new IOException("Integer value is not equal with predefined value");
	}

	@Override
	public void encode(Encoder en) throws IOException {en.write(getBytes());}

	@Override
	public long length() throws UnsupportedOperationException {return getBytes().length;}
	@Override

	public Object getValue() throws UnsupportedOperationException {return value;}

	@Override
	public byte[] getBytes() throws UnsupportedOperationException {
		byte[] b=new byte[Long.SIZE/Byte.SIZE];
		long l=value;
		b[7]=(byte) (l&0xFF);l>>>=Byte.SIZE;
		b[6]=(byte) (l&0xFF);l>>>=Byte.SIZE;
		b[5]=(byte) (l&0xFF);l>>>=Byte.SIZE;
		b[4]=(byte) (l&0xFF);l>>>=Byte.SIZE;
		b[3]=(byte) (l&0xFF);l>>>=Byte.SIZE;
		b[2]=(byte) (l&0xFF);l>>>=Byte.SIZE;
		b[1]=(byte) (l&0xFF);l>>>=Byte.SIZE;
		b[0]=(byte) (l&0xFF);
		for(int i=0; i<(b.length-1);i++) {
			if(b[i]==-1 && b[i+1]<0) {
				byte[] c=new byte[b.length-1];
				System.arraycopy(b, 1, c, 0, c.length);
				b=c;
				i--;
			}
			else {break;}
		}
		for(int i=0; i<(b.length-1);i++) {
			if(b[i]==0 && b[i+1]>=0) {
				byte[] c=new byte[b.length-1];
				System.arraycopy(b, 1, c, 0, c.length);
				b=c;
				i--;
			}
			else {break;}
		}
		return b;
	}
	@Override
	public String toString() {return getValue().toString();}
	@Override
	public void clearContent() {this.value=null;}
}
