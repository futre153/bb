package org;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.net.Socket;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.pabk.emanager.parser.SAAPrtMsgParser;

public class TesterPrt {

	public static void main(String[] args) {
		try {
			int[] xx = new int[5];
			int x = xx.length/2;
			System.out.println(x);
			System.exit(1);
			//String line;
			String[] arg = {"D:\\temp\\00123847.prt", "D:\\temp\\00152845.prt"};
			byte[] start = {27, '(', 's', '1', '1', 'h', 27, '&', 'l', '6', 'D'};
			byte[] end = {27, 'E'};
			String ip = "10.1.14.99";
			//String name = "SWIFT_PRN";
			int port = 9100;
			Socket s = null;
			Logger log = Logger.getLogger(TesterPrt.class.getName());
			for(int i = 0; i < arg.length; i ++) {
				FileInputStream fin = new FileInputStream(arg[i]);
				SAAPrtMsgParser.loadMessages(log, fin);
				fin.close();
			}
			while(SAAPrtMsgParser.hasMoreElements()) {
				s = new Socket(ip, port);
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				out.write(start);
				out.write("TEST - TEST - TEST - TEST - TEST - TEST - TEST - TEST - TEST - TEST - TEST - TEST".getBytes());
				out.write(System.getProperty("line.separator").getBytes());
				String[] msg = (String[]) SAAPrtMsgParser.next();
				for(int i = 0; i < msg.length; i ++) {
					out.write(System.getProperty("line.separator").getBytes());
					out.write(msg[i].getBytes());
				}
				out.write(end);
				//System.out.println(new String(out.toByteArray()));
				s.getOutputStream().write(out.toByteArray());
				s.close();
			}
			//ByteArrayInputStream in = new ByteArrayInputStream();
			/*
			//String[] params = {"cmd.exe", "/c", "echo", filename};
			String[] params = {"cmd.exe", "/c", "lpr", "-S", "10.1.14.99", "-P", "SWIFT_10.1.14.99", "-o", filename};
			Process p = Runtime.getRuntime().exec(params);
			BufferedReader bri = new BufferedReader (new InputStreamReader(p.getInputStream()));
			BufferedReader bre = new BufferedReader (new InputStreamReader(p.getErrorStream()));
			while ((line = bri.readLine()) != null) {
				System.out.println(line);
			}
		    bri.close();
		    while ((line = bre.readLine()) != null) {
		        System.err.println(line);
		    }
		    bre.close();
		    p.waitFor();
		    */
		    System.out.println("Done.");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	    
	}

}
