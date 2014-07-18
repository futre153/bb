package org.acepricot.ber;

import java.io.IOException;

class BERIDOctets extends BERIdentifierOctetsImpl {
	
	private static final int TAG_NUMBER_MASK 			= 0x1F;
	private static final int CLASS_TYPE_MASK 			= 0xC0;
	private static final int CONSTRUCTED_ENCODING_MASK 	= 0x20;
	private static final int MAX_TAG_NUMBER = TAG_NUMBER_MASK - 1;
	
	private static final int APPLICATION		= 0x40;
	private static final int CONTEXT_SPECIFIC	= 0x80;
	private static final int PRIVATE			= 0xC0;
	
	protected int tag;
	protected int css;
	protected int con;
	protected byte[] val;
	
	@Override
	int getTagNumber() {
		return tag;
	}

	@Override
	void setTagNumber(int i) throws IOException {
		i &= BERIDOctets.TAG_NUMBER_MASK;
		if (i == BERIDOctets.TAG_NUMBER_MASK) {
			throw new BERFormatException(String.format(BERFormatException.TAG_NUMBER_LIMIT, BERIDOctets.MAX_TAG_NUMBER));
		}
		tag = i;
	}

	@Override
	int getClassType() {
		return css;
	}

	@Override
	void setClassType(int i) {
		css = i & BERIDOctets.CLASS_TYPE_MASK;
	}

	@Override
	boolean isConstructed() {
		return con > 0;
	}

	@Override
	boolean isPrimitive() {
		return con == 0;
	}

	@Override
	boolean idUniversal() {
		return css == BERConst.UNIVERSAL_CLASS;
	}

	@Override
	boolean idPrivate() {
		return css == BERIDOctets.PRIVATE;
	}

	@Override
	boolean idContextSpecific() {
		return css == BERIDOctets.CONTEXT_SPECIFIC;
	}

	@Override
	boolean idApplication() {
		return css == BERIDOctets.APPLICATION;
	}

	@Override
	public void setValue(byte[] b) throws IOException {
		val = b;		
	}

	@Override
	public byte[] getValue() throws IOException {
		return val;
	}

	@Override
	void setConstructed(int i) {
		con = i & BERIDOctets.CONSTRUCTED_ENCODING_MASK;
	}

	@Override
	void setPrimitive(int i) {
		con = i & BERIDOctets.CONSTRUCTED_ENCODING_MASK;
	}

}
