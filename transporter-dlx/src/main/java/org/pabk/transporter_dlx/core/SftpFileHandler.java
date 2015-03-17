package org.pabk.transporter_dlx.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;

import org.pabk.util.Huffman;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;

import com.sshtools.j2ssh.SftpClient;
import com.sshtools.j2ssh.SshClient;
import com.sshtools.j2ssh.authentication.AuthenticationProtocolState;
import com.sshtools.j2ssh.authentication.PublicKeyAuthenticationClient;
import com.sshtools.j2ssh.configuration.SshConnectionProperties;
import com.sshtools.j2ssh.sftp.SftpFile;
import com.sshtools.j2ssh.transport.ConsoleKnownHostsKeyVerification;
import com.sshtools.j2ssh.transport.publickey.SshPrivateKeyFile;

public class SftpFileHandler extends Thread {
	
	private static final String DOT = ".";
	public static final String FS = System.getProperty("file.separator");
	private static final String DEFAULT_PORT = "22";
	private static final long POOLING_INTERVAL = 1000;
	private static final String DEFAULT_MASK = DOT + "*";
	public static final String TMP_FILE_EXTENSION = DOT + "tmp";
	public static final String ERROR_LOG_EXTENSION = DOT + "err" + DOT + "log";
	public static final String ARCHIVE_FILE_EXTENSION = DOT + "arch";
	public static final String TMP_DIRECTORY = "temp";
	private static final String GET_DIRECTION = "get";
	private static final String PUT_DIRECTION = "put";
	private static final int MAX_BYTES = 0x10000;
	private static final String RFS = "/";
	private static final String DEFAULT_TIMER_CLASS = SimpleTimer.class.getName();
	private Properties props = null;
	private Logger log = null;
	private String hostname;
	private int port;
	private String direction;
	private File propFile;
	private long modify;
	private SshConnectionProperties sshp;
	private String knownHosts;
	private ConsoleKnownHostsKeyVerification ckhkv;
	private PublicKeyAuthenticationClient pk;
	private String username;
	private String privateKey;
	private String remote;
	private String archive;
	private String mask;
	private String sequence;
	private String local;
	private boolean checkFilenameDuplicity;
	private TimerEntry timer;
	private boolean stop;
	private boolean saveSettings = false;
		
