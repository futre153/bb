package org.pabk.winapp.pki.csr;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.pabk.basen.ber.BERImpl;
import org.pabk.basen.rfc.rfc2986.Request;
import org.pabk.basen.rfc.rfc2986.TBSRequest;
import org.pabk.winapp.pki.keys.KeyUtils;

public class InputCSR extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected static final int SAN_INDEX = 16;
	private static final String STORE_TYPE_KEY = "storeType";
	private static final String STORE_LOCATION_KEY = "storeLocation";
	private static final String ALIAS_KEY = "alias";
	private static final String KEY_TYPE_KEY = "keyType";
	private static final String KEY_SIZE_KEY = "keySize";
	private static final String PRIMARY_DN_PFX = "P_";
	private static final String SECONDARY_DN_PFX = "S_";
	private static final String KEY_USAGE_KEY = "keyUsage";
	private static final String EXTENDED_KEY_USAGE_KEY = "extendedKeyUsage";
	private static final String SAN_PREFIX = "SAN_";
	protected static final String CHOOSE_TEMPLATE_NAME = "ProsÌm zadajte meno öablony:";
	protected static final String CUSTOMIZED_DIALOG = "Customized Dialog";
	protected static final Icon TEMPLATE_NAME_ICON = null;
	protected static final String PREVIEW_TEMPLATE_NAME = "Meno öablony";
	protected static final String TEMPLATE_ERROR_TITLE = "Chyba pri vytvorenÌ öablony";
	protected static final String EQUAL_TEMPLATE_NAME_MESSAGE = "Meno öabolny %s uû bolo pouûitÈ" + System.getProperty("line.separator") + "preajtete si pokraËovaù?";
	protected static final String EQUAL_TEMPLATE_NAME_TITLE = "PouûitÈ meno öablony";
	private static final String CONTINUE = "PokraËovaù";
	private static final String CHANGE = "Zmeniù";
	private static final String CANCEL = "Zruöiù";
	protected static final String[] EQUAL_TEMPLATE_NAME_OPTION = new String[]{CONTINUE, CHANGE, CANCEL};
	protected static final Icon EQUAL_TEMPLATE_NAME_ICON = null;
	protected static final String TEMPLATE_PROPERTY_DEFAULT_ENCODING = "UTF-8";
	protected static final String TEMPLATE_PROPERTY_COMMENT = "Template property for %s";
	protected static final String SUCCESSFUL_CREATE_TEMPLATE = "äablona %s bola ˙speöne uloûen·";
	protected static final String TEMPLATE_SUCESSFUL_TITLE = "⁄öpeönÈ uloûenie";
	protected static final String TEMPLATE_MSG_USER_CANCEL = "PouûÌvateæ zruöil vytvorenie öablony";
	private static final String TEMPLATE_ERROR_TITLE2 = "Cbyba pri nahr·vanÌ öablony";
	private static final String EMPTY = "";
	private static final char CHAR_EQUAL = '=';
	private static final String SUCCESSFUL_LOAD_TEMPLATE = "äablona %s bola ˙speöne nahran·";
	private static final String TEMPLATE_SUCESSFUL_LOAD_TITLE = "⁄speönÈ nahratie";
	protected static final String CSR_ERROR_TITLE = "Chyba vstupn˝ch ˙dajov";
	private static final String KEY_STORE_KEY = "keyStore";
	private static final String DEFALUT_ALIAS = "Alias";
	private static final String KEY_KEY = "keyPair";
	private static final String SUBJECT_KEY = "subject";
	private static final String EXTENSIONS_KEY = "extensions";
	private static final String[] EKU_OIDS = {
		"1.3.6.1.5.5.7.3.1",
		"1.3.6.1.5.5.7.3.2",
		"1.3.6.1.5.5.7.3.3",
		"1.3.6.1.5.5.7.3.4",
		"1.3.6.1.5.5.7.3.5",
		"1.3.6.1.5.5.7.3.6"
	};
	private JTextField storeLocationText;
	private JTextField aliasText;
	private JTextField textField_2;
	private JTextField textField_3;
	private JTextField textField_4;
	private JTextField textField_5;
	private JTextField textField_6;
	private JTextField textField_7;
	private JComboBox<String> storeTypeCombo;
	private JComboBox<String> keyTypeCombo;
	private JComboBox<String> keySizeCombo;
	private JComboBox<String> comboBox_3;
	private JComboBox<String> comboBox_4;
	private JButton btnReset;
	private JButton btnZatvori;
	private JButton btnablony;
	private JButton btnVytvori;
	private JButton btnUloiAkoablonu;
	private JScrollPane scrollPane;
	private JPanel primDnPanel;
	private JPanel secDnPanel;
	private JPanel panel_6;
	private InputCSR me;
	
	public void loadTemplate(String absolutePath) {
		Properties prop = new Properties();
		try {
			File f = new File(absolutePath);
			prop.loadFromXML(new FileInputStream(absolutePath));
			storeTypeCombo.setSelectedIndex(Integer.parseInt(prop.getProperty(STORE_TYPE_KEY)));
			storeLocationText.setText(prop.getProperty(STORE_LOCATION_KEY));
			aliasText.setText(prop.getProperty(ALIAS_KEY));
			keyTypeCombo.setSelectedIndex(Integer.parseInt(prop.getProperty(KEY_TYPE_KEY)));
			keySizeCombo.setSelectedIndex(Integer.parseInt(prop.getProperty(KEY_SIZE_KEY)));
			loadPrimaryDN(prop);
			loadSecondaryDN(prop);
			String ku = prop.getProperty(KEY_USAGE_KEY);
			rDigSig.setSelected(ku.charAt(0) == '1');
			rNonRep.setSelected(ku.charAt(1) == '1'); 
			rKeyEnc.setSelected(ku.charAt(2) == '1'); 
			rDatEnc.setSelected(ku.charAt(3) == '1'); 
			rKeyAgr.setSelected(ku.charAt(4) == '1'); 
			rKeyCSi.setSelected(ku.charAt(5) == '1'); 
			rCrlSig.setSelected(ku.charAt(6) == '1'); 
			rEncOnl.setSelected(ku.charAt(7) == '1'); 
			rDecOnl.setSelected(ku.charAt(8) == '1');
			String eku = prop.getProperty(EXTENDED_KEY_USAGE_KEY);
			rSerAut.setSelected(eku.charAt(0) == '1');
			rCliAut.setSelected(eku.charAt(1) == '1'); 
			rCodSig.setSelected(eku.charAt(2) == '1'); 
			rEmaPro.setSelected(eku.charAt(3) == '1'); 
			rTimSta.setSelected(eku.charAt(4) == '1'); 
			rOcsSig.setSelected(eku.charAt(5) == '1');
			loadSAN(prop);
			JOptionPane.showMessageDialog(CreateCSR.getMain(), String.format(SUCCESSFUL_LOAD_TEMPLATE, Templates.getTemplateName(f.getName())), TEMPLATE_SUCESSFUL_LOAD_TITLE, JOptionPane.INFORMATION_MESSAGE);
		}
		catch(Exception err) {
			JOptionPane.showMessageDialog(CreateCSR.getMain(), err.getMessage(), TEMPLATE_ERROR_TITLE2, JOptionPane.ERROR_MESSAGE);
			this.reset();
		}
		finally {
			me.revalidate();
			me.repaint();
		}
	}
	
	private Properties colectData() {
		Properties prop = new Properties();
		prop.setProperty(STORE_TYPE_KEY, Integer.toString(storeTypeCombo.getSelectedIndex()));
		prop.setProperty(STORE_LOCATION_KEY, storeLocationText.getText());
		prop.setProperty(ALIAS_KEY, aliasText.getText());
		prop.setProperty(KEY_TYPE_KEY, Integer.toString(keyTypeCombo.getSelectedIndex()));
		prop.setProperty(KEY_SIZE_KEY, Integer.toString(keySizeCombo.getSelectedIndex()));
		Component[] comps = primDnPanel.getComponents();
		saveDN(comps, prop, PRIMARY_DN_PFX);
		comps = secDnPanel.getComponents();
		Component[] tmp = new Component[comps.length - 1];
		System.arraycopy(comps, 0, tmp, 0, tmp.length);
		saveDN(tmp, prop, SECONDARY_DN_PFX);
		prop.setProperty(KEY_USAGE_KEY, 
				(rDigSig.isSelected() ? "1" : "0") +
				(rNonRep.isSelected() ? "1" : "0") + 
				(rKeyEnc.isSelected() ? "1" : "0") + 
				(rDatEnc.isSelected() ? "1" : "0") + 
				(rKeyAgr.isSelected() ? "1" : "0") + 
				(rKeyCSi.isSelected() ? "1" : "0") + 
				(rCrlSig.isSelected() ? "1" : "0") + 
				(rEncOnl.isSelected() ? "1" : "0") + 
				(rDecOnl.isSelected() ? "1" : "0"));
		prop.setProperty(EXTENDED_KEY_USAGE_KEY, 
				(rSerAut.isSelected() ? "1" : "0") +
				(rCliAut.isSelected() ? "1" : "0") + 
				(rCodSig.isSelected() ? "1" : "0") + 
				(rEmaPro.isSelected() ? "1" : "0") + 
				(rTimSta.isSelected() ? "1" : "0") + 
				(rOcsSig.isSelected() ? "1" : "0"));
		saveSAN(panel_6.getComponents(), prop);
		System.out.println(prop);
		return prop;	
	}
	
	private void loadSAN (Properties prop) {
		while (!(((JPanel) panel_6.getComponent(SAN_INDEX)).getComponent(0) instanceof JComboBox)) {
			panel_6.remove(SAN_INDEX);
		}
		Iterator<String> keys = prop.stringPropertyNames().iterator();
		while(keys.hasNext()) {
			String key = keys.next();
			if(key.startsWith(SAN_PREFIX)) {
				String val = prop.getProperty(key);
				key = key.replaceFirst(SAN_PREFIX, EMPTY) + CHAR_EQUAL;
				JLabel label = new JLabel(key);
				JTextField textField = new JTextField();
				textField.setColumns(32);
				textField.setText(val);
				JPanel panel = new JPanel();
				FlowLayout flowLayout = (FlowLayout) panel.getLayout();
				flowLayout.setAlignment(FlowLayout.LEFT);
				panel.add(label);
				panel.add(textField);
				panel_6.add(panel, SAN_INDEX);
			}
		}
	}
	
	private void loadSecondaryDN (Properties prop) {
		while (secDnPanel.getComponentCount() > 1) {
			secDnPanel.remove(0);
		}
		Iterator<String> keys = prop.stringPropertyNames().iterator();
		while(keys.hasNext()) {
			String key = keys.next();
			if(key.startsWith(SECONDARY_DN_PFX)) {
				String val = prop.getProperty(key);
				key = key.replaceFirst(SECONDARY_DN_PFX, EMPTY) + CHAR_EQUAL;
				JLabel label = new JLabel(key);
				JTextField textField = new JTextField();
				textField.setColumns(10);
				textField.setText(val);
				JLabel label2 = new JLabel(",");
				secDnPanel.add(label2, 0);
				secDnPanel.add(textField, 0);
				secDnPanel.add(label, 0);
			}
		}
	}
	
	private void loadPrimaryDN(Properties prop) {
		Component[] comps = primDnPanel.getComponents(); 
		for(int i = 0; i < comps.length; i ++) {
			if(i > 0) {
				i ++;
			}
			String key = PRIMARY_DN_PFX + ((JLabel) comps[i]).getText().substring(0, ((JLabel) comps[i]).getText().length() - 1);
			i ++;
			String val = prop.getProperty(key);
			((JTextField) comps[i]).setText(val);
		}
	}
	
	private void saveDN (Component[] comps, Properties prop, String prefix) {
		for(int i = 0; i < comps.length; i ++) {
			String key = null;
			String val = null;
			if(i > 0) {
				i ++;
			}
			if(i >= comps.length) {
				break;
			}
			if(comps[i] instanceof JLabel) {
				key = ((JLabel) comps[i]).getText();
				i ++;
				key = key.substring(0, key.length() - 1);
			}
			else {
				break;
			}
			if(comps[i] instanceof JTextField) {
				val = ((JTextField) comps[i]).getText();
			}
			else {
				break;
			}
			if(key != null && val != null) {
				prop.setProperty(prefix + key, val);
			}
		}
	}
	
	private void saveSAN(Component[] comps, Properties prop) {
		for(int i = SAN_INDEX; ((JPanel)comps[i]).getComponent(0) instanceof JLabel; i ++) {
			JPanel panel= (JPanel)comps[i];
			String key = SAN_PREFIX + ((JLabel) panel.getComponent(0)).getText().substring(0, ((JLabel) panel.getComponent(0)).getText().length() - 1);
			String val = ((JTextField) panel.getComponent(1)).getText();
			prop.setProperty(key, val);
		}
	}
	
	private ActionListener actionCombo04 = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().equals("comboBoxChanged") && comboBox_4.getSelectedIndex() > 0) {
				comboBox_4.removeActionListener(actionCombo04);
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
				comboBox_4.addActionListener(actionCombo04);
			}
		}
	};
	
	private ActionListener actionCombo03 = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().equals("comboBoxChanged") && comboBox_3.getSelectedIndex() > 0) {
				comboBox_3.removeActionListener(actionCombo03);
				JPanel panel = new JPanel();
				FlowLayout flowLayout = (FlowLayout) panel.getLayout();
				flowLayout.setAlignment(FlowLayout.LEFT);
				JTextField textField = new JTextField();
				textField.setColumns(32);
				JLabel label = new JLabel(comboBox_3.getItemAt(comboBox_3.getSelectedIndex()) + "=");
				panel.add(label);
				panel.add(textField);
				comboBox_3.getParent().getParent().add(panel, SAN_INDEX);
				comboBox_3.getParent().getParent().validate();
				comboBox_3.getParent().getParent().repaint();
				scrollPane.validate();
				scrollPane.repaint();
				comboBox_3.setSelectedIndex(0);
				comboBox_3.addActionListener(actionCombo03);
			}
		}
	};
	
	private ActionListener actionBtns = new ActionListener() {
		
		
		public void actionPerformed(ActionEvent e) {
			System.out.println(e.getActionCommand());
			if(e.getActionCommand().equals("Reset")) {
				System.out.println(e.getActionCommand() + " was performed");
				me.removeAll();
				reset();
				setListeners();
				me.validate();
				me.repaint();
			}
			else if (e.getActionCommand().equals("Vytvori\u0165")) {
				System.out.println(e.getActionCommand() + " was performed");
				try {
					Hashtable<String, Object> csrDetails = new Hashtable<String, Object>();
					InputCSR.getCSRDetails(csrDetails, me);
				}
				catch (Exception err) {
					JOptionPane.showMessageDialog(CreateCSR.getMain(), err.getClass().getSimpleName() + ": " + err.getMessage(), CSR_ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
					err.printStackTrace();
				}
			}
			else if (e.getActionCommand().equals("\u0160ablony")) {
				System.out.println(e.getActionCommand() + " was performed");
				JFrame main = CreateCSR.getMain(); 
				main.getContentPane().remove(0);
				main.getContentPane().add(CreateCSR.getTemplates(), BorderLayout.CENTER);
				main.getContentPane().validate();
				main.getContentPane().repaint();
			}
			else if (e.getActionCommand().equals("Zatvori\u0165")) {
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
			else if (e.getActionCommand().equals("Ulo\u017Ei\u0165 ako \u0161ablonu")) {
				String image = null;
				String pName = null;
				try {
					System.out.println(e.getActionCommand() + " was performed");
					Properties prop = me.colectData();
					String name;
					String[] names = Templates.templateNames();
					while(true) {
						name = (String) JOptionPane.showInputDialog(CreateCSR.getMain(), CHOOSE_TEMPLATE_NAME, CUSTOMIZED_DIALOG, JOptionPane.PLAIN_MESSAGE, TEMPLATE_NAME_ICON, null, PREVIEW_TEMPLATE_NAME);
						if(name == null) {
							throw new IOException (TEMPLATE_MSG_USER_CANCEL);
						}
						if(name.length() == 0) {
							continue;
						}
						int i = -1;
						for(String n : names) {
							if(name.equals(Templates.getTemplateName(n))) {
								i = JOptionPane.showOptionDialog(CreateCSR.getMain(), String.format(EQUAL_TEMPLATE_NAME_MESSAGE, name), EQUAL_TEMPLATE_NAME_TITLE, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, EQUAL_TEMPLATE_NAME_ICON, EQUAL_TEMPLATE_NAME_OPTION, EQUAL_TEMPLATE_NAME_OPTION[0]);
								break;
							}
							else {
								continue;
							}
						}
						if( i < 1) {
							break;
						}
						else if(i == 1) {
							continue;
						}
						throw new IOException (TEMPLATE_MSG_USER_CANCEL);
					}
					String fullName = Templates.createFullTemplateName(name);
					pName = Templates.getTemplatePropertyName(fullName);
					prop.storeToXML(new FileOutputStream(Templates.getAbsolutePath(pName)), String.format(TEMPLATE_PROPERTY_COMMENT, name), TEMPLATE_PROPERTY_DEFAULT_ENCODING);
					BufferedImage bi = new BufferedImage(panel_6.getWidth(), panel_6.getHeight(), BufferedImage.TYPE_INT_RGB);
					Graphics2D g = bi.createGraphics();
					panel_6.paint(g);
					image = Templates.getTemplateImageName(fullName);
					ImageIO.write(bi, Templates.getExtension(image).toUpperCase(), new File(Templates.getAbsolutePath(image)));
					((Templates) CreateCSR.getTemplates()).reloadTemplates();
					JOptionPane.showMessageDialog(CreateCSR.getMain(), String.format(SUCCESSFUL_CREATE_TEMPLATE, name), TEMPLATE_SUCESSFUL_TITLE, JOptionPane.INFORMATION_MESSAGE);
				}
				catch (Exception err) {
					JOptionPane.showMessageDialog(CreateCSR.getMain(), err.getMessage(), TEMPLATE_ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
					try {
						File f = new File (Templates.getAbsolutePath(pName));
						if(f.exists()) {
							f.delete();
						}
					}
					catch (Exception err2) {
						
					}
					try {
						File f = new File (Templates.getAbsolutePath(image));
						if(f.exists()) {
							f.delete();
						}
					}
					catch (Exception err2) {}
				}
			}
		}
	};
	private JRadioButton rDigSig;
	private JRadioButton rNonRep;
	private JRadioButton rKeyEnc;
	private JRadioButton rDatEnc;
	private JRadioButton rKeyAgr;
	private JRadioButton rKeyCSi;
	private JRadioButton rCrlSig;
	private JRadioButton rEncOnl;
	private JRadioButton rDecOnl;
	
	private JRadioButton rSerAut;
	private JRadioButton rCliAut;
	private JRadioButton rCodSig;
	private JRadioButton rEmaPro;
	private JRadioButton rTimSta;
	private JRadioButton rOcsSig;
		
	
	
	/**
	 * Create the panel.
	 */
	public InputCSR() {
		
		reset();
		setListeners();
		

	}

	
	protected static boolean getCSRDetails(Hashtable<String, Object> details, InputCSR input) throws Exception {
		boolean status = true;
		Properties pro = input.colectData();
		details.put(KEY_STORE_KEY, KeyUtils.getKeyStore(details, InputCSR.getKeyStoreType(pro.getProperty(STORE_TYPE_KEY)), pro.getProperty(STORE_LOCATION_KEY)));
		details.put(ALIAS_KEY, InputCSR.getAlias(pro.getProperty(ALIAS_KEY)));
		details.put(SUBJECT_KEY, InputCSR.getDN(pro));
		details.put(KEY_KEY, KeyUtils.obtainKey(details, (KeyStore) details.get(KEY_STORE_KEY), details.get(ALIAS_KEY), InputCSR.getKeyAlgorithm(pro.getProperty(KEY_TYPE_KEY)), InputCSR.getKeySize(pro.getProperty(KEY_SIZE_KEY)), details.get(SUBJECT_KEY)));
		details.put(EXTENSIONS_KEY, getExtensions(pro));
		BERImpl csr = Request.createRequest((KeyPair) details.get(KEY_KEY), (String[]) details.get(SUBJECT_KEY), (Object[]) details.get(EXTENSIONS_KEY));
		Request.exportToFile(csr, "C:\\Temp\\request.req", Request.PEM_FORMAT);
		KeyUtils.saveKeyStore(InputCSR.getKeyStoreType(pro.getProperty(STORE_TYPE_KEY)), pro.getProperty(STORE_LOCATION_KEY), details.get(KEY_STORE_KEY), details.get(KeyUtils.STORE_PASS_KEY));
		JFrame main = CreateCSR.getMain(); 
		main.getContentPane().remove(0);
		main.getContentPane().add(CreateCSR.getDisplayRequest(csr), BorderLayout.CENTER);
		main.getContentPane().validate();
		main.getContentPane().repaint();
		return status;
	}
	
	
	
	private static Object getExtensions(Properties pro) {
		ArrayList<Object> ext = new ArrayList<Object>();
		String ku = pro.getProperty(KEY_USAGE_KEY);
		for(int i = ku.length() - 1; i >= 0; i --) {
			if(ku.charAt(i) == '1') {
				ku = ku.substring(0, i);
				break;
			}
		}
		if(ku.length() > 0) {
			ext.add(TBSRequest.KEY_USAGE_OID);
			ext.add(ku);
		}
		String eku = pro.getProperty(EXTENDED_KEY_USAGE_KEY);
		ArrayList<String> tmp = new ArrayList<String>();
		for(int i = 0; i < eku.length(); i ++) {
			if(eku.charAt(i) == '1') {
				tmp.add(EKU_OIDS[i]);
			}
		}
		if(tmp.size() > 0) {
			ext.add(TBSRequest.EKU_OID);
			String[] tmp2 = new String[tmp.size()];
			ext.add(tmp.toArray(tmp2));
		}
		Enumeration<?> names = pro.propertyNames();
		ArrayList<String> san = new ArrayList<String>();
		while(names.hasMoreElements()) {
			String key = (String) names.nextElement();
			if(key.startsWith(SAN_PREFIX)) {
				String val = pro.getProperty(key);
				key = key.replaceFirst(SAN_PREFIX, EMPTY);
				for(int i = 1; i < SAN_TYPES2.length; i ++) {
					if(SAN_TYPES[i].equals(key)) {
						san.add(SAN_TYPES2[i]);
						san.add(val);
						break;
					}
				}
			}
		}
		if(san.size() > 0) {
			ext.add(TBSRequest.SAN_OID);
			String[] tmp2 = new String[san.size()];
			ext.add(san.toArray(tmp2));
		}
		if(ext.size() > 0) {
			return ext.toArray();
		}
		else {
			return null;
		}
	}

	private static final String[] DN_LIST = new String[] {"pridajte \u010Fal\u0161iu polo\u017Eku DN", "CN", "OU", "O", "C", "L", "ST", "STREET", "TITLE", "UID", "MAIL", "E", "DC", "SERIALNUMBER", "UNSTRUCTUREDNAME", "UNSTRUCTUREDADDRESS"};
	
	private static String[] getDN(Properties prop) {
		ArrayList<String> list = new ArrayList<String>();
		for(int i = 1; i < DN_LIST.length; i ++) {
			Enumeration<?> names = prop.propertyNames();
			while(names.hasMoreElements()) {
				String key = (String) names.nextElement();
				String val = prop.getProperty(key);
				if(val != null && val.length() > 0) {
					if(key.startsWith(PRIMARY_DN_PFX)) {
						key = key.replaceFirst(PRIMARY_DN_PFX, EMPTY);
						
					}
					else if(key.startsWith(SECONDARY_DN_PFX)) {
						key = key.replaceFirst(SECONDARY_DN_PFX, EMPTY);
					}
					else {
						continue;
					}
					if(key.equals(DN_LIST[i])) {
						
						list.add(key);
						list.add(val);
					}
				}
			}
		}
		String [] tmp = new String[list.size()];
		tmp = list.toArray(tmp);
		return tmp;
	}
	
	private static final String[] KEY_SIZES = new String[] {"512 bit", "1024 bit", "2048 bit", "4096 bit"};
	
	private static int getKeySize(String size) {
		return Integer.parseInt(KEY_SIZES[Integer.parseInt(size)].split(" ")[0]);
	}

	private static final String[] KEY_ALGORITHM_TYPES = new String[] {"RSA", "DSA"};
	
	private static String getKeyAlgorithm(String type) {
		return KEY_ALGORITHM_TYPES[Integer.parseInt(type)];
	}

	private static String getAlias(String alias) {
		if(alias == null || alias.length() == 0) {
			alias = DEFALUT_ALIAS + new Date().getTime();
		}
		return alias;
	}

	private static final String[] KEY_STORE_TYPES = {KeyUtils.JKS, KeyUtils.WINDOWS_STORE, KeyUtils.PKCS12, KeyUtils.PKCS11};
	private static final String[] SAN_TYPES = {"vyberte typ", "e-mail", "DNS", "IP adresa"};
	private static final String[] SAN_TYPES2 = {null, TBSRequest.RFC882_NAME, TBSRequest.DNS_NAME_SAN, TBSRequest.IP_SAN};
	
	
	private static String getKeyStoreType(String index) {
		return KEY_STORE_TYPES[Integer.parseInt(index)];
	}

	private void setListeners() {
		comboBox_3.addActionListener(actionCombo03);
		comboBox_4.addActionListener(actionCombo04);
		btnReset.addActionListener(actionBtns);
		btnZatvori.addActionListener(actionBtns);
		btnablony.addActionListener(actionBtns);
		btnVytvori.addActionListener(actionBtns);
		btnUloiAkoablonu.addActionListener(actionBtns);
	}
	
	private void reset() {
		me = this;
		new Templates();
		
		
		
		setLayout(new BorderLayout(0, 0));
				
		JPanel panel = new JPanel();
		panel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		
		scrollPane = new JScrollPane(panel);
		add(scrollPane, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));
		
		panel_6 = new JPanel();
		panel.add(panel_6, BorderLayout.CENTER);
		panel_6.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		panel_6.setLayout(new BoxLayout(panel_6, BoxLayout.PAGE_AXIS));
		
		JPanel panel_5 = new JPanel();
		panel_6.add(panel_5);
		FlowLayout flowLayout_1 = (FlowLayout) panel_5.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		
		JLabel lblNewLabel_3 = new JLabel("Z\u00E1kladn\u00E9 \u00FAdaje o type \u00FAlo\u017Eiska k\u013E\u00FA\u010Da");
		panel_5.add(lblNewLabel_3);
		
		JPanel panel_7 = new JPanel();
		panel_6.add(panel_7);
		panel_7.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		JLabel lblNewLabel = new JLabel("V\u00FDber typu \u00FAlo\u017Eiska");
		panel_7.add(lblNewLabel);
		
		storeTypeCombo = new JComboBox<String>();
		panel_7.add(storeTypeCombo);
		storeTypeCombo.setModel(new DefaultComboBoxModel<String>(new String[] {"Java KeyStore - JKS", "Microsoft Windows Store", "PKCS #12 - s\u00FAbor *.pfx, *.p12", "PKCS #11 - HW token"}));
		
		JLabel lblNewLabel_1 = new JLabel("umiestnenie \u00FAlo\u017Eiska");
		panel_7.add(lblNewLabel_1);
		
		storeLocationText = new JTextField();
		panel_7.add(storeLocationText);
		storeLocationText.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("meno (alias) s\u00FAkromn\u00E9ho k\u013E\u00FA\u010Da");
		panel_7.add(lblNewLabel_2);
		
		aliasText = new JTextField();
		panel_7.add(aliasText);
		aliasText.setColumns(10);
		
		JPanel panel_12 = new JPanel();
		panel_6.add(panel_12);
		
		JPanel panel_9 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_9.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel_6.add(panel_9);
		
		JLabel lblNastavenieParametrovSkromnho = new JLabel("Nastavenie parametrov s\u00FAkromn\u00E9ho k\u013E\u00FA\u010Da");
		panel_9.add(lblNastavenieParametrovSkromnho);
		
		JPanel panel_10 = new JPanel();
		FlowLayout flowLayout_2 = (FlowLayout) panel_10.getLayout();
		flowLayout_2.setAlignment(FlowLayout.LEFT);
		panel_6.add(panel_10);
		
		JLabel lblTypKa = new JLabel("Typ k\u013E\u00FA\u010Da");
		panel_10.add(lblTypKa);
		
		keyTypeCombo = new JComboBox<String>();
		keyTypeCombo.setModel(new DefaultComboBoxModel<String>(KEY_ALGORITHM_TYPES));
		panel_10.add(keyTypeCombo);
		
		JLabel lblVekosKa = new JLabel("Ve\u013Ekos\u0165 k\u013E\u00FA\u010Da");
		panel_10.add(lblVekosKa);
		
		keySizeCombo = new JComboBox<String>();
		keySizeCombo.setModel(new DefaultComboBoxModel<String>(KEY_SIZES));
		keySizeCombo.setSelectedIndex(2);
		panel_10.add(keySizeCombo);
		
		JPanel panel_11 = new JPanel();
		panel_6.add(panel_11);
		
		JPanel panel_13 = new JPanel();
		FlowLayout flowLayout_3 = (FlowLayout) panel_13.getLayout();
		flowLayout_3.setAlignment(FlowLayout.LEFT);
		panel_6.add(panel_13);
		
		JLabel lblNastavenieParametrovPre = new JLabel("Nastavenie parametrov pre certifik\u00E1ciu");
		panel_13.add(lblNastavenieParametrovPre);
		
		JPanel panel_14 = new JPanel();
		FlowLayout flowLayout_4 = (FlowLayout) panel_14.getLayout();
		flowLayout_4.setAlignment(FlowLayout.LEFT);
		panel_6.add(panel_14);
		
		JLabel lblSubjektnastavenieDn = new JLabel("Subjekt (nastavenie DN pre vlastn\u00EDka, resp. \u00FA\u010Del certifik\u00E1tu)");
		panel_14.add(lblSubjektnastavenieDn);
		
		primDnPanel = new JPanel();
		FlowLayout flowLayout_5 = (FlowLayout) primDnPanel.getLayout();
		flowLayout_5.setAlignment(FlowLayout.LEFT);
		panel_6.add(primDnPanel);
		
		JLabel lblCn = new JLabel("CN=");
		primDnPanel.add(lblCn);
		
		textField_2 = new JTextField();
		primDnPanel.add(textField_2);
		textField_2.setColumns(16);
		
		JLabel label = new JLabel(",");
		primDnPanel.add(label);
		
		JLabel lblNewLabel_4 = new JLabel("OU=");
		primDnPanel.add(lblNewLabel_4);
		
		textField_3 = new JTextField();
		primDnPanel.add(textField_3);
		textField_3.setColumns(10);
		
		JLabel label_1 = new JLabel(",");
		primDnPanel.add(label_1);
		
		JLabel lblO = new JLabel("O=");
		primDnPanel.add(lblO);
		
		textField_4 = new JTextField();
		primDnPanel.add(textField_4);
		textField_4.setColumns(16);
		
		JLabel label_2 = new JLabel(",");
		primDnPanel.add(label_2);
		
		JLabel lblL = new JLabel("L=");
		primDnPanel.add(lblL);
		
		textField_5 = new JTextField();
		primDnPanel.add(textField_5);
		textField_5.setColumns(10);
		
		JLabel label_3 = new JLabel(",");
		primDnPanel.add(label_3);
		
		JLabel lblSt = new JLabel("ST=");
		primDnPanel.add(lblSt);
		
		textField_6 = new JTextField();
		primDnPanel.add(textField_6);
		textField_6.setColumns(10);
		
		JLabel label_4 = new JLabel(",");
		primDnPanel.add(label_4);
		
		JLabel lblC = new JLabel("C=");
		primDnPanel.add(lblC);
		
		textField_7 = new JTextField();
		primDnPanel.add(textField_7);
		textField_7.setColumns(2);
		
		secDnPanel = new JPanel();
		FlowLayout flowLayout_13 = (FlowLayout) secDnPanel.getLayout();
		flowLayout_13.setAlignment(FlowLayout.LEFT);
		panel_6.add(secDnPanel);
		
		comboBox_4 = new JComboBox<String>();
		comboBox_4.setModel(new DefaultComboBoxModel<String>(DN_LIST));
		secDnPanel.add(comboBox_4);
		
		JPanel panel_17 = new JPanel();
		FlowLayout flowLayout_6 = (FlowLayout) panel_17.getLayout();
		flowLayout_6.setAlignment(FlowLayout.LEFT);
		panel_6.add(panel_17);
		
		JLabel lblPouitieSkromnhoKa = new JLabel("Pou\u017Eitie s\u00FAkromn\u00E9ho k\u013E\u00FA\u010Da");
		panel_17.add(lblPouitieSkromnhoKa);
		
		JPanel panel_18 = new JPanel();
		FlowLayout flowLayout_7 = (FlowLayout) panel_18.getLayout();
		flowLayout_7.setAlignment(FlowLayout.LEFT);
		panel_6.add(panel_18);
		
		rDigSig = new JRadioButton("digital signature");
		rDigSig.setSelected(true);
		panel_18.add(rDigSig);
		
		rNonRep = new JRadioButton("non repudiation");
		rNonRep.setSelected(true);
		panel_18.add(rNonRep);
		
		rKeyEnc = new JRadioButton("key encipherment");
		rKeyEnc.setSelected(true);
		panel_18.add(rKeyEnc);
		
		rDatEnc = new JRadioButton("data encipherment");
		rDatEnc.setSelected(true);
		panel_18.add(rDatEnc);
		
		JPanel panel_19 = new JPanel();
		FlowLayout flowLayout_8 = (FlowLayout) panel_19.getLayout();
		flowLayout_8.setAlignment(FlowLayout.LEFT);
		panel_6.add(panel_19);
		
		rKeyAgr = new JRadioButton("key agreement");
		panel_19.add(rKeyAgr);
		
		rKeyCSi = new JRadioButton("key cert sign");
		panel_19.add(rKeyCSi);
		
		rCrlSig = new JRadioButton("CRL sign");
		panel_19.add(rCrlSig);
		
		rEncOnl = new JRadioButton("encipher only");
		panel_19.add(rEncOnl);
		
		rDecOnl = new JRadioButton("decipher only");
		panel_19.add(rDecOnl);
		
		JPanel panel_20 = new JPanel();
		FlowLayout flowLayout_9 = (FlowLayout) panel_20.getLayout();
		flowLayout_9.setAlignment(FlowLayout.LEFT);
		panel_6.add(panel_20);
		
		JLabel lblRozrenPouitieSkromnho = new JLabel("Roz\u0161\u00EDren\u00E9 pou\u017Eitie s\u00FAkromn\u00E9ho k\u013E\u00FA\u010Da");
		panel_20.add(lblRozrenPouitieSkromnho);
		
		JPanel panel_21 = new JPanel();
		FlowLayout flowLayout_10 = (FlowLayout) panel_21.getLayout();
		flowLayout_10.setAlignment(FlowLayout.LEFT);
		panel_6.add(panel_21);
		
		rSerAut = new JRadioButton("server auth");
		panel_21.add(rSerAut);
		
		rCliAut = new JRadioButton("client auth");
		panel_21.add(rCliAut);
		
		rCodSig = new JRadioButton("code signing");
		panel_21.add(rCodSig);
		
		rEmaPro = new JRadioButton("email protection");
		panel_21.add(rEmaPro);
		
		rTimSta = new JRadioButton("time stamping");
		panel_21.add(rTimSta);
		
		rOcsSig = new JRadioButton("OCSP signing");
		panel_21.add(rOcsSig);
		
		JPanel panel_22 = new JPanel();
		FlowLayout flowLayout_11 = (FlowLayout) panel_22.getLayout();
		flowLayout_11.setAlignment(FlowLayout.LEFT);
		panel_6.add(panel_22);
		
		JLabel lblAlternatvneMenoSubjektu = new JLabel("Alternat\u00EDvne meno subjektu (SAN)");
		panel_22.add(lblAlternatvneMenoSubjektu);
		
		JPanel panel_23 = new JPanel();
		FlowLayout flowLayout_12 = (FlowLayout) panel_23.getLayout();
		flowLayout_12.setAlignment(FlowLayout.LEFT);
		panel_6.add(panel_23);
		
		comboBox_3 = new JComboBox<String>();
		comboBox_3.setModel(new DefaultComboBoxModel<String>(SAN_TYPES));
		panel_23.add(comboBox_3);
		
		JPanel panel_8 = new JPanel();
		panel_6.add(panel_8);
		panel_8.setLayout(new BorderLayout(0, 0));
		
		
		JPanel panel_1 = new JPanel();
		add(panel_1, BorderLayout.SOUTH);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_2 = new JPanel();
		panel_1.add(panel_2, BorderLayout.WEST);
		panel_2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		btnReset = new JButton("Reset");
		panel_2.add(btnReset);
		
		btnablony = new JButton("\u0160ablony");
		panel_2.add(btnablony);
		
		btnUloiAkoablonu = new JButton("Ulo\u017Ei\u0165 ako \u0161ablonu");
		panel_2.add(btnUloiAkoablonu);
		
		btnVytvori = new JButton("Vytvori\u0165");
		panel_2.add(btnVytvori);
		
		JPanel panel_3 = new JPanel();
		panel_1.add(panel_3, BorderLayout.CENTER);
		panel_3.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JPanel panel_4 = new JPanel();
		panel_1.add(panel_4, BorderLayout.EAST);
		panel_4.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		btnZatvori = new JButton("Zatvori\u0165");
		panel_4.add(btnZatvori);
		
	}

}
