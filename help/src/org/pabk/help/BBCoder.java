package org.pabk.help;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.xml.bind.DatatypeConverter;

import org.pabk.util.Base64Coder;
import org.pabk.util.Huffman;

public class BBCoder extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int BASE_CLICKED = 0;
	private static final int HUFF_CLICKED = 1;
	private static final int ENCO_CLICKED = 2;
	private static final int DECO_CLICKED = 3;
	private static final int HEXS_CLICKED = 4;
	private static final int EXEC_CLICKED = 99;
	
	private boolean base64 = true;
	private boolean hex = false;
	private boolean huffman = false;
	private boolean encode = true;
	private JCheckBox base;
	private JCheckBox huff;
	private JCheckBox hexs;
	private JRadioButton enc;
	private JRadioButton dec;
	private JTextArea input;
	private JTextArea output;
	private final JFileChooser fc = new JFileChooser();
	private JCheckBox srcFile;
	private JCheckBox dstFile;   
	
	public BBCoder() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100,100,800,600);
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
				
		JPanel panel = new JPanel();
		getContentPane().add(panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{215, 0};
		gbl_panel.rowHeights = new int[]{33, 33, 16, 0, 0};
		gbl_panel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JPanel panel_1 = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panel_1.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.weightx = 1.0;
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.anchor = GridBagConstraints.WEST;
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 0;
		panel.add(panel_1, gbc_panel_1);
		
		base = new JCheckBox("Base64");
		base.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent arg0) {action(BASE_CLICKED);}});
		base.setSelected(true);
		panel_1.add(base);
			
		hexs = new JCheckBox("HexString");
		hexs.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent arg0) {action(HEXS_CLICKED);}});
		panel_1.add(hexs);
				
		huff = new JCheckBox("Huffman");
		huff.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent arg0) {action(HUFF_CLICKED);}});
		panel_1.add(huff);
		
		srcFile = new JCheckBox("source from file");
		panel_1.add(srcFile);
		
		dstFile = new JCheckBox("output to file");
		panel_1.add(dstFile);
		
		JPanel panel_2 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_2.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.weightx = 1.0;
		gbc_panel_2.anchor = GridBagConstraints.WEST;
		gbc_panel_2.insets = new Insets(0, 0, 5, 0);
		gbc_panel_2.gridx = 0;
		gbc_panel_2.gridy = 1;
		panel.add(panel_2, gbc_panel_2);
		
		enc = new JRadioButton("Encode/Encypt");
		enc.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent arg0) {action(ENCO_CLICKED);}});
		enc.setSelected(true);
		panel_2.add(enc);
		
		dec = new JRadioButton("Decode/Decrypt");
		dec.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent arg0) {action(DECO_CLICKED);}});
		panel_2.add(dec);
		
		JPanel panel_3 = new JPanel();
		GridBagConstraints gbc_panel_3 = new GridBagConstraints();
		gbc_panel_3.anchor = GridBagConstraints.NORTHWEST;
		gbc_panel_3.insets = new Insets(0, 0, 5, 0);
		gbc_panel_3.weighty = 1.0;
		gbc_panel_3.fill = GridBagConstraints.BOTH;
		gbc_panel_3.weightx = 1.0;
		gbc_panel_3.gridx = 0;
		gbc_panel_3.gridy = 2;
		panel.add(panel_3, gbc_panel_3);
		GridBagLayout gbl_panel_3 = new GridBagLayout();
		gbl_panel_3.columnWidths = new int[]{4, 0};
		gbl_panel_3.rowHeights = new int[]{16, 0};
		gbl_panel_3.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panel_3.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		panel_3.setLayout(gbl_panel_3);
		
		input = new JTextArea();
		input.setLineWrap(true);
		input.setWrapStyleWord(true);
		
		JScrollPane scrollPane = new JScrollPane(input);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.weighty = 1.0;
		gbc_scrollPane.insets = new Insets(0, 5, 0, 5);
		gbc_scrollPane.weightx = 1.0;
		gbc_scrollPane.anchor = GridBagConstraints.NORTHWEST;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		panel_3.add(scrollPane, gbc_scrollPane);
		
		JPanel panel_4 = new JPanel();
		GridBagConstraints gbc_panel_4 = new GridBagConstraints();
		gbc_panel_4.anchor = GridBagConstraints.NORTHWEST;
		gbc_panel_4.fill = GridBagConstraints.BOTH;
		gbc_panel_4.gridx = 0;
		gbc_panel_4.gridy = 3;
		panel.add(panel_4, gbc_panel_4);
		GridBagLayout gbl_panel_4 = new GridBagLayout();
		gbl_panel_4.columnWidths = new int[]{391, 0};
		gbl_panel_4.rowHeights = new int[]{2, 0};
		gbl_panel_4.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_panel_4.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel_4.setLayout(gbl_panel_4);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
		gbc_scrollPane_1.insets = new Insets(0, 5, 0, 5);
		gbc_scrollPane_1.weighty = 1.0;
		gbc_scrollPane_1.weightx = 1.0;
		gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_1.anchor = GridBagConstraints.NORTHWEST;
		gbc_scrollPane_1.gridx = 0;
		gbc_scrollPane_1.gridy = 0;
		panel_4.add(scrollPane_1, gbc_scrollPane_1);
		
		output = new JTextArea();
		output.setWrapStyleWord(true);
		output.setLineWrap(true);
		output.setEditable(false);
		scrollPane_1.setViewportView(output);
		
		JPanel panel_5 = new JPanel();
		FlowLayout flowLayout_2 = (FlowLayout) panel_5.getLayout();
		flowLayout_2.setAlignment(FlowLayout.LEFT);
		getContentPane().add(panel_5);
		
		JButton btnNewButton = new JButton("Execute");
		btnNewButton.addMouseListener(new MouseAdapter() {@Override	public void mouseClicked(MouseEvent arg0) {action(EXEC_CLICKED);}});
		panel_5.add(btnNewButton);
			
	}
	
	private synchronized void action(int type) {
		switch(type) {
		case BASE_CLICKED:
			base64 = !base64;
			hex = !huffman ? !hex :	!huffman;
			base.setSelected(base64);
			hexs.setSelected(hex);
			break;
		case HEXS_CLICKED:
			hex = !hex;
			base64 = !huffman ? !base64 : !huffman;
			base.setSelected(base64);
			hexs.setSelected(hex);
			break;
		case HUFF_CLICKED:
			huffman = !huffman;
			base64 = (!huffman && !base64 && !hex) ? !huffman : base64;
			base.setSelected(base64);
			huff.setSelected(huffman);
			break;
		case ENCO_CLICKED:
			encode = encode ? encode : !encode;
			enc.setSelected(encode);
			dec.setSelected(!encode);
			break;
		case DECO_CLICKED:
			encode = encode ? !encode : encode;
			enc.setSelected(encode);
			dec.setSelected(!encode);
			break;
		case EXEC_CLICKED:
			File src, dst;
			if(srcFile.isSelected()) {
				try {
					src = BBCoder.getFile(this, "Source file");
					if(src.exists()) {
						BBCoder.loadFileToTextArea(src, input);
					}
					else {
						return;
					}
				}
				catch (Exception e) {
					return;
				}
			}
			if(base64 && huffman && encode) {
				try {
					output.setText(Huffman.encode(input.getText(), null));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else if(base64 && huffman && !encode) {
				try {
					output.setText(Huffman.decode(input.getText(), null));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else if(hex && huffman && encode) {
				try {
					output.setText(DatatypeConverter.printHexBinary(Huffman.encode(input.getText().getBytes(), null)).toUpperCase());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else if(hex && huffman && !encode) {
				try {
					output.setText(new String(Huffman.decode(DatatypeConverter.parseHexBinary(input.getText()), null)));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else if(base64 && !huffman && encode) {
				try {
					output.setText(Base64Coder.encodeString(input.getText()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else if(base64 && !huffman && !encode) {
				try {
					output.setText(Base64Coder.decodeString(input.getText()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else if(hex && !huffman && encode) {
				try {
					output.setText(DatatypeConverter.printHexBinary(input.getText().getBytes()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else if(hex && !huffman && !encode) {
				try {
					output.setText(new String(DatatypeConverter.parseHexBinary(input.getText())));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else if(!hex && !base64 && huffman && encode) {
				try {
					output.setText(new String(Huffman.encode(input.getText().getBytes(), null)));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else if(!hex && !base64 && huffman && !encode) {
				try {
					output.setText(new String(Huffman.decode(input.getText().getBytes(), null)));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(dstFile.isSelected()) {
				try {
					dst = BBCoder.getFile(this, "Destination File");
					if(!dst.exists()) {
						if(dst.createNewFile()) {
							BBCoder.saveTextAreaToFile(dst, output);
						}
						else {
							return;
						}
					}
				}
				catch (Exception e) {
					return;
				}
			}
		default:
		}
		//input.setText("base64=" + base64 + ", hex=" + hex + ", huffman=" + huffman);
	}

	private static void saveTextAreaToFile(File f, JTextArea text) throws IOException {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(f);
			out.write(text.getText().getBytes());
		}
		catch(Exception e) {
			throw new IOException (e);
		}
		finally {
			out.close();
		}
	}

	private static void loadFileToTextArea(File f, JTextArea text) throws IOException {
		text.setText("");
		FileInputStream in = null;
		try {
			in = new FileInputStream(f);
			byte[] b = new byte[4096];
			int i;
			while((i = in.read(b)) >= 0) {
				text.append(new String(b, 0, i));
			}
		}
		catch(Exception e) {
			text.setText("");
			throw new IOException (e);
		}
		finally {
			in.close();
		}
	}

	private static File getFile(BBCoder coder, String name) throws IOException {
		int retValue = coder.fc.showDialog(coder, name);
		if(retValue == JFileChooser.APPROVE_OPTION) {
			File f = coder.fc.getSelectedFile();
			if(f.isDirectory()) {
				throw new IOException ("File " + f.getAbsolutePath() + " is a directory");
			}
			return f;
		}
		throw new IOException ("No file choosen");
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BBCoder frame = new BBCoder();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
