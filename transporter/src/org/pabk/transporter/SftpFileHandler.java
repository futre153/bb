package org.pabk.transporter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;

import org.pabk.util.Huffman;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.util.StatusPrinter;

import com.sshtools.j2ssh.SftpClient;
import com.sshtools.j2ssh.SshClient;
import com.sshtools.j2ssh.authentication.AuthenticationProtocolState;
import com.sshtools.j2ssh.authentication.PublicKeyAuthenticationClient;
import com.sshtools.j2ssh.configuration.SshConnectionProperties;
import com.sshtools.j2ssh.sftp.SftpFile;
import com.sshtools.j2ssh.transport.ConsoleKnownHostsKeyVerification;
import com.sshtools.j2ssh.transport.publickey.InvalidSshKeyException;
import com.sshtools.j2ssh.transport.publickey.SshPrivateKeyFile;

public class SftpFileHandler extends Thread {
	
	private static final String DOT = ".";
	public static final String FS = System.getProperty("file.separator");
	private static final String DEFAULT_PORT = "22";
	private static final String DEFAULT_INTERVAL = "0";
	private static final long POOLING_INTERVAL = 1000;
	private static final String DEFAULT_NEXT_RUN = "600000";
	private static final String DEFAULT_MASK = DOT + "*";
	public static final String TMP_FILE_EXTENSION = DOT + "tmp";
	public static final String ARCHIVE_FILE_EXTENSION = DOT + "arch";
	public static final String TMP_DIRECTORY = "temp";
	private static final int MAX_BYTES = 0x10000;
	private static final String RFS = "/";
	private Properties props = null;
	private Logger log = null;
	private String hostname;
	private int port;
	private long getInterval;
	private long putInterval;
	private long getNextRun;
	private long putNextRun;
	private File propFile;
	private long modify;
	private SshConnectionProperties sshp;
	private String knownHosts;
	private ConsoleKnownHostsKeyVerification ckhkv;
	private PublicKeyAuthenticationClient pk;
	private String username;
	private String privateKey;
	private String getRemote;
	private String maskGetFile;
	private String getSequence;
	private boolean stop;
	private String getLocal;
	private String getArchive;
	private String putRemote;
	private String putLocal;
	private String putArchive;
	private String maskPutFile;
	private String putSequence;
	private boolean checkFilenameDuplicity;
	
	public SftpFileHandler(Properties props) {
		this.props = props;
	}
	
