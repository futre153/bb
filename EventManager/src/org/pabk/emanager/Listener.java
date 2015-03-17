package org.pabk.emanager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class Listener extends HandlerImpl {
	private static final ArrayList<Connection> conns=new ArrayList<Connection>();
	private static ServerSocket ss;
	
	@Override
	public void businessLogic() {
		int port=Const.DEFAULT_LISTENER_PORT;
		try {
			port=Integer.parseInt(pro.getProperty(Const.LISTENER_PORT_KEY));
			log.info("Listener listen on port "+port);
		}
		catch(Exception e) {
			log.info("Listener listen on default port "+port);
		}
		int timeout=Const.DEFAULT_SOCKET_TIMEOUT;
		try {
			timeout=Integer.parseInt(pro.getProperty(Const.SOCKET_TIMEOUT_KEY));
			log.info("Server socket timeout is set to "+timeout+" milis");
		}
		catch(Exception e) {
			log.info("Server socket timeout is set to default value "+timeout+" milis");
		}
		int usertimeout=Const.DEFAULT_USER_TIMEOUT;
		try {
			usertimeout=Integer.parseInt(pro.getProperty(Const.USER_TIMEOUT_KEY));
			log.info("User socket timeout is set to "+usertimeout+" milis");
		}
		catch(Exception e) {
			log.info("User socket timeout is set to default value "+usertimeout+" milis");
		}
		int max=Const.DEFAULT_MAX_CONNECTIONS;
		try {
			int max2=Integer.parseInt(pro.getProperty(Const.MAX_CONNECTIONS_KEY));
			if(max2<=max) {
				max=max2;
				log.info("Maximum incomming connection is set to "+max);
			}
			throw new Exception();
		}
		catch(Exception e) {
			log.info("Maximum incomming connection is set to default value "+max);
		}
		try {
			ss=new ServerSocket(port,max);
			log.info("Server socket was successfully bind on port "+port);
			while(true) {
				ss.setSoTimeout(timeout);
				Socket s=null;
				checkConnections();
				try {
					s=ss.accept();
				}
				catch (SocketTimeoutException e) {
					//System.out.println("Timeout reached");
					if(shutdown){
						forceClosedConnections();
						break;
					}
					continue;
				}
				if(s!=null) {
					Connection con=new Connection();
					con.setSocket(s,log,usertimeout);
					if(con.isConnected()) {
						con.setDaemon(true);
						if(conns.size()<max) {
							conns.add(con);
							con.start();
						}
						else {
							con.close();
							log.warning("Connection refused, maximum allowed connections are reached!");
						}
					}
				}
			}
			ss.close();
		}
		catch (IOException e) {
			log.severe("Server socket was unexpectally closed");
		}
	}

	private void checkConnections() {
		for(int i=0;i<conns.size();i++) {
			Connection con=conns.get(i);
			if(con==null) {conns.remove(i);i--;}
			else if(!con.isConnected()) {conns.remove(i);i++;}
		}
		
	}

	private void forceClosedConnections() {
		for(int i=0;i<conns.size();i++) {
			Connection con=conns.get(i);
			if(con==null) {}
			else if(con.isConnected()) {
				con.send(Interpreter.Msgs.SHUTDOWN_WARNING);
				con.close();
			}
		}
	}

}
