package org.pabk.basen.asn1.oid;

import java.io.Serializable;

public interface OidEntry extends Serializable {
	String getName();
	void setName(String name);
	String getDescription();
	void setDescription(String desc);
}
