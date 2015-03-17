package org.pabk.emanager.snmp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.util.Arrays;

import org.pabk.ber.Encoder;
import org.pabk.emanager.util.Sys;

public class DatagramIOStream implements Encoder {
	
	private static final int MAX_READED_BYTES = 2048;
	private String host="";
	private int level=0;
	private byte[] buffer;
	private int pointer=-1;
	private Integer lock=null;
	
	public DatagramIOStream() {this(new byte[]{},"");}
	
	public DatagramIOStream(byte[] b, String host) {
		buffer=b;
		level=0;
		pointer=0;
		this.host=host;
	}
	
	public DatagramIOStream(DatagramPacket dp) {
		this(Arrays.copyOf(dp.getData(),dp.getLength()),dp.getAddress().getHostAddress());
		//System.out.println(dp.getLength());
		//System.out.println(dp.getData().length);
	}
	
	public String getHostAddress() {return host;}

	public void lock(){lock=pointer;}
	
	public void unlock(){lock=null;}
	
	public void restore(){pointer=lock;unlock();}
	
	@Override
	public void close() {buffer=null;}
	
	public int available() throws IOException{return action(5,null,-1);}
	
	@Override
	public int getLevel() {return level;}

	@Override
	public int read() throws IOException {return action(0,null,-1);}

	private synchronized int action(int i, byte[]b, int bt) throws IOException {
		if(buffer==null){throw new IOException("Stream is not open");}
		int x=pointer;
		if(lock!=null)x=lock;
		if(x>MAX_READED_BYTES) {
			buffer=Sys.cut(buffer, MAX_READED_BYTES)[1];
			pointer-=DatagramIOStream.MAX_READED_BYTES;
			if(lock!=null)lock-=DatagramIOStream.MAX_READED_BYTES;
		}
		switch(i) {
		case 0: {
			if(pointer>=buffer.length)return -1;
			bt=buffer[pointer]&0xFF;
			pointer++;
			return bt;
		}
		case 1: {
			if(pointer>=buffer.length)return -1;
			int len=buffer.length-pointer;
			len=len>b.length?b.length:len;
			System.arraycopy(buffer, pointer, b, 0, len);
			pointer+=len;
			return len;
		}
		case 2: {
			buffer=Sys.join(buffer, new byte[]{(byte)bt});
			return buffer.length;
		}
		case 3: {
			buffer=Sys.join(buffer, b);
			return buffer.length;
		}
		case 4: {
			pointer-=b.length;
			if(pointer<0)throw new IOException("Cannot return data is dismalted");
			//byte[][] c=Sys.cut(buffer,pointer);
			//buffer=Sys.join(Sys.join(c[0], b),c[1]);
			return buffer.length;
		}
		case 5: {
			return buffer.length-pointer;
		}
		default:throw new IOException("Action not defined");
		}
	}

	@Override
	public int read(byte[] b) throws IOException {return action(1,b,-1);}

	@Override
	public void retBytes(byte[] b) throws IOException {action(4,b,-1);}

	@Override
	public void setInputStream(InputStream arg0) {}

	@Override
	public void setLevel(int l) {this.level=l;}

	@Override
	public void setOutputStream(OutputStream arg0) {}

	@Override
	public void write(int bt) throws IOException {action(2,null,bt);}

	@Override
	public void write(byte[] b) throws IOException {action(3,b,-1);}

	public void append(byte[] data) {try {write(data);} catch (IOException e) {e.printStackTrace();}}
	
}
