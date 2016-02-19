package org.pabk.basen.asn1;

import java.io.IOException;

import org.pabk.basen.ber.BERImpl;

public class Sequence extends ASN1Impl {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String MANDATORY_OBJECT = "ASN1object %s is not optional";
	private static final String EMPTY_SEQUENCE = "This sequence cannot be empty";
	
	public Sequence (String name, boolean optional, ASN1Impl ... seq) {
		this.setName(name);
		this.set_class(BERImpl.UNIVERSAL_CLASS);
		this.setConstructed(BERImpl.CONSTRUCTED_ENCODING);
		this.setTag(BERImpl.SEQUENCE_TAG);
		this.setImplicit(false);
		this.setOptional(optional);
		this.setSequences(seq);
	}
	
	
	
	@Override
	public boolean setBERObject(BERImpl ber) throws IOException {
		this.clear();
		if(super.setBERObject(ber)) {
			setSequence(this, ber);
			return true;
		}
		return false;	
	}
	protected Sequence() {}
	@Override
	public Sequence clone() {
		return (Sequence) ASN1Impl.clone(new Sequence(), this);
	}
	
	public static void setSequence(Sequence s, BERImpl ber) throws IOException {
		try {
			ASN1Impl[] seq = s.getSequences();
			if(ber.size() == 0) {
				for(int i = 0; i < seq.length; i ++) {
					if(!seq[i].isOptional()) {
						throw new IOException(String.format(MANDATORY_OBJECT, seq[i].getName()));
					}
				}
			}
			else {
				int index = 0;
				while(index < ber.size()) {
					ASN1Impl[] _new = new ASN1Impl[seq.length];
					int c = 0;
					for(int i = 0; i < seq.length; i ++) {
						if(seq[i].checkId(ber.get(index))) {
							_new[i] = seq[i].clone();
							if(!_new[i].setBERObject(ber.get(index))) {
								throw new IOException("Failed to set BER object to " + s.getName() + ", " + seq[i].getName());
							}
							index ++;
							c ++;
							if(index == ber.size()) {
								for(int j = i + 1; j < seq.length; j ++) {
									if(!seq[j].isOptional()) {
										throw new IOException(String.format(MANDATORY_OBJECT, seq[i].getName()));
									}
								}
								break;
							}
						}
						else {
							if(!seq[i].isOptional()) {
								throw new IOException(String.format(MANDATORY_OBJECT, seq[i].getName()));
							}
						}
					}
					if(c == 0) {
						throw new IOException(EMPTY_SEQUENCE);
					}
					s.add(_new);
				}
			}
		}
		catch(Exception e) {
			s.clear();
			throw new IOException(e);
		}
	}
	
}
