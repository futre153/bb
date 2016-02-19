package org.pabk.winapp.pki.csr;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.xml.bind.DatatypeConverter;

class Templates extends JPanel {
	private JComboBox<String> templatesCombo;
	private JScrollPane scrollPane;
	private Templates me;
	private String[] names;
	
	private ActionListener actionTemplatesCombo = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().equals("comboBoxChanged") && templatesCombo.getSelectedIndex() > 0) {
				templatesCombo.removeActionListener(actionTemplatesCombo);
				try {
					String name = names[templatesCombo.getSelectedIndex() - 1];
					//System.out.println(name.substring(0, name.lastIndexOf(DOT_CHAR)) + BIN_EXTENSION);
					//System.out.println(Templates.getAbsolutePath(name.substring(0, name.lastIndexOf(DOT_CHAR)) + BIN_EXTENSION));
					BufferedImage image = ImageIO.read(new File(Templates.getAbsolutePath(name.substring(0, name.lastIndexOf(DOT_CHAR)) + BIN_EXTENSION)));
					JLabel label = new JLabel(new ImageIcon(image));
					JPanel panel = new JPanel();
					panel.add(label);
					if(scrollPane != null) {
						me.remove(scrollPane);
					}
					scrollPane = new JScrollPane(panel);
					me.add(scrollPane, BorderLayout.CENTER);
					me.revalidate();
					me.repaint();
					//scrollPane.add(comp);
					
					/*
					JLabel label = new JLabel(",");
					JTextField textField = new JTextField();
					textField.setColumns(10);
					comboBox_4.getParent().add(label, 0);
					comboBox_4.getParent().add(textField, 0);
					label = new JLabel(comboBox_4.getItemAt(comboBox_4.getSelectedIndex()) + "=");
					comboBox_4.getParent().add(label, 0);
					comboBox_4.setSelectedIndex(0);
					comboBox_4.getParent().validate();
					comboBox_4.getParent().repaint();
					scrollPane.validate();
					scrollPane.repaint();
					*/
				}
				catch (Exception err) {
					err.printStackTrace();
				}
				templatesCombo.addActionListener(actionTemplatesCombo);
			}
		}
	};
	
	private ActionListener actionBtns = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			System.out.println(e.getActionCommand());
			if (e.getActionCommand().equals("Zatvori\u0165")) {
				System.out.println(e.getActionCommand() + " was performed");
				Container comp = me;
				while(!(comp instanceof JFrame) && comp != null) {
					comp = comp.getParent();
				}
				if(comp != null) {
					((JFrame) comp).setVisible(false);
					((JFrame) comp).dispose();
				}
			}
			else if (e.getActionCommand().equals("Otvori\u0165")) {
				System.out.println(e.getActionCommand() + " was performed");
				try {
					((InputCSR) CreateCSR.getInputCSR()).loadTemplate(Templates.getAbsolutePath(names[templatesCombo.getSelectedIndex() - 1]));
				} catch (URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				JFrame main = CreateCSR.getMain(); 
				main.getContentPane().remove(0);
				main.getContentPane().add(CreateCSR.getInputCSR(), BorderLayout.CENTER);
				main.getContentPane().validate();
				main.getContentPane().repaint();
			}
		}
	};
	
	public Templates() {
		me = this;
		setLayout(new BorderLayout(0, 0));
		
		//((JFrame)this.getRootPane().getParent()).setTitle("\u0160ablony pre \u017Eiadosti o certifik\u00E1ty");
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.NORTH);
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		
		JPanel panel_12 = new JPanel();
		panel.add(panel_12);
		
		templatesCombo = new JComboBox<String>();
		panel_12.add(templatesCombo);
		templatesCombo.addActionListener(actionTemplatesCombo);
		reloadTemplates();
				
		JPanel panel_13 = new JPanel();
		FlowLayout flowLayout_3 = (FlowLayout) panel_13.getLayout();
		flowLayout_3.setAlignment(FlowLayout.LEFT);
		panel.add(panel_13);
		
		JLabel lblNhad = new JLabel("N\u00E1h\u013Ead");
		panel_13.add(lblNhad);
		/*
		scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);
		*/
		
		
		JPanel panel_2 = new JPanel();
		add(panel_2, BorderLayout.SOUTH);
		panel_2.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_3 = new JPanel();
		panel_2.add(panel_3, BorderLayout.WEST);
		
		JButton btnOtvori = new JButton("Otvori\u0165");
		panel_3.add(btnOtvori);
		btnOtvori.addActionListener(actionBtns);
		
		JPanel panel_4 = new JPanel();
		panel_2.add(panel_4, BorderLayout.CENTER);
		
		JPanel panel_5 = new JPanel();
		panel_2.add(panel_5, BorderLayout.EAST);
				
		JButton btnZatvori = new JButton("Zatvori\u0165");
		panel_5.add(btnZatvori);
		btnZatvori.addActionListener(actionBtns);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final char DOT_CHAR = '.';
	private static final char SLASH_CHAR = '/';
	private static final URL TEMPLATE_URL = Templates.class.getResource(SLASH_CHAR + Templates.class.getPackage().getName().replace(DOT_CHAR, SLASH_CHAR));
	private static final String TEMPLATE_RESOURCE_NOT_FOUND = "Template URL %s not found!";
	private static final String TEMPLATE_RESOURCE_NOT_DIR = "Template resource %s must be a directory!";
	private static final String TEMPLATE_FILENAME_MASK = "[C][S][R][_][T][e][m][p][l][a][t][e][_][0-9A-Fa-f]+[_]+[0-9]+[\\.][x][m][l]";
	private static final FilenameFilter TEMPLATE_FILENAME_FILTER = new SimpleFilenameFilter(TEMPLATE_FILENAME_MASK);
	private static final String TEMPLATE_NAME_FORMAT = "CSR_Template_%s_%d";
	private static final char UNDERSCORE_CHAR = '_';
	private static final String XML_EXTENSION = ".xml";
	private static final String BIN_EXTENSION = ".png";
	private static final char SPACE_CHAR = ' ';
	private static final char MINUS_CHAR = '-';
	private static final String UNKNOWN = "neznámy";
	private static final String DATE_FORMA_PATTERN = "dd MMM yyyy HH:mm:ss";
	private static final String SLOVAK_ISO_COUTRY_CODE = "SK";
	private static SimpleDateFormat df;
	
	public static String getTemplatePropertyName (String fullName) {
		return fullName + XML_EXTENSION;
	}
	
	public static String getTemplateImageName (String fullName) {
		return fullName + BIN_EXTENSION;
	}
	
	public static String createFullTemplateName (String name) {
		return String.format(TEMPLATE_NAME_FORMAT, DatatypeConverter.printHexBinary(name.getBytes()), new Date().getTime());
	}
	
	public static String getTemplateName(String fullName) {
		return new String(DatatypeConverter.parseHexBinary(fullName.split(new String(new char[]{UNDERSCORE_CHAR}))[2]));
	}
	
	public static String[] templateNames() throws IOException, URISyntaxException {
		//System.out.println(TEMPLATE_URL.toURI().toString() + SLASH_CHAR + "name");
		//URL TEMPLATE_URL = Templates.class.getResource(SLASH_CHAR + Templates.class.getPackage().getName().replace(DOT_CHAR, SLASH_CHAR));
		try {
			if(TEMPLATE_URL == null) {
				throw new IOException(String.format(TEMPLATE_RESOURCE_NOT_FOUND, Templates.class.getName()));
			}
			else {
				File dir = new File(TEMPLATE_URL.toURI());
				if(dir.isDirectory()) {
					return dir.list(TEMPLATE_FILENAME_FILTER);
				}
				else {
					throw new IOException (String.format(TEMPLATE_RESOURCE_NOT_DIR, TEMPLATE_URL.toString()));
				}
			}
		}
		catch (Exception e) {
			throw new IOException (e);
		}
	}

	public static String getAbsolutePath(String pName) throws URISyntaxException {
		return new File(TEMPLATE_URL.toURI()).getAbsolutePath() + SLASH_CHAR + pName;
	}

	public static String getExtension(String fName) {
		return fName.substring(fName.lastIndexOf(DOT_CHAR) + 1);
	}

	public void reloadTemplates() {
		templatesCombo.removeActionListener(actionTemplatesCombo);
		ArrayList<String> list = new ArrayList<String>();
		list.add("Vyberte \u0161ablonu");
		try {
			names = Templates.templateNames();
			for(String name: names) {
				list.add(Templates.getTemplateName(name) + SPACE_CHAR + MINUS_CHAR + SPACE_CHAR + Templates.getTempateDate(name));
			}
		}
		catch (Exception e) {}
		//System.out.println(list);
		String[] model = new String[list.size()];
		model = list.toArray(model);
		templatesCombo.setModel(new DefaultComboBoxModel<String>(model));
		templatesCombo.setSelectedIndex(0);
		templatesCombo.addActionListener(actionTemplatesCombo);
	}

	private static String getTempateDate(String name) {
		try {
			if(df == null) {
				Locale locale = Locale.getDefault();
				Locale[] locales = Locale.getAvailableLocales();
				for (Locale l: locales) {
					if(l.getCountry().toUpperCase().equals(SLOVAK_ISO_COUTRY_CODE)) {
						locale = l;
						break;
					}
				}
				df = new SimpleDateFormat(DATE_FORMA_PATTERN, locale);
			}
			String d = name.split(new String(new char[]{UNDERSCORE_CHAR}))[3];
			//System.out.println(d.substring(0, d.lastIndexOf(DOT_CHAR)));
			return df.format(new Date(Long.parseLong(d.substring(0, d.lastIndexOf(DOT_CHAR)))));
		}
		catch (Exception e) {
			e.printStackTrace();
			return UNKNOWN;
		}
	}
}
