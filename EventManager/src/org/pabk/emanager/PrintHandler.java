package org.pabk.emanager;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.Socket;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.logging.Logger;

import org.pabk.emanager.parser.SAAEventParser;
import org.pabk.emanager.parser.SAAPrtMsgParser;
import org.pabk.emanager.util.Sys;

public class PrintHandler extends HandlerImpl {
	
	private static final String PRINTER_IP_KEY = "printhandler.printer.ip";
	private static final String DEFAULT_PRINTER_IP = "10.1.14.99";
	private static final String NULL_PROPERTIES_ERROR = "Properties are null. %s will be interrupted";
	private static final String KEY_PROPERTY_ERROR = "Key of property cannot be null";
	private static final String PROPERTY_NULL_ERROR  = "Property %s cannot have null value";
	private static final String DEFAULT_PROPERTY_SET = "Set default value %s for property %s";
	private static final String PROPERTY_SET = "Loaded value %s for property %s from properties.";
	private static final String PROPERTY_PARSE_INTEGER_ERROR = "Failed to parse integer for property %s from value %s in handler %s";
	private static final String PROPERTY_PARSE_LONG_ERROR = "Failed to parse long integer for property %s from value %s in handler %s";
	private static final String PROPERTY_PARSE_BYTE_ERROR = "Failed to parse byte for property %s from value %s in handler %s";
	private static final String PROPERTY_CAST_ERROR = "Failed to create an instance of %s for property %s from value %s in handler %s";
	private static final String PRINTER_PORT_KEY = "printhandler.printer.port";
	private static final String DEFAULT_PRINTER_PORT = "9100";
	private static final String SEPARATOR_KEY = "printhandler.separator";
	private static final String DEFAULT_SEPARATOR = ",";
	private static final String PRINTER_TYPE_KEY = "printhandler.printer.type";
	private static final String DEFAULT_PRINTER_TYPE = "HP";
	private static final String START_SEQUENCE_KEY = "printhandler.printer.%s.startPCLsequence";
	private static final String DEFAULT_START_SEQUENCE = "27,40,115,49,49,104,27,38,108,54,68";
	private static final String END_SEQUENCE_KEY = "printhandler.printer.%s.endPCLsequence";
	private static final String DEFAULT_END_SEQUENCE = "27,69";
	private static final String QUEUES_KEY = "printhandler.queues";
	private static final String DEFAULT_QUEUES = "p1,p2,p3";
	private static final String QUEUES_KEY_KEY = "printhandler.queue.%s.key";
	private static final String DEFAULT_QUEUES_KEY = "SOURCE";
	private static final String QUEUES_PATTERN_KEY = "printhandler.queue.%s.pattern";
	private static final String HEADER_KEY = "printhandler.page.header";
	private static final String FOOTER_KEY = "printhandler.page.footer";
	private static final String AMT_SERVERS_KEY = "printhandler.amtservers";
	private static final Object MSG_TEXT = "MSG_TEXT";
	private static final Object SOURCE = "SOURCE";
	private static final String PRINTER_POOL_INTERVAL_KEY = "printhandler.poolinterval";
	private static final String DEFAULT_PRINTER_POOL_INTERVAL = "10";
	private static final String DATEFORMAT_KEY = "printhandler.dateformat";
	private static final String DEFAULT_DATEFORMAT = "EEEE, dd. MMMM YYYY - HH:mm:ss z";
	private static final String LOCALE_KEY = "printhandler.localization";
	private static final byte[] BOLD_PCL = {27, '(', 's', '3', 'B'};
	private static final byte[] BOLD_END_PCL = {27, '(', 's', '0', 'B'};
	private static final byte[] UNDERLINE_PCL = {27, '&', 'd', '3', 'D'};
	private static final byte[] UNDERLINE_END_PCL = {27, '&', 'd', '@'};
	private static String printerIP;
	private static int printerPort;
	private static String printerType;
	private static String separator;
	private static byte[] startPCLSequence;
	private static byte[] endPCLSequence;
	private static String[][] queues;
	private static String header;
	private static String footer;
	private static String[] AMTServers;
	private static long poolInterval;
	private static SimpleDateFormat dateFormat;
	private static Locale locale;
	
	@Override
	public void businessLogic() {
		try {
			loadSettings();
			
			sleep=new Sleeper();
			while(true) {
				log.info(this.getClass().getSimpleName()+" is working");
				if(shutdown)break;
				//String[] saaServer=DatagramCollector.getSAAServers();
				String tableName=MessageCollector.getTableName();
				log.info(this.getClass().getSimpleName()+" found "+AMTServers.length+" AMT client servers");
				for(int i=0;i<AMTServers.length;i++) {
					execute(log, SAAPrtMsgParser.getMsgFromDB(AMTServers[i], tableName, this, SAAPrtMsgParser.NOT_EXECUTED), AMTServers[i]);
				}
				log.info(this.getClass().getSimpleName()+" is SLEEPING");
				sleep.sleep(poolInterval);
				if(shutdown)break;
			}
			
			
		} catch (Exception e) {
			if (! (e instanceof IOException)) {
				e.printStackTrace();
			}
		}
	}
	
