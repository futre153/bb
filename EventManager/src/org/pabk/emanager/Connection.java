package org.pabk.emanager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Logger;

public class Connection extends Thread {

	private Socket socket;
	private Logger log;
	private PrintWriter out;
	private String remote;
	private byte[] el=System.getProperty(Const.SYSTEM_LINE_SEPARATOR_KEY).getBytes();
	private int to;

	public void setSocket(Socket s, Logger log, int timeout) {
		this.socket=s;
		this.log=log;
		this.to=timeout;
		try {
			out=new PrintWriter(socket.getOutputStream());
		}
		catch(Exception e) {
			close();
		}
		if(isConnected()) {
			remote=socket.getRemoteSocketAddress().toString();
			log.info("Connection from "+remote+" was successfully created!");
			send(Interpreter.Msgs.WELCOME);
		}
	}
	
	public void run() {	
		while(true) {
			try {
				//System.out.println("Socket "+socket+" listen");
				socket.setSoTimeout(to);
				String line=readLn();
				socket.setSoTimeout(0);
				log.info("Read message from "+remote+": " + line);
				Interpreter.getInterpreter().addEntry(this, line);
				Interpreter.getInterpreter().sleep.wakeup();
				//System.out.println("Socket "+socket+" done");
			}
			catch (IOException e) {
				if(e instanceof SocketTimeoutException) {
					send(remote+" "+Interpreter.Msgs.INACTIVITY_TIMEOUT);
				}
				close();
				break;
			}
		}
	}
	
	public void send(String msg) {
		log.info("Send message to "+remote+": " + msg);
		out.append(msg);
		out.append(System.getProperty(Const.SYSTEM_LINE_SEPARATOR_KEY));
		out.flush();
	}

	public void close() {
		try {socket.close();}
		catch (Exception e) {}
		socket=null;
		log.info("Connection from "+remote+" was closed!");
	}

	public boolean isConnected() {
		return socket!=null; 
	}
	
	private String readLn() throws IOException {
		ByteArrayOutputStream bout=new ByteArrayOutputStream();
		byte[] b=null;
		try {
			while(true) {
				int i=socket.getInputStream().read();
				if(i<0){throw new Exception();}
				bout.write(i);
				if(bout.size()>2) {
					b=bout.toByteArray();
					for(i=0;i<el.length;i++) {
						if(!(el[i]==b[b.length-el.length+i])) {
							break;
						}
					}
					if(i==el.length) {
						break;
					}
				}
			}
			return new String(bout.toByteArray(),"utf-8").trim();
		}
		catch (Exception e) {
			if(e instanceof SocketTimeoutException) {
				throw (SocketTimeoutException)e;
			}
			throw new IOException("Error reading stream");
		}
	}

	public void sendError(Exception e) {
		send("ERROR: "+e.getClass().getName()+": "+e.getMessage()); 
	}
	
}
