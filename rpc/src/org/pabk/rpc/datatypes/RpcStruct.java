package org.pabk.rpc.datatypes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

abstract class RpcStruct implements RpcDatatype {
	
	private RpcDatatype[] value;
	
	protected RpcStruct (RpcDatatype ...dtps) {
		setValue(dtps == null ? new RpcDatatype[0] : dtps); 
	}
	
	@Override
	public RpcDatatype[] getValue() {
		return value;
	}

	@Override
	public void setValue(Object nr) {
		value = (RpcDatatype[]) nr;		
	}

	@Override
	public void encode(OutputStream out) throws IOException {
		for(int i = 0 ; i < value.length; i ++) {
			value[i].encode(out);
		}
	}

	@Override
	public byte[] decode(InputStream in) throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		for(int i = 0 ; i < value.length; i ++) {
			bout.write(value[i].decode(in));
		}
		return bout.toByteArray();
	}

}