	private void execute(Logger log, ArrayList<Hashtable<String, Object>> list, String server) {
		log.info("Found " + list.size() + " global unprocessed message/s");
		//Print handler
		for(int i = 0; i < queues.length; i ++) {
			executePrint(log, findForType(list, queues[i][1],"\\\\\\\\"+server+"\\\\" + queues[i][2]), queues[i][0]);
		}
	}
	
	private void executePrint (Logger log, ArrayList<Hashtable<String, Object>> list, String name) {
		log.info("Found " + list.size() + " unprocessed message/s for printing from queue " + name);
		int counter = 0;
		for(int i = 0; i < list.size(); i ++) {
			Hashtable<String, Object> tab = list.get(i);
			try {
				printFINMessage(log, tab, name);
			}
			catch (IOException e) {
				continue;
			}
			counter ++;
			try {
				DBConnector.getDb(false,null).update(
						MessageCollector.getTableName(),
						new String[]{SAAPrtMsgParser.STATUS},
						new String[]{SAAPrtMsgParser.EXECUTED},
						"WHERE " + SAAPrtMsgParser.ID + " = '" + tab.get(SAAEventParser.ID)+ "'");
			}
			catch (SQLException e) {e.printStackTrace();}
		}
		log.info(String.format("Finished jobs on print queue %s. Files printed: %d, files skipped: %d", name, counter, list.size() - counter));
	}
	
	
	private void printFINMessage (Logger log, Hashtable<String, Object> msg, String name) throws IOException {
		String filename = null, source = null;
		try {
			String msgText = (String) msg.get(MSG_TEXT);
			source = (String) msg.get(SOURCE);
			filename = Sys.getPathOfTmpFile(msgText);
			log.info(String.format("File %s from source %s will be prepared to print on printer %s from queue %s", filename, source, printerIP.toString(), name));
			Socket s = new Socket(printerIP, printerPort);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			out.write(startPCLSequence);
			if(header != null) {
				out.write(header.getBytes());
				out.write(System.getProperty("line.separator").getBytes());
				out.write(System.getProperty("line.separator").getBytes());
			}
			String time = "%1$2s%2$s%1$64s";
			String datetime = dateFormat.format(new Date());
			out.write(UNDERLINE_PCL);
			out.write(BOLD_PCL);
			out.write(String.format(time, " ", datetime).getBytes());
			out.write(BOLD_END_PCL);
			out.write(UNDERLINE_END_PCL);
			out.write(System.getProperty("line.separator").getBytes());
			
			out.write(System.getProperty("line.separator").getBytes());
			out.write(System.getProperty("line.separator").getBytes());
			
			FileInputStream in = new FileInputStream(filename);
			int i = -1;
			while ((i = in.read()) >= 0) {
				out.write(i);
			}
			in.close();
			if(footer != null) {
				out.write(System.getProperty("line.separator").getBytes());
				out.write(System.getProperty("line.separator").getBytes());
				out.write(footer.getBytes());
			}
			out.write(endPCLSequence);
			//System.out.println(new String(out.toByteArray()));
			s.getOutputStream().write(out.toByteArray());
			s.close();
			log.info(String.format("File %s from source %s was successfully printed on printer %s from queue %s", filename, source, printerIP.toString(), name));
		}
		catch (Exception e) {
			log.warning(String.format("Failed to print file %s from source %s on printer %s from queue %s", filename, source, printerIP.toString(), name));
			throw new IOException(e);
		}
	}
	
	private ArrayList<Hashtable<String,Object>> findForType(ArrayList<Hashtable<String, Object>> tab, String key, String regExp) {
		//System.out.println(regExp);
		ArrayList<Hashtable<String, Object>> tmp=new ArrayList<Hashtable<String, Object>>();
		for(int i = 0; i < tab.size(); i ++) {
			Object item=tab.get(i).get(key);
			//System.out.println(item);
			if(item != null) {
				if(item instanceof String) {
					//System.out.println(((String) item).matches(regExp));
					if(((String) item).matches(regExp))tmp.add(tab.get(i));
				}
			}
		}
		return tmp;
	}
	
