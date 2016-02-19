package org.pabk.basen.asn1;

import java.io.IOException;

import org.pabk.basen.ber.BERImpl;

public class BERBoolean extends ASN1Impl {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public BERBoolean (String name, boolean optional) {
		this.setName(name);
		this.set_class(BERImpl.UNIVERSAL_CLASS);
		this.setConstructed(BERImpl.PRIMITIVE_ENCODING);
		this.setTag(BERImpl.BOOLEAN_TAG);
		this.setImplicit(false);
		this.setOptional(optional);
	}
	private BERBoolean() {}
	@Override
	public BERBoolean clone() {
		return (BERBoolean) ASN1Impl.clone(new BERBoolean(), this);
	}
	@Override
	public void setValue(Object ... objs) throws IOException {
		super.setValue(new byte[]{(objs == null || objs.length == 0 || objs[0] == null) ? 0 : ((objs[0] instanceof Boolean) ? (byte) ((boolean) objs[0] ? -1 : 0) : (objs[0] instanceof Number) ? (byte) (((Number)objs[0]).byteValue() == 0 ? 0 : -1) : 0)});
	}
}
