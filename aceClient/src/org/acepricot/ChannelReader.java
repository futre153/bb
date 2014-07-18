package org.acepricot;

import java.io.InputStream;
import java.io.OutputStream;

public class ChannelReader extends Thread {
	
	private InputStream in;
	private OutputStream out;

	public ChannelReader (InputStream in, OutputStream out) {
		this.in = in;
		this.out = out;
	}
	
	public void run() {
		int i = 0;
		try {
			while((i = in.read()) >= 0) {
				out.write(i);
			}
		}
		catch (Exception e) {}
		try {
			in.close();
		} catch (Exception e) {}
	}
	
	public OutputStream getOutputStream() {
		return out;
	}
}