	private void loadSettings() throws IOException {
		separator = (String) PrintHandler.getProperty(this, SEPARATOR_KEY, DEFAULT_SEPARATOR, true, String.class, null);
		printerIP = (String) PrintHandler.getProperty(this, PRINTER_IP_KEY, DEFAULT_PRINTER_IP, true, String.class, separator);
		printerPort = (int) PrintHandler.getProperty(this, PRINTER_PORT_KEY, DEFAULT_PRINTER_PORT, true, int.class, separator);
		printerType = (String) PrintHandler.getProperty(this, PRINTER_TYPE_KEY, DEFAULT_PRINTER_TYPE, true, String.class, separator);
		startPCLSequence = (byte[]) PrintHandler.getProperty(this, String.format(START_SEQUENCE_KEY, printerType), DEFAULT_START_SEQUENCE, true, byte.class, separator);
		endPCLSequence = (byte[]) PrintHandler.getProperty(this, String.format(END_SEQUENCE_KEY, printerType), DEFAULT_END_SEQUENCE, true, byte.class, separator);
		String[] tmp = ((String) PrintHandler.getProperty(this, QUEUES_KEY, DEFAULT_QUEUES, true, String.class, separator)).split(separator);
		queues = new String[tmp.length][3];
		for(int i = 0; i < queues.length; i ++) {
			queues[i][0] = tmp[i];
			queues[i][1] = (String) PrintHandler.getProperty(this, String.format(QUEUES_KEY_KEY, tmp[i]), DEFAULT_QUEUES_KEY, true, String.class, separator);
			queues[i][2] = (String) PrintHandler.getProperty(this, String.format(QUEUES_PATTERN_KEY, tmp[i]), null, true, String.class, separator);
		}
		header = (String) PrintHandler.getProperty(this, HEADER_KEY, null, false, String.class, separator);
		footer = (String) PrintHandler.getProperty(this, FOOTER_KEY, null, false, String.class, separator);
		AMTServers = ((String) PrintHandler.getProperty(this, AMT_SERVERS_KEY, Const.DEFAULT_ALLIANCE_ACCESS_SERVERS, true, String.class, separator)).split(separator);
		poolInterval = ((long) PrintHandler.getProperty(this, PRINTER_POOL_INTERVAL_KEY, DEFAULT_PRINTER_POOL_INTERVAL, true, long.class, separator)) * 60 * 1000;
		String pattern = (String) PrintHandler.getProperty(this, DATEFORMAT_KEY, DEFAULT_DATEFORMAT, true, String.class, separator);
		locale = (Locale) PrintHandler.getProperty(this, LOCALE_KEY, null, false, Locale.class, separator);
		locale = locale == null ? Locale.getDefault() : locale;
		dateFormat = new SimpleDateFormat(pattern, locale);
	}
	
	
	private static final Object parse(Class<?> _class, String value, String key, String handlerName) throws IOException {
		try {
			if(_class.equals(String.class)) {
				return value;
			}
			else if(_class.equals(int.class)) {
				try {
					return Integer.parseInt(value);
				}
				catch(Exception e) {
					throw new IOException(String.format(PROPERTY_PARSE_INTEGER_ERROR, key, value, handlerName));
				}
			}
			else if(_class.equals(byte.class)) {
				try {
					return (byte) Integer.parseInt(value);
				}
				catch(Exception e) {
					throw new IOException(String.format(PROPERTY_PARSE_BYTE_ERROR, key, value, handlerName));
				}
			}
			else if(_class.equals(long.class)) {
				try {
					return Long.parseLong(value);
				}
				catch(Exception e) {
					throw new IOException(String.format(PROPERTY_PARSE_LONG_ERROR, key, value, handlerName));
				}
			}
			else {
				throw new IOException("");
			}
		}
		catch(Exception e) {
			throw new IOException (e);
		}
	}
	
	
	private static final Object getProperty(HandlerImpl handler, String key, String _default, boolean notNull, Class<?> _class, String separator) throws IOException {
		try {
			if(handler.pro == null) {
				throw new IOException(String.format(NULL_PROPERTIES_ERROR, handler.getClass().getSimpleName()));
			}
			if(key == null) {
				throw new IOException(String.format(KEY_PROPERTY_ERROR));
			}
			String value = handler.pro.getProperty(key);
			if(value == null) {
				if(_default == null && notNull) {
					throw new IOException (String.format(PROPERTY_NULL_ERROR, key));
				}
				value = _default;
				handler.log.info(String.format(DEFAULT_PROPERTY_SET, value, key));
				//return _default;
			}
			else {
				handler.log.info(String.format(PROPERTY_SET, value, key));
			}
			try {
				return PrintHandler.parse(_class, value, key, handler.getClass().getSimpleName());
			}
			catch (IOException e) {
				try {
					return _class.getDeclaredConstructor(String.class).newInstance(value);
				} catch (Exception e1) {
					try {
						String[] array = value.split(separator);
						Object obj = Array.newInstance(_class, array.length);
						for(int i = 0; i < array.length; i ++) {
							Array.set(obj, i, PrintHandler.parse(_class, array[i], key, handler.getClass().getSimpleName()));
						}
						return obj;
					}
					catch (Exception e2) {
						throw new IOException(String.format(PROPERTY_CAST_ERROR, _class.getSimpleName(), key, value, handler.getClass().getSimpleName()));
					}
				}
			}
		}
		catch (Exception e) {
			handler.log.severe(e.getMessage());
			throw new IOException(e);
		}
	}
	
}
