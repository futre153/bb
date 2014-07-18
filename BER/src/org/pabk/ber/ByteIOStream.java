package org.pabk.ber;

import java.io.ByteArrayInputStream;

final class ByteIOStream extends ByteArrayInputStream {

	private static final int MAX_LENGTH = 1024;

	public ByteIOStream(byte[] buf) {
		super(buf);
	}

	void append(byte[] b) {
		//System.out.println(this.available());
		//System.out.println("count="+this.count+",mark="+this.mark+",pos="+this.pos);
		buf=BER.join(this.buf, b);
		count+=b.length;
		if(buf.length>MAX_LENGTH) {
			buf=BER.cut(buf,pos,buf.length);
			count-=pos;
			pos=0;
			mark=0;
		}
		//System.out.println("count="+this.count+",mark="+this.mark+",pos="+this.pos);
		//System.out.println(this.available());
	}
	
}
