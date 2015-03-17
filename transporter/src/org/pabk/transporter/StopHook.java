package org.pabk.transporter;


public class StopHook extends Thread {
	
	private SftpFileHandler[] sftpFileHandler;
	
	public StopHook(SftpFileHandler[] sftpFileHandler) {
		this.sftpFileHandler = sftpFileHandler;
	}
	
	public void run() {
		for(int i = 0; i < sftpFileHandler.length; i ++) {
			try {
				sftpFileHandler[i].setStop(true);
				sftpFileHandler[i].join();
				System.out.println(String.format("\tThread nr. %d [%s] was stopped ", i, sftpFileHandler[i].getName()));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Application TRANSPORTER is stopped");
	}
	
}
