package com.acepricot.finance.sync.client;

public class Sleeper {

	public synchronized void sleep(long i)  {
		try {
			wait(i);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
	}

}
