package org.pabk.rpc;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TcpIpConnection extends Connection {
	
	Socket s = null;
	private boolean shutdown = false;
	public TcpIpConnection(Object[] params) throws IOException {
		try {
			s = new Socket();
			s.connect(new InetSocketAddress((String) params[0], (int) params[1]), (int) params[2]);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IOException (e);
		}
	}
	
	public void run() {
		Exception error = null;
		try {
			while(!shutdown ) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		catch (Exception e) {
			error = e;
		}
		finally {
			try {
				s.close();
			} catch (IOException e) {
				if(error != null) {
					error.printStackTrace();
				}
				e.printStackTrace();
			}
		}
	}
	
	
	@Override
	int readBytes(byte[] b, int off, int len) throws IOException {
		return this.s.getInputStream().read(b, off, len);
	}
	@Override
	void writeBytes(byte[] b, int off, int len) throws IOException {
		this.s.getOutputStream().write(b, off, len);		
	}

	@Override
	void close() {
		shutdown = true;
	}
		
}
