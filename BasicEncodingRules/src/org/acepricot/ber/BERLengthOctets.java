package org.acepricot.ber;

import java.io.IOException;

public class BERLengthOctets extends BERLengthImpl {
	
	private static final int MAX_SUPPORTED_LENGH = 0xFFFFFF;
	private static final int UNSIGNED_BYTE = 0xFF;
	static final int SHORT_LENGTH_MASK = 0x7F;
	
	protected byte[] val;
		
	@Override
	public void setValue(byte[] b) throws IOException {
		val = b;
	}

	@Override
	public byte[] getValue() throws IOException {
		return val;
	}

	@Override
	boolean isDefinite() {
		return val != null;
	}

	@Override
	boolean isIndefinite() {
		return val == null;
	}

	@Override
	boolean isShort() {
		return val == null ? Boolean.FALSE : val.length == 1;
	}

	@Override
	boolean isLong() {
		return val == null ? Boolean.FALSE : val.length > 1;
	}

	@Override
	long getContentLength() throws IOException {
		byte[] b = getValue();
		if(b == null) return -1;
		long l = 0x0L;
		switch(b.length) {
		case 1:
			return b[0];
		case 2:
			l |= (b[1] & UNSIGNED_BYTE);
			break;
		case 3:
			l |= (b[1] & UNSIGNED_BYTE);
			l <<= Byte.SIZE;
			l |= (b[2] & UNSIGNED_BYTE);
			break;
		case 4:
			l |= (b[1] & UNSIGNED_BYTE);
			l <<= Byte.SIZE;
			l |= (b[2] & UNSIGNED_BYTE);
			l <<= Byte.SIZE;
			l |= (b[3] & UNSIGNED_BYTE);
			break;
		default:
			throw new BERFormatException(String.format(BERFormatException.UNSUPPORTED_LENGTH_OCTET, MAX_SUPPORTED_LENGH));
		}
		return l;
	}

	@Override
	void setContentLength(long l) throws IOException {
		byte[] b = null;
		if(l > BERLengthOctets.MAX_SUPPORTED_LENGH) {
			throw new BERFormatException(String.format(BERFormatException.UNSUPPORTED_LENGTH_OCTET, MAX_SUPPORTED_LENGH));
		}
		else if(l <= BERLengthOctets.MAX_SUPPORTED_LENGH && l > BERLengthOctets.SHORT_LENGTH_MASK) {
			b = new byte[Long.numberOfTrailingZeros(Long.highestOneBit(l))/Byte.SIZE + 2];
			b[0] = (byte) (b.length + BERLengthOctets.SHORT_LENGTH_MASK);
			switch(b.length) {
			case 2:
				b[1] = (byte) (l & UNSIGNED_BYTE);
				break;
			case 3:
				b[2] = (byte) (l & UNSIGNED_BYTE);
				l >>= Byte.SIZE;
				b[1] = (byte) (l & UNSIGNED_BYTE);
				break;
			case 4:
				b[3] = (byte) (l & UNSIGNED_BYTE);
				l >>= Byte.SIZE;
				b[2] = (byte) (l & UNSIGNED_BYTE);
				l >>= Byte.SIZE;
				b[1] = (byte) (l & UNSIGNED_BYTE);
				break;
			default:
				throw new BERFormatException(String.format(BERFormatException.UNKNOWN_LENGTH_ERROR, b.length));
			}
		}
		else if(l <= BERLengthOctets.SHORT_LENGTH_MASK && l >= 0) {
			b = new byte[]{(byte) l};
		}
		setValue(b);
	}

}
