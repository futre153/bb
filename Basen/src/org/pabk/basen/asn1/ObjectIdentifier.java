package org.pabk.basen.asn1;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.ietf.jgss.Oid;
import org.pabk.basen.ber.BERImpl;
import org.pabk.basen.ber.encoders.EncoderUtil;

public class ObjectIdentifier extends ASN1Impl {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ObjectIdentifier (String name, boolean optional) {
		this.setName(name);
		this.set_class(BERImpl.UNIVERSAL_CLASS);
		this.setConstructed(BERImpl.PRIMITIVE_ENCODING);
		this.setTag(BERImpl.OBJECT_IDENTIFIER_TAG);
		this.setImplicit(false);
		this.setOptional(optional);
	}
	private ObjectIdentifier() {}
	@Override
	public ObjectIdentifier clone() {
		return (ObjectIdentifier) ASN1Impl.clone(new ObjectIdentifier(), this);
	}
	public void setValue (Object ...objs) throws IOException {
		if(objs != null && objs.length == 1 && objs[0] != null) {
			try {
				Oid oid = null;
				if(objs[0] instanceof Oid) {
					oid = (Oid) objs[0];
				}
				else {
					if(objs[0] instanceof int[]) {
						oid = new Oid(EncoderUtil.join((int[]) objs[0], "."));
					}
					else if(objs[0] instanceof String) {
						oid = new Oid((String) objs[0]);
					}
				}	
				if(oid != null) {
					BERImpl ber = new BERImpl();
					ByteArrayInputStream in = new ByteArrayInputStream(oid.getDER());
					oid = null;
					ber.decode(in);
					this.setBERObject(ber);
				}
			}
			catch(Exception e) {
				throw new IOException (e);
			}
		}
	}
}
