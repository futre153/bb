package org.pabk.winapp.pki.keys;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;

import org.pabk.basen.ber.BERImpl;
import org.pabk.basen.rfc.rfc5280.Certificate;

public class KeyUtils {
	public static final String JKS = "JKS";
	public static final String PKCS11 = "PKCS11";
	public static final String PKCS12 = "PKCS12";
	public static final String WINDOWS_STORE = "WINDOWS-STORE";
	private static final String UNSUPPORTED_KEYSTORE = "Typ ˙loûiska %s nie je podporovan˝";
	private static final String DEFUALT_KEY_ALGORITHM = "RSA";
	private static final Hashtable<String, KeyPairGenerator> generators = new Hashtable<String, KeyPairGenerator>();
	private static final char UNDERSCORE = '_';
	private static final String EMPTY = "";
	private static final int DEFAULT_KEY_SIZE = 2048;
	private static final int MIN_KEY_SIZE = 1024;
	private static final int MAX_KEY_SIZE = 4096;
	private static final String NULL_INPUT_SOURCE = "%s musÌ maù definovan˝ zdroj";
	private static final String UNSUPPORTED_IO_STREAM = "Trieda %s nemÙûe byù konvertovan· na zdroj";
	private static final String ADD_KEYSTORE_PASS = "ProsÌm zadajte heslo pre ˙loûisko priv·tneho kæ˙Ëa:";
	private static final String ADD_KEY_PASS = "ProsÌm zadajte heslo pre priv·tny kæ˙Ë:";
	private static final String OK = "OK";
	private static final String VERIFY_KEYSTORE_PASS = "zopakujte Vaöe heslo pre ˙loûisko kæ˙Ëa:";
	private static final String VERIFY_KEY_PASS = "zopakujte Vaöe heslo pre priv·tny kæ˙Ë:";
	private static final String CANCEL = "Zruöiù";
	private static final Icon KEYSTORE_PASS_TITLE_ICON = null;
	private static final String KEYSTORE_PASS_TITLE = "Heslo pre ˙loûisko kæ˙Ëa";
	private static final String KEYSTORE_PASS_ERROR_TITLE = "Chyba pri zad·vanÌ hesla";
	private static final String JKS_SPECIAL = "~@#$%^&*()_+-=[]\\,./{}:|<>?";
	private static final String USER_CANCEL = "PouûÌvateæ preruöil oper·ciu";
	private static final Object VERIFY_PASS_FAILS = "Hesl· musia byù rovnakÈ v oboch riadkoch";
	public static final String STORE_PASS_KEY = "storePassword";
	private static final String SAME_AS_STORE_PASS = "Heslo zhodnÈ s heslom ˙loûiska";
	private static final String KEY_PASS_TITLE = "Heslo pre s˙kromn˝ kæ˙Ë" ;
	private static final Icon KEY_PASS_TITLE_ICON = null;
	private static final String KEY_SPECIAL = JKS_SPECIAL;
	private static final String KEY_PASS_KEY = "privateKeyPass";
	private static final String DEFAULT_SIGNATURE = "SHA256withRSA";
	private static PasswordPolicy jksPassPolicy = new CommonPasswordPolicy(8, 32, true, true, true, JKS_SPECIAL, '!', '~');
	private static PasswordPolicy privateKeyPassPolicy = new CommonPasswordPolicy(8, 32, true, true, true, KEY_SPECIAL, '!', '~');
	private static ActionListener radioLis = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println(e.getActionCommand());
			System.out.println(e.getSource());
			if(e.getActionCommand().equals(SAME_AS_STORE_PASS) && e.getSource() instanceof JRadioButton) {
				JRadioButton src = (JRadioButton) e.getSource();
				Component[] comps = src.getParent().getComponents();
				for(int i = 0; i < comps.length; i ++) {
					if(comps[i] instanceof JPasswordField) {
						comps[i].setEnabled(!src.isSelected());
					}
				}
			}
		}
		
	};
	public static void saveKeyStore (String keyStoreType, Object ... args) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException{
		if(keyStoreType.equals(JKS)) {
			saveJKS(args);
		}
		else if (keyStoreType.equals(PKCS11)) {

		}
		else if (keyStoreType.equals(PKCS12)) {

		}
		else if (keyStoreType.equals(WINDOWS_STORE)) {
			
		}
		else {
			throw new KeyStoreException(String.format(UNSUPPORTED_KEYSTORE, keyStoreType));
		}
	}
	
	
	private static void saveJKS(Object[] args) throws IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException {
		// TODO Auto-generated method stub
		String path = (String) args[0];
		KeyStore keyStore = (KeyStore) args[1];
		char[] storePass = (char[]) args[2];
		FileOutputStream out = new FileOutputStream(path);
		keyStore.store(out, storePass);
		out.close();
	}


	
	
	public static KeyStore getKeyStore (Hashtable<String, Object> details, String keyStoreType, Object ... args) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
		if(keyStoreType.equals(JKS)) {
			return getJKS(details, args);
		}
		else if (keyStoreType.equals(PKCS11)) {
			return getPKCS11(args);
		}
		else if (keyStoreType.equals(PKCS12)) {
			return getPKCS11(args);
		}
		else if (keyStoreType.equals(WINDOWS_STORE)) {
			return getWinStore(args);
		}
		throw new KeyStoreException(String.format(UNSUPPORTED_KEYSTORE, keyStoreType));
	}
	
	
	
	private static KeyStore getWinStore(Object[] args) {
		// TODO Auto-generated method stub
		return null;
	}

	private static KeyStore getPKCS11(Object[] args) {
		// TODO Auto-generated method stub
		return null;
	}

	private static KeyStore getJKS(Hashtable<String, Object> details, Object[] args) throws IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException {
		InputStream in = null;
		if(args.length > 0) {
			in = getKeyInputStream(args[0]);
		}
		else {
			throw new KeyStoreException(String.format(NULL_INPUT_SOURCE, JKS));
		}
		char[] pass = null;
		if(args.length > 1) {
			pass = getStorePass (KeyUtils.jksPassPolicy , args[1]);
		}
		else {
			pass = getStorePass (KeyUtils.jksPassPolicy);
		}
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		ks.load(in, pass);
		details.put(STORE_PASS_KEY, pass);
		return ks;
	}
	
	private static char[] getKeyPass(PasswordPolicy policy, Object ... objects) throws KeyStoreException {
		if(objects != null && objects.length > 0 && objects[0] != null) {
			if (objects[0] instanceof String) {
				return ((String) objects[0]).toCharArray();
			}
			else if (objects[0] instanceof char[]) {
				return (char[]) objects[0];
			}
			
		}
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		JPasswordField pass1 = new JPasswordField(20);
		JPasswordField pass2 = new JPasswordField(20);
		panel.add(new JLabel (ADD_KEY_PASS));
		panel.add(pass1);
		panel.add(new JLabel (VERIFY_KEY_PASS));
		panel.add(pass2);
		JRadioButton radio = new JRadioButton (SAME_AS_STORE_PASS);
		radio.addActionListener(radioLis);
		panel.add(radio);
		String[] options = {OK, CANCEL};
		while(true) {
			int opt = JOptionPane.showOptionDialog(null, panel, KEY_PASS_TITLE, JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE, KEY_PASS_TITLE_ICON, options, options[0]);
			if(opt == 0) {
				char[] p1 = pass1.getPassword();
				char[] p2 = pass2.getPassword();
				if(radio.isSelected()) {
					p1 = (char[]) objects[1];
					p2 = p1;
				}
				if(policy != null && !policy.checkPassword(p1)) {
					JOptionPane.showMessageDialog(null, policy.getErrorMessage(), KEYSTORE_PASS_ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
				}
				else {
					if (!Arrays.equals(p1, p2)) {
						JOptionPane.showMessageDialog(null, VERIFY_PASS_FAILS, KEYSTORE_PASS_ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
					}
					else {
						return p1;
					}
				}
			}
			else {
				throw new KeyStoreException(USER_CANCEL);
			}
		}
	}
	
	
	private static char[] getStorePass(PasswordPolicy policy, Object ... objects) throws KeyStoreException {
		if(objects != null && objects.length > 0) {
			if (objects[0] instanceof String) {
				return ((String) objects[0]).toCharArray();
			}
			else if (objects[0] instanceof char[]) {
				return (char[]) objects[0];
			}
			
		}
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		JPasswordField pass1 = new JPasswordField(20);
		JPasswordField pass2 = new JPasswordField(20);
		panel.add(new JLabel (ADD_KEYSTORE_PASS));
		panel.add(pass1);
		panel.add(new JLabel (VERIFY_KEYSTORE_PASS));
		panel.add(pass2);
		String[] options = {OK, CANCEL};
		while(true) {
			int opt = JOptionPane.showOptionDialog(null, panel, KEYSTORE_PASS_TITLE, JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE, KEYSTORE_PASS_TITLE_ICON, options, options[0]);
			if(opt == 0) {
				if(policy != null && !policy.checkPassword(pass1.getPassword())) {
					JOptionPane.showMessageDialog(null, policy.getErrorMessage(), KEYSTORE_PASS_ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
				}
				else {
					if (!Arrays.equals(pass1.getPassword(), pass2.getPassword())) {
						JOptionPane.showMessageDialog(null, VERIFY_PASS_FAILS, KEYSTORE_PASS_ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
					}
					else {
						return pass1.getPassword();
					}
				}
			}
			else {
				throw new KeyStoreException(USER_CANCEL);
			}
		}
	}
	
	private static InputStream getKeyInputStream(Object object) throws IOException, KeyStoreException {
		if(object == null) {
			throw new KeyStoreException(String.format(NULL_INPUT_SOURCE, JKS)); 
		}
		else if(object instanceof String || object instanceof File) {
			File f = null;
			if(object instanceof File) {
				f = (File) object;
			}
			else {
				if(((String)object).length() > 0) {
					f = new File((String) object);
				}
				else {
					throw new KeyStoreException(String.format(NULL_INPUT_SOURCE, JKS));
				}
			}
			if(f.exists()) {
				return new FileInputStream((String) object);
			}
			else {
				return null;
			}
		}
		else if (object instanceof InputStream) {
			return (InputStream) object;
		}
		else {
			throw new IOException (String.format(UNSUPPORTED_IO_STREAM, object.getClass().getName()));
		}
	}
	
	public static KeyPair obtainKey(Hashtable<String, Object> details, KeyStore ks, Object ... props) throws KeyStoreException, NoSuchAlgorithmException, NoSuchProviderException, UnrecoverableKeyException, InvalidKeyException, SignatureException, CertificateException, IOException {
		if(ks.getType().equalsIgnoreCase(JKS)) {
			return obtainFromJKS(details, ks, props);
		}
		/*else if (ks.getType().equals(PKCS11)) {
			return obtainFromPKCS11(object);
		}
		else if (ks.getType().equals(PKCS12)) {
			return obtainFromPKCS12(object);
		}
		else if (ks.getType().equals(WINDOWS_STORE)) {
			return obtainFromWinStore(object);
		}*/
		throw new KeyStoreException(String.format(UNSUPPORTED_KEYSTORE, ks.getType()));
	}
	private static PrivateKey obtainFromWinStore(Object[] keyProps) {
		// TODO Auto-generated method stub
		return null;
	}
	private static PrivateKey obtainFromPKCS12(Object[] keyProps) {
		// TODO Auto-generated method stub
		return null;
	}
	private static PrivateKey obtainFromPKCS11(Object[] keyProps) {
		// TODO Auto-generated method stub
		return null;
	}
	private static KeyPair obtainFromJKS(Hashtable<String, Object> details, KeyStore ks, Object ... objs) throws KeyStoreException, NoSuchAlgorithmException, NoSuchProviderException, UnrecoverableKeyException, InvalidKeyException, SignatureException, IOException, CertificateException {
		String alias = (String) objs[0];
		char[] storePass = (char[]) details.get(STORE_PASS_KEY);
		char[] keyPass = getKeyPass(privateKeyPassPolicy, null, storePass);
		String alg = (String) objs[1];
		int size =  (int) objs[2];
		String[] dn = (String[]) objs[3];
		details.put(KEY_PASS_KEY, keyPass);
		if(ks.containsAlias(alias)) {
			return new KeyPair(ks.getCertificate(alias).getPublicKey(), (PrivateKey) ks.getKey(alias, keyPass));
		}
		else {
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			KeyPair pair = createJKSKeyEntry(alg, null, size);
			BERImpl ber = Certificate.createCertificate(pair, 0, DEFAULT_SIGNATURE, dn, 0, new Date().getTime(), dn);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ber.encode(out);
			Certificate.exportToFile(ber, "C:\\Temp\\test.cer", "PEM");
			ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
			Collection<?> c = cf.generateCertificates(in);
			Iterator<?> i = c.iterator();
			X509Certificate cer = null;
			if (i.hasNext()) {
				Object obj = i.next();
				cer = (X509Certificate) obj;
			}
			KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(keyPass);
			KeyStore.PrivateKeyEntry pkEntry = new KeyStore.PrivateKeyEntry(pair.getPrivate(), new X509Certificate[]{cer});
			ks.setEntry(alias, pkEntry, protParam);
			return pair;
		}
	}
	public static KeyPair createJKSKeyEntry(String algorithm, String provider, int keysize) throws NoSuchAlgorithmException, NoSuchProviderException {
		KeyPair pair = createKey(algorithm, provider, keysize);
		return pair;
	}
	
	private static KeyPair createKey(String algorithm, String provider, int keysize) throws NoSuchAlgorithmException, NoSuchProviderException {
		keysize = getKeySize(keysize); 
		algorithm = algorithm == null ? DEFUALT_KEY_ALGORITHM : algorithm;
		KeyPairGenerator generator = getKeyPairGenerator (algorithm, provider);
		generator.initialize(keysize);
		return generator.generateKeyPair();
	}
	
	private static Integer getKeySize(Integer keysize) {
		int size = keysize == null ? DEFAULT_KEY_SIZE : keysize;
		return size < MIN_KEY_SIZE ? MIN_KEY_SIZE : (size > MAX_KEY_SIZE ? MAX_KEY_SIZE : size);
	}
	
	private static KeyPairGenerator getKeyPairGenerator (String algorithm, String provider) throws NoSuchAlgorithmException, NoSuchProviderException {
		String key = algorithm + UNDERSCORE + (provider == null ? EMPTY : provider);
		KeyPairGenerator generator = generators.get(key);
		if(generator == null) {
			if(provider == null || provider.length() == 0) {
				generator = KeyPairGenerator.getInstance(algorithm);
			}
			else {
				generator =  KeyPairGenerator.getInstance(algorithm, provider);
			}
			generators.put(key, generator);
		}
		return generator;
	}

}
