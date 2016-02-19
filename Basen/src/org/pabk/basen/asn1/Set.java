package org.pabk.basen.asn1;

import java.io.IOException;

import org.pabk.basen.ber.BERImpl;

public class Set extends ASN1Impl {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Set(String name, boolean optional, ASN1Impl ... seq) {
		this.setName(name);
		this.set_class(BERImpl.UNIVERSAL_CLASS);
		this.setConstructed(BERImpl.CONSTRUCTED_ENCODING);
		this.setTag(BERImpl.SET_TAG);
		this.setImplicit(false);
		this.setOptional(optional);
		this.setSequences(seq);
	}
	protected Set() {}
	@Override
	public Set clone() {
		return (Set) ASN1Impl.clone(new Set(), this);
	}
	public boolean setBERObject(BERImpl ber) throws IOException {
		this.clear();
		int index = 0;
		ASN1Impl[] _new = null;
		if(super.setBERObject(ber)) {
			while(index < ber.size()) {
				index = assignBer(ber, index, this, this.getSequences(), _new);
			}
		}
		else {
			return false;
		}
		return true;
	}
	
	private static int assignBer(BERImpl ber, int index, ASN1Impl set, ASN1Impl[] seq, ASN1Impl[] _new) {
		if(_new == null) {
			_new = new ASN1Impl[seq.length];
		}
		BERImpl child = ber.get(index);
		int i = 0;
		for(; i < seq.length; i ++) {
			//if(_new[i] != null && seq[i].checkId(child)) {
			if(_new[i] == null && seq[i].checkId(child)) {
				try {
					ASN1Impl asn = seq[i].clone();
					if(!asn.setBERObject(child)) {
						continue;
					}
					_new[i] = asn;
				}
				catch(Exception e) {
					continue;
				}
				break;
			}
		}
		if(i == seq.length) {
			for(i = 0; i < _new.length; i ++) {
				if(_new[i] != null) {
					break;
				}
			}
			if(i == _new.length) {
				set.clear();
				return -1;
			}
			else {
				for(i = 0; i <_new.length; i ++) {
					if(_new[i] == null && (!seq[i].isOptional())) {
						set.clear();
						return -1;
					}
				}
				set.add(_new);
				_new = null;
				return index;
			}
		}
		else {
			for(i = 0; i < _new.length; i ++) {
				if(_new[i] == null) {
					return index + 1;
				}
			}
			set.add(_new);
			_new = null;
		}
		return index + 1;
	}
	
}
