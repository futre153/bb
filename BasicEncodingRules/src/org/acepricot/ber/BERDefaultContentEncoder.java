package org.acepricot.ber;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

final class BERDefaultContentEncoder implements BERContentDecoder,	BERContentEncoder {
	
	private boolean primitive;
	private boolean definite;
	
	private BERDefaultContentEncoder(boolean p, boolean d) {
		setPrimitive(p);
		setDefinite(d);
	}
	
	private boolean isPrimitive() {
		return primitive;
	}

	public void setPrimitive(boolean primitive) {
		this.primitive = primitive;
	}

	private boolean isDefinite() {
		return definite;
	}

	public void setDefinite(boolean definite) {
		this.definite = definite;
	}

	@Override
	public long encode(BEROutputStream out, BERImpl ber) throws IOException {
		if(isPrimitive() && isDefinite()) {
			return encodePrimitiveDefinite(out, ber);
		}
		else if (isPrimitive() && (!isDefinite())) {
			return encodePrimitiveIndefinite(out, ber);
		}
		else if((!isPrimitive()) && isDefinite()) {
			return encodeConstructedDefinite(out, ber);
		}
		else {
			return encodeConstructedIndefinite(out, ber);
		}

	}

	private long encodeConstructedIndefinite(BEROutputStream out, BERImpl ber) throws IOException {
		long l = encodeConstructedDefinite(out, ber);
		out.write(0);
		out.write(0);
		return l+2;
	}

	private long encodeConstructedDefinite(BEROutputStream out, BERImpl ber) throws IOException {
		long l = 0;
		for(int i = 0; i < ber.getConctructedContent().size(); i++) {
			l += ber.getConctructedContent().get(i).encode(out);
		}
		return l;
	}

	private long encodePrimitiveIndefinite(BEROutputStream out, BERImpl ber) throws IOException {
		long l = encodePrimitiveDefinite(out, ber);
		out.write(0);
		out.write(0);
		return l + 2;
	}

	private long encodePrimitiveDefinite(BEROutputStream out, BERImpl ber) throws IOException {
		out.write(ber.getValue());
		return ber.getValue().length;
	}

	@Override
	public long decode(BERInputStream in, BERImpl ber) throws IOException {
		if(isPrimitive() && isDefinite()) {
			return decodePrimitiveDefinite(in, ber);
		}
		else if (isPrimitive() && (!isDefinite())) {
			return decodePrimitiveIndefinite(in, ber);
		}
		else if((!isPrimitive()) && isDefinite()) {
			return decodeConstructedDefinite(in, ber);
		}
		else {
			return decodeConstructedIndefinite(in, ber);
		}
	}

	private long decodeConstructedIndefinite(BERInputStream in, BERImpl ber) throws IOException {
		long x = ber.getLengthOctets().getContentLength(), l = 0;
		ArrayList<BERImpl> nodes = new ArrayList<BERImpl>();
		while (x != 0) {
			BER node = new BER();
			l += node.decode(in);
			if(BERImpl.EOC_TAG_NUMBER == node.getIDOctets().getTagNumber()) {
				x = 0;
			}
			nodes.add(node);
		}
		ber.setConstructedContent(nodes);
		return l;
	}

	private long decodeConstructedDefinite(BERInputStream in, BERImpl ber) throws IOException {
		long l = ber.getLengthOctets().getContentLength(), x = l;
		ArrayList<BERImpl> nodes = new ArrayList<BERImpl>();
		while(x != 0) {
			BER node = new BER();
			x -= node.decode(in);
			if(x < 0) {
				throw new BERFormatException(String.format(BERFormatException.CONTENT_OCTETS_LENGTH_MISH_MATCH));
			}
			nodes.add(node);
		}
		ber.setConstructedContent(nodes);
		return l;
	}

