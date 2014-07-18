package org.acepricot.ber;

import java.io.IOException;

abstract class BERLengthImpl implements BERBase {
	
	@Override
	public long decode(BERInputStream in) throws IOException {
		int b = in.read();
		int l = b & BERLengthOctets.SHORT_LENGTH_MASK;
		byte[] bt = null;
		if(b > BERLengthOctets.SHORT_LENGTH_MASK) {
			if (l == BERLengthOctets.SHORT_LENGTH_MASK) {
				throw new BERFormatException(String.format(BERFormatException.FORBIDEN_LANGTH_VALUE, b));
			}
			else if (l > 0) {
				bt = new byte[l + 1];
				bt[0] = (byte) b;
				in.read(bt, 1, l);
			}
		}
		else {
			bt = new byte[]{(byte) b};			
		}
		setValue(bt);
		return bt.length;
	}

	@Override
	public long encode(BEROutputStream out) throws IOException {
		byte[] b = (byte[]) getValue();
		if(b == null) b = new byte[]{(byte) (BERLengthOctets.SHORT_LENGTH_MASK+1)};
		out.write(b);
		return b.length;
	}
	
	abstract boolean isDefinite();
	abstract boolean isIndefinite();
	abstract boolean isShort();
	abstract boolean isLong();
	abstract long getContentLength() throws IOException;
	abstract void setContentLength(long l) throws IOException;
}
