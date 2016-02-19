/*
 * Decompiled with CFR 0_98.
 */
package manager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Date;

import org.pabk.ber.BERInteger;
import org.pabk.ber.BEROctetString;
import org.pabk.ber.BERSequence;
import org.pabk.ber.BaseBEREncoder;
import org.pabk.ber.Encoder;


public class Start {
	/*
	 * argument[0] - port
	 * algorithm expects existing of "trap" directory in working directory of JVM
	 * output filename of trap files is "trap\d{13}", numbers represents time in milliseconds
	 */
    public static void main(String[] argument) {
    	DatagramSocket ds = null;
    	SAATrap trap = new SAATrap("SAATrap");
    	final String LS = System.getProperty("line.separator"); 
    	Encoder en = new BaseBEREncoder();
        try {
            ds = new DatagramSocket(/*8162*/Integer.parseInt(argument[0]));
            byte[] bt = new byte[ds.getReceiveBufferSize()];
            do {
                DatagramPacket dp = new DatagramPacket(bt, bt.length);
                ds.receive(dp);
                BERSequence msg = trap.getMessage().clone();
                en.setInputStream(new ByteArrayInputStream(bt, 0, bt.length));
                en.setOutputStream(new ByteArrayOutputStream());
                msg.decode(en, -1);
                long eventNr = (long) (((BERInteger) ((BERSequence) msg.forName(SAATrap.SAA_EVENT_NUMBER)).forName(SAATrap.VALUE)).getValue());
				String description = new String((byte[]) (((BEROctetString) ((BERSequence) msg.forName(SAATrap.SAA_EVENT_DESCRIPTION)).forName(SAATrap.VALUE)).getValue()));
				System.out.println(String.format("Datagram %d received%s%s", eventNr, LS, description));
                switch ((int) eventNr) {
                case 8005:
                	if(description.contains("Nack received: {1:F21POBN")) {
                		saveTrap(bt, dp);
                	}
                	break;
                case 10018:
                case 10023:
                case 10050:
                case 10117:
                	if(description.indexOf("Message Partner FileInput,") == 0) {
                		saveTrap(bt, dp);
                	}
                	else if(description.indexOf("Message Partner FileInputCZ,") == 0) {
                		saveTrap(bt, dp);
                	}
                	else if(description.indexOf("Message Partner FileInputTest,") == 0) {
                		saveTrap(bt, dp);
                	}
                	else if(description.indexOf("Message Partner FileInputTstCZ,") == 0) {
                		saveTrap(bt, dp);
                	}
                	else {
                		break;
                	}
                }
                
            } while (true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
        	ds.close();
        }
    }
    
    private static void saveTrap(byte[] b, DatagramPacket p) {
    	String fName = "traps/trap" + new Date().getTime();
        FileOutputStream out = null;
        try {
	        out = new FileOutputStream(fName);
	        out.write(b, 0, p.getLength());
	        System.out.println("Datagram saved as " + fName);
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
        finally {
        	try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }
}