	public void run() {
		setStop(false);
		try {
			getLogger();
			loadSettings();
			log.info("Setting successfully loaded");
			while(getInterval > 0 || putInterval > 0) {
				reloadSettings();
				if(getInterval > 0) {
					long next = checkInterval(getNextRun, getInterval);
					if(next >= 0) {
						getNextRun = next;
						getFiles();
					}
				}
				if(putInterval > 0) {
					long next = checkInterval(putNextRun, putInterval);
					if(next >= 0) {
						putNextRun = next;
						putFiles();
					}
				}
				if(isStop()) {
					break;
				}
				try {
					sleep(POOLING_INTERVAL);
				} catch (InterruptedException e) {
					log.error(e.getMessage());
				}
				//log.info(String.format("%d, %d", getNextRun, Calendar.getInstance().getTimeInMillis()));
			}
		}
		catch(Exception e) {
			log.error(e.getMessage());
		}
		finally {
			try {
				saveSettings();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void saveSettings() throws IOException {
		if(propFile.exists()) {
			if(!propFile.delete()) {
				return;
			}
		}
		if(propFile.createNewFile()) {
			FileOutputStream out = null;
			try {
				out = new FileOutputStream(propFile);
				props.storeToXML(out, String.format("Properties for %s", this.getName()));
				log.info(String.format("Properties for %s was successfully saved", this.getName()));
			}
			catch(IOException e) {
				log.error(e.getMessage());
			}
			finally {
				out.close();
			}
		}
	}

	private boolean authenticate(SshClient ssh, String s) throws IOException {
		int result = ssh.authenticate(pk);
		switch(result) {
		case AuthenticationProtocolState.CANCELLED:
			log.error(String.format("%S: SSH authentication cancelled", s));
			return false;
		case AuthenticationProtocolState.FAILED:
			log.error(String.format("%S: SSH authentication failed", s));
			return false;
		case AuthenticationProtocolState.PARTIAL:
			log.error(String.format("%S: SSH authentication partial", s));
			return false;
		case AuthenticationProtocolState.READY:
			log.error(String.format("%S: SSH authentication ready", s));
			return false;
		case AuthenticationProtocolState.COMPLETE:
			log.info(String.format("%S: SSH authentication complete", s));
			return true;
		default:
			log.error(String.format("%S: SSH authentication unknown state", s));
			return false;
		}
	}
	
	private void putFiles() throws Exception {
		SshClient ssh = new SshClient();
		try {
			ssh.connect(sshp, ckhkv);
			if(authenticate(ssh, "PUT")) {
				SftpClient sftp = null;
				try {
					sftp = ssh.openSftpClient();
					processPutFiles(sftp);
				}
				catch(Exception e) {
					log.error(e.getMessage());
					throw e;
				}
				finally {
					sftp.quit();
				}
			}
			
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		finally {
			ssh.disconnect();
			log.info("PUT: SSH disconnected");
		}
		
	}
	
	private void getFiles() throws Exception {
		SshClient ssh = new SshClient();
		try {
			ssh.connect(sshp, ckhkv);
			if(authenticate(ssh, "GET")) {
				SftpClient sftp = null;
				try {
					sftp = ssh.openSftpClient();
					processGetFiles(sftp);
				}
				catch(Exception e) {
					log.error(e.getMessage());
					throw e;
				}
				finally {
					sftp.quit();
				}
			}
			
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		finally {
			ssh.disconnect();
			log.info("GET: SSH disconnected");
		}
		
	}
	
	private void processPutFiles(SftpClient sftp) {
		ArrayList<File> files = null;
		File[] list = new File(putLocal).listFiles();
		//log.info(Arrays.toString(list));
		files = getNormalisedList(list, maskPutFile);
		//log.info(files.toString());
		File file = null;
		while((file = next(files, putSequence)) != null) {
			log.info(String.format("PUT: Found file %s", file.getName()));
			try {
				putFile(sftp, file);
			}
			catch(Exception e) {
				break;
			}
			putSequence = increaseSequence(putSequence);
			props.setProperty(Transporter.PUT_SEQUENCE, putSequence);
		}
	}
	
	private void processGetFiles(SftpClient sftp) {
		ArrayList<SftpFile> sFiles = null;
		try {
			List<?> list = sftp.ls(getRemote);
			//log.info(list.toString());
			sFiles = getNormalisedList(list, maskGetFile);
		} catch (IOException e) {
			log.error(e.getMessage());
			//throw e;
		}
		SftpFile sFile = null;
		while((sFile = nextFile(sFiles, getSequence)) != null) {
			log.info(String.format("GET: Found file %s", sFile.getFilename()));
			try {
				getFile(sftp, sFile);
			}
			catch(Exception e) {
				break;
			}
			getSequence = increaseSequence(getSequence);
			props.setProperty(Transporter.GET_SEQUENCE, getSequence);
		}
	}
	
	private void putFile(SftpClient sftp, File file) throws Exception {
		SftpFile dst = new SftpFile(putRemote + RFS + file.getName());
		String tmpFilename = createTmpFilename(file.getName());
		File tmp = new File(TMP_DIRECTORY + FS + tmpFilename);
		File arch = new File(putArchive + FS + file.getName() + ARCHIVE_FILE_EXTENSION);
		try {
			if(arch.exists() && checkFilenameDuplicity) {
				throw new IOException(String.format("PUT: File %s was allready processed", file.getName()));
			}
			log.info(String.format("PUT: File %s will be temporarily created for transfer purpose", tmpFilename));
			if(!file.renameTo(tmp)) {
				throw new IOException(String.format("PUT: Cannot rename file from %s to %s", file.getName(), tmp.getName()));
			}
			try {
				copyFile(tmp, arch);
				try {
					sftp.put(tmp.getAbsolutePath(), dst.getAbsolutePath());
					try {
						if(tmp.delete()) {
							log.info(String.format("PUT: File %s was successfully uploaded to the remote side", file.getName()));
						}
						else {
							throw new IOException (String.format("PUT: Failed to delete temporary file %s", tmp.getName()));
						}
					}
					catch (Exception e) {
						try {copyFile(arch, tmp);} catch (Exception e4){}
						throw e;
					}
				}
				catch (IOException e) {
					try {
						sftp.rm(dst.getAbsolutePath());
					}
					catch(Exception e3) {}
					throw e;
				}
			}
			catch(IOException e) {
				try{arch.delete();} catch (Exception e2) {}
				throw e;
			}
		}
		catch(IOException e) {
			log.error(e.getMessage());
			e.printStackTrace();
			try{tmp.renameTo(file);} catch(Exception e1) {}
			throw e;
		}
	}
	
	private void getFile(SftpClient sftp, SftpFile sFile) throws Exception {
		File dst = new File(getLocal + FS + sFile.getFilename());
		String tmpFilename = createTmpFilename(sFile.getFilename());
		File tmp = new File(TMP_DIRECTORY + FS + tmpFilename);
		File arch = new File(getArchive + FS + sFile.getFilename() + ARCHIVE_FILE_EXTENSION);
		try {
			if(arch.exists() && checkFilenameDuplicity) {
				throw new IOException(String.format("GET: File %s was allready processed", sFile.getFilename()));
			}
			log.info(String.format("GET: File %s will be temporarily created for transfer purpose", tmpFilename));
			sftp.get(sFile.getAbsolutePath(), tmp.getAbsolutePath());
			try {
				copyFile(tmp, arch);
				try {
					if(!tmp.renameTo(dst)) {
						throw new IOException(String.format("GET: Cannot rename file from %s to %s", tmp.getName(), dst.getName()));
					}
					try {
						sftp.rm(sFile.getAbsolutePath());
						log.info(String.format("GET: File %s was successfully downloaded to the local side", sFile.getFilename()));
					}
					catch(Exception e) {
						try {
							sftp.put(tmp.getAbsolutePath(), sFile.getAbsolutePath());
						}
						catch(Exception e3) {}
						throw e;
					}
				}
				catch (IOException e) {
					try {dst.renameTo(tmp);} catch (Exception e2) {}
					throw e;
				}
			}
			catch (IOException e) {
				try {arch.delete();} catch(Exception e1) {}
				throw e;
			}
		}
		catch (IOException e) {
			log.error(e.getMessage());
			e.printStackTrace();
			throw e;
		}
		finally {
			if(tmp != null && tmp.exists() && tmp.isFile()) {
				try {tmp.delete();} catch (Exception e) {}
			}
		}
	}

	private void copyFile(File src, File dst) throws IOException {
		if(dst.exists()) {
			dst.delete();
		}
		dst.createNewFile();
		FileInputStream in = null;
		FileOutputStream out = null;
		try {
			in = new FileInputStream(src);
			out = new FileOutputStream(dst);
			byte[] b = new byte[MAX_BYTES];
			int i = -1;
			while((i = in.read(b)) >= 0) {
				out.write(b, 0, i);
			}
		}
		catch (IOException e) {
			throw e;
		}
		finally {
			try {in.close();} catch (Exception e) {}
			try {out.close();} catch (Exception e) {}
		}
	}

	private String createTmpFilename(String filename) {
		//long date = Calendar.getInstance().getTimeInMillis();
		//double rand = Math.floor(Math.random() * 10000);
		return String.format("%2$s%1$s%3$d%1$s%4$04d%5$s", DOT, filename, Calendar.getInstance().getTimeInMillis(),  (int) Math.floor(Math.random() * 10000), TMP_FILE_EXTENSION);
	}

	private String increaseSequence(String sequence) {
		long seq;
		try {
			seq = Long.parseLong(sequence);
		}
		catch (Exception e) {
			seq = -1;
		}
		if(seq < 0) {
			return sequence;
		}
		else {
			seq ++;
			String result = String.format("%0" + sequence.length() + "d", seq);
			result = result.substring(result.length() - sequence.length());
			log.info(String.format("GET: Sequence updated from %s to %s", sequence, result));
			return result;
		}
	}

	private SftpFile nextFile(ArrayList<SftpFile> sFiles, String sequence) {
		long seq;
		try {
			seq = Long.parseLong(sequence);
		}
		catch (Exception e) {
			seq = -1;
		}
		if(sFiles.size() > 0) {
			if(seq < 0) {
				return sFiles.remove(0);
			}
			else {
				for(int i = 0; i < sFiles.size(); i ++) {
					if(sFiles.get(i).getFilename().contains(sequence)) {
						return sFiles.remove(i);
					}
				}
			}
		}
		return null;
	}
	
	private File next(ArrayList<File> files, String sequence) {
		long seq;
		try {
			seq = Long.parseLong(sequence);
		}
		catch (Exception e) {
			seq = -1;
		}
		if(files.size() > 0) {
			if(seq < 0) {
				return files.remove(0);
			}
			else {
				for(int i = 0; i < files.size(); i ++) {
					if(files.get(i).getName().contains(sequence)) {
						return files.remove(i);
					}
				}
			}
		}
		return null;
	}
	
	private ArrayList<SftpFile> getNormalisedList(List<?> list, String mask) {
		ArrayList<SftpFile> sFiles = new ArrayList<SftpFile>();
		for(int i = 0; i < list.size(); i ++) {
			SftpFile file = (SftpFile)list.get(i);
			if((file.isFile()) && (((mask == null) || (mask.length() == 0)) || (file.getFilename().matches(mask)))) {
				sFiles.add(file);
			}
			/*if(mask == null || mask.length() == 0) {
				sFiles.add(0, file);
				continue;
			}
			if ((file.isFile()) && (file.getFilename().matches(mask))) {
				sFiles.add(0, file);
			}*/
		}
		return sFiles;
	}
	
	private ArrayList<File> getNormalisedList(File[] list, String mask) {
		ArrayList<File> files = new ArrayList<File>();
		for(int i = 0; i < list.length; i ++) {
			File file = list[i];
			if((file.isFile()) && (((mask == null) || (mask.length() == 0)) || (file.getName().matches(mask)))) {
				files.add(file);
			}
			/*if(mask == null || mask.length() == 0) {
				files.add(0, file);
				continue;
			}
			if ((file.isFile()) && (file.getName().matches(mask))) {
				files.add(0, file);
			}*/
		}
		return files;
	}
	
	private void reloadSettings() throws Exception {
		if(!propFile.exists() || propFile.isDirectory()) {
			String msg = String.format("Properties file %s is missing, thread %s is going down", propFile.getName(), this.getName());
			log.error(msg);
			throw new IOException(msg);
		}
		if (propFile.lastModified() > modify) {
			FileInputStream in = null;
			try {
				in = new FileInputStream(propFile);
				props.loadFromXML(in);
				loadSettings();
				modify = propFile.lastModified();
				log.info(String.format("Properties was sucesfully reloaded from %s", propFile.getName()));
			}
			catch(Exception e) {
				throw e;
			}
			finally {
				try {in.close();} catch (IOException e) {}
			} 
		}
	}

	private long checkInterval(long nextRun, long interval) {
		Calendar c = GregorianCalendar.getInstance();
		long time = c.getTimeInMillis();
		long next = nextTime(interval, c);
		return nextRun < 0 ? next : (time > nextRun ? next : -1L); 
	}

	private long nextTime(long interval, Calendar c) {
		interval %= (1000*60*60*24);
		long actual = c.getTimeInMillis();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		long start = c.getTimeInMillis();
		return start + ((actual - start) / interval + 1) * interval;
	}

	private void loadSettings() throws InvalidSshKeyException, Exception {
		log.info("Property 1 loaded");
		hostname = props.getProperty(Transporter.HOSTNAME);
		log.info("Property 2 loaded");
		port = Integer.parseInt(props.getProperty(Transporter.PORT, DEFAULT_PORT));
		log.info("Property 3 loaded");
		getInterval = Long.parseLong(props.getProperty(Transporter.GET_INTERVAL, DEFAULT_INTERVAL));
		log.info("Property 4 loaded");
		putInterval = Long.parseLong(props.getProperty(Transporter.PUT_INTERVAL, DEFAULT_INTERVAL));
		log.info("Property 5 loaded");
		getNextRun = Long.parseLong(props.getProperty(Transporter.GET_NEXT_RUN, DEFAULT_NEXT_RUN));
		log.info("Property 6 loaded");
		putNextRun = Long.parseLong(props.getProperty(Transporter.PUT_NEXT_RUN, DEFAULT_NEXT_RUN));
		log.info("Property 7 loaded");
		propFile = new File(props.getProperty(Transporter.PROPATH));
		log.info("Property 8 loaded");
		modify = Long.parseLong(props.getProperty(Transporter.MODIFY, Long.toString(propFile.lastModified())));
		log.info("Property 9 loaded");
		knownHosts = props.getProperty(Transporter.KNOWN_HOSTS);
		log.info("Property 10 loaded");
		username = props.getProperty(Transporter.USERNAME);
		log.info("Property 11 loaded");
		privateKey = props.getProperty(Transporter.PRIVATE_KEY);
		log.info("Property 12 loaded");
		getRemote = props.getProperty(Transporter.GET_REMOTE);
		log.info("Property 13 loaded");
		getLocal = props.getProperty(Transporter.GET_LOCAL);
		log.info("Property 14 loaded");
		getArchive = props.getProperty(Transporter.GET_ARCHIVE);
		log.info("Property 15 loaded");
		maskGetFile = props.getProperty(Transporter.MASK_GET_FILE, DEFAULT_MASK);
		log.info("Property 15 loaded");
		getSequence = props.getProperty(Transporter.GET_SEQUENCE, null);
		log.info("Property 17 loaded");
		putRemote = props.getProperty(Transporter.PUT_REMOTE);
		log.info("Property 18 loaded");
		putLocal = props.getProperty(Transporter.PUT_LOCAL);
		log.info("Property 19 loaded");
		putArchive = props.getProperty(Transporter.PUT_ARCHIVE);
		log.info("Property 20 loaded");
		maskPutFile = props.getProperty(Transporter.MASK_PUT_FILE, DEFAULT_MASK);
		log.info("Property 21 loaded");
		putSequence = props.getProperty(Transporter.PUT_SEQUENCE, null);
		log.info("Property 22 loaded");
		checkFilenameDuplicity = Boolean.parseBoolean(props.getProperty(Transporter.CHECK_FILENAME_DUPLICITY, Boolean.toString(Boolean.TRUE)));
		log.info("Property 23 loaded");
		sshp = new SshConnectionProperties();
		log.info("Property 24 loaded");
		sshp.setHost(hostname);
		log.info("Property 25 loaded");
		sshp.setPort(port);
		log.info("Property 26 loaded");
		ckhkv = new ConsoleKnownHostsKeyVerification(knownHosts);
		log.info("Property 27 loaded");
		pk = new PublicKeyAuthenticationClient();
		log.info("Property 28 loaded");
		pk.setUsername(username);
		log.info("Property 29 loaded");
		SshPrivateKeyFile file = SshPrivateKeyFile.parse(new File(privateKey));
		log.info("Property 30 loaded");
		pk.setKey(file.toPrivateKey(Huffman.decode(props.getProperty(Transporter.PASSPHRASE), null)));
		log.info("Property 31 loaded");
	}

	public void getLogger () {
		String name = this.getName();
		
		//System.setProperty("logback.debug", "true");
		
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		
		PatternLayoutEncoder ple = new PatternLayoutEncoder();
		ple.setPattern(props.getProperty(Transporter.LOG_PATTERN));
		ple.setContext(lc);
		ple.start();
		
		RollingFileAppender<ILoggingEvent> fileApp = new RollingFileAppender<ILoggingEvent> ();
		//File f = new File(props.getProperty(Transporter.LOG_DIRECTORY) + System.getProperty("file.separator") + name + props.getProperty(Transporter.LOG_EXTENSION));
		fileApp.setFile(props.getProperty(Transporter.LOG_DIRECTORY) + System.getProperty("file.separator") + name + props.getProperty(Transporter.LOG_EXTENSION));
		//fileApp.setFile(f.getAbsolutePath());
		fileApp.setContext(lc);
				
		TimeBasedRollingPolicy<ILoggingEvent> tbrp = new TimeBasedRollingPolicy<ILoggingEvent>();
		tbrp.setParent(fileApp);
		//f = new File(props.getProperty(Transporter.LOG_DIRECTORY) + System.getProperty("file.separator") + name + ".%d{yyyy-MM-dd}" + props.getProperty(Transporter.LOG_EXTENSION) + ".zip");
		tbrp.setFileNamePattern(props.getProperty(Transporter.LOG_DIRECTORY) + System.getProperty("file.separator") + name + ".%d{yyyy-MM-dd}" + props.getProperty(Transporter.LOG_EXTENSION) + ".zip");
		tbrp.setCleanHistoryOnStart(false);
		//tbrp.setFileNamePattern(f.getAbsolutePath());
		tbrp.setContext(lc);
		tbrp.setMaxHistory(Integer.parseInt(props.getProperty(Transporter.LOG_HISTORY)));
		tbrp.start();
		
		fileApp.setRollingPolicy(tbrp);
		fileApp.setEncoder(ple);
		fileApp.start();
		
		log = lc.getLogger(name);
		log.addAppender(fileApp);
		
		StatusPrinter.print(lc);
		
		log.info(String.format("Logger for %s was saccessfully started", name));
	}

	private final boolean isStop() {
		return stop;
	}

	final void setStop(boolean stop) {
		this.stop = stop;
	}
	
}
