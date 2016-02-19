package org.pabk.basen.asn1;

import java.awt.GridBagConstraints;

import javax.swing.JPanel;
import javax.swing.BoxLayout;

public class ASN1List extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ASN1List(ASN1Impl[][] list) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		int k = 1;
		for(int i = 0; i < list.length; i ++) {
			ASN1Impl[] seq = list[i];
			for(int j = 0; j < seq.length; j ++) {
				ASN1Impl asn = seq[j];
				if(asn != null) {
					ASN1Item value = new ASN1Item(asn);
					GridBagConstraints gbcValue = new GridBagConstraints();
					gbcValue.fill = GridBagConstraints.VERTICAL;
					gbcValue.anchor = GridBagConstraints.WEST;
					gbcValue.gridx = 1;
					gbcValue.gridy = k;
					add(value, gbcValue);
					k ++;
				}
			}
		}
	}
}
