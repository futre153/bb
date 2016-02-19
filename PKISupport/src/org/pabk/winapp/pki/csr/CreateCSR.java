package org.pabk.winapp.pki.csr;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.pabk.basen.ber.BERImpl;

public class CreateCSR {

	private static JPanel inputCSR;
	private static JPanel templates;
	private static JPanel request;
	private static JFrame frame;
	
	public static void main(String[] args) {
		try {
			/*UIManager.LookAndFeelInfo[] lafis = UIManager.getInstalledLookAndFeels();
	        for (UIManager.LookAndFeelInfo lafi : lafis) {
	            System.out.println(lafi);
	        }*/
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		UIManager.put("swing.boldMetal", Boolean.FALSE);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					initialize();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public CreateCSR() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private static void initialize() {
		inputCSR = new InputCSR();
		templates = new Templates();
		request = new DisplayRequest();
		frame = new JFrame();
		frame.setBounds(100, 100, 1024, 768);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		frame.getContentPane().add(getInputCSR(), BorderLayout.CENTER);
		//frame.getContentPane().add(getTemplates(), BorderLayout.CENTER);
	}
	
	static JPanel getDisplayRequest(BERImpl ber) throws IOException {
		DisplayRequest.setText((DisplayRequest) request, ber);
		return request;
	}
	
	static JFrame getMain() {
		return frame;
	}
	static JPanel getInputCSR() {
		return inputCSR;
	}
	static JPanel getTemplates() {
		return templates;
	}
}
