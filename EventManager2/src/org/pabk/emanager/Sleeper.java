package org.pabk.emanager;


public class Sleeper {

	public synchronized void sleep(long interval) {
		try {wait(interval);} catch (InterruptedException e) {}
	}
	public synchronized void wakeup() {
		//System.out.println("wakeup");
		notify();
	}
	
}
