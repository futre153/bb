package org.pabk.applet;
import java.applet.Applet;
import java.lang.reflect.InvocationTargetException;
import java.security.KeyStore;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


public class PrepSigner extends Applet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private static KeyStore store;
	@SuppressWarnings("unused")
	private static String storeType;
	private static String defaultStoreType=Const.WINDOWS_CERTSTORE_TYPE;
	
	public void init() {
		 try {
	            SwingUtilities.invokeAndWait(new Runnable() {
	                public void run() {
	                    JLabel lbl = new JLabel("Hello World");
	                    add(lbl);
	                    JLabel label=new JLabel(defaultStoreType);
	                    add(label);
	                    add(createCertGUI());
	                }
	            });
	        } catch (Exception e) {
	            System.err.println("createGUI didn't complete successfully");
	        }
	}
	
	public void choiceCertificate() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					add(createCertGUI());
					repaint();
				}
			});
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	public JPanel createCertGUI() {
		JPanel frame=new JPanel();
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new SpringLayout());
		
		//frame.add(label);
		
		
		frame.setVisible(true);
		return frame;
		
	}

}
