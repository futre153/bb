package org.acepricot.asn1.rfc1557;

import org.acepricot.asn1.impl.ImpAny;
import org.acepricot.asn1.impl.ImpInteger;
import org.acepricot.asn1.impl.ImpOctetString;
import org.acepricot.asn1.impl.ImpSequence;

public class Message extends ImpSequence {
	
	private ImpInteger version;
	private ImpOctetString cummunity;
	private ImpAny data;
		
	protected Message(String name, ImpInteger version, ImpOctetString community, ImpAny data) {
		super(name);
		this.version = version;
		this.cummunity = community;
		this.data = data;
	}

	protected final ImpInteger getVersion() {
		return version;
	}

	protected final void setVersion(ImpInteger version) {
		this.version = version;
	}

	protected final ImpOctetString getCummunity() {
		return cummunity;
	}

	protected final void setCummunity(ImpOctetString cummunity) {
		this.cummunity = cummunity;
	}

	protected final ImpAny getData() {
		return data;
	}

	protected final void setData(ImpAny data) {
		this.data = data;
	}

}
