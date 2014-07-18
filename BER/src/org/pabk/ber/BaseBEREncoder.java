package org.pabk.ber;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class BaseBEREncoder implements Encoder {
	
	//private int buffer;
	private InputStream in;
	private OutputStream out;
	private ByteIOStream buf=new ByteIOStream(new byte[]{});
	private int level;
		
	@Override
	public void setInputStream(InputStream in) {this.in=in;}
	@Override
	public void setOutputStream(OutputStream out) {this.out=out;}

	@Override
	public void write(int bt) throws IOException {out.write(bt);}
	@Override
	public void write(byte[] b) throws IOException {out.write(b);}

	@Override
	public int read() throws IOException {
		if(buf.available()>0) return buf.read();
		return in.read();
	}
	@Override
	public int read(byte[] b) throws IOException {
		int i=buf.available();
		buf.read(b, 0, i);
		return in.read(b, i, b.length-i);
	}

	@Override
	public void close() {
		try {
			if(in!=null)in.close();
			if(out!=null)out.close();
		}
		catch (IOException e) {}
	}
	@Override
	public void retBytes(byte[] b) throws IOException {
		buf.append(b);
	}
	@Override
	public void setLevel(int level) {this.level=level;}
		
	@Override
	public int getLevel() {return level;}
}