	public SftpFileHandler(Properties props) {
		this.props = props;
	}
	
	
	public void run() {
		setStop(false);
		try {
			SftpFileHandler.getLogger(this, props);
			loadSettings();
			long nextRun = timer.getNextRun();
			String timerName = timer.getName();
			int c = 200;
			log.debug(new Date(nextRun) + ", " + nextRun);
			log.debug("-----------------------------------");
			long[] t = timer.getNextRuns(c);
			for(int i = 0; i < t.length; i ++) {
				log.debug(new Date(t[i]) + ", " + t[i]);
			}
			log.debug("-----------------------------------");
			while(!isStop()) {
				reloadSettings();
				log.debug(new Date().toString() + ", " + new Date(timer.getNextRun()).toString());
				//log.debug();
				if(timer.isRunTime(nextRun)) {
					log.info(String.format("SFTP transfer starts. Schedule %s is aplied", timerName));
					if(direction.equalsIgnoreCase(GET_DIRECTION)) {
						getFiles();
					}
					if(direction.equalsIgnoreCase(PUT_DIRECTION)) {
						putFiles();
					}
					nextRun = timer.getNextRun();
					timerName = timer.getName();
				}
				if(saveSettings ) {
					saveSettings();
					saveSettings = false;
				}
				if(isStop()) {
					break;
				}
				try {
					sleep(POOLING_INTERVAL);
				} catch (InterruptedException e) {
					log.error(e.getMessage());
				}	
			}
		}
		catch(Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		finally {
			try {saveSettings();} catch (IOException e) {log.error(e.getMessage());}
			//stopLogger();
		}
	}
	
	

	private void saveSettings() throws IOException {
		if(props != null) {
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
	
	private void putFiles() {
		SshClient ssh = new SshClient();
		try {
			ssh.connect(sshp, ckhkv);
			if(authenticate(ssh, PUT_DIRECTION.toUpperCase())) {
				SftpClient sftp = null;
				try {
					sftp = ssh.openSftpClient();
					processPutFiles(sftp);
				}
				catch(Exception e) {
					log.error(e.getMessage());
				}
				finally {
					sftp.quit();
				}
			}
			
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		finally {
			ssh.disconnect();
			log.info("PUT: SSH disconnected");
		}
		
	}
	
	private void getFiles() {
		SshClient ssh = new SshClient();
		try {
			ssh.connect(sshp, ckhkv);
			if(authenticate(ssh, GET_DIRECTION.toUpperCase())) {
				SftpClient sftp = null;
				try {
					sftp = ssh.openSftpClient();
					processGetFiles(sftp);
				}
				catch(Exception e) {
					log.error(e.getMessage());
				}
				finally {
					sftp.quit();
				}
			}
			
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		finally {
			ssh.disconnect();
			log.info("GET: SSH disconnected");
		}
		
	}
	
	private void processPutFiles(SftpClient sftp) {
		ArrayList<File> files = null;
		File[] list = new File(local).listFiles();
		//log.info(Arrays.toString(list));
		files = getNormalisedList(list, mask);
		//log.info(files.toString());
		File file = null;
		while((file = next(files, sequence)) != null) {
			log.info(String.format("PUT: Found file %s", file.getName()));
			try {
				putFile(sftp, file);
			}
			catch(Exception e) {
				e.printStackTrace();
				break;
			}
			sequence = increaseSequence(sequence);
			props.setProperty(Transporter.SEQUENCE, sequence);
		}
	}
	
	private void processGetFiles(SftpClient sftp) {
		ArrayList<SftpFile> sFiles = null;
		try {
			List<?> list = sftp.ls(remote);
			//log.info(list.toString());
			sFiles = getNormalisedList(list, mask);
		} catch (IOException e) {
			log.error(e.getMessage());
			//throw e;
		}
		SftpFile sFile = null;
		while((sFile = nextFile(sFiles, sequence)) != null) {
			log.info(String.format("GET: Found file %s", sFile.getFilename()));
			try {
				getFile(sftp, sFile);
			}
			catch(Exception e) {
				e.printStackTrace();
				break;
			}
			sequence = increaseSequence(sequence);
			props.setProperty(Transporter.SEQUENCE, sequence);
		}
	}
	
	private void putFile(SftpClient sftp, File file) throws Exception {
		SftpFile dst = new SftpFile(remote + RFS + file.getName());
		String tmpFilename = createTmpFilename(file.getName());
		File tmp = new File(TMP_DIRECTORY + FS + tmpFilename);
		File arch = new File(archive + FS + file.getName() + ARCHIVE_FILE_EXTENSION);
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
			try{tmp.renameTo(file);} catch(Exception e1) {}
			throw e;
		}
	}
	
	private void getFile(SftpClient sftp, SftpFile sFile) throws Exception {
		File dst = new File(local + FS + sFile.getFilename());
		String tmpFilename = createTmpFilename(sFile.getFilename());
		File tmp = new File(TMP_DIRECTORY + FS + tmpFilename);
		File arch = new File(archive + FS + sFile.getFilename() + ARCHIVE_FILE_EXTENSION);
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
		return String.format("%2$s%1$s%3$d%1$s%4$04d%5$s", DOT, filename, GregorianCalendar.getInstance().getTimeInMillis(),  (int) Math.floor(Math.random() * 10000), TMP_FILE_EXTENSION);
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
			saveSettings = true;
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
			//if(mask == null || mask.length() == 0) {
			//	sFiles.add(0, file);
			//	continue;
			//}
			//if ((file.isFile()) && (file.getFilename().matches(mask))) {
			//	sFiles.add(0, file);
			//}
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
			//if(mask == null || mask.length() == 0) {
			//	files.add(0, file);
			//	continue;
			//}
			//if ((file.isFile()) && (file.getName().matches(mask))) {
			//	files.add(0, file);
			//}
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
/*
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
*/
	private void loadSettings() throws Exception {
		hostname = props.getProperty(Transporter.HOSTNAME);
		port = Integer.parseInt(props.getProperty(Transporter.PORT, DEFAULT_PORT));
		propFile = new File(props.getProperty(Transporter.PROPATH));
		modify = Long.parseLong(props.getProperty(Transporter.MODIFY, Long.toString(propFile.lastModified())));
		knownHosts = props.getProperty(Transporter.KNOWN_HOSTS);
		username = props.getProperty(Transporter.USERNAME);
		privateKey = props.getProperty(Transporter.PRIVATE_KEY);
		remote = props.getProperty(Transporter.REMOTE);
		local = props.getProperty(Transporter.LOCAL);
		archive = props.getProperty(Transporter.ARCHIVE);
		mask = props.getProperty(Transporter.MASK, DEFAULT_MASK);
		sequence = props.getProperty(Transporter.SEQUENCE, null);
		checkFilenameDuplicity = Boolean.parseBoolean(props.getProperty(Transporter.CHECK_FILENAME_DUPLICITY, Boolean.toString(Boolean.TRUE)));
		direction = props.getProperty(Transporter.DIRECTION);
		timer = (TimerEntry) Class.forName(props.getProperty(Transporter.TIMER_CLASS, DEFAULT_TIMER_CLASS)).newInstance();
		timer.loadTimer(props);
		
		sshp = new SshConnectionProperties();
		sshp.setHost(hostname);
		sshp.setPort(port);
		ckhkv = new ConsoleKnownHostsKeyVerification(knownHosts);
		pk = new PublicKeyAuthenticationClient();
		pk.setUsername(username);
		SshPrivateKeyFile file = SshPrivateKeyFile.parse(new File(privateKey));
		pk.setKey(file.toPrivateKey(Huffman.decode(props.getProperty(Transporter.PASSPHRASE), null)));
	}

	public static synchronized void getLogger (SftpFileHandler t, Properties prop) throws Exception {
		String name = t.getName();
		
		//System.setProperty("logback.debug", "true");
		
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		PatternLayoutEncoder ple = new PatternLayoutEncoder();
		ple.setPattern(prop.getProperty(Transporter.LOG_PATTERN));
		ple.setContext(lc);
		ple.start();
		
		RollingFileAppender<ILoggingEvent> fileApp = new RollingFileAppender<ILoggingEvent> ();
		//File f = new File(props.getProperty(Transporter.LOG_DIRECTORY) + System.getProperty("file.separator") + name + props.getProperty(Transporter.LOG_EXTENSION));
		fileApp.setFile(prop.getProperty(Transporter.LOG_DIRECTORY) + System.getProperty("file.separator") + name + prop.getProperty(Transporter.LOG_EXTENSION));
		//fileApp.setFile(f.getAbsolutePath());
		fileApp.setContext(lc);
				
		TimeBasedRollingPolicy<ILoggingEvent> tbrp = new TimeBasedRollingPolicy<ILoggingEvent>();
		tbrp.setParent(fileApp);
		//f = new File(props.getProperty(Transporter.LOG_DIRECTORY) + System.getProperty("file.separator") + name + ".%d{yyyy-MM-dd}" + props.getProperty(Transporter.LOG_EXTENSION) + ".zip");
		tbrp.setFileNamePattern(prop.getProperty(Transporter.LOG_DIRECTORY) + System.getProperty("file.separator") + name + ".%d{yyyy-MM-dd}" + prop.getProperty(Transporter.LOG_EXTENSION) + ".zip");
		tbrp.setCleanHistoryOnStart(false);
		//tbrp.setFileNamePattern(f.getAbsolutePath());
		tbrp.setContext(lc);
		tbrp.setMaxHistory(Integer.parseInt(prop.getProperty(Transporter.LOG_HISTORY)));
		tbrp.start();
		
		fileApp.setRollingPolicy(tbrp);
		fileApp.setEncoder(ple);
		fileApp.start();
		
		t.log = lc.getLogger(name);
		t.log.setLevel(Level.INFO);
		t.log.addAppender(fileApp);
		
		//StatusPrinter.print(lc);
		t.log.info(String.format("Logger for %s was saccessfully started", name));
	}
	
	private final boolean isStop() {
		return stop;
	}

	final void setStop(boolean stop) {
		this.stop = stop;
	}
	
}
