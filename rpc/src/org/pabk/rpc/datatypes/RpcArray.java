package org.pabk.rpc.datatypes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

abstract class RpcArray extends RpcStruct {
	
	private Class<?> _class;
	
	public RpcArray (Class<?> _class, RpcDatatype ... dtps) {
		setValue(dtps == null ? new RpcDatatype[0] : dtps);
		this._class = _class; 
	}
	
	@Override
	public void encode(OutputStream out) throws IOException {
		RpcDatatype[] value = getValue();
		RpcUnsigned32 count = new RpcUnsigned32(value.length);
		count.encode(out);
		for(int i = 0 ; i < value.length; i ++) {
			value[i].encode(out);
		}
	}

	@Override
	public byte[] decode(InputStream in) throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		RpcUnsigned32 count = new RpcUnsigned32(0);
		if(count.getValue() > Integer.MAX_VALUE) {
			throw new IOException("Index out of bounds " + count);
		}
		bout.write(count.decode(in));
		RpcDatatype[] value = new RpcDatatype[count.getValue().intValue()];
		this.setValue(value);
		for(int i = 0 ; i < value.length; i ++) {
			try {
				value[i] = (RpcDatatype) _class.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new IOException (e);
			}
			bout.write(value[i].decode(in));
		}
		return bout.toByteArray();
	}
	
	public RpcUnsigned32 getCount() {
		return new RpcUnsigned32 (getValue().length);
	}
}
