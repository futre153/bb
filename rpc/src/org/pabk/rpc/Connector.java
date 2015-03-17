package org.pabk.rpc;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import org.pabk.rpc.datatypes.RpcUnsigned16;
import org.pabk.rpc.datatypes.RpcUnsigned32;
import org.pabk.rpc.datatypes.RpcUnsigned8;

public class Connector extends HashMap <Object, Connection> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Connector store = new Connector();
	
	private Connector() {}
	
	public static Connection newConnection(Object id, int type, Object... params) throws IOException {
		Connection con;
		switch(type) {
		case Connection.TCP_IP:
			con = new TcpIpConnection(params);
			break;
		default:
			throw new IOException ("Coonetion type " + type + " does not defined");
		}
		con.setDaemon(true);
		if(id != null) { 
			con.setName(id.toString());
		}
		else {
			id = con.getName();
		}
		store.put(id, con);
		con.start();
		return con;
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		Long l = 0x0807060504030201L;
		System.out.println(l);
		System.out.println(new RpcUnsigned32(l));
		System.out.println(new RpcUnsigned16(l));
		System.out.println(new RpcUnsigned8(l));
		System.exit(1);
		Connection con = Connector.newConnection("new-conn", Connection.TCP_IP, "localhost", 135, 0);
		byte[] b = new byte[1024];
		//con.writeBytes(b, 0, b.length);
		con.readBytes(b, 0, b.length);
		System.out.println(Arrays.toString(b));
		con.join();
		//con.close();
	}
	
}
