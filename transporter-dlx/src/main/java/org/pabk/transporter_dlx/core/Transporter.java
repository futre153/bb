package org.pabk.transporter_dlx.core;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

import org.pabk.http.tserver.TServer;

public class Transporter extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String NAME =						"name";
	public static final String HOSTNAME =					"hostname";
	public static final String PORT =						"port";
	public static final String DIRECTION =					"direction";
	public static final String USERNAME =					"username";
	public static final String KNOWN_HOSTS =				"knownHosts";
	public static final String PRIVATE_KEY =				"privateKey";
	public static final String REMOTE =						"remote";
	public static final String LOCAL =						"local";
	public static final String ARCHIVE =					"archive";
	public static final String PASSPHRASE =					"passphrase";
	public static final String MODIFY =						"modify";
	public static final String MASK =						"mask";
	public static final String SEQUENCE =					"sequence";
	public static final String PROPATH =					"propath";
	public static final String LOG_PATTERN =				"logbackPattern";
	public static final String LOG_DIRECTORY =				"logDirectory";
	public static final String LOG_EXTENSION =				"logExtension";
	public static final String LOG_HISTORY =				"logHistory";
	public static final String CHECK_FILENAME_DUPLICITY =	"checkFilenameDuplicity";
	public static final String TIMER =						"timer";
	public static final String TIMER_CLASS =				"timerClass";
	
	private static SftpFileHandler[] sftpFileHandler;
	private static Properties[] config;
	private static StopHook hook;
	
	//private static Properties[] config = null;
	//private static String proPath = null;
	private static final Properties _default = new Properties();
	private static final String DEFAULT_PRO_PATH = "/org/pabk/transporter_dlx/default.xml";
	private static final String ENGINES_DIR = "engines";
	private static final String PID_FILE = "pid";
	private static final String ERROR_LOG = "transporter.err.log";
			
	private JTextArea textArea;
    
    private JButton buttonStart = new JButton("Start");
    private JButton buttonStop = new JButton("Stop");
    private JButton buttonClear = new JButton("Clear");
    private JButton buttonExit = new JButton("Exit");
	private static PrintStream oldOut;
	private static PrintStream oldErr;
	private static PrintStream errorLog;
	
	private Transporter() {
		super("Transporter");
		
		textArea = new JTextArea(50, 10);
        textArea.setEditable(false);
        
        PrintStream printStream = new PrintStream(new TransportOutputStream(textArea, null));
		//PrintStream printStream = new PrintStream(new TextAreaOutputStream());
                
        System.setOut(printStream);
         
        // creates the GUI
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(10, 10, 10, 10);
        constraints.anchor = GridBagConstraints.WEST;
         
        add(buttonStart, constraints);
        
        constraints.gridx = 1;
        add(buttonStop, constraints);
        
        constraints.gridx = 2;
        add(buttonClear, constraints);
        
        constraints.anchor = GridBagConstraints.EAST;
        constraints.gridx = 3;
        add(buttonExit, constraints);
        
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 4;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
         
        add(new JScrollPane(textArea), constraints);
        
        buttonStart.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				startTransport();				
			}});
        buttonStop.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				stopTransport();				
			}});
        /* 
        // adds event handler for button Start
        buttonStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                printLog();
            }
        });
         */
        // adds event handler for button Clear
        
        buttonClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                try {
					textArea.getDocument().remove(0, textArea.getDocument().getLength());
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
				//oldOut.println(textArea.getDocument().getLength());
            }
        });
        
        buttonExit.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				exitTransport();
			}});
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(640, 480);
        setLocationRelativeTo(null);    // centers on screen
        startTransport();
    }
	
	private void exitTransport() {
		stopTransport();	
		try {
			TServer.stopServer();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.dispose();
	}
	
	private void stopTransport() {
		if(Transporter.isStarted()) {
			Runtime.getRuntime().removeShutdownHook(hook);
			Transporter.hook.start();
			try {
				Transporter.hook.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			buttonStart.setEnabled(true);
			buttonStop.setEnabled(false);
		}
		else {
			buttonStart.setEnabled(false);
			buttonStop.setEnabled(true);
		}
	}
	
	
	private void startTransport() {
		if(Transporter.isStarted()) {
			buttonStart.setEnabled(false);
			buttonStop.setEnabled(true);
		}
		else {
			
			ArrayList<Properties> a = new ArrayList<Properties>();
			File engineDir = new File(ENGINES_DIR);
			File[] engines = engineDir.listFiles();
			if(engines == null) {
				config = new Properties[0];
			}
			else {
				for(int i = 0; i < engines.length; i ++) {
					if(engines[i].isFile()) {
						try {
							Properties p = loadCurrent(engines[i]);
							if(p != null) {
								p.setProperty(PROPATH, engines[i].getAbsolutePath());
								p.setProperty(MODIFY, Long.toString(engines[i].lastModified()));
								a.add(p);
							}
						}
						catch(Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
			config = new Properties[a.size()];
			config = a.toArray(config);
			
			
			
			sftpFileHandler = new SftpFileHandler[config.length];
			System.out.println("Application TRANSPORTER is started");
			System.out.println(String.format("    %d threads will be used", config.length));
			for(int i = 0; i < config.length; i ++) {
				sftpFileHandler[i] = new SftpFileHandler(config[i]);
				sftpFileHandler[i].setDaemon(true);
				sftpFileHandler[i].setName(config[i].getProperty(Transporter.NAME));
			    sftpFileHandler[i].start();
			    System.out.println(String.format("    Thread nr. %d [%s] is successfully started", i, sftpFileHandler[i].getName()));
			}
			if(config.length > 0) {
				Transporter.hook = new StopHook(sftpFileHandler);
				Runtime.getRuntime().addShutdownHook(hook);
				buttonStart.setEnabled(false);
				buttonStop.setEnabled(true);
			}
			else {
				buttonStart.setEnabled(true);
				buttonStop.setEnabled(false);
				//System.err.println("No trasport engines are defined, could not start any trasport");
				System.err.println("Nie sú definované žiadne transportné pravidlá");
			}
		}
	}
	
	private static boolean isStarted() {
		if(sftpFileHandler != null) {
			for(int i = 0; i < sftpFileHandler.length; i ++) {
				if(sftpFileHandler[i].isAlive()) {
					return true;
				}
			}
		}
		return false;
	}
    
	public static void main(String[] args) {
		/*
		PrintStream ps = new PrintStream(new TransportOutputStream(null, null));
		ps.print("Janíèko");
		ps.flush();
		System.exit(1);
		*/
		
		File[] dir = new File(SftpFileHandler.TMP_DIRECTORY).listFiles();
		if(dir != null) {
			for(int i = 0; i < dir.length; i ++) {
				if(dir[i].isFile() && dir[i].getName().contains(PID_FILE)) {
					System.err.println("Application is allready started");
					System.exit(1);
				}
			}
		}
		else {
			System.err.println("Failed to access to the temporary directory");
			System.exit(1);
		}
		try {
			File tmp = File.createTempFile(PID_FILE, SftpFileHandler.TMP_FILE_EXTENSION, new File(SftpFileHandler.TMP_DIRECTORY));
			tmp.deleteOnExit();
		} catch (IOException e) {
			System.err.println("File system is failed to check");
			System.exit(1);
		}
		try {
			_default.loadFromXML(Transporter.class.getResourceAsStream(DEFAULT_PRO_PATH));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		
		oldOut = System.out;
        oldErr = System.err;
        
		File f = new File(_default.getProperty(LOG_DIRECTORY) + SftpFileHandler.FS + ERROR_LOG);
		PrintStream printStream;
		try {
			printStream = new PrintStream(f);
			System.setErr(printStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
		
		TServer.startServer(null);
		SwingUtilities.invokeLater(new Runnable(){

			public void run() {
				
				new Transporter().setVisible(true);
				
			}});
		
		
		
		
		
		
		
		/*for(int i = 0; i < config.length; i ++) {
			try {
				sftpFileHandler[i].join();
				System.out.println(String.format("\tThread nr. %d [%s] was stopped ", i, sftpFileHandler[i].getName()));
			} catch (InterruptedException e) {
				System.exit(0);
				e.printStackTrace();
			}
		}
		Runtime.getRuntime().removeShutdownHook(hook);
		System.out.println("Appliaction TRANSPORTER is stopped");*/
	}
	
	public static PrintStream getErr() {
		return oldErr;
	}
	
	public static PrintStream getOut() {
		return oldOut;
	}
	
	public static Properties loadCurrent(File props) throws IOException {
		Properties p = null;
		if(props != null) {
			if(props.exists() && props.isFile()) {
				FileInputStream in = null;
				try {
					p = new Properties (_default);
					in = new FileInputStream(props);
					p.loadFromXML(in);
				}
				catch(IOException e) {
					p = null;
					throw e;
				}
				finally {
					try {in.close();} catch (IOException e) {}
				}
			}
		}
		return p;
	}

	public static PrintStream getErrorLog() {
		return errorLog;
	}
}

