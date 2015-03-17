package org.pabk.proxyforwarder;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class StopListeners extends Thread {
	
	private ServerSocket ss;
	
	public StopListeners(ServerSocket ss) {
		this.ss = ss;
	}

	public void run() {
		ArrayList<Listener> list = Listener.getListeners();
		for(int i = 0; i < list.size(); i++) {
			Listener listener = list.get(i);
			if(listener == null) continue;
			if(listener.isAlive()) {
				listener.shutdown();
				try {
					listener.join();
				} catch (InterruptedException e) {
					continue;
				}
			}
		}
		try {
			ss.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("PROXY FORWARDER IS STOPPED");
	}
}
