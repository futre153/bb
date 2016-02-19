package org.pabk.basen.rfc.rfc5280;

import java.io.IOException;
import java.util.ArrayList;

import org.pabk.basen.asn1.ASN1Impl;
import org.pabk.basen.asn1.BERBoolean;
import org.pabk.basen.asn1.ObjectIdentifier;
import org.pabk.basen.asn1.OctetString;
import org.pabk.basen.asn1.Sequence;
import org.pabk.basen.ber.BERImpl;

public class Extensions extends Sequence {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String EXT_NAME = "extension";
	private static final boolean EXT_OPT = false;
	private static final String EXT_ID_NAME = "extID";
	private static final boolean EXT_ID_OPT = false;
	private static final String CRIT_NAME = "critical";
	private static final boolean CRIT_OPT = true;
	private static final String EXT_VAL_NAME = "extnValue";
	private static final int EXT_VAL_PC = BERImpl.PRIMITIVE_ENCODING;
	private static final boolean EXT_VAL_OPT = false;

	private final ASN1Impl[] EXT_SEQ = {
		new ObjectIdentifier(EXT_ID_NAME, EXT_ID_OPT),
		new BERBoolean(CRIT_NAME, CRIT_OPT),
		new OctetString(EXT_VAL_NAME, EXT_VAL_PC, EXT_VAL_OPT)
	};

	private final ASN1Impl[] EXTS_SEQ = {
		new Sequence(EXT_NAME, EXT_OPT, EXT_SEQ)
	};
	
	public Extensions(String name, boolean optional) {
		super(name, optional);
		this.setSequences(EXTS_SEQ);
	}
	
	public static BERImpl createExtensions (Object ... exts) throws IOException {
		BERImpl exs = new BERImpl(BERImpl.SEQUENCE_TAG, BERImpl.CONSTRUCTED_ENCODING);
		ArrayList<BERImpl> chi = new ArrayList<BERImpl>();
		if (exts != null) {
			for(int i = 0; i < exts.length; i ++) {
				try {
					BERImpl ber;
					if(exts[i] instanceof BERImpl) {
						ber = (BERImpl) exts[i];
					}
					else {
						ber = createExtension((String) exts[i], (Boolean) exts[i + 1], (String) exts[i + 2]);
						i += 2;
					}
					chi.add(ber);
				}
				catch(Exception e) {
					break;
				}
			}
		}
		BERImpl[] tmp = new BERImpl[chi.size()];
		tmp = chi.toArray(tmp);
		exs.getEncoder().setValue(exs, tmp);
		return exs;
	}
	
	public static BERImpl createExtension (String type, boolean critical, Object value) throws IOException {
		BERImpl ext = new BERImpl(BERImpl.SEQUENCE_TAG, BERImpl.CONSTRUCTED_ENCODING);
		BERImpl oid = new BERImpl (BERImpl.OBJECT_IDENTIFIER_TAG, BERImpl.PRIMITIVE_ENCODING);
		BERImpl cri = new BERImpl (BERImpl.BOOLEAN_TAG, BERImpl.PRIMITIVE_ENCODING);
		BERImpl val = new BERImpl (BERImpl.OCTETSTRING_TAG, BERImpl.PRIMITIVE_ENCODING);
		oid.getEncoder().setValue(oid, type);
		cri.getEncoder().setValue(cri, critical ? new byte[]{-1} : new byte[]{0});
		val.getEncoder().setValue(val, value);
		if(critical) {
			ext.getEncoder().setValue(ext, new BERImpl[] {oid, cri, val});
		}
		else {
			ext.getEncoder().setValue(ext, new BERImpl[] {oid, val});
		}
		return ext;
	}
	
}
