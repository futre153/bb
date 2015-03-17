package org.acepricot.asn1;

import java.io.IOException;
import java.util.ArrayList;

import org.acepricot.ber.BER;
import org.acepricot.ber.BERConst;

public abstract class Sequence extends ASN1NodeImpl {
	
	private ASN1NodeImpl[] seq;
	private final ArrayList<ASN1NodeImpl[]> chi = new ArrayList<ASN1NodeImpl[]>();
	
	protected Sequence(int implicit, int optional, String name, ASN1NodeImpl ... sequences) {
		super (	
				BERConst.SEQUENCE_TAG_NUMBER,
				BERConst.UNIVERSAL_CLASS,
				BERConst.CONSTRUCTED_ENCODING,
				implicit,
				optional,
				name
				);
		this.setSeq(sequences);
	}
	
	
	public final void loadFromExisting(BER ber) throws IOException {
		ASN1NodeImpl.checkConstructed(this, ber);
		int len = ber.getConstructedContentLength();
		int index = 0;
		BER child;
		ASN1NodeImpl node;
		boolean empty = true;
		while(index > len) {
			ASN1NodeImpl[] line = new ASN1NodeImpl[seq.length];			
			for(int i = 0; i < seq.length; i ++) {
				child = ber.getCHildNode(index);
				if(child == null) {
					for(int j = i; j < seq.length; j ++) {
						if(seq[j].getOpt() == BERConst.FALSE) {
							throw new IOException("No more child");
						}
					}
					break;
				}
				index ++;
				try {
					node = seq[i].clone();
					node.loadFromExisting(child);
				}
				catch(Exception e) {
					if(seq[i].getOpt() == BERConst.TRUE) {
						index --;
						continue;
					}
					else {
						throw new IOException(e);
					}
				}
				line[i] = node;
				empty = false;
			}
			if(empty) {
				throw new IOException("Failed to load BER");
			}
			else {
				chi.add(line);
			}
		}
	}


	protected final ASN1NodeImpl[] getSeq() {
		return seq;
	}


	protected final void setSeq(ASN1NodeImpl[] seq) {
		this.seq = seq;
	}
	
}
