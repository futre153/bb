package org.pabk.ber;

import java.io.IOException;

public class Optional extends BER {
	
	private BER opt;
	
	public Optional (String name, BER ber) {
		this.opt=ber;
		this.setName(name);
	}
	
	
	
	public void decode(Encoder en, long l) throws IOException {
		try {
			opt.decode(en, -1);
			this.opt.setName(name);
		}
		catch(IOException e) {
			opt=null;
		}
	}


	public void encode(Encoder en) throws IOException {
		if(opt!=null) {
			opt.encode(en);
		}
	}

	public Object getValue() throws UnsupportedOperationException {
		if(opt==null) return null;
		else return opt.getValue();
	}

	public byte[] getBytes()throws UnsupportedOperationException {
		if(opt==null) return new byte[]{};
		else return opt.getBytes();
	}
	
	public long length() {
		if(opt==null) return 0L;
		else return opt.length();
	}
	
	public void clearContent() {
		opt=null;
	}
	
	public String toString() {
		if(opt==null) return "";
		else return opt.toString();
	}
	
	@Override
	public BER clone() throws CloneNotSupportedException {
		BER ber=null;
		if(opt!=null) ber=opt.clone();
		return new Optional(this.getName(),ber);
	}
}
