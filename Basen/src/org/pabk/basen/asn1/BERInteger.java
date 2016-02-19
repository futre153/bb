package org.pabk.basen.asn1;

import java.io.IOException;
import java.math.BigInteger;

import org.pabk.basen.ber.BERImpl;

public class BERInteger extends ASN1Impl {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public BERInteger (String name, boolean optional) {
		this.setName(name);
		this.set_class(BERImpl.UNIVERSAL_CLASS);
		this.setConstructed(BERImpl.PRIMITIVE_ENCODING);
		this.setTag(BERImpl.INTEGER_TAG);
		this.setImplicit(false);
		this.setOptional(optional);
	}
	private BERInteger() {}
	@Override
	public BERInteger clone() {
		return (BERInteger) ASN1Impl.clone(new BERInteger(), this);
	}
	public void setValue(Object obj) throws IOException {
		BigInteger bi = BigInteger.ZERO;
		try {
			if(obj instanceof Number) {
				bi = BigInteger.valueOf(((Number) obj).longValue());
			}
			else if (obj instanceof BigInteger) {
				bi = (BigInteger) obj;
			}
		}
		catch (Exception e) {}
		finally {
			super.setValue(bi.toByteArray());
		}
	}
	
}
