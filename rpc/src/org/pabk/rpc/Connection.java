package org.pabk.rpc;

import java.io.IOException;

abstract class Connection extends Thread {

	public static final int TCP_IP = 0x00;
	
	abstract int readBytes(byte[] b, int off, int len) throws IOException;
	abstract void writeBytes(byte[] b, int off, int len) throws IOException;
	abstract void close();
}
