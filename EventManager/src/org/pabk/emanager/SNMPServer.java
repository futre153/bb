package org.pabk.emanager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

import org.pabk.emanager.snmp.DatagramStream;

public class SNMPServer extends HandlerImpl {

	@Override
	public void businessLogic() {
		
		int port=Const.DEFAULT_SNMP_PORT;
		try {
			port=Integer.parseInt(pro.getProperty(Const.SNMP_PORT_KEY));
			log.info("SNMP Server listen on port "+port);
		}
		catch(Exception e) {
			log.info("SNMP Server listen on default port "+port);
		}
		
		int timeout=Const.DEFAULT_SNMP_TIMEOUT;
		try {
			timeout=Integer.parseInt(pro.getProperty(Const.SNMP_TIMEOUT_KEY));
			log.info("SNMP server socket timeout is set to "+timeout+" milis");
		}
		catch(Exception e) {
			log.info("SNMP server socket timeout is set to default value "+timeout+" milis");
		}
		
		
		
		
		try {
			
			DatagramSocket ds=new DatagramSocket(port);
			log.info("SNMP server socket was successfully bind on port "+port);
			while(true) {
				ds.setSoTimeout(timeout);
				byte[] bt = new byte[ds.getReceiveBufferSize()];
				DatagramPacket dp=new DatagramPacket(bt, bt.length);
				try {
					ds.receive(dp);
				}
				catch (SocketTimeoutException e) {
					if(shutdown){break;}continue;
				}
				if(dp!=null) {
					log.info("Datagram recieved from: "+dp.getAddress().getHostAddress());
					DatagramStream.setStream(dp);				
				}
			}
			ds.close();
		}
		catch (IOException e) {
			log.severe("SNPM Server socket was unexpectally closed");
		}
		

	}

}
