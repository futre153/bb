package org.pabk.basen.asn1;

import java.io.IOException;

import org.pabk.basen.ber.BERImpl;


public class Tag extends ASN1Impl {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Tag(String name, int _class, int pc, int tag, boolean implicit, boolean optional, ASN1Impl ... seq) {
		this.setName(name);
		this.set_class(_class);
		this.setConstructed(pc);
		this.setTag(tag);
		this.setImplicit(implicit);
		this.setOptional(optional);
		this.setSequences(seq);
	}
	private Tag() {}
	@Override
	public Tag clone() {
		return (Tag) ASN1Impl.clone(new Tag(), this);
	}
		//return new Tag (this.getName(), this.get_class(), this.getConstructed(), this.getTag(), this.isImplicit(), this.isOptional(), ASN1Impl.clone(this.getSequences()));
	
	public boolean setBERObject(BERImpl ber) throws IOException {
		try {
			if(super.setBERObject(ber)) {
				BERImpl child;
				ASN1Impl asn = this.getSequences()[0].clone();
				if(ber.hasConstructedEncoding() && !asn.isImplicit()) {
					child = ber.get(0);
				}
				else {
					child = ber;
				}
				if(asn.setBERObject(child)) {
					this.add(new ASN1Impl[]{asn});
					return true;
				}
			}
			return false;
		}
		catch (Exception e) {
			throw new IOException (e);
		}
	}
	
}
