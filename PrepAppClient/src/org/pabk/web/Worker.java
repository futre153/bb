package org.pabk.web;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.pabk.util.Base64Coder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

class Worker extends Thread {
	
	private static final int LOAD_ACTIVE_GLOBAL_ENTRIES = 0x01;
	private static final int LOAD_ARCHIVE_GLOBAL_ENTRIES = 0x02;
	private static final int LOAD_LOACL_ENTRIES = 0x03;
	private static final String LOAD_ACTIVE_GLOBAL_ENTRIES_DESC = "naèítanie globálneho zoznamu aktívnych položiek";
	private static final String LOAD_ARCHIVE_GLOBAL_ENTRIES_DESC = "naèítanie globálneho zoznamu archivovaných položiek";
	private static final String LOAD_LOACL_ENTRIES_DESC = "Prebieha vyh¾adavanie záznamov pod¾a Vášho výberu";
	
	private String description;
	private int percentage;
	private int workId;
	private Object[] args;
	private Object object;
	
	public void run() {
		try {
			switch(this.getWorkId()) {
			case LOAD_ACTIVE_GLOBAL_ENTRIES:
			case LOAD_ARCHIVE_GLOBAL_ENTRIES:
				Worker.loadRecords (this, this.getArgs());
				break;
			case LOAD_LOACL_ENTRIES:
				Worker.searchEntries(this, this.getArgs());
			default:
			}
			this.setWorkId(0);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	

	public static boolean searchRecords(ScrapEntries entries, ArrayList<Worker> workers, ScrapEntries local) {
		if(checkWorkingId(PrepAppClientServlet.getGLobalWorkers(), LOAD_ACTIVE_GLOBAL_ENTRIES, LOAD_ARCHIVE_GLOBAL_ENTRIES, LOAD_LOACL_ENTRIES) && checkWorkingId(workers, LOAD_ACTIVE_GLOBAL_ENTRIES, LOAD_ARCHIVE_GLOBAL_ENTRIES, LOAD_LOACL_ENTRIES)) {
			Worker w = new Worker();
			w.setWorkId(LOAD_LOACL_ENTRIES);
			workers.add(w);
			w.setDescription(LOAD_LOACL_ENTRIES_DESC);
			w.setPercentage(0);
			w.setArgs(new Object[]{entries, local});
			w.setObject(null);
			w.setDaemon(true);
			w.start();
			return true;
		}
		return false;
	}
	
	public static void loadActiveRecords(Properties props, ArrayList<Worker> workers) {
		if(checkWorkingId(PrepAppClientServlet.getGLobalWorkers(), LOAD_ACTIVE_GLOBAL_ENTRIES) && checkWorkingId(workers, LOAD_ACTIVE_GLOBAL_ENTRIES)) {
			Worker w = new Worker();
			ScrapEntries entries = new ScrapEntries(props);
			PrepAppClientServlet.setActiveEntries(entries);
			w.setWorkId(LOAD_ACTIVE_GLOBAL_ENTRIES);
			PrepAppClientServlet.getGLobalWorkers().add(w);
			w.setDescription(LOAD_ACTIVE_GLOBAL_ENTRIES_DESC);
			w.setPercentage(0);
			w.setArgs(new Object[]{props, entries, props.getProperty(PrepAppClientServlet.LIVE_VIEW_NAME_KEY)});
			w.setObject(null);
			w.setDaemon(true);
			w.start();
		}
	}
	
	public static void loadArchiveRecords(Properties props, ArrayList<Worker> workers) {
		if(checkWorkingId(PrepAppClientServlet.getGLobalWorkers(), LOAD_ARCHIVE_GLOBAL_ENTRIES) && checkWorkingId(workers, LOAD_ARCHIVE_GLOBAL_ENTRIES)) {
			Worker w = new Worker();
			ScrapEntries entries = new ScrapEntries(props);
			PrepAppClientServlet.setArchiveEntries(entries);
			w.setWorkId(LOAD_ARCHIVE_GLOBAL_ENTRIES);
			PrepAppClientServlet.getGLobalWorkers().add(w);
			w.setDescription(LOAD_ARCHIVE_GLOBAL_ENTRIES_DESC);
			w.setPercentage(0);
			w.setArgs(new Object[]{props, entries, props.getProperty(PrepAppClientServlet.ARCHIVE_VIEW_NAME_KEY)});
			w.setObject(null);
			w.setDaemon(true);
			w.start();
		}
	}
	
	private static boolean checkWorkingId(ArrayList<Worker> workers, int...ids) {
		for(int i = 0; i < workers.size(); i ++) {
			int id = workers.get(i).getWorkId();
			for(int j = 0; j < ids.length; j ++) {
				if(id == ids[j]) {
					return false;
				}
			}
		}
		return true;		
	}
	

	private static void searchEntries(Worker worker, Object...objects) throws Exception {
		ScrapEntries entries = (ScrapEntries) objects[0];
		ScrapEntries local = (ScrapEntries) objects[1];
		for(int i = 0; i < entries.size(); i ++) {
			local.add(entries.get(i));
			//System.out.println(local.add(entries.get(i)) + ", " + i);
			worker.setPercentage((i + 1) * 100 / entries.size());
		}
	}

	
	private static synchronized void loadRecords(Worker w, Object ...objects) throws IOException {
		Properties prop = (Properties) objects[0];
		ScrapEntries entries = (ScrapEntries) objects[1];
		String view = (String) objects[2];
		if(view.equals(prop.getProperty(PrepAppClientServlet.LIVE_VIEW_NAME_KEY))) {
			loadDirectoryView (w, entries, prop);
		}
		else {
			loadArchiveView (w, entries, prop);
		}
	}
	
	private static void loadArchiveView(Worker w, ScrapEntries entries, Properties prop) throws IOException {
		ZipFile zipFile = null;
		try {
			String archiveFile = prop.getProperty(PrepAppClientServlet.ARCHIVE_FILE_KEY);
			String encoding = prop.getProperty(PrepAppClientServlet.ARCHIVE_FILE_ENCODING_KEY);
			zipFile = new ZipFile(archiveFile, encoding == null ? Charset.defaultCharset() : Charset.forName(encoding));
			Enumeration <? extends ZipEntry> list = zipFile.entries();
			long length = 0;
			while (list.hasMoreElements()) {
				list.nextElement();
				length ++;
			}
			list = zipFile.entries();
			long counter = 0;
			while (list.hasMoreElements()) {
				ZipEntry entry = list.nextElement();
				loadEntry (entries, entry.getName(), zipFile.getInputStream(entry), encoding);
				counter ++;
				w.setPercentage((int) (counter * 100 / length));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new IOException(e);
		}
		finally {
			zipFile.close();
		}
	}
	
	
	private static void loadDirectoryView (Worker w, ScrapEntries entries, Properties prop) throws IOException {
		File liveDir = new File (prop.getProperty(PrepAppClientServlet.LIVE_STORE_KEY));
		File[] files = liveDir.listFiles();
		for(int i = 0; i < files.length; i ++) {
			if(files[i].isFile()) {
				loadEntry (entries, files[i].getName(), new FileInputStream(files[i]), prop.getProperty(PrepAppClientServlet.LIVE_STORE_ENCODING_KEY));
			}
			w.setPercentage((i + 1) * 100 / files.length);
		}
	}
	
	private static void loadEntry (ScrapEntries entries, String name, InputStream in, String chs) {
		try {
			BufferedReader reader = new BufferedReader (new InputStreamReader(in, chs == null ? Charset.defaultCharset() : Charset.forName(chs)));
			Hashtable<String, String> entry = new Hashtable<String, String>();
			String key = null;
			String val = null;
			while (((key = reader.readLine()) != null) && ((val = reader.readLine()) != null)) {
				entry.put(key, val);
			}
			Hashtable<String, Object> entry2 = new Hashtable<String, Object>();
			String request = entry.get(PrepAppClientServlet.REQUEST_KEY);
			if(request != null) {
				parseRequest (entry2, request);
			}
			String execTime = entry.get(PrepAppClientServlet.EXECUTIONTIME_KEY);
			if(execTime != null) {
				parseTime (PrepAppClientServlet.EXECUTIONTIME_KEY, entry2, execTime);
				//System.out.println(entry2.get(PrepAppClientServlet.EXECUTIONTIME_KEY));
			}
			String delivTime = entry.get(PrepAppClientServlet.RECEPTIONTIME_KEY);
			if(delivTime != null) {
				parseTime (PrepAppClientServlet.RECEPTIONTIME_KEY, entry2, delivTime);
			}
			String fault = entry.get(PrepAppClientServlet.FAULT_KEY);
			if(fault != null) {
				parseFault (entry2, fault);
			}
			String id = entry.get(PrepAppClientServlet.INTERNAL_ID_KEY);
			if(id != null) {
				parseInternalId (entry2, id);
			}
			entry2.put(PrepAppClientServlet.NAME_KEY, name);
			entries.add(entry2);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				in.close();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	static void parseRequest(Hashtable<String, Object> entry2, String request) throws SAXException, IOException {
		entry2.put(PrepAppClientServlet.ENCODED_REQUEST_KEY, request);
		entry2.put(PrepAppClientServlet.REQUEST_KEY, PrepAppClientServlet.getDOMBuilder().parse(new ByteArrayInputStream (Base64Coder.decode(request))));
		Element root = ((Document) entry2.get(PrepAppClientServlet.REQUEST_KEY)).getDocumentElement();
		String[] name = root.getTagName().split(":", 2);
		String pfx = name[0];
		String local = name[1];
		if(name[1].length() == 0) {
			local = name[0];
			pfx = name[1];
		}
		if(local.equalsIgnoreCase(PrepAppClientServlet.SOAP_ENVELOPE)) {
			NodeList nl = root.getChildNodes();
			for (int i = 0; i < nl.getLength(); i ++) {
				Node node = nl.item(i);
				if(node.getNodeType() == Node.ELEMENT_NODE && ((Element) node).getTagName().equalsIgnoreCase(pfx + ':' + PrepAppClientServlet.SOAP_BODY)) {
					nl = node.getChildNodes();
					for (i = 0; i < nl.getLength(); i ++) {
						node = nl.item(i);
						if(node.getNodeType() == Node.ELEMENT_NODE) {
							entry2.put(((Element) node).getTagName(), ((Element) node).getTextContent().trim());
						}
					}
					break;
				}
			}
			
		}
	}
	
	private static void parseInternalId(Hashtable<String, Object> entry2, String id) {
		entry2.put(PrepAppClientServlet.INTERNAL_ID_KEY, id);
	}

	private static void parseFault(Hashtable<String, Object> entry2, String fault) {
		entry2.put(PrepAppClientServlet.FAULT_KEY, fault);
	}

	private static void parseTime(String key, Hashtable<String, Object> entry2, String time) {
		entry2.put(key, new Date(Long.parseLong(time)));		
	}
	
	final String getDescription() {
		return description;
	}


	final void setDescription(String description) {
		this.description = description;
	}


	final int getPercentage() {
		return percentage;
	}


	final void setPercentage(int percentage) {
		this.percentage = percentage;
	}

	final int getWorkId() {
		return workId;
	}

	final void setWorkId(int workId) {
		this.workId = workId;
	}

	final Object[] getArgs() {
		return args;
	}

	final void setArgs(Object[] args) {
		this.args = args;
	}

	final Object getObject() {
		return object;
	}

	final void setObject(Object object) {
		this.object = object;
	}


}
