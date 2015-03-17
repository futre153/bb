package org.pabk.emanager.snmp;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Arrays;

import org.pabk.emanager.DatagramCollector;
import org.pabk.emanager.Sleeper;

public class DatagramStream extends Thread {
	
	private DatagramPacket packet;
	private static final ArrayList<DatagramIOStream> streams=new ArrayList<DatagramIOStream>();
	
	public static final void setStream(DatagramPacket dp) {
		DatagramStream dat=new DatagramStream();
		dat.setDaemon(true);
		dat.setPacket(dp);
		dat.start();
	}

	private void setPacket(DatagramPacket dp) {this.packet=dp;}
	
	
	public void run() {
		while(DatagramCollector.getInstance()==null) {
			new Sleeper().sleep(100);
		}
		while(!DatagramCollector.getInstance().isStopped()) {
			new Sleeper().sleep(100);
		}
		int i=0;
		for(;i<streams.size();i++) {
			if(packet.getAddress().getHostAddress().equals(streams.get(i).getHostAddress())) {
				streams.get(i).append(Arrays.copyOf(packet.getData(),packet.getLength()));
				break;
			}
		}
		if(i>=streams.size()) {
			streams.add(new DatagramIOStream(packet));
		}
		DatagramCollector.getInstance().wakeUp();

	}

	public static ArrayList<DatagramIOStream> getStreams() {return streams;}
	
}
