package org.pabk.basen.asn1;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.pabk.basen.ber.BERImpl;

public class ASN1Item extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String UNIVERSAL_NAME 			= "UNIVERSAL";
	private static final String APPLICATION_NAME 		= "APPLICATION";
	private static final String CONTEXT_SPECIFIC_NAME 	= "CONTEXT-SPECIFIC";
	private static final String PRIVATE_NAME 			= "PRIVATE";

	private static final String[] CLASS_NAME = {
		UNIVERSAL_NAME, APPLICATION_NAME, CONTEXT_SPECIFIC_NAME, PRIVATE_NAME
	};

	private static final String IMPLICIT_NAME = "IMPLICIT";
	private static final String EXPLICIT_NAME = "EXPLICIT";

	private static final String[] UNIVERSAL_TAG_NAME = {
		"EOC (End-of-Content)",
		"BOOLEAN",
		"INTEGER",
		"BIT STRING",
		"OCTET STRING",
		"NULL",
		"OBJECT IDENTIFIER",
		"Object Descriptor",
		"EXTERNAL",
		"REAL (float)",
		"ENUMERATED",
		"EMBEDDED PDV",
		"UTF8String",
		"RELATIVE-OID",
		"(reserved)",
		"(reserved)",
		"SEQUENCE",
		"SET",
		"NumericString",
		"PrintableString",
		"T61String",
		"VideotexString",
		"IA5String",
		"UTCTime",
		"GeneralizedTime",
		"GraphicString",
		"VisibleString",
		"GeneralString",
		"UniversalString",
		"CHARACTER STRING",
		"BMPString",
		"TAG"
	};

	private static final String OPTIONAL_NAME = "OPTIONAL";
	private static final String MANDATORY_NAME = "MANDATORY";
	private static final String VALUE_NOT_SET = "[VALUE IS NOT SET]";
	private static final String MINUS_TEXT = "-";
	protected static final String PLUS_TEXT = "+";
	
	private boolean open;
	private GridBagConstraints gbcValue;
	private JPanel value;
	private JLabel notSet = new JLabel(VALUE_NOT_SET);
	private JPanel main;
	private JButton button;
	
	public ASN1Item(ASN1Impl asn) {
		main = this;
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		this.setOpen(true);
		button = new JButton(MINUS_TEXT);
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent me) {
				if(open) {
					main.remove(value);
					button.setText(PLUS_TEXT);
					open = false;
				}
				else {
					main.add(value, gbcValue);
					button.setText(MINUS_TEXT);
					open = true;
				}
				
			}
		});
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button.setPreferredSize(new Dimension(14, 14));
		button.setMinimumSize(new Dimension(14, 14));
		button.setMaximumSize(new Dimension(14, 14));
		button.setMargin(new Insets(0, 0, 0, 0));
		GridBagConstraints gbc_button = new GridBagConstraints();
		gbc_button.insets = new Insets(0, 0, 0, 5);
		gbc_button.gridx = 0;
		gbc_button.gridy = 0;
		add(button, gbc_button);
		
		JLabel itemId = new JLabel(ASN1Item.getItemId(asn));
		GridBagConstraints gbcItemId = new GridBagConstraints();
		gbcItemId.anchor = GridBagConstraints.WEST;
		gbcItemId.insets = new Insets(0, 0, 0, 0);
		gbcItemId.gridx = 1;
		gbcItemId.gridy = 0;
		add(itemId, gbcItemId);
		
		JLabel lblValue = new JLabel("VALUE:");
		GridBagConstraints gbc_lblValue = new GridBagConstraints();
		gbc_lblValue.anchor = GridBagConstraints.WEST;
		gbc_lblValue.insets = new Insets(0, 0, 0, 0);
		gbc_lblValue.gridx = 1;
		gbc_lblValue.gridy = 1;
		add(lblValue, gbc_lblValue);
		
		value = new JPanel();
		gbcValue = new GridBagConstraints();
		gbcValue.fill = GridBagConstraints.VERTICAL;
		gbcValue.anchor = GridBagConstraints.WEST;
		gbcValue.gridx = 1;
		gbcValue.gridy = 2;
		add(value, gbcValue);
		value.setLayout(new BoxLayout(value, BoxLayout.Y_AXIS));
		
		setValue(value, notSet, asn);
	}
	
	private static void setValue (JPanel value, JLabel notSet, ASN1Impl asn) {
		value.removeAll();
		if(asn.getTag() == BERImpl.CHOICE) {
			if(asn.size() > 0 && asn.get(0).length > 0 && asn.get(0)[0] != null) {
				asn = asn.get(0)[0];
			}
		}
		if(asn.getBERObject() == null) {
			value.add(notSet);
		}
		else {
			if(asn.getConstructed() == BERImpl.CONSTRUCTED_ENCODING || asn.get_class() != BERImpl.UNIVERSAL_CLASS) {
				ASN1Impl[][] list = new ASN1Impl[asn.size()][0];
				ASN1Impl[][] list2 = asn.toArray(list);
				value.add(new ASN1List(list2));
			}
			else {
				//String[] val = ASN1Viewer.printHex(asn.getBERObject().getContentOctets());
				String[] val = ASN1Impl.split(asn.toString());
				value.removeAll();
				for(int i = 0; i < val.length; i ++) {
					GridBagConstraints gbcValue = new GridBagConstraints();
					gbcValue.fill = GridBagConstraints.VERTICAL;
					gbcValue.anchor = GridBagConstraints.WEST;
					gbcValue.gridx = 1;
					gbcValue.gridy = i + 1;
					JLabel line = new JLabel(val[i]);
					Font font = line.getFont();
					line.setFont(new Font("Courier New", font.getStyle(), font.getSize()));
					value.add(line, gbcValue);
				}
			}
		}
	}
	
	private static String getItemId(ASN1Impl asn) {
		StringBuffer sb = new StringBuffer(asn.getName());
		if(asn.getTag() == BERImpl.CHOICE) {
			if(asn.size() > 0 && asn.get(0).length > 0 && asn.get(0)[0] != null) {
				asn = asn.get(0)[0];
			}
			else {
				sb.append(" CHOICE");
				return sb.toString();
			}
		}
		sb.append(' ');
		sb.append(CLASS_NAME[asn.get_class() >> Integer.numberOfTrailingZeros(BERImpl.CLASS_MASK)]);
		sb.append(' ');
		sb.append(asn.isImplicit() ? IMPLICIT_NAME : EXPLICIT_NAME);
		sb.append(' ');
		sb.append((asn.get_class() == BERImpl.UNIVERSAL_CLASS && asn.getTag() < 31) ? UNIVERSAL_TAG_NAME[asn.getTag()] : UNIVERSAL_TAG_NAME[UNIVERSAL_TAG_NAME.length - 1]);
		sb.append(' ');
		sb.append('[');
		sb.append(asn.getTag());
		sb.append(']');
		sb.append(' ');
		sb.append(asn.isOptional() ? OPTIONAL_NAME : MANDATORY_NAME);
		return sb.toString();
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}
	
}
