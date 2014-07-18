package org.pabk.ber;

import java.io.IOException;

class LengthOctets implements BaseBER {
	
	private static final int MAX_SHORT_LENGTH = 0x7F;
	private static final long INDEFINITE_INDEX = -2;
	
	private static final String LENGTH_DECODE_FAIL1 = "Length: the value 11111111 shall not be used for long form";
	private static final String LENGTH_NOT_DEFINED = "Length not defined";
	private static final String LENGTH_DECODE_FAIL2 = "The length higher as "+Long.MAX_VALUE+" is not supported";
	private static final String LENGTH_ENCODE_FAIL = "Length encode: The length is not defined";
	private static final String LENGTH_DECODE_FAIL3 = "Length decode fail: decoded length is not equals to predefined length";
	
	static final LengthOctets ZERO = new LengthOctets(0);
	public static final LengthOctets ONE = new LengthOctets(1);
	
	private long len;
	
	LengthOctets(){len=-1;}
	LengthOctets(long l) {len=l<0?INDEFINITE_INDEX:l;}
	
	public LengthOctets clone() throws CloneNotSupportedException {return new LengthOctets(len);}
	
	@Override
	public Object getValue() throws UnsupportedOperationException {return len;}

	@Override
	public void decode(Encoder en, long y) throws IOException {
		int b=en.read();
		long l=len;
		len=b&MAX_SHORT_LENGTH;
		if(b>MAX_SHORT_LENGTH) {
			if (len>0) {
				if(len==MAX_SHORT_LENGTH) throw new IOException(LENGTH_DECODE_FAIL1);
				int x=Long.bitCount((Long.MAX_VALUE))/8+1;
				if(len>x) throw new IOException(LENGTH_DECODE_FAIL2);
				x=(int) len;
				len=0x00L;
				for(int i=0;i<x;i++) {
					len=(i>0)?(len<<0x08):len;
					len|=en.read();
				}
			}
			else {
				len=INDEFINITE_INDEX;
			}
		}
		if(l>=0 && l!=len) throw new IOException(LENGTH_DECODE_FAIL3);
	}
	
	boolean isDefinite() {
		if(len<0) throw new UnsupportedOperationException(LENGTH_NOT_DEFINED);
		return len>0;
	}
	
	@Override
	public void encode(Encoder encoder) throws IOException {
		if (len==INDEFINITE_INDEX) {encoder.write(MAX_SHORT_LENGTH+1);}
		else if(len<=MAX_SHORT_LENGTH) {encoder.write((int) len);}
		else if(len>MAX_SHORT_LENGTH) {
			@SuppressWarnings("unused")
			int l=Long.numberOfTrailingZeros(Long.highestOneBit(len))/8+1;
			encoder.write(Long.numberOfTrailingZeros(Long.highestOneBit(len))/8+MAX_SHORT_LENGTH+2);
			encoder.write(getBytes(len));
		}
		else throw new IOException (LENGTH_ENCODE_FAIL);
	}
	
	private static byte[] getBytes(long l) {
		byte[] b=new byte[Long.numberOfTrailingZeros(Long.highestOneBit(l))/8+1];
		for(int i=0; i<b.length;i++) {
			b[b.length-1]=(byte) (l&0xFF);
			l>>=8;
		}
		return b;
	}
	@Override
	public long length() throws UnsupportedOperationException {
		if (len==INDEFINITE_INDEX) {return 1;}
		else if(len<=MAX_SHORT_LENGTH) {return 1;}
		else if(len>MAX_SHORT_LENGTH) {return 1+getBytes(len).length;}
		else throw new UnsupportedOperationException (LENGTH_NOT_DEFINED);
	}
	@Override
	public byte[] getBytes() throws UnsupportedOperationException {
		if (len==INDEFINITE_INDEX) {return new byte[]{(byte) (MAX_SHORT_LENGTH+1)};}
		else if(len<=MAX_SHORT_LENGTH) {return new byte[]{(byte) len};}
		else if(len>MAX_SHORT_LENGTH) {
			@SuppressWarnings("unused")
			int l=Long.numberOfTrailingZeros(Long.highestOneBit(len))/8+1;
			byte[] b=new byte[]{(byte) (Long.numberOfTrailingZeros(Long.highestOneBit(len))/8+MAX_SHORT_LENGTH+2)};
			return BER.join(b, getBytes(len));
		}
		else throw new UnsupportedOperationException (LENGTH_ENCODE_FAIL);
	}
	
	public String toString() {
		return "(length="+(this.isDefinite()?len:"UNDEFINED")+")";
	}
	@Override
	public void clearContent() {len=-1;}
}
