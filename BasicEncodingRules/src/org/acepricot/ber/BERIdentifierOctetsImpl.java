package org.acepricot.ber;

import java.io.IOException;

abstract class BERIdentifierOctetsImpl implements BERBase {

	@Override
	public long decode(BERInputStream in) throws IOException {
		int b = in.read();
		setTagNumber(b);
		setClassType(b);
		setConstructed(b);
		setValue(new byte[]{(byte) b});
		return 0x01L;
	}

	@Override
	public long encode(BEROutputStream out) throws IOException {
		out.write(this.getClassType()|(isPrimitive()?BERConst.PRIMITIVE_ENCODING:BERConst.CONSTRUCTED_ENCODING)|this.getTagNumber());
		return 0x01L;
	}
	
	abstract int getTagNumber();
	abstract void setTagNumber(int i) throws IOException;
	abstract int getClassType();
	abstract void setClassType(int i);
	abstract boolean isConstructed();
	abstract boolean isPrimitive();
	abstract void setConstructed(int i);
	abstract void setPrimitive(int i);
	abstract boolean idUniversal();
	abstract boolean idPrivate();
	abstract boolean idContextSpecific();
	abstract boolean idApplication();
}
