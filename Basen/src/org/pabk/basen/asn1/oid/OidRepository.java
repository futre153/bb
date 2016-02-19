package org.pabk.basen.asn1.oid;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;

import org.pabk.util.Huffman;

public class OidRepository extends Hashtable <String, OidEntry> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String OID_ENTRY_LIST = ".entryList";
	private static final String DEFAULT_OID_ENTRY_LIST = "entryList.xml";
	private static final String OID_NOT_FOUND = "Oid %s not found in repository";
	private static final String NULL_OID_VAULE = "Oid cannot have a null value";
	private static final String REPOSITORY_COMMENT = "OID Repository - last saved on %s";
	private static OidRepository repository = new OidRepository();
	
	private OidRepository (){
		super();
		repository = this;
		loadRepository(System.getProperty(OidRepository.class + OID_ENTRY_LIST));
	}
	
	private static InputStream openSource (String list) {
		InputStream in = null;
		try {
			in = new FileInputStream(list);
		}
		catch(Exception e) {
			try {
				try {
					in.close();
				}
				catch(Exception e1){}
				in = OidRepository.class.getResourceAsStream(DEFAULT_OID_ENTRY_LIST);
			}
			catch (Exception e1) {}
		}
		return in;
	}

	public static void save() {
		saveRepository(System.getProperty(OidRepository.class + OID_ENTRY_LIST));		
	}
	
	private static synchronized void saveRepository(String list) {
		OutputStream out = null;
		try {
			out = openDestination(list);
			Properties prop = new Properties();
			Iterator<String> oids = repository.keySet().iterator();
			while(oids.hasNext()) {
				try {
					String name = oids.next();
					prop.setProperty(name, OidRepository.saveEntry(repository.get(name)));
				}
				catch(Exception e) {
					e.printStackTrace();
					/*TODO log*/
				}
			}
			prop.storeToXML(out, String.format(REPOSITORY_COMMENT, SimpleDateFormat.getInstance().format(new Date())));
		}
		catch (Exception e) {
			
		}
		finally {
			if(out != null) {
				try {
					out.close();
				} catch (IOException e) {}
			}
		}
	}

	private static String saveEntry(OidEntry oidEntry) throws Exception {
		ByteArrayOutputStream bout = new ByteArrayOutputStream(); 
		ObjectOutputStream out = new ObjectOutputStream(bout);
		out.writeObject(oidEntry);
		out.flush();
		return Huffman.encode(new String(bout.toByteArray()), null);
	}

	private static OutputStream openDestination(String list) {
		OutputStream out = null;
		try {
			out = new FileOutputStream(list);
		}
		catch(Exception e) {
			try {
				out = new FileOutputStream(new File(OidRepository.class.getResource(DEFAULT_OID_ENTRY_LIST).toURI()));
			} catch (Exception e1) {}
		}
		return out;
	}

	private static synchronized void loadRepository(String list) {
		InputStream in = null;
		try {
			in = openSource(list);
			Properties prop = new Properties();
			prop.loadFromXML(in);
			Iterator<String> oids = prop.stringPropertyNames().iterator();
			while(oids.hasNext()) {
				try {
					String name = oids.next();
					repository.put(name, OidRepository.loadEntry(prop.getProperty(name)));
				}
				catch(Exception e) {
					e.printStackTrace();
					/*TODO log*/
				}
			}
		}
		catch (Exception e) {
			repository.clear();
		}
		finally {
			try {
				if(in != null) {
					in.close();
				}
			} catch (IOException e) {}
		}
	}

	private static OidEntry loadEntry(String entry) throws Exception {
		//System.out.println(Huffman.decode(entry, null));
		ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(Huffman.decode(entry, null).getBytes()));
		OidEntry e =(OidEntry) in.readObject();
		return e;
				
	}
	
	public static boolean add(OidEntry entry) {
		OidEntry rem = repository.put(entry.getName(), entry);
		if(rem != null) {
			repository.put(rem.getName(), rem);
			return false;
		}
		else {
			return true;
		}
	}
	
	public static String getDescription(Object oid) {
		if(oid == null) {
			return NULL_OID_VAULE;
		}
		String oName = oid.toString();
		OidEntry o = repository.get(oid.toString());
		if(o == null) {
			return String.format(OID_NOT_FOUND, oName);
		}
		else {
			return o.getDescription();
		}
	}
}
