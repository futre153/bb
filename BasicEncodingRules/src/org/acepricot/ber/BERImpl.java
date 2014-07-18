package org.acepricot.ber;

import java.io.IOException;
import java.util.ArrayList;

abstract class BERImpl implements BERBase {
	
	private static final BERIOStream NULL_STREAM = new BERNullStream();

	static int EOC_TAG_NUMBER = 0x00;
	
	protected BERIDOctets ido = new BERIDOctets();
	protected BERLengthOctets lno = new BERLengthOctets();
	private byte[] eva;
	protected ArrayList<BERImpl> cco = null;
			
	@Override
	public long decode(BERInputStream in) throws IOException {
		long l = 0x0L;
		l += ido.decode(in);
		l += lno.decode(in);
		l += getContentDecoder().decode(in, this);
		/*
		if(ido.isConstructed()) {
			l+=getConstructedContent(in);
			
			l+=BERImpl.getContentDecoder(BERContentDecoder.SIMPLE_CONSTRUCTED_CONTENT_DECODER).decode(in, this);
		}
		else {
			l+=BERImpl.getContentDecoder(COntentDecoderImpl).decode(in, this);
		}*/
		return l;
	}
	
	abstract BERContentDecoder getContentDecoder();
	
	BERIDOctets getIDOctets() {
		return ido;
	}

	ArrayList<BERImpl> getConctructedContent() {
		return cco;
	}

	void setConstructedContent(ArrayList<BERImpl> cco) {
		this.cco = cco;
	}

	void setIDOctets (BERIDOctets ido) {
		this.ido = ido;
	}

	BERLengthOctets getLengthOctets() {
		return lno;
	}

	void setLengthOctets(BERLengthOctets lno) {
		this.lno = lno;
	}

	abstract BERContentEncoder getContentEncoder();
	/*
	private long getConstructedContent(BERInputStream in) throws IOException {
		
	}*/

	@Override
	public long encode(BEROutputStream out) throws IOException {
		long lc = this.getContentEncoder().encode(NULL_STREAM, this);
		if(this.getLengthOctets().isDefinite()) {
			this.getLengthOctets().setContentLength(lc);
		}
		long l = this.ido.encode(out);
		l += this.lno.encode(out);
		l += getContentEncoder().encode(out, this);
		return l;
	}

	@Override
	public void setValue(byte[] b) throws IOException {
		 eva = b;
		 this.getLengthOctets().setContentLength(b.length);
	}

	@Override
	public byte[] getValue() throws IOException {
		return eva;
	}

	void setEncodedContent(byte[] b) {
		this.eva = b;
	}
	

}