	private long decodePrimitiveIndefinite(BERInputStream in, BERImpl ber) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int i = in.read(), j;	
		do {
			out.write(i);
			j = i;
			i = in.read();
		}
		while(!(i == j && i == BERImpl.EOC_TAG_NUMBER));
		byte[] b = out.toByteArray();
		ber.setValue(b);
		return b.length;
	}

	private long decodePrimitiveDefinite(BERInputStream in, BERImpl ber) throws IOException {
		byte[] b = new byte[(int) ber.getLengthOctets().getContentLength()];
		in.read(b);
		ber.setValue(b);
		return b.length;
	}

	public static BERDefaultContentEncoder getInstance(boolean primitive,	boolean definite) {
		return new BERDefaultContentEncoder (primitive,definite);
	}

	@Override
	public Object decodeValue(BERImpl ber) throws IOException {
		switch (ber.getIDOctets().getClassType()) {
		default:
			switch(ber.getIDOctets().getTagNumber()) {
			case BERConst.EOC_TAG_NUMBER:
				return "EOC";
			case BERConst.OCTETSTRING_TAG_NUMBER:
				return decodeOctetStringValue(ber);
			case BERConst.OBJECTIDENTIFIER_TAG_NUMBER:
				return decodeOidValue(ber);
			default:
				throw new BERFormatException(String.format(BERFormatException.VALUE_DECODE_MISSING, BERConst.getClassType(ber.getIDOctets().getClassType()), ber.getIDOctets().getTagNumber())); 
			}
		}
	}

	private Object decodeOctetStringValue(BERImpl ber) throws IOException {
		return ber.getValue();
	}

	private Object decodeOidValue(BERImpl ber) throws IOException {
		StringBuffer sb = new StringBuffer();
		byte[] b = ber.getValue();
		if(b != null) {
			if(b.length > 0) {
				int x = b[0]&0xFF;
				if(x > 80) {
					sb.append("2."+(x - 80));
				}
				else {
					sb.append((x / 40) + "." + (x % 40));
				}
				x = 0;
				for(int i = 1; i < b.length; i++) {
					x <<= 0x07;
					x |= (b[i]&0x7F);
					if(b[i] >= 0) {
						sb.append("." + x);
						x = 0;
					}
				}
			}
		}
		return sb.toString();
	}

	private static void encodeOctetStringValue(Object value, BERImpl ber) throws IOException {
		if(value instanceof byte[]) {
			byte[] b = (byte[]) value;
			ber.setValue(b);
		}
		else {
			throw new BERFormatException(String.format(BERFormatException.WRONG_VALUE_CLASS, value.getClass().getName(), ber.getIDOctets().getTagNumber(), "byte[]"));
		}
	}
	
	@Override
	public void encodeValue(Object value, BERImpl ber, boolean definite) throws IOException {
		this.setDefinite(definite);
		switch (ber.getIDOctets().getClassType()) {
		default:
			switch(ber.getIDOctets().getTagNumber()) {
			case BERConst.OBJECTIDENTIFIER_TAG_NUMBER:
				encodeOidValue(value, ber);
				break;
			case BERConst.OCTETSTRING_TAG_NUMBER:
				encodeOctetStringValue(value, ber);
				break;
			default:
				throw new BERFormatException(String.format(BERFormatException.VALUE_UNCODE_MISSING, BERConst.getClassType(ber.getIDOctets().getClassType()), ber.getIDOctets().getTagNumber())); 
			}
		}
	}

	private static void encodeOidValue(Object value, BERImpl ber) throws IOException {
		if(value instanceof String) {
			if(((String)value).length() !=0) {
				String[] s = ((String) value).split("\\.");
				int[] o = new int[s.length];
				int l = 1;
				for(int i = 0; i < s.length; i++) {
					try {
						o[i] = Integer.parseInt(s[i]);
					}
					catch(Exception e) {
						throw new BERFormatException(e);
					}
					if(i > 1) {
						l += (Integer.numberOfTrailingZeros(Integer.highestOneBit(o[i]))/0x07 + 1);
					}
				}
				byte[] b = new byte[l];
				if(b.length<1 || o.length<2) {
					throw new BERFormatException(String.format(BERFormatException.WRONG_OID_VALUE_FORMAT));
				}
				l = 0;
				b[l] = (byte) (o[0] * 40 + o[1]);
				l++;
				for(int i = 2; i < o.length; i++) {
					int k = (Integer.numberOfTrailingZeros(Integer.highestOneBit(o[i]))/0x07 + 1);
					for(int j = 0; j < k; j++) {
						int m = o[i]&0x7F;
						o[i] >>= 0x07;
						if(j > 0) {
							m |= 0x80;
						}
						if((l + k - j - 1) >= b.length) {
							throw new BERFormatException(String.format(BERFormatException.WRONG_OID_VALUE_FORMAT));
						}
						b[l + k - j - 1] = (byte) m;
					}
					l += k;
				}
				ber.setValue(b);
			}
			else {
				ber.setValue(new byte[]{});
			}
		}
		else {
			throw new BERFormatException(String.format(BERFormatException.WRONG_VALUE_CLASS, value.getClass().getName(), ber.getIDOctets().getTagNumber(), "String"));
		}
	}

}
