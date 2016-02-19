package org.pabk.basen.asn1;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.cert.CertificateException;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import org.pabk.basen.rfc.rfc2986.Request;
import org.pabk.util.Base64Coder;

public class ASN1Viewer extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	
	/**
	 * Launch the application.
	 * @throws IOException 
	 * @throws CertificateException 
	 */
	public static void main(String[] args) throws CertificateException, IOException {
		
		//FileInputStream in = new FileInputStream("D:\\Dokumenty\\SWIFT\\certs\\swp01.req");
		File f = new File("D:\\Dokumenty\\SWIFT\\certs\\swp01.req");
		BufferedReader reader = new BufferedReader(new FileReader(f));
		ByteArrayOutputStream out = new ByteArrayOutputStream((int) f.length()); 
		String line;
		boolean load = false;
		while((line = reader.readLine()) != null) {
			if(line.contains("END NEW CERTIFICATE REQUEST")) {
				load = false;
			}
			if(load) {
				out.write(line.getBytes());
			}
			if(line.contains("BEGIN NEW CERTIFICATE REQUEST")) {
				load = true;
			}
		}
		ByteArrayInputStream in = new ByteArrayInputStream(Base64Coder.decodeLines(out.toString()));
		final Request asn = new Request("alias", in);
		//System.out.println(asn.getCertificate());
		reader.close();
		
		/*final BERInteger asn = new BERInteger("integer", false);
		asn.setValue(Long.MIN_VALUE);*/
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ASN1Viewer frame = new ASN1Viewer(asn);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ASN1Viewer(ASN1Impl asn) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 756, 534);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);
		panel.setLayout(new GridLayout(0, 4, 0, 0));
		
		JCheckBox chckbxNewCheckBox = new JCheckBox("New check box");
		panel.add(chckbxNewCheckBox);
		
		JCheckBox chckbxNewCheckBox_1 = new JCheckBox("New check box");
		panel.add(chckbxNewCheckBox_1);
		
		JCheckBox chckbxNewCheckBox_2 = new JCheckBox("New check box");
		panel.add(chckbxNewCheckBox_2);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(null);
		panel_1.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		//contentPane.add(panel_1, BorderLayout.CENTER);
		
		JScrollPane scrollPane = new JScrollPane(panel_1);
		contentPane.add(scrollPane, BorderLayout.CENTER);
			
		ASN1Impl[][] list = {{asn}};
		ASN1List panel_2 = new ASN1List(list);
		panel_1.add(panel_2);
		
		/*
		JPanel panel_2 = new JPanel();
		panel_1.add(panel_2);
		panel_2.setLayout(new BoxLayout(panel_2, BoxLayout.Y_AXIS));
		
		JPanel panel_3 = new JPanel();
		panel_2.add(panel_3);
		GridBagLayout gbl_panel_3 = new GridBagLayout();
		gbl_panel_3.columnWidths = new int[]{0, 0, 0};
		gbl_panel_3.rowHeights = new int[]{0, 0, 0, 0};
		gbl_panel_3.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_panel_3.rowWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
		panel_3.setLayout(gbl_panel_3);
		
		JButton button = new JButton("+");
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button.setMargin(new Insets(0, 0, 0, 0));
		button.setPreferredSize(new Dimension(14, 14));
		button.setMinimumSize(new Dimension(14, 14));
		button.setMaximumSize(new Dimension(14, 14));
		GridBagConstraints gbc_button = new GridBagConstraints();
		gbc_button.insets = new Insets(0, 0, 5, 5);
		gbc_button.gridx = 0;
		gbc_button.gridy = 0;
		panel_3.add(button, gbc_button);
		
		JLabel lblVersionUniversalExplicit = new JLabel("version UNIVERSAL EXPLICIT INTEGER [2] MANDATORY");
		GridBagConstraints gbc_lblVersionUniversalExplicit = new GridBagConstraints();
		gbc_lblVersionUniversalExplicit.anchor = GridBagConstraints.WEST;
		gbc_lblVersionUniversalExplicit.insets = new Insets(0, 0, 5, 0);
		gbc_lblVersionUniversalExplicit.gridx = 1;
		gbc_lblVersionUniversalExplicit.gridy = 0;
		panel_3.add(lblVersionUniversalExplicit, gbc_lblVersionUniversalExplicit);
		
		JLabel lblValue = new JLabel("VALUE:");
		GridBagConstraints gbc_lblValue = new GridBagConstraints();
		gbc_lblValue.insets = new Insets(0, 0, 5, 0);
		gbc_lblValue.anchor = GridBagConstraints.WEST;
		gbc_lblValue.gridx = 1;
		gbc_lblValue.gridy = 1;
		panel_3.add(lblValue, gbc_lblValue);
		
		JPanel panel_6 = new JPanel();
		GridBagConstraints gbc_panel_6 = new GridBagConstraints();
		gbc_panel_6.fill = GridBagConstraints.BOTH;
		gbc_panel_6.gridx = 1;
		gbc_panel_6.gridy = 2;
		panel_3.add(panel_6, gbc_panel_6);
		panel_6.setLayout(new BoxLayout(panel_6, BoxLayout.Y_AXIS));
		
		JPanel panel_7 = new JPanel();
		panel_6.add(panel_7);
		GridBagLayout gbl_panel_7 = new GridBagLayout();
		gbl_panel_7.columnWidths = new int[]{104, 0};
		gbl_panel_7.rowHeights = new int[]{304, 0};
		gbl_panel_7.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_panel_7.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel_7.setLayout(gbl_panel_7);
		
		JTextArea txtrBezkBolOdvolan = new JTextArea();
		txtrBezkBolOdvolan.setLineWrap(true);
		txtrBezkBolOdvolan.setWrapStyleWord(true);
		txtrBezkBolOdvolan.setText("Bez\u00E1k bol odvolan\u00FD po d\u00F4kladnom sk\u00FAman\u00ED a objekt\u00EDvnom pos\u00FAden\u00ED v\u0161etk\u00FDch dostupn\u00FDch s\u00FA\u010Dast\u00ED, zvl\u00E1\u0161\u0165 t\u00FDch, ktor\u00E9 sa objavili v priebehu apo\u0161tolskej vizit\u00E1cie uskuto\u010Dnenej v Trnavskej arcidiec\u00E9ze na prelome mesiacov janu\u00E1r a febru\u00E1r 2012. Uv\u00E1dza sa to v stanovisku \u0160t\u00E1tneho sekretari\u00E1tu Sv\u00E4tej stolice, ktor\u00FD po\u017Eiadal o zverejnenie Apo\u0161tolsk\u00FA nunciat\u00FAru na Slovensku a Konferenciu biskupov Slovenska.");
		GridBagConstraints gbc_txtrBezkBolOdvolan = new GridBagConstraints();
		gbc_txtrBezkBolOdvolan.weightx = 10.0;
		gbc_txtrBezkBolOdvolan.fill = GridBagConstraints.BOTH;
		gbc_txtrBezkBolOdvolan.weighty = 10.0;
		gbc_txtrBezkBolOdvolan.anchor = GridBagConstraints.WEST;
		gbc_txtrBezkBolOdvolan.gridx = 0;
		gbc_txtrBezkBolOdvolan.gridy = 0;
		panel_7.add(txtrBezkBolOdvolan, gbc_txtrBezkBolOdvolan);
		
		JPanel panel_4 = new JPanel();
		panel_2.add(panel_4);
		GridBagLayout gbl_panel_4 = new GridBagLayout();
		gbl_panel_4.columnWidths = new int[]{0, 0, 0};
		gbl_panel_4.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_panel_4.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_panel_4.rowWeights = new double[]{0.0, 0.0, 1.0, 1.0, Double.MIN_VALUE};
		panel_4.setLayout(gbl_panel_4);
		
		JButton button_1 = new JButton("-");
		button_1.setPreferredSize(new Dimension(14, 14));
		button_1.setMinimumSize(new Dimension(14, 14));
		button_1.setMaximumSize(new Dimension(14, 14));
		button_1.setMargin(new Insets(0, 0, 0, 0));
		GridBagConstraints gbc_button_1 = new GridBagConstraints();
		gbc_button_1.insets = new Insets(0, 0, 5, 5);
		gbc_button_1.gridx = 0;
		gbc_button_1.gridy = 0;
		panel_4.add(button_1, gbc_button_1);
		
		JLabel lblTbscertificateUniversalExplicit = new JLabel("tbsCertificate UNIVERSAL EXPLICIT SEQUENCE [16] OPTIONAL");
		GridBagConstraints gbc_lblTbscertificateUniversalExplicit = new GridBagConstraints();
		gbc_lblTbscertificateUniversalExplicit.anchor = GridBagConstraints.WEST;
		gbc_lblTbscertificateUniversalExplicit.insets = new Insets(0, 0, 5, 0);
		gbc_lblTbscertificateUniversalExplicit.gridx = 1;
		gbc_lblTbscertificateUniversalExplicit.gridy = 0;
		panel_4.add(lblTbscertificateUniversalExplicit, gbc_lblTbscertificateUniversalExplicit);
		
		JLabel lblValue_1 = new JLabel("VALUE:");
		GridBagConstraints gbc_lblValue_1 = new GridBagConstraints();
		gbc_lblValue_1.anchor = GridBagConstraints.WEST;
		gbc_lblValue_1.insets = new Insets(0, 0, 5, 0);
		gbc_lblValue_1.gridx = 1;
		gbc_lblValue_1.gridy = 1;
		panel_4.add(lblValue_1, gbc_lblValue_1);
		
		JPanel panel_5 = new JPanel();
		GridBagConstraints gbc_panel_5 = new GridBagConstraints();
		gbc_panel_5.insets = new Insets(0, 0, 5, 0);
		gbc_panel_5.fill = GridBagConstraints.BOTH;
		gbc_panel_5.gridx = 1;
		gbc_panel_5.gridy = 2;
		panel_4.add(panel_5, gbc_panel_5);
		*/
	}
	
	
	
	
}
