package org.pabk.basen.asn1;

import java.io.IOException;

import org.pabk.basen.ber.BERImpl;

public class Choice extends ASN1Impl {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		
	//private ASN1Impl choosen;
	
	public Choice (String name, boolean optional, ASN1Impl ... seq) {
		this.setName(name);
		this.setOptional(optional);
		this.setSequences(seq);
		this.setImplicit(false);
		this.setTag(BERImpl.CHOICE);
	}
	
	public boolean checkId(BERImpl ber) {
		ASN1Impl[] seq = this.getSequences();
		for(int i = 0; i < seq.length; i ++) {
			if(seq[i].checkId(ber)) {
				return true;
			}
		}
		return false;
	}

	public String toString() {
		return this.get(0)[0].toString();
	}
	
	public boolean setBERObject(BERImpl ber) throws IOException {
		ASN1Impl[] seq = this.getSequences();
		for(int i = 0; i < seq.length; i ++) {
			if(seq[i].get_class() == ber.get_class() && seq[i].getConstructed() == ber.getConstructed() && seq[i].getTag() == ber.getTag()) {
				/*
				this.set_class(ber.get_class());
				this.setConstructed(ber.getConstructed());
				this.setTag(ber.getTag());
				return super.setBERObject(ber);
				*/
				ASN1Impl c = seq[i].clone();
				try {
					if(c.setBERObject(ber)) {
						this.add(new ASN1Impl[]{c});
					
						return true;
					}
				}
				catch(Exception e) {
					/*
					 * TODO
					 */
				}
			}
		}
		return false;
	}
	
	protected Choice() {}
	@Override
	public Choice clone() {
		return (Choice) ASN1Impl.clone(new Choice(), this);
	}

}
