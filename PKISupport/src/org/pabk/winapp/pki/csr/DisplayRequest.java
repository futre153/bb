package org.pabk.winapp.pki.csr;

import java.awt.BorderLayout;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import org.pabk.basen.ber.BERImpl;
import org.pabk.basen.rfc.rfc2986.Request;
import java.awt.Insets;
import java.awt.Font;

public class DisplayRequest extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 * 
	 */
	private JTextArea textArea;
	
	public DisplayRequest() {
		setLayout(new BorderLayout(0, 0));
		
		JPanel panel_4 = new JPanel();
		panel_4.setBorder(new EmptyBorder(10, 10, 10, 10));
		add(panel_4, BorderLayout.CENTER);
		panel_4.setLayout(new BoxLayout(panel_4, BoxLayout.X_AXIS));
		
		JPanel panel_5 = new JPanel();
		panel_5.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_4.add(panel_5);
		panel_5.setLayout(new BoxLayout(panel_5, BoxLayout.X_AXIS));
		
		textArea = new JTextArea();
		panel_5.add(textArea);
		textArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
		textArea.setMargin(new Insets(10, 10, 10, 10));
		
				
		JPanel panel = new JPanel();
		add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_1 = new JPanel();
		panel.add(panel_1, BorderLayout.WEST);
		
		JButton btnUloiDoSboru = new JButton("Ulo\u017Ei\u0165 do s\u00FAboru");
		panel_1.add(btnUloiDoSboru);
		
		JPanel panel_2 = new JPanel();
		panel.add(panel_2, BorderLayout.CENTER);
		
		JPanel panel_3 = new JPanel();
		panel.add(panel_3, BorderLayout.EAST);
		
		JButton btnZatvori = new JButton("Zatvori\u0165");
		panel_3.add(btnZatvori);

	}
	
	public static void setText(DisplayRequest dr, BERImpl req) throws IOException {
		dr.textArea.setText(null);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Request.exportToStream(req, out, Request.PEM_FORMAT);
		dr.textArea.setText(new String(out.toByteArray()));
	}
	
}
